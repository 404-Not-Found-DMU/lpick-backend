package com.notfound.lpickbackend.userinfo.command.application.service;


import com.notfound.lpickbackend.common.elasticsearch.service.DataSyncService;
import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.s3.service.S3Uploader;
import com.notfound.lpickbackend.common.s3.util.FileValidationPolicy;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.ExpertRequest;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.ExpertAdvancementRequest;
import com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request.ExpertRequestRejectCause;
import com.notfound.lpickbackend.userinfo.command.repository.ExpertRequestCommandRepository;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpertRequestCommandService {

    private final ExpertRequestCommandRepository expertRequestCommandRepository;
    private final UserInfoCommandRepository userInfoCommandRepository;

    private final S3Uploader s3Uploader;
    private final FileValidationPolicy fileValidationPolicy;
    private final DataSyncService dataSyncService;

    @Transactional
    public void expertAdvancementRequest(String oauthId, ExpertAdvancementRequest request, List<MultipartFile> files) {
        // 사용자가 현재 신청상태 또는 승낙상태인지 확인(즉, 반려 상태인 expertRequest만 지니거나, 아예 expertRequest가 없어야함)
        if (expertRequestCommandRepository.existsByUserInfo_OauthIdAndStatusIn(oauthId, List.of(ExpertRequestStatus.APPROVED, ExpertRequestStatus.PENDING)))
            throw new CustomException(ErrorCode.CAN_NOT_EXPERT_ADVANCEMENT_REQUEST_AGAIN);

        // 선택한 장르가 없거나 2개를 초과할 경우
        if(request.getMusicGenreList().size() > 2 || request.getMusicGenreList().size() < 1) {
            throw new CustomException(ErrorCode.EXPERT_REQUEST_GENRE_SIZE_MUST_ONE_OR_TWO);
        }

        List<String> fileUploadResult = new ArrayList<>();
        for(MultipartFile file : files) {
            try {
                fileValidationPolicy.validateForExpertRequest(file);
                fileUploadResult.add(s3Uploader.upload(file, "ExpertRequest/" + file.getContentType()));
            } catch(Exception e) { // 원래는 validate 실패시 기존 s3 상 upload 내역 다시 제거하는 로직 구현해야함. 근데 시가닝 없다...
                throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }

        UserInfo targetUser = userInfoCommandRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO));

        ExpertRequest expertRequest = ExpertRequest.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phNum(request.getPhNum())
                .affiliation(request.getAffiliation())
                .musicGenre(request.getMusicGenreList())
                .requestMemo(request.getRequestMemo())
                .status(ExpertRequestStatus.PENDING)
                .requestFileJson(fileUploadResult)
                .userInfo(targetUser)
                .build();

        ExpertRequest resultRequest = expertRequestCommandRepository.save(expertRequest);

        dataSyncService.syncExpertRequest(resultRequest);
    }

    @Transactional
    public void expertAdvancementDelete(String oauthId) {

        ExpertRequest targetRequest = expertRequestCommandRepository.findByUserInfo_OauthIdAndStatusIn(oauthId, List.of(ExpertRequestStatus.PENDING))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EXPERT_REQUEST));

        for(String fileUrl : targetRequest.getRequestFileJson()) s3Uploader.deleteByUrlToKey(fileUrl);

        dataSyncService.deleteExpertRequest(targetRequest.getExpertRequestId());
        expertRequestCommandRepository.delete(targetRequest);
    }

    @Transactional
    public void acceptExpertRequest(String requestId) {
        ExpertRequest targetRequest = expertRequestCommandRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EXPERT_REQUEST));

        targetRequest.markApproved();
        expertRequestCommandRepository.save(targetRequest);
    }

    @Transactional
    public void rejectExpertRequest(ExpertRequestRejectCause cause, String requestId) {
        ExpertRequest targetRequest = expertRequestCommandRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EXPERT_REQUEST));

        targetRequest.markRejected(cause.getRejectedCause());
        expertRequestCommandRepository.save(targetRequest);
    }
}

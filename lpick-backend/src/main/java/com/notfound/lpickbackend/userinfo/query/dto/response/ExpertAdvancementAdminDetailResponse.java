package com.notfound.lpickbackend.userinfo.query.dto.response;

import com.notfound.lpickbackend.common.s3.dto.S3DataValue;
import com.notfound.lpickbackend.common.s3.util.FileInfoResolver;
import com.notfound.lpickbackend.servicedata.command.application.domain.inherenceENUM.MusicGenre;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.ExpertRequest;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ExpertAdvancementAdminDetailResponse {
    private String userId;
    private Instant createdAt;
    private Instant modifiedAt;
    private String userName;
    private String userEmail;
    private String userPhNum;
    private List<MusicGenre> genre;
    private List<S3DataValue> dataUrlList;
    private ExpertRequestStatus status;
    private String requestMemo;

    public static ExpertAdvancementAdminDetailResponse from(ExpertRequest expertRequest) {

        List<S3DataValue> s3DataValueList = expertRequest.getRequestFileJson()
                .stream().map(FileInfoResolver::resolve).toList();

        return ExpertAdvancementAdminDetailResponse.builder()
                .userId(expertRequest.getUserInfo().getOauthId()) // 혹시나.. 해서.. 일단 제공.. EntityGraph라 N+1 발생 X
                .createdAt(expertRequest.getCreatedAt())
                .modifiedAt(expertRequest.getDecisionAt())
                .userName(expertRequest.getName())
                .userEmail(expertRequest.getEmail())
                .userPhNum(expertRequest.getPhNum())
                .genre(expertRequest.getMusicGenre())
                .dataUrlList(s3DataValueList)
                .status(expertRequest.getStatus())
                .requestMemo(expertRequest.getRequestMemo())
                .build();
    }
}

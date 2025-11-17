package com.notfound.lpickbackend.common.s3.util;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileValidationPolicy {
    private final AppFileUploadProperties props;

    // 공통 유틸
    private void validate(MultipartFile file, AppFileUploadProperties.Feature feature) {
        String originalName = file.getOriginalFilename();
        String ext = FileInfoResolver.extractExtension(originalName); // 소문자 확장자
        long size = file.getSize();

        if (!feature.getAllowedExtensions().contains(ext)) {
            throw new CustomException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        if (size > feature.getMaxSize().toBytes()) {
            throw new CustomException(ErrorCode.FILE_TOO_LARGE);
        }
    }

    // 전문가 승급 요청용
        public void validateForExpertRequest(MultipartFile file) {
            validate(file, props.getExpertRequest());
        }

        // 프로필 이미지 업로드용
        public void validateForProfileImage(MultipartFile file) {
        validate(file, props.getProfileImage());
    }
}

package com.notfound.lpickbackend.common.s3.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.util.List;

/** yml에 적힌 파일 업로드 기능별 파일 양식 화이트리스트 프롭 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.file-upload")
public class AppFileUploadProperties {

    private Feature expertRequest = new Feature();
    private Feature profileImage  = new Feature();

    @Getter
    @Setter
    public static class Feature {
        /**
         * 허용할 확장자 목록 (소문자 기준)
         */
        private List<String> allowedExtensions;

        /**
         * 허용 최대 파일 크기 (bytes 단위 가정)
         */
        private DataSize maxSize;
    }
}

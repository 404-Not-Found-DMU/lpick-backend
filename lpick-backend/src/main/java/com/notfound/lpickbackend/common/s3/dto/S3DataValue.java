package com.notfound.lpickbackend.common.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** S3 데이터를 내려줄때, 표기할 명칭, url, 컨텐츠 타입을 표기하는 경우 사용 */
@Getter
@Builder
public class S3DataValue {
    private String originFileName;
    private String fileUrl;
    private String contentType;
}

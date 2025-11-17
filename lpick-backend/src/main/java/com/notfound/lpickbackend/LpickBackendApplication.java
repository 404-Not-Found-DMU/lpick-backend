package com.notfound.lpickbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import com.notfound.lpickbackend.common.s3.util.AppFileUploadProperties;

@EnableJpaAuditing
@EnableConfigurationProperties(AppFileUploadProperties.class) // s3에 업로드할 수 있는 파일 양식을 yml 기반으로 기능별로 제어 가능
@SpringBootApplication
public class LpickBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LpickBackendApplication.class, args);
    }

}

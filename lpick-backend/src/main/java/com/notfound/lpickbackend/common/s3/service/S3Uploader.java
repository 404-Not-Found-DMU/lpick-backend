package com.notfound.lpickbackend.common.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Component
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Uploader(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    // 주어진 파일을 변환해서 업로드해주는 총체적 로직
    public String uploadFiles(MultipartFile multipartFile, String dirName) throws IOException {
        // 1) 오리지널 파일명과 UUID로 키 생성
        String origin = multipartFile.getOriginalFilename();
        String key    = String.format("%s/%s_%s", dirName, UUID.randomUUID(), origin);

        // 2) 메타데이터 세팅
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // 3) 스트리밍 업로드
        try (InputStream is = multipartFile.getInputStream()) {
            amazonS3Client.putObject(bucket, key, is, metadata);
        }

        // 4) 업로드된 객체의 URL 리턴
        return amazonS3Client.getUrl(bucket, key).toString();
    }

    // 받아온 파일을 변환한 후 s3에 실제로 업로드 해주는 로직
    public String upload(MultipartFile multipartFile, String dir) throws IOException {
        String origin = multipartFile.getOriginalFilename();
        String key    = String.format("%s/%s_%s", dir, UUID.randomUUID(), origin);

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(multipartFile.getSize());
        meta.setContentType(multipartFile.getContentType());

        amazonS3Client.putObject(bucket, key,
                multipartFile.getInputStream(), meta);

        return amazonS3Client.getUrl(bucket, key).toString();
    }

    /**
     * S3에서 특정 디렉토리(dirName) 아래의 파일(fileName)을 삭제합니다.
     *
     * @param dirName   삭제할 파일이 속한 가상 디렉토리 이름 (예: "record")
     * @param fileName  삭제할 파일명 (UUID_원본이름 포함)
     */
    public void deleteFile(String dirName, String fileName) {
        // S3 key는 "dirName/fileName" 형태입니다.
        String key = String.format("%s/%s", dirName, fileName);

        // 삭제 요청
        amazonS3Client.deleteObject(
                new DeleteObjectRequest(bucket, key)
        );
    }


}
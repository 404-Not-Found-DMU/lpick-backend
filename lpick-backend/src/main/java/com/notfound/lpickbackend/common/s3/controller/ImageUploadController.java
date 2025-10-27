package com.notfound.lpickbackend.common.s3.controller;

import com.notfound.lpickbackend.common.s3.dto.ImageResponse;
import com.notfound.lpickbackend.common.s3.service.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image-upload")
@Tag(name = "이미지 업로드 컨트롤러", description = "이미지 업로드")
public class ImageUploadController {

    private final S3Uploader s3Uploader;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "이미지 단일 업로드", description = "도메인 타입은 NOTICE, COURSE, QUESTION, ANSWER, FAQ 중 어떤걸 작성하냐에 따라 골라서 보내주시면 됩니다.")
    ResponseEntity<ImageResponse> imageUpload(@RequestPart(name = "imageFile") MultipartFile file) throws IOException {

        return ResponseEntity.ok(
                ImageResponse
                        .builder()
                        .url(s3Uploader.upload(file, "image"))
                        .build()
        );
    }
}

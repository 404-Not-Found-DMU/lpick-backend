package com.notfound.lpickbackend.common.s3.util;

import com.notfound.lpickbackend.common.s3.dto.S3DataValue;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** s3 url째로 저장되어있는 s3 데이터 url(이미지 등)에서 원래 명칭, 컨텐츠 타입을 얻는 목적의 클래스입니다.  */
@NoArgsConstructor
public final class FileInfoResolver {

    private static final Map<String, String> EXT_TO_MIME = new HashMap<>();

    static {
        // 문서
        EXT_TO_MIME.put("pdf", "application/pdf");
        EXT_TO_MIME.put("doc", "application/msword");
        EXT_TO_MIME.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        EXT_TO_MIME.put("hwp", "application/x-hwp");      // 환경에 맞게 필요시 변경

        // 이미지
        EXT_TO_MIME.put("jpg", "image/jpeg");
        EXT_TO_MIME.put("jpeg", "image/jpeg");
        EXT_TO_MIME.put("png", "image/png");
        EXT_TO_MIME.put("gif", "image/gif");
        EXT_TO_MIME.put("webp", "image/webp");

        // 기타 자주 쓸 수 있는 타입 예시
        EXT_TO_MIME.put("txt", "text/plain");
        EXT_TO_MIME.put("zip", "application/zip");
        EXT_TO_MIME.put("7z", "application/x-7z-compressed");
    }
    /**
     * 전체 URL로부터 FileInfo 추출.
     * - fileName: UUID_원래이름.ext → stripUuidPrefix()를 통해 원래이름.ext로 정리
     * - contentType: 확장자 기반 MIME 추정
     */
    public static S3DataValue resolve(String url) {
        String rawFileName = extractFileName(url);
        String displayName = stripUuidPrefix(rawFileName);
        String contentType = resolveContentType(rawFileName);

        return S3DataValue.builder()
                .originFileName(displayName)
                .fileUrl(url)
                .contentType(contentType)
                .build();
    }

    /**
     * URL에서 마지막 path segment를 파일명으로 추출.
     * 예: https://.../expert-requests/uuid_portfolio.pdf → uuid_portfolio.pdf
     */
    public static String extractFileName(String url) {
        try {
            URI uri = URI.create(url);
            String path = uri.getPath();  // /expert-requests/uuid_portfolio.pdf
            int lastSlash = path.lastIndexOf('/');
            String lastSegment = (lastSlash >= 0) ? path.substring(lastSlash + 1) : path;
            return URLDecoder.decode(lastSegment, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // URL 파싱 실패 시에는 전체 URL을 fallback으로 사용
            return url;
        }
    }

    /**
     * 파일명에서 확장자 부분만 추출 (소문자 반환).
     * 예: "uuid_portfolio.PDF" → "pdf"
     */
    public static String extractExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * 업로더에서 "UUID_원래파일명.ext" 형태로 저장하는 경우,
     * 화면에는 "원래파일명.ext"만 보여주고 싶을 때 사용.
     * 필요 없다면 stripUuidPrefix() 호출 안 해도 됨.
     */
    public static String stripUuidPrefix(String fileName) {
        if (fileName == null) {
            return "";
        }
        int underscore = fileName.indexOf('_');
        if (underscore > 0 && underscore < fileName.length() - 1) {
            return fileName.substring(underscore + 1);
        }
        return fileName;
    }

    /**
     * 파일명 확장자를 기준으로 contentType 추정.
     * 모르는 확장자는 application/octet-stream 으로 처리.
     */
    public static String resolveContentType(String fileName) {
        String ext = extractExtension(fileName);
        if (ext.isEmpty()) {
            return "application/octet-stream";
        }
        return EXT_TO_MIME.getOrDefault(ext, "application/octet-stream");
    }
}
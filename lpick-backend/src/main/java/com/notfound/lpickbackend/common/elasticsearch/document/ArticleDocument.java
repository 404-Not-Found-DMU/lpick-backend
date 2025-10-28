package com.notfound.lpickbackend.common.elasticsearch.document;

import com.notfound.lpickbackend.community.command.domain.Article;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;

@Getter
@Builder
@Document(indexName = "articles") // Elasticsearch 인덱스 이름 지정
@Mapping(mappingPath = "elasticsearch/article-mapping.json") // 매핑 파일 경로 (선택 사항)
@Setting(settingPath = "elasticsearch/document-settings.json") // 설정 파일 경로 (선택 사항)
@ToString
public class ArticleDocument {

    // 통합 검색을 위한 타입 상수 정의
    public static final String DOCUMENT_TYPE = "ARTICLE";

    @Id
    @Field(type = FieldType.Keyword) // 정확한 일치 검색
    private String articleId;

    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer", // 자동완성 분석기
            searchAnalyzer = "korean_analyzer" // 검색 시 한국어 분석기
    )
    private String title;

    @Field(
            type = FieldType.Text,
            analyzer = "korean_analyzer" // 본문 검색을 위한 한국어 분석기
    )
    private String content;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private Instant createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private Instant modifiedAt;

    @Field(type = FieldType.Keyword) // Enum 값을 문자열 키워드로 저장
    private String isDel;

    @Field(type = FieldType.Keyword) // Enum 값을 문자열 키워드로 저장
    private String articleType;

    @Field(type = FieldType.Keyword) // Enum 값을 문자열 키워드로 저장
    private String articleBadge;

    @Field(type = FieldType.Keyword) // 작성자 ID (정확한 일치)
    private String authorId;

    @Field(
            type = FieldType.Text,
            analyzer = "autocomplete_analyzer", // 작성자 닉네임도 검색 대상
            searchAnalyzer = "korean_analyzer"
    )
    private String authorNickname; // UserInfo의 닉네임 (가정)

    // JPA 엔티티를 Document로 변환하는 헬퍼 메서드
    public static ArticleDocument from(Article article) {
        UserInfo author = article.getOauth();
        String authorId = null;
        String authorNickname = null;

        if (author != null) {
            // UserInfo 엔티티의 실제 ID 필드명과 닉네임 필드명으로 변경해야 합니다.
            // 예: author.getOauthId(), author.getNickname()
            authorId = author.getOauthId(); // UserInfo에 getOauthId()가 있다고 가정
            authorNickname = author.getNickname(); // UserInfo에 getNickname()이 있다고 가정
        }

        return ArticleDocument.builder()
                .articleId(article.getArticleId())
                .title(article.getTitle())
                .content(article.getContent())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .isDel(article.getIsDel().name()) // Enum -> String
                .articleType(article.getArticleType().name()) // Enum -> String
                .articleBadge(article.getArticleBadge().name()) // Enum -> String
                .authorId(authorId)
                .authorNickname(authorNickname)
                .build();
    }
}

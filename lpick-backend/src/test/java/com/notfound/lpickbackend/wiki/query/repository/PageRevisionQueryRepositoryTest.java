package com.notfound.lpickbackend.wiki.query.repository;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;


import java.time.Instant;
import java.util.List;

@ActiveProfiles("test") // test 프로파일 설정 기반으로 동작하는 테스트코드
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional // 테스트가 종료되면 DB 상태를 테스트 동작 이전으로 롤백
class PageRevisionQueryRepositoryTest {

    private PageRevisionQueryService pageRevisionQueryService;

    @Autowired
    private PageRevisionQueryRepository pageRevisionQueryRepository;

    @Autowired
    private TestEntityManager em;  // 엔티티를 미리 Persist할 때 사용

    @BeforeEach
    void setUp() {
        // pageRevisionQueryRepository 는 @Autowired 로 주입되므로, 이를 사용해 서비스 객체를 직접 생성
        this.pageRevisionQueryService = new PageRevisionQueryService(pageRevisionQueryRepository);
    }

    @Test
    @DisplayName("JPA_TEST: 가장 최근 생성된 PageRevision들을 PageRequest 기준으로 페이징 조회한다. 단, WikiPage 그룹 별로 1개 씩만 추출하도록 한다.")
    void getRecentlyCreatedPageRevision() {
        // --- given: 샘플 데이터 자체를 아예 data.sql에 추가했으므로 생략. ---


        // --- when: PageRequest.of(0, 3, createdAt DESC) 기준으로 “가장 최근(최신)” 3개만 조회 ---
        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<PageRevision> resultPage = pageRevisionQueryService.getLatestRevisionPerWiki(pageRequest);

        // --- then: revision-5, revision-6, revision-4 순으로 총 3개 반환 확인`
        assertThat(resultPage.getContent()).hasSize(3);

        // content 리스트를 꺼내서 순서대로 비교
        List<PageRevision> actualList = resultPage.getContent();
        assertThat(actualList.get(0).getRevisionId()).isEqualTo("revision-5");
        assertThat(actualList.get(1).getRevisionId()).isEqualTo("revision-6");
        assertThat(actualList.get(2).getRevisionId()).isEqualTo("revision-4");
    }

}
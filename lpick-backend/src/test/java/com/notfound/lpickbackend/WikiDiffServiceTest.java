package com.notfound.lpickbackend;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.notfound.lpickbackend.wiki.command.application.domain.PageRevision;
import com.notfound.lpickbackend.wiki.query.service.PageRevisionQueryService;
import com.notfound.lpickbackend.wiki.query.service.logic.WikiDiffServiceV2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 테스트 대상 : WikiDiffServiceV2
 * 목적 : getTwoRevisionDiffHtml()를 이용한 diff 비교 시의 로직 및 예외처리가 정상 동작하는지 확인한다.
 */

@ExtendWith(MockitoExtension.class)
class WikiDiffServiceTest {

    @Mock // 메소드 명칭만 있는 비어있는 서비스를 인터페이스 느낌으로 가져옴
    private PageRevisionQueryService pageRevisionQueryService;

    @InjectMocks // wikiDiffService를 실제로 생성
    private WikiDiffServiceV2 wikiDiffService;

    // 테스트 메소드 명명규칙은 아래와 같이 작성.
    // given{{주어진 상황}}_when{{테스트 호출 메소드}}_then{{예상결과}}

    @Test
    void givenNoRevisions_whenGetTwoRevisionDiffHtml_thenThrowException() {
        // mock객체인 pageRevisionQueryService의 getTwoRevision()에 대한 input/output을 설정
        given(pageRevisionQueryService.readTwoRevision(anyString(), anyString(), anyString()))
                .willReturn(Collections.emptyList());
        
        // 실행 시 특정 예외와 에러메시지가 나타나는지 확인
        assertThatThrownBy(() ->
                wikiDiffService.getTwoRevisionDiffHtml("wiki", "v1", "v2")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리비전 비교대상이 정상적으로 불러와지지않음.");
    }


    @Test
    void givenOneRevision_whenGetTwoRevisionDiffHtml_thenThrowException() {
        PageRevision single = pageRevision("irrelevant");
        given(pageRevisionQueryService.readTwoRevision(anyString(), anyString(), anyString()))
                .willReturn(Collections.singletonList(single));

        assertThatThrownBy(() ->
                wikiDiffService.getTwoRevisionDiffHtml("wiki", "v1", "v2")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("리비전 비교대상이 정상적으로 불러와지지않음.");
    }


    @Test
    void givenTwoSameContentRevisions_whenGetTwoRevisionDiffHtml_thenReturnEmpty() {
        PageRevision a = pageRevision("SAME");
        PageRevision b = pageRevision("SAME");

        given(pageRevisionQueryService.readTwoRevision("wiki", "old", "new"))
                .willReturn(Arrays.asList(a, b));

        String html = wikiDiffService.getTwoRevisionDiffHtml("wiki", "old", "new");
        assertThat(html).isEmpty();
    }

    @Test
    void givenDifferentRevisions_thenReturnValidHtml() {
        // "\n" 있어야 groupByLine()이 라인 단위로 묶습니다.
        PageRevision oldRev = pageRevision("Line1\n");
        PageRevision newRev = pageRevision("Line2\n");
        given(pageRevisionQueryService.readTwoRevision("wiki", "old", "new"))
                .willReturn(List.of(oldRev, newRev));

        String html = wikiDiffService.getTwoRevisionDiffHtml("wiki", "old", "new");

        // — 테이블 헤더 확인 —
        assertThat(html).startsWith("<table class=\"diff-table\"");
        assertThat(html).contains("<th>old</th>", "<th>new</th>", "<th>old ➤ new</th>");

        // — 삭제된 숫자 '1' 이 빨간 span 안에, 앞에 'Line' 이 있는 형태 확인 —
        assertThat(html)
                .contains("Line<span class=\"opennamu_diff_red\">1</span>");
        // — 추가된 숫자 '2' 가 초록 span 안에, 앞에 'Line' 이 있는 형태 확인 —
        assertThat(html)
                .contains("Line<span class=\"opennamu_diff_green\">2</span>");

        // — 테이블 닫힘 태그 —
        assertThat(html).endsWith("</table>");
    }


    // PageRevision 엔티티 표현 위한 mock 생성
    private PageRevision pageRevision(String content) {
        PageRevision rev = mock(PageRevision.class);
        lenient().when(rev.getContent()).thenReturn(content); // lenient()로 설정하여 테스트시 getcontent가 동작하지 않아도 stub 이상 없도록 설정.
        return rev;
    }
}


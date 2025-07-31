package com.notfound.lpickbackend.userinfo.query.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UserActivityResponse {
    private Integer articleCount;
    private Integer commentCount;
    private Integer wikiEditCount; // 순수하게 위키 편집에만 기준하는가? 아니면 위키 토론 참여 등을 포함하는가? => 우선 위키 편집(== PageRevision 수정 아이디) 기준함.
    private Integer debateChatCount; // 토론 글에 의견을 제시한 개수

}
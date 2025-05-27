package com.notfound.lpickbackend.wikipage.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class WikiPageCreateRequestDTO {

    // 문서 제목
    String title;

    // 문서 내용
    String content;

    // 임시로 넣는 사용자 ID
    String userId;
}

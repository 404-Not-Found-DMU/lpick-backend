package com.notfound.lpickbackend.servicedata.query.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class SearchResult {

    private final String id;

    /**
     * 검색된 도큐먼트의 이름 (Album 이름, Artist 이름, Gear 이름)
     */
    private final String name;

    /**
     * 도큐먼트의 종류 (예: "Album", "Artist", "Gear")
     */
    private final String documentType;
}

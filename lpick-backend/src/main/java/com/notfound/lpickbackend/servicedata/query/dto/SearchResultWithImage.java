package com.notfound.lpickbackend.servicedata.query.dto;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiPage;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class SearchResultWithImage {

    private String wikiId;

    private String albumId;

    private String name;

    private double similarity;

    private double distance;

    private String imageUrl;

    public SearchResultWithImage(WikiPage wikiPage) {
        this.wikiId = wikiPage.getWikiId();
        this.name = wikiPage.getTitle();
    }

    public SearchResultWithImage(WikiPage wikiPage, ImageSearchResult imageSearchResult) {
        this.wikiId = wikiPage.getWikiId();
        this.name = wikiPage.getTitle();
        this.albumId = imageSearchResult.getRelease_id();
        this.similarity = imageSearchResult.getSimilarity();
        this.distance = imageSearchResult.getDistance();
    }
}

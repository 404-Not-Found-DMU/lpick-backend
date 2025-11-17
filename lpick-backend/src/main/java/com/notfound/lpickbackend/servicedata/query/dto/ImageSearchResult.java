package com.notfound.lpickbackend.servicedata.query.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageSearchResult { // 이미지 검색 ai 검색 결과(해당 이미지 정보)

    private int rank;
    private int image_id;
    private double similarity;
    private double distance;
    private String genre;
    private String filename;
    private String image_path;
    private String release_id;  // nullable이므로 Integer
    private String title;        // nullable 허용

}

package com.notfound.lpickbackend.servicedata.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageSearchResponse {

    private boolean success;
    private double query_time_ms;
    private int total_results;
    private List<ImageSearchResult> results;
}

package com.notfound.lpickbackend.wiki.revision_domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Canonicalizer {
    private Canonicalizer(){}

    // textBlocks: depth → id 정렬
    public static void sortTextBlocks(ObjectNode root) {
        JsonNode blocks = root.path("textBlocks");
        if (!(blocks instanceof ArrayNode arr)) return;

        List<ObjectNode> list = new ArrayList<>();
        arr.forEach(n -> { if (n instanceof ObjectNode o) list.add(o); });

        list.sort(
                Comparator.<ObjectNode>comparingInt(o -> o.path("depth").asInt())
                        .thenComparing(o -> o.path("id").asText(""))
        );

        arr.removeAll();
        list.forEach(arr::add);
    }

    // lp만 대상: tracklist.tracks 를 number(숫자) 오름차순
    public static void sortTracksByNumber(ObjectNode root) {
        var type = root.at("/categoryData/type").asText();
        if (!"lp".equals(type)) return;

        JsonNode tracks = root.at("/categoryData/data/tracklist/tracks");
        if (!(tracks instanceof ArrayNode arr)) return;

        List<ObjectNode> list = new ArrayList<>();
        arr.forEach(n -> { if (n instanceof ObjectNode o) list.add(o); });

        list.sort(Comparator.comparingInt(o -> {
            try { return Integer.parseInt(o.path("number").asText()); }
            catch (Exception e) { return Integer.MAX_VALUE; }
        }));

        arr.removeAll();
        list.forEach(arr::add);
    }
}
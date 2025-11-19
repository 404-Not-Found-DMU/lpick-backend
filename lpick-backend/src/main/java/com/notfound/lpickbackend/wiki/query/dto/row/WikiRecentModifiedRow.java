package com.notfound.lpickbackend.wiki.query.dto.row;

import com.notfound.lpickbackend.wiki.command.application.domain.WikiPageClass;

import java.time.Instant;

public record WikiRecentModifiedRow(
        String wikiId,
        String title,
        Instant createdAt,
        WikiPageClass wikiClass
) {}
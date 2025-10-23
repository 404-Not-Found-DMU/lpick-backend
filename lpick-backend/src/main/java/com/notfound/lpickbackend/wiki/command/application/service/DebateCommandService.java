package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.security.details.OAuth2UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebateCommandService {
    public String createDebate(String wikiId, OAuth2UserDetails userDetail) {
        return "null";
    }
}

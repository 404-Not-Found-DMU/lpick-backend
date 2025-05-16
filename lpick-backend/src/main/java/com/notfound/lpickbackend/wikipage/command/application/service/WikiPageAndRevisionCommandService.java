package com.notfound.lpickbackend.wikipage.command.application.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
* WikiPage와 Revision 생성 서비스 로직을 하나의 트랜잭션에서 수행하기 위한 서비스
* */
@Service
@RequiredArgsConstructor
public class WikiPageAndRevisionCommandService {

    private final WikiPageCommandService wikiPageCommandService;
}

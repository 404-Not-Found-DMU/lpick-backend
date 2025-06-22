package com.notfound.lpickbackend.userinfo.command.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainCommandService {

    private final UserInfoCommandService userInfoCommandService;
}

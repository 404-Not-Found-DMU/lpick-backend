package com.notfound.lpickbackend.userinfo.query.service;

import com.notfound.lpickbackend.userinfo.query.repository.UserTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTierQueryService {
    private final UserTierRepository userTierRepository;

}

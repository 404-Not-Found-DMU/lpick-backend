package com.notfound.lpickbackend.userInfo.query.service;

import com.notfound.lpickbackend.userInfo.query.repository.UserTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTierQueryService {
    private final UserTierRepository userTierRepository;

}

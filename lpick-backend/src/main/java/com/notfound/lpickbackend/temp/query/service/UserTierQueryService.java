package com.notfound.lpickbackend.temp.query.service;

import com.notfound.lpickbackend.temp.query.repository.UserTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTierQueryService {
    private final UserTierRepository userTierRepository;

}

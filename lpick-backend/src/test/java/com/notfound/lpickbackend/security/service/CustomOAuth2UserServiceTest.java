package com.notfound.lpickbackend.security.service;

import com.notfound.lpickbackend.AUTO_ENTITIES.Tier;
import com.notfound.lpickbackend.AUTO_ENTITIES.UserInfo;
import com.notfound.lpickbackend.TestUtil;
import com.notfound.lpickbackend.security.details.CustomOAuthUser;
import com.notfound.lpickbackend.tier.query.repository.TierCommandRepository;
import com.notfound.lpickbackend.temp.command.repository.UserInfoCommandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Mock
    private UserInfoCommandRepository userInfoCommandRepository;

    @Mock
    private TierCommandRepository tierCommandRepository;

    @Mock
    private OAuth2UserRequest userRequest;

    /**
     * 기존 유저가 로그인할 경우,
     * DB에서 유저 정보를 불러오고 새로운 객체를 생성하지 않는지 검증
     */
    @Test
    void loadOldUser() {
        String kakaoId = "kakao-123";

        // ClientRegistration mock 설정 (OAuth2 provider 정보)
        when(userRequest.getClientRegistration()).thenReturn(TestUtil.mockClientRegistration("kakao"));

        // OAuth2User mock 생성 및 id 설정
        OAuth2User mockedOAuth2User = mock(OAuth2User.class);
        when(mockedOAuth2User.getAttribute("id")).thenReturn(kakaoId);

        // 해당 id의 유저가 이미 DB에 존재한다고 가정
        when(userInfoCommandRepository.findById(kakaoId))
                .thenReturn(Optional.of(UserInfo.builder().oauthId(kakaoId).build()));

        // CustomOAuth2UserService를 Spy로 생성, super.loadUser 대신 mock 반환 (super.loadUser 실행하면 진짜로 외부 api 통신해버림)
        CustomOAuth2UserService spyService = spy(customOAuth2UserService);
        doReturn(mockedOAuth2User).when(spyService).getOAuth2User(any(OAuth2UserRequest.class));

        // loadUser 실행
        OAuth2User result = spyService.loadUser(userRequest);

        // 반환된 객체가 CustomOAuthUser인지, oauthId가 일치하는지 확인
        assertTrue(result instanceof CustomOAuthUser);
        assertEquals(kakaoId, ((CustomOAuthUser) result).getUserInfo().getOauthId());
    }

    /**
     * 신규 유저가 로그인할 경우,
     * DB에 저장된 적 없는 유저 정보를 생성하고 저장하는지 검증
     */
    @Test
    void loadNewUser() {
        String kakaoId = "kakao-new";

        // ClientRegistration mock 설정
        when(userRequest.getClientRegistration()).thenReturn(TestUtil.mockClientRegistration("kakao"));

        // OAuth2User mock 생성 및 id 속성 설정
        OAuth2User mockedOAuth2User = mock(OAuth2User.class);
        when(mockedOAuth2User.getAttribute("id")).thenReturn(kakaoId);

        // 유저가 DB에 존재하지 않음
        when(userInfoCommandRepository.findById(kakaoId)).thenReturn(Optional.empty());

        // 기본 Tier 정보를 반환
        Tier defaultTier = Tier.builder().tierId("1").build();
        when(tierCommandRepository.findById("1")).thenReturn(Optional.of(defaultTier));

        // 저장된 유저 객체를 캡처하기 위한 captor 설정
        ArgumentCaptor<UserInfo> captor = ArgumentCaptor.forClass(UserInfo.class);
        when(userInfoCommandRepository.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        // super.loadUser를 호출하지 않고 mock된 OAuth2User 반환
        CustomOAuth2UserService spyService = spy(customOAuth2UserService);
        doReturn(mockedOAuth2User).when(spyService).getOAuth2User(any(OAuth2UserRequest.class));

        // loadUser 실행
        OAuth2User result = spyService.loadUser(userRequest);

        // 새로 저장된 유저 정보 확인
        UserInfo saved = captor.getValue();
        assertEquals(kakaoId, saved.getOauthId());
        assertEquals(defaultTier, saved.getTier());

        // 반환 타입 및 래핑된 정보 확인
        assertTrue(result instanceof CustomOAuthUser);
    }
}

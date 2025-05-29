package com.notfound.lpickbackend;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

public class TestUtil {
    public static ClientRegistration mockClientRegistration(String registrationId) {
        return ClientRegistration.withRegistrationId(registrationId)
                .clientId("test-client-id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .tokenUri("https://test.com/token")
                .authorizationUri("https://test.com/authorize")
                .userInfoUri("https://test.com/userinfo")
                .userNameAttributeName("id")
                .clientSecret("test-secret")
                .scope("profile")
                .build();
    }

}

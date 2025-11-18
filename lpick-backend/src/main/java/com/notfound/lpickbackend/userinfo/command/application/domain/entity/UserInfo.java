package com.notfound.lpickbackend.userinfo.command.application.domain.entity;

import com.notfound.lpickbackend.userinfo.command.application.dto.infodto.UserRegistrationRequest;
import com.notfound.lpickbackend.userinfo.command.application.dto.infodto.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "user_info")
public class UserInfo {
    @Id
    @Column(name = "oauth_id", nullable = false, length = 50)
    private String oauthId;

    @Column(name = "nickname", nullable = true, length = 50)
    private String nickname;

    @Column(name = "profile", length = 200)
    private String profile;

    @Column(name = "point", nullable = false)
    private Integer point;

    @Column(name = "stack_point", nullable = false)
    private Integer stackPoint;

    @Column(name = "about", length = Integer.MAX_VALUE)
    private String about;

    @Column(name = "lpti", length = 4)
    private String lpti;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tier_id", nullable = false)
    private Tier tier;

    @OneToMany(mappedBy = "oauth", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserAuth> userAuthList = new ArrayList<>();

    public List<Auth> getAuthorities() {
        return userAuthList.stream()
                .map(UserAuth::getAuth)
                .collect(Collectors.toList());
    }

    public void editAbout(String about) {
        this.about = about;
    }

    public void registration(UserRegistrationRequest request, String profile) {
        if(request.getAbout() == null || request.getAbout().isBlank()) {
            this.about = "자기소개를 작성해주세요";
        } else {
            this.about = request.getAbout();
        }
        this.nickname = request.getNickname();
        this.profile = profile;
    }

    // 회원가입이랑 똑같긴 한데 혹시 모를 유지보수를 대비해 분리
    public void update(UserUpdateRequest request, String profile) {
        if(request.getAbout() == null || request.getAbout().isBlank()) {
            this.about = "자기소개를 작성해주세요";
        } else {
            this.about = request.getAbout();
        }
        this.nickname = request.getNickname();
        if (profile != null) {
            this.profile = profile;
        }
    }

    public void deleteUserInfo() {
        this.profile = "";
        this.nickname = "";
        this.about = "";
        this.lpti = null;
    }

    public void updateLPTI(String lpti) {
        this.lpti = lpti;
    }
}
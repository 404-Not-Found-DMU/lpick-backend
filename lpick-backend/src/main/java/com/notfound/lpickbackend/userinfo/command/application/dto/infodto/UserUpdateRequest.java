package com.notfound.lpickbackend.userinfo.command.application.dto.infodto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    private String nickname;

    private String about;
}

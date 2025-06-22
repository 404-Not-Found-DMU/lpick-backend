package com.notfound.lpickbackend.userinfo.command.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserAlbumApplyRequest {
    @NotEmpty
    private String albumId;
}

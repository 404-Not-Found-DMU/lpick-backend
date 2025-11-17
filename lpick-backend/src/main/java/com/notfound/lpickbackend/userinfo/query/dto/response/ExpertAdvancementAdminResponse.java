package com.notfound.lpickbackend.userinfo.query.dto.response;

import com.notfound.lpickbackend.servicedata.command.application.domain.inherenceENUM.MusicGenre;
import com.notfound.lpickbackend.userinfo.command.application.domain.inherenceENUM.ExpertRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ExpertAdvancementAdminResponse {
    private String requestId;
    private Instant createdAt;
    private String userName;
    private String userEmail;
    private List<MusicGenre> genre;
    private ExpertRequestStatus status;
}

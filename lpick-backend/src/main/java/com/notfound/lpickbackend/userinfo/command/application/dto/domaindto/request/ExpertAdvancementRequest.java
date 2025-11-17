package com.notfound.lpickbackend.userinfo.command.application.dto.domaindto.request;

import com.notfound.lpickbackend.servicedata.command.application.domain.inherenceENUM.MusicGenre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertAdvancementRequest {
    private String name;
    private String email;
    private String phNum;
    private String affiliation;
    private List<MusicGenre> musicGenreList;
    private String requestMemo;
}

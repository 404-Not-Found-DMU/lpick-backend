package com.notfound.lpickbackend.wiki.query.dto.response;

import com.notfound.lpickbackend.wiki.query.dto.DebateChatInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DebateChatResponse {

    private List<DebateChatInfo> messageList;

    private DebateBallotStatResponse ballot;
}

package com.notfound.lpickbackend.wiki.command.application.service;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import com.notfound.lpickbackend.common.websocket.dto.DebateChatMessage;
import com.notfound.lpickbackend.userinfo.command.application.domain.entity.UserInfo;
import com.notfound.lpickbackend.userinfo.command.repository.UserInfoCommandRepository;
import com.notfound.lpickbackend.wiki.command.application.domain.Debate;
import com.notfound.lpickbackend.wiki.command.application.domain.DebateChat;
import com.notfound.lpickbackend.wiki.command.repository.DebateChatCommandRepository;
import com.notfound.lpickbackend.wiki.command.repository.DebateCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebateChatCommandService {

    private final DebateChatCommandRepository debateChatCommandRepository;
    private final DebateCommandRepository debateCommandRepository;
    private final UserInfoCommandRepository userInfoCommandRepository;

    public void saveChat(String debateId, DebateChatMessage message, String userId) {
        UserInfo messageOwner = userInfoCommandRepository.findByOauthId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO));

        Debate debate = debateCommandRepository.findById(debateId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEBATE));

        DebateChat chatMessage = DebateChat.builder()
                .content(message.getContent())
                .isBlind(false)
                .dt(debate)
                .oauth(messageOwner)
                .build();

        debateChatCommandRepository.save(chatMessage);
    }


}

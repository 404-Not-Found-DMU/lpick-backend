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
import com.notfound.lpickbackend.wiki.query.dto.DebateChatInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebateChatCommandService {

    private final DebateChatCommandRepository debateChatCommandRepository;
    private final DebateCommandRepository debateCommandRepository;
    private final UserInfoCommandRepository userInfoCommandRepository;

    public DebateChatInfo saveChat(String debateId, DebateChatMessage message, String userId) {
        UserInfo messageOwner = userInfoCommandRepository.findByOauthId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO));

        Debate debate = debateCommandRepository.findById(debateId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEBATE));

        DebateChat parentChat = null;
        if(message.getParentDebateChatId() != null) parentChat = debateChatCommandRepository.findById(message.getParentDebateChatId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEBATE_CHAT));

        DebateChat chatMessage = DebateChat.builder()
                .content(message.getContent())
                .isBlind(false)
                .dt(debate)
                .oauth(messageOwner)
                .parentDebateChat(parentChat)
                .build();

        DebateChat savedChatMessage = debateChatCommandRepository.save(chatMessage);

        return DebateChatInfo.builder()
                .chatId(savedChatMessage.getDscId())
                .userNickname(messageOwner.getNickname())
                .content(savedChatMessage.getContent())
                .isBlind(savedChatMessage.isBlind())
                .createdAt(savedChatMessage.getCreatedAt())
                .isAnswerTo(parentChat.getDscId())
                .build();
    }


}

package com.notfound.lpickbackend.community.command.application.service;

import com.notfound.lpickbackend.community.command.application.dto.CommentCreate;
import com.notfound.lpickbackend.community.command.application.dto.CommentUpdate;
import com.notfound.lpickbackend.community.command.repository.CommentCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCommandService {

    private final CommentCommandRepository commentCommandRepository;

    public void createComment(CommentCreate commentCreate) {

    }

    public void updateComment(CommentUpdate commentUpdate) {

    }

    public void deleteComment(String commentId) {

    }

    public void createChildComment(String commentId, CommentCreate commentCreate) {

    }
}

package com.mutsasns.service;

import com.mutsasns.domain.comment.Comment;
import com.mutsasns.domain.comment.dto.CommentDetailResponse;
import com.mutsasns.domain.post.Post;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.repository.CommentRepository;
import com.mutsasns.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Page<CommentDetailResponse> list(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트를 찾을 수 없습니다."));

        Page<Comment> comments = commentRepository.findCommentsByPost(pageable, post);

        List<CommentDetailResponse> commentDetailResponses = comments.stream()
                .map(Comment::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(commentDetailResponses);
    }
}

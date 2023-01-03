package com.mutsasns.service;

import com.mutsasns.domain.comment.Comment;
import com.mutsasns.domain.comment.dto.CommentRequest;
import com.mutsasns.domain.comment.dto.CommentResponse;
import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.user.User;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.repository.CommentRepository;
import com.mutsasns.repository.PostRepository;
import com.mutsasns.repository.UserRepository;
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
    private final UserRepository userRepository;

    public Page<CommentResponse> list(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트를 찾을 수 없습니다."));

        Page<Comment> comments = commentRepository.findCommentsByPost(pageable, post);

        List<CommentResponse> commentResponses = comments.stream()
                .map(Comment::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(commentResponses);
    }

    public CommentResponse create(Long postId, CommentRequest commentRequest, String userName){

        //포스트 있는지 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트를 찾을 수 없습니다."));

        //유저 있는지 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "해당 유저를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .comment(commentRequest.getComment())
                .user(user)
                .post(post)
                .build();
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.builder()
                .id(savedComment.getId())
                .comment(savedComment.getComment())
                .userName(savedComment.getUser().getUserName())
                .postId(savedComment.getPost().getId())
                .createdAt(savedComment.getCreatedAt())
                .lastModifiedAt(savedComment.getLastModifiedAt())
                .build();
    }
}

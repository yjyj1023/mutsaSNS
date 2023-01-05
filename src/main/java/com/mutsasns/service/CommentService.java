package com.mutsasns.service;

import com.mutsasns.domain.comment.Comment;
import com.mutsasns.domain.comment.dto.CommentDeleteResponse;
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

    public Page<CommentResponse> listComment(Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트를 찾을 수 없습니다."));

        Page<Comment> comments = commentRepository.findCommentsByPost(pageable, post);

        List<CommentResponse> commentResponses = comments.stream()
                .map(Comment::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(commentResponses);
    }

    public CommentResponse createComment(Long postId, CommentRequest commentRequest, String userName) {

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

    public CommentResponse updateComment(Long postId, Long id, CommentRequest commentRequest, String userName) {
        //유저 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s user가 없습니다.", userName)));

        //포스트 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트를 찾을 수 없습니다."));

        //댓글 확인
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, "해당 댓글이 존재하지 않습니다."));

        //작성자와 로그인한 유저가 맞는지 확인
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "작성자와 유저가 다릅니다.");
        }

        comment.setComment(commentRequest.getComment());

        Comment savedComment = commentRepository.saveAndFlush(comment);

        return CommentResponse.builder()
                .id(savedComment.getId())
                .comment(savedComment.getComment())
                .userName(savedComment.getUser().getUserName())
                .postId(savedComment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(savedComment.getLastModifiedAt())
                .build();
    }

    public CommentDeleteResponse deleteComment(Long postId, Long id, String userName){
        //유저 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s user가 없습니다.", userName)));

        //포스트 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트를 찾을 수 없습니다."));

        //댓글 확인
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND, "해당 댓글이 존재하지 않습니다."));

        commentRepository.deleteById(comment.getId());

        return CommentDeleteResponse.builder()
                .message("댓글 삭제 완료")
                .id(comment.getId())
                .build();
    }
}

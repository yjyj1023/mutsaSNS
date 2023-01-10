package com.mutsasns.service;

import com.mutsasns.domain.comment.Comment;
import com.mutsasns.domain.comment.dto.CommentRequest;
import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.post.dto.PostRequest;
import com.mutsasns.domain.user.User;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.repository.AlarmRepository;
import com.mutsasns.repository.CommentRepository;
import com.mutsasns.repository.PostRepository;
import com.mutsasns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class CommentServiceTest {
    CommentService commentService;
    CommentRepository commentRepository = mock(CommentRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    PostRepository postRepository = mock(PostRepository.class);
    AlarmRepository alarmRepository = mock(AlarmRepository.class);

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postRepository, userRepository, alarmRepository);
    }

    @Test
    @DisplayName("댓글 수정 실패(1) - 포스트 존재하지 않음")
    void updateComment_fail_1() {
        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);
        Comment mockComment = mock(Comment.class);

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.findById(mockPost.getId()))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(commentRepository.findById(any()))
                .thenReturn(Optional.of(mockComment));

        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            commentService.updateComment(100L, mockComment.getId(), commentRequest, mockUser.getUserName());
        });

        assertEquals(ErrorCode.POST_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 실패(2) - 작성자!=유저")
    void updateComment_fail_2() {
        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        // 첫번째 유저(작성자)
        User user = User.builder()
                .id(1L)
                .userName("YeonJae")
                .password("1234")
                .build();

        //두번째 유저
        User user2 = User.builder()
                .id(2L)
                .userName("YeonJae123456")
                .password("1234")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("title")
                .body("body")
                .user(user)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .comment("comment")
                .user(user)
                .post(post)
                .build();


        Mockito.when(userRepository.findByUserName(user.getUserName()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByUserName(user2.getUserName()))
                .thenReturn(Optional.of(user2));

        Mockito.when(postRepository.findById(post.getId()))
                .thenReturn(Optional.of(post));

        Mockito.when(commentRepository.findById(comment.getId()))
                .thenReturn(Optional.of(comment));

        // 첫번째 유저가 등록한 1번 댓글을 두번째 유저가 수정하려고 하면 에러
        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            commentService.updateComment(post.getId(), comment.getId(), commentRequest, user2.getUserName());
        });

        assertEquals(ErrorCode.INVALID_PERMISSION,appException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 실패(3) - 유저 존재하지 않음")
    void updateComment_fail_3() {
        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);
        Comment mockComment = mock(Comment.class);

        Mockito.when(userRepository.findByUserName(mockUser.getUserName()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.findById(mockPost.getId()))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(commentRepository.findById(mockComment.getId()))
                .thenReturn(Optional.of(mockComment));

        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            commentService.updateComment(mockPost.getId(), mockComment.getId(), commentRequest, "yeonjae99");
        });

        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 실패(1) - 유저 존재하지 않음")
    void deleteComment_fail_1() {

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);
        Comment mockComment = mock(Comment.class);

        Mockito.when(userRepository.findByUserName(mockUser.getUserName()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.findById(mockPost.getId()))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(commentRepository.findById(mockComment.getId()))
                .thenReturn(Optional.of(mockComment));

        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            commentService.deleteComment(mockPost.getId(), mockComment.getId(), "yeonjae99");
        });

        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 실패(2) - 댓글이 존재하지 않음")
    void deleteComment_fail_2() {

        // 첫번째 유저(작성자)
        User user = User.builder()
                .id(1L)
                .userName("YeonJae")
                .password("1234")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("title")
                .body("body")
                .user(user)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .comment("comment")
                .user(user)
                .post(post)
                .build();

        Mockito.when(userRepository.findByUserName(user.getUserName()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(post.getId()))
                .thenReturn(Optional.of(post));

        Mockito.when(commentRepository.findById(comment.getId()))
                .thenReturn(Optional.of(comment));

        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            commentService.deleteComment(post.getId(), 100L, user.getUserName());
        });

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, appException.getErrorCode());
    }

}
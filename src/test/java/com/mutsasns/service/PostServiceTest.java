package com.mutsasns.service;

import com.mutsasns.domain.comment.Comment;
import com.mutsasns.domain.likes.Likes;
import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.post.dto.PostDetailResponse;
import com.mutsasns.domain.post.dto.PostRequest;
import com.mutsasns.domain.user.User;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.repository.AlarmRepository;
import com.mutsasns.repository.LikeRepository;
import com.mutsasns.repository.PostRepository;
import com.mutsasns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class PostServiceTest {
    PostService postService;
    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    LikeRepository likeRepository = mock(LikeRepository.class);
    AlarmRepository alarmRepository = mock(AlarmRepository.class);

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository, likeRepository, alarmRepository);
    }

    @Test
    @DisplayName("포스트 조회 성공")
    void detailPost_success() {

        User user = User.builder()
                .id(1l)
                .userName("YeonJae")
                .password("1234")
                .build();

        Post post = Post.builder()
                .id(1l)
                .title("title")
                .body("body")
                .user(user)
                .build();

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        //에러없이 조회 성공
        Assertions.assertDoesNotThrow(() -> postService.detailPost(post.getId()));

        assertEquals(user.getUserName(), post.getUser().getUserName());

    }

    @Test
    @DisplayName("포스트 등록 성공")
    void createPost_success() {
        PostRequest postRequest = PostRequest.builder()
                .body("body")
                .title("title")
                .build();

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.save(any()))
                .thenReturn(mockPost);

        //에러 없이 등록 성공
        Assertions.assertDoesNotThrow(() -> postService.createPost(postRequest, mockUser.getUserName()));
    }

    @Test
    @DisplayName("포스트 등록 실패 - 유저가 존재하지 않을 때")
    void createPost_fail() {
        PostRequest postRequest = PostRequest.builder()
                .body("body")
                .title("title")
                .build();

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        Mockito.when(userRepository.findByUserName(mockUser.getUserName()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.save(any()))
                .thenReturn(mockPost);

        // YeonJae라는 유저를 등록했는데 YeonJae456789가 작성하려고 하면 에러
        Assertions.assertThrows(AppException.class, () -> {
            postService.createPost(postRequest, "YeonJae456789");
        });
    }

    @Test
    @DisplayName("포스트 수정 성공")
    void updatePost_success() {
        PostRequest postRequest = PostRequest.builder()
                .body("body")
                .title("title")
                .build();

        User user = User.builder()
                .id(1l)
                .userName("YeonJae")
                .password("1234")
                .build();

        Post post = Post.builder()
                .id(1l)
                .title("title")
                .body("body")
                .user(user)
                .build();

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        Mockito.when(postRepository.save(any()))
                .thenReturn(post);

        // 에러없이 수정 성공
        Assertions.assertDoesNotThrow(() -> postService.updatePost(postRequest, post.getId(), user.getUserName()));

    }

    @Test
    @DisplayName("포스트 수정 실패 (1) - 포스트 존재하지 않음")
    void updatePost_fail_1() {
        PostRequest postRequest = PostRequest.builder()
                .body("body")
                .title("title")
                .build();

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.findById(mockPost.getId()))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(postRepository.save(any()))
                .thenReturn(mockPost);

        // 1번 포스트만 등록되어 있는데 123번 포스트를 수정하려고 하면 에러
        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            postService.updatePost(postRequest, 123l, mockUser.getUserName());
        });

        assertEquals(ErrorCode.POST_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("포스트 수정 실패 (2) - 작성자!=유저")
    void updatePost_fail_2() {
        PostRequest postRequest = PostRequest.builder()
                .body("body")
                .title("title")
                .build();

        // 첫번째 유저(작성자)
        User user = User.builder()
                .id(1l)
                .userName("YeonJae")
                .password("1234")
                .build();

        //두번째 유저
        User user2 = User.builder()
                .id(2l)
                .userName("YeonJae123456")
                .password("1234")
                .build();

        Post post = Post.builder()
                .id(1l)
                .title("title")
                .body("body")
                .user(user)
                .build();

        //유저 두명 등록
        Mockito.when(userRepository.findByUserName(user.getUserName()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByUserName(user2.getUserName()))
                .thenReturn(Optional.of(user2));

        Mockito.when(postRepository.findById(post.getId()))
                .thenReturn(Optional.of(post));

        Mockito.when(postRepository.save(any()))
                .thenReturn(post);

        // 첫번째 유저가 등록한 1번 포스트를 두번째 유저가 수정하려고 하면 에러
        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            postService.updatePost(postRequest, post.getId(), user2.getUserName());
        });

        assertEquals(ErrorCode.INVALID_PERMISSION,appException.getErrorCode());
    }

    @Test
    @DisplayName("포스트 수정 실패 (3) - 유저 존재하지 않음")
    void updatePost_fail_3() {
        PostRequest postRequest = PostRequest.builder()
                .body("body")
                .title("title")
                .build();

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        Mockito.when(userRepository.findByUserName(mockUser.getUserName()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.findById(mockPost.getId()))
                .thenReturn(Optional.of(mockPost));

        Mockito.when(postRepository.save(any()))
                .thenReturn(mockPost);

        // 등록되어 있지 않은 YeonJae123456가 수정하려고 하면 에러
        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            postService.updatePost(postRequest, mockPost.getId(), "YeonJae123456");
        });

        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("포스트 삭제 성공")
    void deletePost_success() {
        User user = User.builder()
                .id(1l)
                .userName("YeonJae")
                .password("1234")
                .build();

        Post post = Post.builder()
                .id(1l)
                .title("title")
                .body("body")
                .user(user)
                .build();

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        // 에러없이 삭제 성공
        Assertions.assertDoesNotThrow(() -> postService.deletePost(post.getId(), user.getUserName()));

    }

    @Test
    @DisplayName("포스트 삭제 실패 (1) - 유저 존재하지 않음")
    void deletePost_fail_1() {

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        Mockito.when(userRepository.findByUserName(mockUser.getUserName()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(mockPost));

        // 등록되지 않은 YeonJae123456가 삭제하려고 하면 에러
        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            postService.deletePost(mockPost.getId(), "YeonJae123456");
        });

        assertEquals(ErrorCode.USERNAME_NOT_FOUND, appException.getErrorCode());
    }

    @Test
    @DisplayName("포스트 삭제 실패 (2) - 포스트 존재하지 않음")
    void deletePost_fail_2() {

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.findById(mockPost.getId()))
                .thenReturn(Optional.of(mockPost));

        // 존재하지 않는 123번 포스트를 삭제하려고 하면 에러
        AppException appException = Assertions.assertThrows(AppException.class, () -> {
            postService.deletePost(123l, mockUser.getUserName());
        });

        assertEquals(ErrorCode.POST_NOT_FOUND, appException.getErrorCode());
    }
}
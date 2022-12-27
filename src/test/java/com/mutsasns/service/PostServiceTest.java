package com.mutsasns.service;

import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.post.dto.PostRequest;
import com.mutsasns.domain.user.User;
import com.mutsasns.exception.AppException;
import com.mutsasns.repository.PostRepository;
import com.mutsasns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

class PostServiceTest {
    PostService postService;
    PostRepository postRepository = Mockito.mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository);
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
    }

    @Test
    @DisplayName("포스트 등록 성공")
    void createPost_success() {
        PostRequest postRequest = PostRequest.builder()
                .body("body")
                .title("title")
                .build();

        User user = User.builder()
                .id(1l)
                .userName("YeonJae")
                .password("1234")
                .build();

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.save(any()))
                .thenReturn(new Post(1l, "title1", "body1", user));

        //에러 없이 등록 성공
        Assertions.assertDoesNotThrow(() -> postService.createPost(postRequest, user.getUserName()));
    }

    @Test
    @DisplayName("포스트 등록 실패 - 유저가 존재하지 않을 때")
    void createPost_fail() {
        PostRequest postRequest = PostRequest.builder()
                .body("body")
                .title("title")
                .build();

        User user = User.builder()
                .id(1l)
                .userName("YeonJae")
                .password("1234")
                .build();

        Mockito.when(userRepository.findByUserName(user.getUserName()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.save(any()))
                .thenReturn(new Post(1l, "title1", "body1", user));

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

        Mockito.when(postRepository.findById(post.getId()))
                .thenReturn(Optional.of(post));

        Mockito.when(postRepository.save(any()))
                .thenReturn(post);

        // 1번 포스트만 등록되어 있는데 123번 포스트를 수정하려고 하면 에러
        Assertions.assertThrows(AppException.class, () -> {
            postService.updatePost(postRequest, 123l, user.getUserName());
        });
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
        Assertions.assertThrows(AppException.class, () -> {
            postService.updatePost(postRequest, post.getId(), user2.getUserName());
        });
    }

    @Test
    @DisplayName("포스트 수정 실패 (3) - 유저 존재하지 않음")
    void updatePost_fail_3() {
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

        Mockito.when(userRepository.findByUserName(user.getUserName()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(post.getId()))
                .thenReturn(Optional.of(post));

        Mockito.when(postRepository.save(any()))
                .thenReturn(post);

        // 등록되어 있지 않은 YeonJae123456가 수정하려고 하면 에러
        Assertions.assertThrows(AppException.class, () -> {
            postService.updatePost(postRequest, post.getId(), "YeonJae123456");
        });
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

        Mockito.when(userRepository.findByUserName(user.getUserName()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        // 등록되지 않은 YeonJae123456가 삭제하려고 하면 에러
        Assertions.assertThrows(AppException.class, () -> {
            postService.deletePost(post.getId(), "YeonJae123456");
        });
    }

    @Test
    @DisplayName("포스트 삭제 실패 (2) - 포스트 존재하지 않음")
    void deletePost_fail_2() {
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

        Mockito.when(postRepository.findById(post.getId()))
                .thenReturn(Optional.of(post));

        // 존재하지 않는 123번 포스트를 삭제하려고 하면 에러
        Assertions.assertThrows(AppException.class, () -> {
            postService.deletePost(123l, user.getUserName());
        });
    }
}
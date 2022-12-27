package com.mutsasns.service;

import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.post.dto.PostRequest;
import com.mutsasns.domain.user.User;
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
    @DisplayName("등록 성공")
    void createPost_success() {
        PostRequest postRequest = PostRequest.builder()
                .body("body")
                .title("title")
                .build();

        User user = new User(1l, "YeonJae", "1234");

        Mockito.when(userRepository.findByUserName(any()))
                .thenReturn(Optional.of(user));

        Mockito.when(postRepository.save(any()))
                .thenReturn(new Post(1l,"title1","body1",user ));

        Assertions.assertDoesNotThrow(() -> postService.createPost(postRequest, user.getUserName()));
    }




}
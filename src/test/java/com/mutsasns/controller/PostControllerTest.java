package com.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.post.dto.PostDetailResponse;
import com.mutsasns.domain.post.dto.PostListResponse;
import com.mutsasns.domain.post.dto.PostRequest;
import com.mutsasns.domain.post.dto.PostResponse;
import com.mutsasns.domain.user.User;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("포스트 상세 조회 성공")
    @WithMockUser
    void detailPost_success() throws Exception {

        User user = User.builder()
                .id(0l)
                .userName("YeonJae")
                .password("qwer1234")
                .build();

        Post post = Post.builder()
                .id(0l)
                .title("title")
                .body("body")
                .user(user)
                .build();

        when(postService.detailPost(any())).thenReturn(PostDetailResponse.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .body(post.getBody())
                        .userName(post.getUser().getUserName())
                        .build());

        mockMvc.perform(get("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(post)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.title").exists())
                .andExpect(jsonPath("$.result.body").exists())
                .andExpect(jsonPath("$.result.userName").exists());

    }

    @Test
    @DisplayName("포스트 작성 성공")
    @WithMockUser
    void createPost_success() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.createPost(any(), any())).thenReturn(PostResponse.builder()
                .postId(0l)
                .message("포스트 등록 완료")
                .build());

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("포스트 작성 실패(1) - JWT가 없는 경우")
    @WithAnonymousUser
    void createPost_fail_1() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.createPost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_TOKEN, "토큰이 존재하지 않습니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 작성 실패(2) - JWT가 유효하지 않은 경우")
    @WithAnonymousUser
    void createPost_fail_2() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.createPost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 작성 실패(3) - JWT가 만료된 경우")
    @WithAnonymousUser
    void createPost_fail_3() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.createPost(any(), any())).thenThrow(new AppException(ErrorCode.EXPIRED_TOKEN, "만료된 토큰입니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 수정 성공")
    @WithMockUser
    void updatePost_success() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.updatePost(any(), any(), any())).thenReturn(PostResponse.builder()
                .postId(1l)
                .message("포스트 등록 완료")
                .build());

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("포스트 수정 실패(1) - 인증 실패")
    @WithAnonymousUser
    void updatePost_fail_1() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.updatePost(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 수정 실패(2) - 작성자 불일치")
    @WithMockUser
    void updatePost_fail_2() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.updatePost(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, "작성자와 유저가 다릅니다."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("포스트 수정 실패(3) - 데이터베이스 에러")
    @WithMockUser
    void updatePost_fail_3() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.updatePost(any(), any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, "DB에러"));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("포스트 수정 실패(4) - 포스트 없음")
    @WithMockUser
    void updatePost_fail_4() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.updatePost(any(), any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("포스트 삭제 성공")
    @WithMockUser
    void deletePost_success() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.deletePost(any(), any())).thenReturn(PostResponse.builder()
                .postId(1l)
                .message("포스트 삭제 완료")
                .build());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("포스트 삭제 실패(1) - 인증 실패")
    @WithAnonymousUser
    void deletePost_fail_1() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 삭제 실패(2) - 작성자 불일치")
    @WithMockUser
    void deletePost_fail_2() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, "작성자와 유저가 다릅니다."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("포스트 삭제 실패(3) - 데이터베이스 에러")
    @WithMockUser
    void deletePost_fail_3() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, "DB에러"));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("포스트 삭제 실패(4) - 포스트 없음")
    @WithMockUser
    void deletePost_fail_4() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("title")
                .body("body")
                .build();

        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }
}
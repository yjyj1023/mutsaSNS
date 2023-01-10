package com.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutsasns.domain.comment.dto.CommentDeleteResponse;
import com.mutsasns.domain.comment.dto.CommentRequest;
import com.mutsasns.domain.comment.dto.CommentResponse;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("댓글 작성 성공")
    @WithMockUser
    void createComment_success() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        when(commentService.createComment(any(), any(), any())).thenReturn(CommentResponse.builder()
                .id(1L)
                .comment("comment")
                .userName("yeonjae")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.comment").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists());
    }

    @Test
    @DisplayName("댓글 작성 실패(1) - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void createComment_fail_1() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        when(commentService.createComment(any(), any(), any())).thenReturn(CommentResponse.builder()
                .id(1L)
                .comment("comment")
                .userName("yeonjae")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 작성 실패(2) - 게시물이 존재하지 않는 경우")
    @WithMockUser
    void createComment_fail_2() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        when(commentService.createComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, "게시물이 존재하지 않습니다."));

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    @WithMockUser
    void updateComment_success() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        when(commentService.updateComment(any(), any(), any(), any())).thenReturn(CommentResponse.builder()
                .id(1L)
                .comment("comment")
                .userName("yeonjae")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.comment").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists());
    }

    @Test
    @DisplayName("댓글 수정 실패(1) - 인증 실패")
    @WithAnonymousUser
    void updateComment_fail_1() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        when(commentService.updateComment(any(), any(), any(), any())).thenReturn(CommentResponse.builder()
                .id(1L)
                .comment("comment")
                .userName("yeonjae")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 수정 실패(2) - Post 없는 경우")
    @WithMockUser
    void updateComment_fail_2() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        when(commentService.updateComment(any(), any(), any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, "게시물이 존재하지 않습니다."));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @DisplayName("댓글 수정 실패(3) - 작성자 불일치")
    @WithMockUser
    void updateComment_fail_3() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        when(commentService.updateComment(any(), any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, "작성자와 유저가 다릅니다."));

        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @DisplayName("댓글 수정 실패(4) - 데이터베이스 에러")
    @WithMockUser
    void updateComment_fail_4() throws Exception {

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("comment")
                .build();

        when(commentService.createComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, "데이터 베이스 에러"));

        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    @WithMockUser
    void deleteComment_success() throws Exception {

        when(commentService.deleteComment(any(), any(), any())).thenReturn(CommentDeleteResponse.builder()
                .id(1L)
                .message("댓글 삭제 완료")
                .build());

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("댓글 삭제 실패(1) - 인증 실패")
    @WithAnonymousUser
    void deleteComment_fail_1() throws Exception {

        when(commentService.deleteComment(any(), any(), any())).thenReturn(CommentDeleteResponse.builder()
                .id(1L)
                .message("댓글 삭제 완료")
                .build());

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 삭제 실패(2) - Post없는 경우")
    @WithMockUser
    void deleteComment_fail_2() throws Exception {

        when(commentService.deleteComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, "게시물이 존재하지 않습니다."));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @DisplayName("댓글 삭제 실패(3) - 작성자 불일치")
    @WithMockUser
    void deleteComment_fail_3() throws Exception {

        when(commentService.deleteComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, "작성자와 유저가 다릅니다."));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @DisplayName("댓글 삭제 실패(4) - 데이터베이스 에러")
    @WithMockUser
    void deleteComment_fail_4() throws Exception {

        when(commentService.deleteComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, "데이터 베이스 에러"));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    @WithMockUser
    void listComment_success() throws Exception {

        when(commentService.listComment(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.pageable").exists());
    }
}
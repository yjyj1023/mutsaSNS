package com.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutsasns.domain.user.dto.UserDto;
import com.mutsasns.domain.user.dto.UserJoinRequest;
import com.mutsasns.domain.user.dto.UserLoginRequest;
import com.mutsasns.domain.user.dto.UserLoginResponse;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.service.UserService;
import com.mutsasns.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Value("${jwt.token.secret}")
    private String secretKey;
    private long expireTimeMs = 1000 * 60 * 60; //1시간

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void join_success() throws Exception {
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("YeonJae")
                .password("qwer1234")
                .build();

        UserDto userDto = UserDto.builder()
                .id(0l)
                .userName("YeonJae")
                .password("qwer1234")
                .build();

        when(userService.join(any())).thenReturn(userDto);

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.userId").exists());
    }

    @Test
    @DisplayName("회원가입 실패 (userName 중복)")
    @WithMockUser
    void join_fail() throws Exception {
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("YeonJae")
                .password("qwer1234")
                .build();

        when(userService.join(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USER_NAME, "UserName이 중복됩니다."));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void login_success() throws Exception {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .userName("YeonJae")
                .password("qwer1234")
                .build();

        String token = JwtUtil.createToken(userLoginRequest.getUserName(), secretKey, expireTimeMs);

        when(userService.login(any())).thenReturn(new UserLoginResponse(token));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.jwt").exists());
    }

    @Test
    @DisplayName("로그인 실패(1) - userName없음")
    @WithMockUser
    void login_fail_1() throws Exception {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .userName("YeonJae")
                .password("qwer1234")
                .build();

        when(userService.login(any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, "Not founded"));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }

    @Test
    @DisplayName("로그인 실패(2) - password틀림")
    @WithMockUser
    void login_fail_2() throws Exception {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .userName("YeonJae")
                .password("qwer1234")
                .build();

        when(userService.login(any())).thenThrow(new AppException(ErrorCode.INVALID_PASSWORD, "패스워드가 잘못되었습니다."));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").exists())
                .andExpect(jsonPath("$.result.message").exists());
    }
}
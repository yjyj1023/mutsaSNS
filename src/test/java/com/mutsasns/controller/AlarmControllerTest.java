package com.mutsasns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutsasns.service.AlarmService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
class AlarmControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AlarmService alarmService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("알람 목록 조회 성공")
    @WithMockUser
    void createComment_success() throws Exception {

        when(alarmService.alarm(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/alarms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.pageable").exists());
    }

    @Test
    @DisplayName("알람 목록 조회 실패 - 로그인하지 않은 경우")
    @WithAnonymousUser
    void createComment_fail() throws Exception {

        when(alarmService.alarm(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/alarms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}
package com.mutsasns.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class PostListResponse {
    private Long id;
    private String title;
    private String body;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

}

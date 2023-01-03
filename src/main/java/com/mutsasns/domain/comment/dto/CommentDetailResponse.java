package com.mutsasns.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDetailResponse {
    private Long id;
    private String comment;
    private String userName;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}

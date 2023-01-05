package com.mutsasns.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CommentResponse {
    private Long id;
    private String comment;
    private String userName;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}

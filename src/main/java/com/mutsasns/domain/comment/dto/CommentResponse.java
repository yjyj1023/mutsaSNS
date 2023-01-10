package com.mutsasns.domain.comment.dto;

import com.mutsasns.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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

    public static Page<CommentResponse> toPaging(Page<Comment> comments){
        return comments.map(c -> CommentResponse.builder()
                .id(c.getId())
                .comment(c.getComment())
                .userName(c.getUser().getUserName())
                .postId(c.getPost().getId())
                .createdAt(c.getCreatedAt())
                .lastModifiedAt(c.getLastModifiedAt())
                .build());

    }
}

package com.mutsasns.domain.comment;

import com.mutsasns.domain.Base;
import com.mutsasns.domain.comment.dto.CommentDetailResponse;
import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public CommentDetailResponse toResponse(){
        return CommentDetailResponse.builder()
                .id(this.id)
                .comment(this.comment)
                .userName(this.user.getUserName())
                .postId(this.post.getId())
                .createdAt(this.getCreatedAt())
                .lastModifiedAt(this.getLastModifiedAt())
                .build();
    }
}

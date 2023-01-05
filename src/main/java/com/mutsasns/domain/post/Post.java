package com.mutsasns.domain.post;

import com.mutsasns.domain.Base;
import com.mutsasns.domain.comment.Comment;
import com.mutsasns.domain.likes.Likes;
import com.mutsasns.domain.post.dto.PostDetailResponse;
import com.mutsasns.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Post extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Likes> likes;

    public PostDetailResponse toResponse() {
        return PostDetailResponse.builder()
                .id(this.id)
                .title(this.title)
                .body(this.body)
                .userName(this.user.getUserName())
                .createdAt(this.getCreatedAt())
                .lastModifiedAt(this.getLastModifiedAt())
                .build();
    }
}

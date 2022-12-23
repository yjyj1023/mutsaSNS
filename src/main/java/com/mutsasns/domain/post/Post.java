package com.mutsasns.domain.post;

import com.mutsasns.domain.Base;
import com.mutsasns.domain.post.dto.PostCreateResponse;
import com.mutsasns.domain.post.dto.PostListResponse;
import com.mutsasns.domain.post.dto.PostResponse;
import com.mutsasns.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    public PostListResponse toResponse(){
        return PostListResponse.builder()
                .id(this.id)
                .title(this.title)
                .body(this.body)
                .userName(this.user.getUserName())
                .createdAt(this.getCreatedAt())
                .lastModifiedAt(this.getLastModifiedAt())
                .build();
    }
}

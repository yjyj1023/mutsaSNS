package com.mutsasns.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class PostResponse {
    private List content;
    private Pageable pageable;
}

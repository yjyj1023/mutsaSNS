package com.mutsasns.controller;

import com.mutsasns.domain.Response;
import com.mutsasns.domain.post.dto.PostCreateRequest;
import com.mutsasns.domain.post.dto.PostCreateResponse;
import com.mutsasns.domain.post.dto.PostResponse;
import com.mutsasns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<PostCreateResponse> create(@RequestBody PostCreateRequest postCreateRequest, Authentication authentication){
        String userName = authentication.getName();
        return Response.success(postService.createPost(postCreateRequest, userName));
    }

    @GetMapping
    public Response<PostResponse> list(Pageable pageable){
        return Response.success(postService.findAllList(pageable));
    }
}

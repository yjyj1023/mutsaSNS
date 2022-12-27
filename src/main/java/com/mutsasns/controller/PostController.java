package com.mutsasns.controller;

import com.mutsasns.domain.Response;
import com.mutsasns.domain.post.dto.PostDetailResponse;
import com.mutsasns.domain.post.dto.PostListResponse;
import com.mutsasns.domain.post.dto.PostRequest;
import com.mutsasns.domain.post.dto.PostResponse;
import com.mutsasns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public Response<PostListResponse> list(Pageable pageable) {
        return Response.success(postService.findAllList(pageable));
    }

    @GetMapping("/{id}")
    public Response<PostDetailResponse> detailPost(@PathVariable Long id) {
        return Response.success(postService.detailPost(id));
    }

    @PostMapping
    public Response<PostResponse> create(@RequestBody PostRequest postRequest, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(postService.createPost(postRequest, userName));
    }

    @PutMapping("/{id}")
    public Response<PostResponse> update(@RequestBody PostRequest postRequest, @PathVariable Long id, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(postService.updatePost(postRequest, id, userName));
    }

    @DeleteMapping("/{id}")
    public Response<PostResponse> delete(@PathVariable Long id, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(postService.deletePost(id, userName));
    }
}

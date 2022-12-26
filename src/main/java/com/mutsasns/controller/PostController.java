package com.mutsasns.controller;

import com.mutsasns.domain.Response;
import com.mutsasns.domain.post.dto.PostCreateRequest;
import com.mutsasns.domain.post.dto.PostCreateResponse;
import com.mutsasns.domain.post.dto.PostListResponse;
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

    @GetMapping
    public Response<PostResponse> list(Pageable pageable){
        return Response.success(postService.findAllList(pageable));
    }

    @GetMapping("/{id}")
    public Response<PostListResponse> detailPost(@PathVariable Long id){
        return Response.success(postService.detailPost(id));
    }

    @PostMapping
    public Response<PostCreateResponse> create(@RequestBody PostCreateRequest postCreateRequest, Authentication authentication){
        String userName = authentication.getName();
        return Response.success(postService.createPost(postCreateRequest, userName));
    }

    @PutMapping("/{id}")
    public Response<PostCreateResponse> update(@RequestBody PostCreateRequest postCreateRequest,@PathVariable Long id, Authentication authentication){
        String userName = authentication.getName();
        return Response.success(postService.updatePost(postCreateRequest, id,userName));
    }

    @DeleteMapping("/{id}")
    public Response<PostCreateResponse> delete(@PathVariable Long id, Authentication authentication){
        String userName = authentication.getName();
        return Response.success(postService.deletePost(id, userName));
    }
}

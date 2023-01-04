package com.mutsasns.controller;

import com.mutsasns.domain.Response;
import com.mutsasns.domain.post.dto.PostDetailResponse;
import com.mutsasns.domain.post.dto.PostRequest;
import com.mutsasns.domain.post.dto.PostResponse;
import com.mutsasns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public Response<Page<PostDetailResponse>> list(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
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

    @GetMapping("/my")
    public Response<Page<PostDetailResponse>> myFeed(Authentication authentication, Pageable pageable){
        String userName = authentication.getName();
        return Response.success(postService.myFeed(userName, pageable));
    }

    @PostMapping("/{postId}/likes")
    public Response<String> likePush(@PathVariable Long postId, Authentication authentication){
        String userName = authentication.getName();
        return Response.success(postService.likePush(postId, userName));
    }

    @GetMapping("/{postId}/likes")
    public Response<Integer> likeCount(@PathVariable Long postId){
        return Response.success(postService.likeCount(postId));
    }
}

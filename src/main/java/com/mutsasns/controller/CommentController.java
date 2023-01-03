package com.mutsasns.controller;

import com.mutsasns.domain.Response;
import com.mutsasns.domain.comment.dto.CommentRequest;
import com.mutsasns.domain.comment.dto.CommentResponse;
import com.mutsasns.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public Response<Page<CommentResponse>> list(@PathVariable Long postId, @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return Response.success(commentService.list(postId, pageable));
    }

    @PostMapping("/posts/{postId}/comments")
    public Response<CommentResponse> create(@PathVariable Long postId, @RequestBody CommentRequest commentRequest, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(commentService.create(postId, commentRequest, userName));
    }

//    public Response<> update() {
//
//    }
//
//    public Response<> delete() {s
//
//    }
}

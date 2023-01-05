package com.mutsasns.controller;

import com.mutsasns.domain.Response;
import com.mutsasns.domain.comment.dto.CommentDeleteResponse;
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
    public Response<Page<CommentResponse>> listComment(@PathVariable Long postId, @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return Response.success(commentService.listComment(postId, pageable));
    }

    @PostMapping("/posts/{postId}/comments")
    public Response<CommentResponse> createComment(@PathVariable Long postId, @RequestBody CommentRequest commentRequest, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(commentService.createComment(postId, commentRequest, userName));
    }

    @PutMapping("/posts/{postId}/comments/{id}")
    public Response<CommentResponse> updateComment(@PathVariable Long postId, @PathVariable Long id, @RequestBody CommentRequest commentRequest, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(commentService.updateComment(postId, id, commentRequest, userName));
    }

    @DeleteMapping("/posts/{postId}/comments/{id}")
    public Response<CommentDeleteResponse> delete(@PathVariable Long postId, @PathVariable Long id, Authentication authentication) {
        String userName = authentication.getName();
        return Response.success(commentService.deleteComment(postId, id, userName));
    }
}

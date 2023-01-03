package com.mutsasns.controller;

import com.mutsasns.domain.Response;
import com.mutsasns.domain.comment.dto.CommentDetailResponse;
import com.mutsasns.domain.post.dto.PostDetailResponse;
import com.mutsasns.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public Response<Page<CommentDetailResponse>> list(@PathVariable Long postId, @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return Response.success(commentService.list(postId, pageable));
    }

//    public Response<> create() {
//
//    }
//
//    public Response<> update() {
//
//    }
//
//    public Response<> delete() {s
//
//    }
}

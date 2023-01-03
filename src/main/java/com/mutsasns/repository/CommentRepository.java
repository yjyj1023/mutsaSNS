package com.mutsasns.repository;

import com.mutsasns.domain.comment.Comment;
import com.mutsasns.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findCommentsByPost(Pageable pageable, Post post);
}

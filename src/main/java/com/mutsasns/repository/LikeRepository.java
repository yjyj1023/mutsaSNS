package com.mutsasns.repository;

import com.mutsasns.domain.likes.Likes;
import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    List<Likes> findAllByPost(Post post);
    Integer countByUser(User user);

}

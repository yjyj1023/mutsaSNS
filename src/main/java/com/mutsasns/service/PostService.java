package com.mutsasns.service;

import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.post.dto.PostCreateRequest;
import com.mutsasns.domain.post.dto.PostCreateResponse;
import com.mutsasns.domain.post.dto.PostListResponse;
import com.mutsasns.domain.post.dto.PostResponse;
import com.mutsasns.domain.user.User;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.repository.PostRepository;
import com.mutsasns.repository.UserRepository;
import com.mutsasns.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Value("${jwt.token.secret}")
    private String secretKey;

    //포스트 작성
    public PostCreateResponse createPost(PostCreateRequest postCreateRequest, Authentication authentication) {

        String userName = authentication.getName();

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s user가 없습니다.", userName)));

        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .body(postCreateRequest.getBody())
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);
        return PostCreateResponse.builder()
                .postId(savedPost.getId())
                .message("포스트 등록 완료")
                .build();
    }

    //리스트 출력
    public PostResponse findAllList(Pageable pageable){
        Page<Post> posts = postRepository.findAll(pageable);
        List<PostListResponse> postListResponses = posts.stream()
                .map(Post::toResponse)
                .collect(Collectors.toList());
        return PostResponse.builder()
                .content(postListResponses)
                .pageable(pageable)
                .build();
    }
}


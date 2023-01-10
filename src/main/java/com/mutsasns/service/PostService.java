package com.mutsasns.service;

import com.mutsasns.domain.alarm.Alarm;
import com.mutsasns.domain.likes.Likes;
import com.mutsasns.domain.post.Post;
import com.mutsasns.domain.post.dto.PostDetailResponse;
import com.mutsasns.domain.post.dto.PostRequest;
import com.mutsasns.domain.post.dto.PostResponse;
import com.mutsasns.domain.user.User;
import com.mutsasns.exception.AppException;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.repository.AlarmRepository;
import com.mutsasns.repository.LikeRepository;
import com.mutsasns.repository.PostRepository;
import com.mutsasns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final AlarmRepository alarmRepository;

    //전체 포스트 리스트 출력
    public Page<PostDetailResponse> findAllList(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        List<PostDetailResponse> postDetailResponses = posts.stream()
                .map(Post::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(postDetailResponses);
    }


    //상세 포스트 조회
    public PostDetailResponse detailPost(Long id) {

        //포스트 확인
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createdAt(post.getCreatedAt())
                .lastModifiedAt(post.getLastModifiedAt())
                .build();
    }

    //포스트 작성
    public PostResponse createPost(PostRequest postRequest, String userName) {
        //유저 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s user가 없습니다.", userName)));

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .body(postRequest.getBody())
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponse.builder()
                .postId(savedPost.getId())
                .message("포스트 등록 완료")
                .build();
    }

    //포스트 수정
    public PostResponse updatePost(PostRequest postRequest, Long id, String userName) {
        //유저 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s user가 없습니다.", userName)));

        //포스트 확인
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 존재하지 않습니다."));

        //작성자와 로그인한 유저가 맞는지 확인
        if (!user.getId().equals(post.getUser().getId())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "작성자와 유저가 다릅니다.");
        }

        Post verifiedPost = Post.builder()
                .id(post.getId())
                .title(postRequest.getTitle())
                .body(postRequest.getBody())
                .user(user)
                .build();

        Post savedPost = postRepository.save(verifiedPost);
        return PostResponse.builder()
                .postId(savedPost.getId())
                .message("포스트 수정 완료")
                .build();
    }

    //포스트 삭제
    public PostResponse deletePost(Long id, String userName) {
        //유저 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s user가 없습니다.", userName)));

        //포스트 확인
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 존재하지 않습니다."));

        //작성자와 로그인한 유저가 맞는지 확인
        if (!user.getId().equals(post.getUser().getId())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION, "작성자와 유저가 다릅니다.");
        }
        postRepository.deleteById(id);
        return PostResponse.builder()
                .postId(id)
                .message("포스트 삭제 완료")
                .build();
    }

    //마이피드 조회
    public Page<PostDetailResponse> myFeed(String userName, Pageable pageable) {
        //유저 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s user가 없습니다.", userName)));

        Page<Post> posts = postRepository.findAllByUser(user, pageable);

        return PostDetailResponse.toPaging(posts);
    }

    //좋아요 누르기
    public String likePush(Long postId, String userName) {
        //유저 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s user가 없습니다.", userName)));

        //포스트 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 존재하지 않습니다."));

        System.out.println(likeRepository.countByUser(user));

        //이전에 좋아요 눌렀는지 확인
        if (likeRepository.countByUser(user) >= 1) {
            throw new AppException(ErrorCode.DUPLICATED_LIKE, "좋아요는 두 번 누를 수 없습니다.");
        }

        Likes likes = Likes.builder()
                .post(post)
                .user(user)
                .build();

        likeRepository.save(likes);

        //new comment 알람 등록
        Alarm alarm = Alarm.builder()
                .alarmType("NEW_LIKE_ON_POST")
                .fromUserId(user.getId())
                .targetId(postId)
                .user(user)
                .text("new like!")
                .build();

        alarmRepository.save(alarm);

        return "좋아요를 눌렀습니다.";
    }

    //좋아요 개수
    public int likeCount(Long postId) {
        //포스트 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 존재하지 않습니다."));

        List<Likes> likes = likeRepository.findAllByPost(post);

        return likes.size();
    }
}


package com.mutsasns.service;

import com.mutsasns.domain.user.User;
import com.mutsasns.domain.user.dto.*;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.exception.AppException;
import com.mutsasns.repository.UserRepository;
import com.mutsasns.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String secretKey;
    private long expireTimeMs = 1000 * 60 * 60; //1시간

    public UserDto join(UserJoinRequest userJoinRequest){
        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME,
                            String.format("%s 는 이미 있습니다.", userJoinRequest.getUserName()));
                });

        User savedUser = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));

        return UserDto.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .password(savedUser.getPassword())
                .build();
    }


    public UserLoginResponse login(UserLoginRequest userLoginRequest){
        //userName 있는지 확인
        User user = userRepository.findByUserName(userLoginRequest.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND,
                        String.format("%s는 가입된 적이 없습니다.", userLoginRequest.getUserName())));

        //password가 일치하는지 확인
        if(!encoder.matches(userLoginRequest.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD,"password가 잘못 되었습니다.");
        }

        String token = JwtUtil.createToken(userLoginRequest.getUserName(),secretKey,expireTimeMs);

        return new UserLoginResponse(token);
    }
}

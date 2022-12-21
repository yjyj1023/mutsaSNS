package com.mutsasns.domain.dto;

import com.mutsasns.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserJoinRequest {
    private Long id;
    private String userName;
    private String password;

    public User toEntity(String password){
        return User.builder()
                .userName(this.userName)
                .password(password)
                .build();
    }
}

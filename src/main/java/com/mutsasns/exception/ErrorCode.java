package com.mutsasns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    DUPLICATED_USERNAME(HttpStatus.CONFLICT, "UserName 중복"),
    NOT_FOUND_USERNAME(HttpStatus.NOT_FOUND, "UserName이 없음"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "password가 틀림");

    private HttpStatus status;
    private String message;

}

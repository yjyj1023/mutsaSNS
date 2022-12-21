package com.mutsasns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinException extends RuntimeException{
    private ErrorCode errorCode;
    private String message;

}

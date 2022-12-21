package com.mutsasns.exception;

import com.mutsasns.domain.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> userJoinExceptionHandler(UserException e){
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name(), e.getMessage()));
    }
}

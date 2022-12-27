package com.mutsasns.exception.jwt;

import com.mutsasns.domain.Response;
import com.mutsasns.exception.ErrorCode;
import com.mutsasns.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        JSONObject responseJson = new JSONObject();  // result랑 resultCode 들어가는 json
        JSONObject result = new JSONObject(); // result안에 errorCode랑 message 들어가는 json

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PERMISSION, "인증되지 않은 사용자입니다.");
        Response response1 = new Response("ERROR", result);
        try {
            result.put("errorCode", errorResponse.getErrorCode());
            result.put("message", errorResponse.getMessage());

            responseJson.put("resultCode", response1.getResultCode());
            responseJson.put("result", response1.getResult());

            response.getWriter().println("{ \"resultCode\" : \"" + responseJson.get("resultCode")
                    + "\", \"result\" : " + responseJson.get("result") + "}");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
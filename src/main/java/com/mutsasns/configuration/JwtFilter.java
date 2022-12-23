package com.mutsasns.configuration;

import com.mutsasns.exception.ErrorCode;
import com.mutsasns.utils.JwtUtil;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization: {}", authorization);

        //token 안보내면 block
        if(authorization == null || !authorization.startsWith("Bearer ")){
            log.error("인증되지 않은 사용자 입니다.");
            filterChain.doFilter(request,response);
            return;
        }

        try{
            //Token 꺼내기
            String token = authorization.split(" ")[1];

            //Token에서 UserName 꺼내기
            String userName = JwtUtil.getUserName(token, secretKey);
            log.info("userName:{}", userName);

            //권한 부여
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

            //Detail을 넣어준다.
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
            log.info("처리 완료");

        }catch (UnsupportedJwtException e){
            log.error("예상하는 형식과 다른 형식이거나 구성의 JWT일 때");
            request.setAttribute("exception", "INVALID_TOKEN");
            filterChain.doFilter(request,response);
        }catch (MalformedJwtException e){
            log.error("JWT가 올바르게 구성되지 않았을 때");
            request.setAttribute("exception", "INVALID_TOKEN");
            filterChain.doFilter(request,response);
        }catch (ExpiredJwtException e){
            log.error("JWT를 생성할 때 지정한 유효기간이 초과되었을 때");
            request.setAttribute("exception", "EXPIRED_TOKEN");
            filterChain.doFilter(request,response);
        }catch (SignatureException e){
            log.error("JWT의 기존 서명을 확인하지 못했을 때");
            request.setAttribute("exception", "INVALID_TOKEN");
            filterChain.doFilter(request,response);
        }catch (IllegalArgumentException e){
            log.error("잘못 된 파라미터(인자)가 넘어갔을 때");
            request.setAttribute("exception", "INVALID_TOKEN");
            filterChain.doFilter(request,response);
        }


    }
}
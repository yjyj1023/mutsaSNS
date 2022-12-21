package com.mutsasns.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/api/v1/users/join").permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
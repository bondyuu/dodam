package com.team1.dodam.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.global.error.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointException implements
    AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().println(
        new ObjectMapper().writeValueAsString(
            ResponseDto.fail(ErrorCode.LOGIN_REQUIRED)
        )
    );
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}

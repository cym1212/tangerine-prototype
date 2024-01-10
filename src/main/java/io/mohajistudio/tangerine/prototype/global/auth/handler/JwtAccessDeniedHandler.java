package io.mohajistudio.tangerine.prototype.global.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMemberDTO = (SecurityMemberDTO) authentication.getPrincipal();

        Map<String, String> exception = new HashMap<>();
        if(securityMemberDTO.getRole().name().equals("GUEST")) {
            exception.put("message", "접근 권한이 없습니다, 먼저 회원가입을 진행해주세요");
        } else {
            exception.put("message", "접근 권한이 없습니다");
        }
        objectMapper.writeValue(response.getWriter(), exception);
    }
}

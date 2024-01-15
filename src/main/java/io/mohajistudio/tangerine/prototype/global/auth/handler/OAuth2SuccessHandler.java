package io.mohajistudio.tangerine.prototype.global.auth.handler;

import com.nimbusds.jose.shaded.gson.JsonObject;
import io.mohajistudio.tangerine.prototype.global.auth.dto.GeneratedTokenDTO;
import io.mohajistudio.tangerine.prototype.global.auth.service.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        JsonObject jsonObject = new JsonObject();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        boolean registered = Boolean.TRUE.equals(oAuth2User.getAttribute("registered"));

        Long id = oAuth2User.getAttribute("id");
        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");
        String role = oAuth2User.getAuthorities().stream().findFirst().orElseThrow(IllegalAccessError::new).getAuthority();
        GeneratedTokenDTO generatedTokenDTO;

        if (!registered) {
            generatedTokenDTO = jwtProvider.generateGuestToken(id, email, provider, role);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            jsonObject.addProperty("isRegistered", false);
        } else {
            generatedTokenDTO = jwtProvider.generateTokens(id, email, provider, role);
            jsonObject.addProperty("isRegistered", true);
        }

        jsonObject.addProperty("accessToken", generatedTokenDTO.getAccessToken());
        jsonObject.addProperty("refreshToken", generatedTokenDTO.getRefreshToken());
        response.getWriter().write(jsonObject.toString());
    }
}
package io.mohajistudio.tangerine.prototype.global.config;

import io.mohajistudio.tangerine.prototype.global.auth.handler.JwtAccessDeniedHandler;
import io.mohajistudio.tangerine.prototype.global.auth.handler.JwtAuthenticationEntryPoint;
import io.mohajistudio.tangerine.prototype.global.auth.filter.JwtAuthFilter;
import io.mohajistudio.tangerine.prototype.global.auth.handler.OAuth2FailureHandler;
import io.mohajistudio.tangerine.prototype.global.auth.handler.OAuth2SuccessHandler;
import io.mohajistudio.tangerine.prototype.global.auth.service.CustomOAuth2UserService;
import io.mohajistudio.tangerine.prototype.global.auth.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtProvider jwtProvider;
    private static final String[] AUTHORITY_MEMBER = {"MEMBER", "MANAGER", "ADMIN"};
    private static final String AUTHORITY_GUEST = "GUEST";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.GET, "/", "/members/*", "/posts", "/posts/*", "/login/kakao", "/posts/*/comments", "/members/*/follows", "/members/*/followMembers", "/nickname-duplicate").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/swagger", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    auth.requestMatchers(HttpMethod.PATCH, "/tokens").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/register").hasAuthority(AUTHORITY_GUEST);
                    auth.requestMatchers(HttpMethod.GET, "/places", "/places/kakao", "/places/categories").hasAnyAuthority(AUTHORITY_MEMBER);
                    auth.requestMatchers(HttpMethod.POST, "/posts", "/places", "/posts/*/comments", "/places/kakao", "/places/recommend").hasAnyAuthority(AUTHORITY_MEMBER);
                    auth.requestMatchers(HttpMethod.PATCH, "/logout", "/posts/*/favorites", "/posts/*", "/posts/*/comments/*", "/members/*/follows", "/posts/*/scrap").hasAnyAuthority(AUTHORITY_MEMBER);
                    auth.requestMatchers(HttpMethod.DELETE, "/posts/*", "/posts/*/comments/*").hasAnyAuthority(AUTHORITY_MEMBER);
                }).csrf(AbstractHttpConfigurer::disable).sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2Login -> {
                    oauth2Login.successHandler(oAuth2SuccessHandler);
                    oauth2Login.failureHandler(oAuth2FailureHandler);
                    oauth2Login.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService));
                })
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(userAuthenticationEntryPoint);
                    exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler);
                })
                .addFilterBefore(new JwtAuthFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

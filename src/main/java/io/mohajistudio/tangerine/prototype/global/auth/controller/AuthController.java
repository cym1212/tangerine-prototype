package io.mohajistudio.tangerine.prototype.global.auth.controller;

import io.mohajistudio.tangerine.prototype.global.auth.dto.GeneratedTokenDTO;
import io.mohajistudio.tangerine.prototype.global.auth.dto.TokenModifyDTO;
import io.mohajistudio.tangerine.prototype.global.auth.dto.RegisterDTO;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMemberDTO;
import io.mohajistudio.tangerine.prototype.global.auth.service.AuthService;
import io.mohajistudio.tangerine.prototype.global.auth.service.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth API")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입 형식에 맞게 데이터를 전달해주세요.")
    public GeneratedTokenDTO register(@Valid @RequestBody RegisterDTO registerDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();
        return authService.register(securityMember, registerDTO);
    }

    @PatchMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 합니다.")
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();
        authService.logout(securityMember.getId());
    }

    @PatchMapping("/tokens")
    @Operation(summary = "로그아웃", description = "Access Token과 남은 기간에 따라 Refresh Token을 재발급 합니다.")
    public GeneratedTokenDTO tokenModify(@Valid @RequestBody TokenModifyDTO tokenModifyRequest) {
        return jwtProvider.reissueToken(tokenModifyRequest.getRefreshToken());
    }

    @GetMapping("/nickname-duplicate")
    @Operation(summary = "닉네임 중복 검사", description = "닉네임이 중복인지 확인합니다.")
    public void nicknameDuplicateCheck(@RequestParam("nickname") String nickname) {
        authService.checkNicknameDuplicate(nickname);
    }
}
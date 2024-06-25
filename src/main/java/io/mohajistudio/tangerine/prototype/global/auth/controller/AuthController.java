package io.mohajistudio.tangerine.prototype.global.auth.controller;

import io.mohajistudio.tangerine.prototype.domain.member.domain.MemberProfile;
import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberProfileDTO;
import io.mohajistudio.tangerine.prototype.domain.member.mapper.MemberMapper;
import io.mohajistudio.tangerine.prototype.global.auth.dto.AppLoginDTO;
import io.mohajistudio.tangerine.prototype.global.auth.dto.GeneratedTokenDTO;
import io.mohajistudio.tangerine.prototype.global.auth.dto.TokenModifyDTO;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMemberDTO;
import io.mohajistudio.tangerine.prototype.global.auth.service.AuthService;
import io.mohajistudio.tangerine.prototype.global.auth.service.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth API")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final MemberMapper memberMapper;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입 형식에 맞게 데이터를 전달해주세요.")
    public GeneratedTokenDTO register(@Valid @RequestBody MemberProfileDTO memberProfileRegisterDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();

        MemberProfile memberProfile = memberMapper.toEntity(memberProfileRegisterDTO);
        return authService.register(securityMember, memberProfile);
    }

    @PostMapping("/withdrawal")
    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 합니다.")
    public void withdrawal(@AuthenticationPrincipal SecurityMemberDTO securityMemberDTO) {
        authService.withdrawal(securityMemberDTO.getId());
    }

    @PatchMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 합니다.")
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();
        authService.logout(securityMember.getId());
    }

    @PostMapping("/app/login/kakao")
    @Operation(summary = "네이티브에서 카카오 로그인", description = "AccessToken을 넘겨주어 검증 후 회원이 아닐경우 계정을 생성합니다.")
    public GeneratedTokenDTO appKakaoLogin(@Valid @RequestBody AppLoginDTO appLoginDTO) {
        return authService.loginKakao(appLoginDTO.getToken());
    }

    @PostMapping("/app/login/google")
    @Operation(summary = "네이티브에서 구글 로그인", description = "idToken을 넘겨주어 검증 후 회원이 아닐경우 계정을 생성합니다.")
    public GeneratedTokenDTO appGoogleLogin(@Valid @RequestBody AppLoginDTO appLoginDTO) {
        return authService.loginGoogle(appLoginDTO.getToken());
    }

    @PostMapping("/app/login/apple")
    @Operation(summary = "네이티브에서 애플 로그인", description = "code를 넘겨주어 검증 후 회원이 아닐경우 계정을 생성합니다.")
    public GeneratedTokenDTO appAppleLogin(@Valid @RequestBody AppLoginDTO appLoginDTO) {
        return authService.loginApple(appLoginDTO.getToken());
    }

    @PatchMapping("/tokens")
    @Operation(summary = "토큰 재발급", description = "Access Token과 남은 기간에 따라 Refresh Token을 재발급 합니다.")
    public GeneratedTokenDTO tokenModify(@Valid @RequestBody TokenModifyDTO tokenModifyRequest) {
        return jwtProvider.reissueToken(tokenModifyRequest.getRefreshToken());
    }

    @GetMapping("/nickname-duplicate")
    @Operation(summary = "닉네임 중복 검사", description = "닉네임이 중복인지 확인합니다.")
    public void nicknameDuplicateCheck(@RequestParam("nickname") String nickname) {
        authService.checkNicknameDuplicate(nickname);
    }
}
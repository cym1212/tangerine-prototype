package io.mohajistudio.tangerine.prototype.global.auth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.mohajistudio.tangerine.prototype.global.auth.dto.GeneratedTokenDTO;
import io.mohajistudio.tangerine.prototype.global.auth.dto.OAuth2AttributeDTO;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.domain.MemberProfile;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMemberDTO;
import io.mohajistudio.tangerine.prototype.global.config.JwtProperties;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.enums.Role;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberProfileRepository;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import io.mohajistudio.tangerine.prototype.infra.upload.service.S3UploadService;
import io.mohajistudio.tangerine.prototype.infra.upload.utils.UploadUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class AuthService {
    private final JwtProperties jwtConfig;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final JwtProvider jwtProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final S3UploadService s3UploadService;
    private final AppleOAuthService appleOAuthService;
    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String APPLE_URL = "https://appleid.apple.com/auth/token";

    public GeneratedTokenDTO register(SecurityMemberDTO securityMember, MemberProfile memberProfile) {
        Optional<Member> findMember = memberRepository.findById(securityMember.getId());
        if (findMember.isEmpty()) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Member member = findMember.get();

        Optional<MemberProfile> findMemberProfile = memberProfileRepository.findByMemberId(member.getId());
        if (findMemberProfile.isPresent()) {
            throw new BusinessException(ErrorCode.MEMBER_PROFILE_DUPLICATION);
        }
        memberProfile.setMember(member);

        checkNicknameDuplicate(memberProfile.getNickname());

        memberRepository.updateRole(member.getId(), Role.MEMBER);

        if (memberProfile.getProfileImage() != null) {
            memberProfile.setProfileImage(s3UploadService.copyImage(memberProfile.getProfileImage(), UploadUtils.TEMPORARY_PATH, UploadUtils.PROFILE_IMAGES_PATH));
        }

        memberProfileRepository.save(memberProfile);

        securityMember.setRole(Role.MEMBER);

        return jwtProvider.generateTokens(securityMember);
    }

    public GeneratedTokenDTO loginKakao(String kakaoAccessToken) {
        String registrationId = "kakao";

        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(KAKAO_API_URL);
            getRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken);
            CloseableHttpResponse response = client.execute(getRequest);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String jsonString = handler.handleResponse(response);

            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                ObjectMapper objectMapper = new ObjectMapper();
                TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {
                };

                Map<String, Object> originAttributes = objectMapper.readValue(jsonString, typeReference);
                OAuth2AttributeDTO oAuth2Attribute = OAuth2AttributeDTO.of(registrationId, originAttributes);

                OAuth2User oAuth2User = customOAuth2UserService.processOAuth2Login(oAuth2Attribute);

                return onAuthenticationSuccess(oAuth2User);
            } else {
                throw new BusinessException(jsonString, ErrorCode.APP_OAUTH2_LOGIN_FAIL);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.APP_OAUTH2_LOGIN_FAIL);
        }
    }

    public GeneratedTokenDTO loginGoogle(String googleIdToken) {
        String registrationId = "google";
        HttpTransport transport = new NetHttpTransport();

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);


        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, new GsonFactory())
                .setAudience(Collections.singletonList(clientRegistration.getClientId()))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(googleIdToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String userId = payload.getSubject();
                String email = payload.getEmail();
                Map<String, Object> originAttributes = new HashMap<>();
                originAttributes.put("email", email);
                originAttributes.put("id", userId);

                OAuth2AttributeDTO oAuth2Attribute = OAuth2AttributeDTO.of(registrationId, originAttributes);

                OAuth2User oAuth2User = customOAuth2UserService.processOAuth2Login(oAuth2Attribute);

                return onAuthenticationSuccess(oAuth2User);
            } else {
                throw new BusinessException("유효하지 않은 IdToken", ErrorCode.APP_OAUTH2_LOGIN_FAIL);
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new BusinessException(e.getMessage(), ErrorCode.APP_OAUTH2_LOGIN_FAIL);
        }
    }

    public GeneratedTokenDTO loginApple(String code) {
        try {
            String appleClientSecret = jwtProvider.createAppleClientSecret();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", jwtConfig.getAppleClientId());
            params.add("client_secret", appleClientSecret);
            params.add("code", code);
            params.add("grant_type", "authorization_code");
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(APPLE_URL, HttpMethod.POST, httpEntity, String.class);

            JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(response.getBody()))
                    .getAsJsonObject();

            OAuth2AttributeDTO oAuth2Attribute = appleOAuthService.createAppleUser(jsonObject.get("id_token").getAsString());
            OAuth2User oAuth2User = customOAuth2UserService.processOAuth2Login(oAuth2Attribute);
            return onAuthenticationSuccess(oAuth2User);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.APP_OAUTH2_LOGIN_FAIL);
        }
    }

    public void logout(Long memberId) {
        memberRepository.logout(memberId);
    }

    public void checkNicknameDuplicate(String nickname) {
        Optional<MemberProfile> findMemberProfile = memberProfileRepository.findByNickname(nickname);
        if (findMemberProfile.isPresent()) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATE);
        }
    }

    public GeneratedTokenDTO onAuthenticationSuccess(OAuth2User oAuth2User) {
        boolean registered = Boolean.TRUE.equals(oAuth2User.getAttribute("registered"));

        Long id = oAuth2User.getAttribute("id");
        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");
        String role = oAuth2User.getAuthorities().stream().findFirst().orElseThrow(IllegalAccessError::new).getAuthority();
        GeneratedTokenDTO generatedTokenDTO;

        if (!registered) {
            generatedTokenDTO = jwtProvider.generateGuestToken(id, email, provider, role);
            generatedTokenDTO.setIsRegistered(false);
        } else {
            generatedTokenDTO = jwtProvider.generateTokens(id, email, provider, role);
            generatedTokenDTO.setIsRegistered(true);
        }
        return generatedTokenDTO;
    }
}

package io.mohajistudio.tangerine.prototype.global.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2AttributeDTO {
    private Map<String, Object> attributes;
    private String attributeKey;
    private String provider;
    private String email;

    public static OAuth2AttributeDTO of(String provider, String attributeKey, Map<String, Object> attributes) {
        return switch (provider) {
            case "kakao" -> ofKakao(provider, attributeKey, attributes);
            case "google" -> ofGoogle(provider, attributeKey, attributes);
            default -> throw new RuntimeException();
        };
    }

    @SuppressWarnings("unchecked")
    private static OAuth2AttributeDTO ofKakao(String provider, String attributeKey, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2AttributeDTO.builder().provider(provider).attributes(kakaoProfile).attributeKey(attributeKey).email((String) kakaoAccount.get("email")).build();
    }

    private static OAuth2AttributeDTO ofGoogle(String provider, String attributeKey, Map<String, Object> attributes) {
        return OAuth2AttributeDTO.builder().provider(provider).attributes(attributes).attributeKey(attributeKey).email((String) attributes.get("email")).build();
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", attributeKey);
        map.put("key", attributeKey);
        map.put("provider", provider);
        map.put("email", email);

        return map;
    }
}
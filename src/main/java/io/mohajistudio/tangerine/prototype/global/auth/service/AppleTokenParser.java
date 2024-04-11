package io.mohajistudio.tangerine.prototype.global.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AppleTokenParser {

    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;

    private final ObjectMapper objectMapper;

    public Map<String, String> parseHeader(final String idToken) {
        try {
            final String encodedHeader = idToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            final String decodedHeader = new String(Base64.getDecoder().decode(encodedHeader));
            TypeReference<Map<String, String>> typeReference = new TypeReference<>() {
            };
            return objectMapper.readValue(decodedHeader, typeReference);
        } catch (JsonMappingException e) {
            throw new JwtException("Apple의 idToken 값이 JWT 형식인지, 값이 정상적인지 확인해주세요.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Decode된 Header를 Map 형태로 분류할 수 없습니다. 헤더를 확인해주세요.");
        }
    }

    public Jws<Claims> extractClaims(final String appleToken, final PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(appleToken);
    }
}
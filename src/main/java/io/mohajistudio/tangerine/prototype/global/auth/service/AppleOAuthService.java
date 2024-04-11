package io.mohajistudio.tangerine.prototype.global.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.mohajistudio.tangerine.prototype.global.auth.dto.ApplePublicKeyResponseDTO;
import io.mohajistudio.tangerine.prototype.global.auth.dto.OAuth2AttributeDTO;
import io.mohajistudio.tangerine.prototype.global.config.JwtProperties;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

import static io.mohajistudio.tangerine.prototype.global.enums.ErrorCode.INTERNAL_SERVER_ERROR;

@RequiredArgsConstructor
@Component
public class AppleOAuthService {
    private final JwtProperties jwtProperties;
    private final AppleTokenParser appleTokenParser;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;

    public OAuth2AttributeDTO createAppleUser(final String appleIdToken) {
        final Map<String, String> appleIdTokenHeader = appleTokenParser.parseHeader(appleIdToken);
        final ApplePublicKeyResponseDTO.Key applePublicKey = getPublicKeys(appleIdTokenHeader.get("kid"), appleIdTokenHeader.get("alg"));
        final PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(applePublicKey);
        final Jws<Claims> claims = appleTokenParser.extractClaims(appleIdToken, publicKey);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", claims.getBody().get("email"));
        attributes.put("sub", claims.getBody().get("sub"));

        return OAuth2AttributeDTO.of("apple", attributes);
    }

    ApplePublicKeyResponseDTO.Key getPublicKeys(String kid, String alg) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet("https://appleid.apple.com/auth/keys");
            CloseableHttpResponse response = client.execute(getRequest);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String jsonString = handler.handleResponse(response);

            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                ObjectMapper objectMapper = new ObjectMapper();

                ApplePublicKeyResponseDTO applePublicKeys = objectMapper.readValue(jsonString, ApplePublicKeyResponseDTO.class);

                Optional<ApplePublicKeyResponseDTO.Key> matchedKey = applePublicKeys.getMatchedKeyBy(kid, alg);
                if (matchedKey.isEmpty()) {
                    throw new JwtException("idToken의 kid, alg와 일치하는 pulbic key가 존재하지 않습니다.");
                }
                return matchedKey.get();
            } else {
                throw new JwtException("Apple로부터 pulbic key를 발급받는데 실패했습니다.");
            }
        } catch (IOException e) {
            throw new JwtException("Apple로부터 pulbic key를 발급받는데 실패했습니다.");
        }
    }

    public PrivateKey getPrivateKey() {
        try {
            InputStream privateKey = new ClassPathResource(jwtProperties.getApplePrivateKey()).getInputStream();
            String result = new BufferedReader(new InputStreamReader(privateKey)).lines().collect(Collectors.joining("\n"));
            String key = result.replace("-----BEGIN PRIVATE KEY-----\n", "")
                    .replace("-----END PRIVATE KEY-----", "").replaceAll("\\n", "");

            byte[] decodedKey = Base64.getDecoder().decode(key);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new BusinessException("private key를 불러오는데 실패했습니다.", INTERNAL_SERVER_ERROR);
        }
    }
}

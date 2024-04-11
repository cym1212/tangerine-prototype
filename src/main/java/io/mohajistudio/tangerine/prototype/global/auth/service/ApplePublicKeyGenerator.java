package io.mohajistudio.tangerine.prototype.global.auth.service;

import io.jsonwebtoken.JwtException;
import io.mohajistudio.tangerine.prototype.global.auth.dto.ApplePublicKeyResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
public class ApplePublicKeyGenerator {
    private static final int POSITIVE_SIGN_NUMBER = 1;

    public PublicKey generatePublicKey(final ApplePublicKeyResponseDTO.Key applePublicKey) {
        final byte[] nBytes = Base64.getUrlDecoder().decode(applePublicKey.getN());
        final byte[] eBytes = Base64.getUrlDecoder().decode(applePublicKey.getE());

        final BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
        final BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);
        final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(applePublicKey.getKty());
            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new JwtException("잘못된 Apple Public Key입니다.");
        }
    }
}
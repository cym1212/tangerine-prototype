package io.mohajistudio.tangerine.prototype.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtProperties {
    private String secretKey;
    private String appleTeamId;
    private String appleKeyId;
    private String appleAud;
    private String appleClientId;
    private String applePrivateKey;
}
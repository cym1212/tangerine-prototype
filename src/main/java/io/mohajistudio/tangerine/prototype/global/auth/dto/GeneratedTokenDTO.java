package io.mohajistudio.tangerine.prototype.global.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class GeneratedTokenDTO {
    private String accessToken;
    private String refreshToken;
    @Setter
    private Boolean isRegistered;
}

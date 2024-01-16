package io.mohajistudio.tangerine.prototype.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TokenModifyDTO {
    @NotNull
    @NotBlank
    @Schema(description = "Refresh Token")
    private String refreshToken;
}

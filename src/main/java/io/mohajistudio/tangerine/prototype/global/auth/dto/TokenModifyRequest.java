package io.mohajistudio.tangerine.prototype.global.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TokenModifyRequest {
    @NotNull
    @NotBlank
    @Schema(description = "Refresh Token")
    private String refreshToken;
}

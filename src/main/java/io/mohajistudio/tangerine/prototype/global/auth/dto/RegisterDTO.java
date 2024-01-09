package io.mohajistudio.tangerine.prototype.global.auth.dto;

import io.mohajistudio.tangerine.prototype.global.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterDTO {
    @NotNull
    @Past
    @Schema(description = "멤버 생년월일", example = "1999-01-07")
    private LocalDate birthday;

    @NotNull
    @Schema(description = "멤버 성별", example = "M")
    private Gender gender;

    @NotNull
    @NotBlank
    @Size(min = 9, max = 15)
    @Schema(description = "멤버 핸드폰 번호", example = "01012345678")
    private String phone;

    @NotNull
    @NotBlank
    @Size(max = 20, min = 2)
    @Schema(description = "멤버 닉네임", example = "송눈섭")
    private String nickname;

    @Size(max = 255)
    @Schema(description = "멤버 프로필 이미지", example = "https://thumbnail.com")
    private String thumbnail;
    @NotNull
    @NotBlank
    @Size(max = 20, min = 2)
    private String name;
}

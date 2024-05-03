package io.mohajistudio.tangerine.prototype.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MemberProfileDTO {
    @Schema(description = "MemberProfile Id", example = "1")
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 20, min = 2)
    @Schema(description = "멤버 닉네임", example = "송눈섭")
    private String nickname;

    @Size(max = 100)
    @Schema(description = "멤버 소개", example = "안녕하세요~ 올리버 쌤입니다.")
    private String introduction;

    @Size(max = 255)
    @Schema(description = "멤버 프로필 이미지", example = "https://thumbnail.com")
    private String profileImage;
}

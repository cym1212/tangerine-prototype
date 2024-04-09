package io.mohajistudio.tangerine.prototype.domain.member.dto;


import io.mohajistudio.tangerine.prototype.global.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemberProfileDTO {
    @Schema(description = "MemberProfile Id", example = "1")
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 20, min = 2)
    @Schema(description = "멤버 이름", example = "한창희")
    private String name;

    @NotNull
    @NotBlank
    @Size(max = 20, min = 2)
    @Schema(description = "멤버 닉네임", example = "송눈섭")
    private String nickname;

    @Size(max = 100)
    @Schema(description = "멤버 소개", example = "안녕하세요~ 올리버 쌤입니다.")
    private String introduction;

    @NotNull
    @Schema(description = "멤버 성별", example = "M")
    private Gender gender;

    @NotNull
    @NotBlank
    @Size(min = 9, max = 15)
    @Schema(description = "멤버 핸드폰 번호", example = "01012345678")
    private String phone;

    @Size(max = 255)
    @Schema(description = "멤버 프로필 이미지", example = "https://thumbnail.com")
    private String profileImage;

    @NotNull
    @Past
    @Schema(description = "멤버 생년월일", example = "1999-01-07")
    private LocalDate birthday;

    @Getter
    @Setter
    public static class Compact {
        @Schema(description = "MemberProfile Id", example = "1")
        private Long id;

        @Schema(description = "멤버 닉네임", example = "송눈섭")
        private String nickname;

        @Schema(description = "멤버 닉네임", example = "송눈섭")
        private String profileImage;
    }

    @Getter
    @Setter
    public static class Modify {
        @NotNull
        @NotBlank
        @Size(max = 20, min = 2)
        @Schema(description = "멤버 이름", example = "한창희")
        private String name;

        @NotNull
        @NotBlank
        @Size(max = 20, min = 2)
        @Schema(description = "멤버 닉네임", example = "송눈섭")
        private String nickname;

        @Size(max = 100)
        @Schema(description = "멤버 소개", example = "안녕하세요~ 올리버 쌤입니다.")
        private String introduction;

        @NotNull
        @NotBlank
        @Size(min = 9, max = 15)
        @Schema(description = "멤버 핸드폰 번호", example = "01012345678")
        private String phone;

        @Size(max = 255)
        @Schema(description = "멤버 프로필 이미지", example = "https://thumbnail.com")
        private String profileImage;
    }
}

package io.mohajistudio.tangerine.prototype.domain.member.dto;


import io.mohajistudio.tangerine.prototype.global.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemberProfileDTO {
    @Schema(description = "MemberProfile Id", example = "1")
    private Long id;
    @Schema(description = "멤버 이름", example = "한창희")
    private String name;
    @Schema(description = "멤버 닉네임", example = "송눈섭")
    private String nickname;
    @Schema(description = "멤버 성별", example = "M")
    private Gender gender;
    @Schema(description = "멤버 핸드폰 번호", example = "01012345678")
    private String phone;
    @Schema(description = "멤버 프로필 이미지", example = "https://thumbnail.com")
    private String thumbnail;
    @Schema(description = "멤버 생년월일", example = "1999-01-07")
    private LocalDate birthday;
}

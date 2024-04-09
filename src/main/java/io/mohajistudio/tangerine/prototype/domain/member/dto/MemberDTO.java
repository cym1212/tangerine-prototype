package io.mohajistudio.tangerine.prototype.domain.member.dto;

import io.mohajistudio.tangerine.prototype.global.enums.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
    @Schema(description = "Member Id", example = "1")
    private Long id;

    @Schema(description = "소셜 로그인 제공자", example = "kakao")
    private Provider provider;

    @Schema(description = "작성자 프로필")
    private MemberProfileDTO memberProfile;
}

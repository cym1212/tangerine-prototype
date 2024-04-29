package io.mohajistudio.tangerine.prototype.domain.member.dto;

import io.mohajistudio.tangerine.prototype.global.enums.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
    @Schema(description = "Member Id", example = "1")
    private Long id;

    @Schema(description = "작성자 프로필")
    private MemberProfileDTO memberProfile;

    @Getter
    @Setter
    public static class Details extends MemberDTO {
        @Schema(description = "소셜 로그인 제공자", example = "kakao")
        private Provider provider;

        @Schema(description = "읽지 않은 알림 개수", example = "0")
        private int unreadNotificationsCnt;

        @Schema(description = "이메일", example = "example@example.com")
        private String email;
    }

    @Getter
    @Setter
    public static class Notification {
        @NotNull
        @NotEmpty
        private String notificationToken;
    }
}

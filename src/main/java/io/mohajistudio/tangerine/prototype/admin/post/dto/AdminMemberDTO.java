package io.mohajistudio.tangerine.prototype.admin.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AdminMemberDTO {
    @Schema(description = "Member Id", example = "1")
    private Long id;
    @Schema(description = "작성자 프로필")
    private AdminMemberProfileDTO memberProfile;
}

package io.mohajistudio.tangerine.prototype.domain.member.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public class FollowDTO {
    @Schema(name = "FollowDTO.Follow", description = "내가 팔로우 한 멤버 목록을 반환할 때 사용할 DTO")
    static class Follow {
        @ArraySchema(arraySchema = @Schema(description = "내가 팔로우 한 멤버 목록"))
        Set<MemberDTO> follows;
    }

    static class FollowMember {
        @ArraySchema(arraySchema = @Schema(description = "나를 팔로우 한 멤버 목록"))
        Set<MemberDTO> followMembers;
    }
}

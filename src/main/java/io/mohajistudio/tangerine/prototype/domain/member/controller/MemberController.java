package io.mohajistudio.tangerine.prototype.domain.member.controller;

import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberDTO;
import io.mohajistudio.tangerine.prototype.domain.member.mapper.MemberMapper;
import io.mohajistudio.tangerine.prototype.domain.member.service.MemberService;
import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberProfileDTO;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMember;
import io.mohajistudio.tangerine.prototype.global.common.PageableParam;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "Member API")
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @GetMapping("/{memberId}")
    @Operation(summary = "멤버 프로필", description = "멤버 프로필을 조회합니다.")
    public MemberProfileDTO memberDetails(@PathVariable("memberId") Long memberId) {
        return memberService.findMemberProfile(memberId);
    }

    @PatchMapping("/{memberId}/follows")
    @Operation(summary = "팔로우할 멤버 추가/삭제", description = "팔로우 할 멤버를 추가 또는 삭제합니다.")
    public void followMemberModify(@PathVariable("memberId") Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMember securityMember = (SecurityMember) authentication.getPrincipal();

        if (Objects.equals(memberId, securityMember.getId())) {
            throw new BusinessException(ErrorCode.SELF_FOLLOW);
        }

        memberService.modifyFollowMember(securityMember.getId(), memberId);
    }

    @GetMapping("/{memberId}/follows")
    @Operation(summary = "내가 팔로우 한 멤버 목록 조회", description = "page와 size 값을 넘기면 페이징 된 내가 팔로우한 멤버 목록을 반환합니다. 기본 값은 page는 1, size는 10 입니다.")
    public Page<MemberDTO> followListByPage(@PathVariable("memberId") Long memberId, @ModelAttribute PageableParam pageableParam) {
        Pageable pageable = PageRequest.of(pageableParam.getPage() - 1, pageableParam.getSize());

        return memberService.findFollowListByPage(memberId, pageable).map(memberMapper::toDTO);
    }

    @GetMapping("/{memberId}/followMembers")
    @Operation(summary = "나를 팔로우 한 멤버 목록 조회", description = "page와 size 값을 넘기면 페이징 된 나를 팔로우 한 멤버 목록을 반환합니다. 기본 값은 page는 1, size는 10 입니다.")
    public Page<MemberDTO> followMembersListByPage(@PathVariable("memberId") Long memberId, @ModelAttribute PageableParam pageableParam) {
        Pageable pageable = PageRequest.of(pageableParam.getPage() - 1, pageableParam.getSize());

        return memberService.findFollowMemberListByPage(memberId, pageable).map(memberMapper::toDTO);
    }
}

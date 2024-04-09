package io.mohajistudio.tangerine.prototype.domain.member.controller;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.domain.MemberProfile;
import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberDTO;
import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberProfileDTO;
import io.mohajistudio.tangerine.prototype.domain.member.mapper.MemberMapper;
import io.mohajistudio.tangerine.prototype.domain.member.service.MemberService;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PlaceBlockDTO;
import io.mohajistudio.tangerine.prototype.domain.post.dto.PostDTO;
import io.mohajistudio.tangerine.prototype.domain.post.mapper.PlaceBlockMapper;
import io.mohajistudio.tangerine.prototype.domain.post.mapper.PostMapper;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMemberDTO;
import io.mohajistudio.tangerine.prototype.global.common.PageableParam;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "Member API")
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final PostMapper postMapper;
    private final PlaceBlockMapper placeBlockMapper;

    @GetMapping("/{memberId}")
    @Operation(summary = "멤버 조회", description = "멤버를 조회합니다.")
    public MemberDTO memberDetails(@PathVariable("memberId") Long memberId) {
        Member member = memberService.findMember(memberId);
        return memberMapper.toDTO(member);
    }

    @PatchMapping("/{memberId}/member-profiles")
    @Operation(summary = "멤버 프로필 수정", description = "멤버 프로필을 수정합니다")
    public void memberProfileModify(@PathVariable("memberId") Long memberId, @Valid @RequestBody MemberProfileDTO.Modify memberProfileModifyDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();

        if (!Objects.equals(memberId, securityMember.getId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        MemberProfile memberProfile = memberMapper.toEntity(memberProfileModifyDTO);
        memberService.modifyMemberProfile(memberId, memberProfile);
    }

    @PostMapping(value = "/{memberId}/member-profiles/profile-images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "스토리지에 이미지 업로드", description = "이미지 값을 넘기면 S3에 이미지를 저장하고 주소를 반환합니다.")
    public String profileImageUpload(@PathVariable("memberId") Long memberId, @RequestPart(value = "profileImage") MultipartFile profileImage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();

        if (!Objects.equals(memberId, securityMember.getId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        return memberService.uploadProfileImage(profileImage, memberId);
    }

    @PatchMapping("/{memberId}/follows")
    @Operation(summary = "팔로우할 멤버 추가/삭제", description = "팔로우 할 멤버를 추가 또는 삭제합니다.")
    public void followMemberModify(@PathVariable("memberId") Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();

        if (Objects.equals(memberId, securityMember.getId())) {
            throw new BusinessException(ErrorCode.SELF_FOLLOW);
        }

        memberService.modifyFollowMember(securityMember.getId(), memberId);
    }

    @GetMapping("/{memberId}/follows")
    @Operation(summary = "팔로우 한 멤버 목록 조회", description = "page와 size 값을 넘기면 페이징 된 내가 팔로우한 멤버 목록을 반환합니다. 기본 값은 page는 1, size는 10 입니다.")
    public Page<MemberDTO> followListByPage(@PathVariable("memberId") Long memberId, @ModelAttribute PageableParam pageableParam) {
        Pageable pageable = PageRequest.of(pageableParam.getPage(), pageableParam.getSize());

        return memberService.findFollowListByPage(memberId, pageable).map(memberMapper::toDTO);
    }

    @GetMapping("/{memberId}/followMembers")
    @Operation(summary = "나를 팔로우 한 멤버 목록 조회", description = "page와 size 값을 넘기면 페이징 된 나를 팔로우 한 멤버 목록을 반환합니다. 기본 값은 page는 1, size는 10 입니다.")
    public Page<MemberDTO> followMembersListByPage(@PathVariable("memberId") Long memberId, @ModelAttribute PageableParam pageableParam) {
        Pageable pageable = PageRequest.of(pageableParam.getPage(), pageableParam.getSize());

        return memberService.findFollowMemberListByPage(memberId, pageable).map(memberMapper::toDTO);
    }

    @GetMapping("/{memberId}/posts")
    @Operation(summary = "특정 멤버가 작성한 게시글", description = "특정 멤버가 작성한 게시글들을 조회힙니다.")
    public Page<PostDTO.Compact> postListByPage(@PathVariable("memberId") Long memberId, @ModelAttribute PageableParam pageableParam) {
        Pageable pageable = PageRequest.of(pageableParam.getPage(), pageableParam.getSize());

        Page<Post> postListByPage = memberService.findPostListByPage(memberId, pageable);
        return postListByPage.map(postMapper::toCompactDTO);
    }

    @GetMapping("/{memberId}/place-blocks")
    @Operation(summary = "특정 멤버가 작성한 게시글", description = "특정 멤버가 작성한 게시글들을 조회힙니다.")
    public Page<PlaceBlockDTO.Details> placeBlockListByPage(@PathVariable("memberId") Long memberId, @ModelAttribute PageableParam pageableParam) {
        Pageable pageable = PageRequest.of(pageableParam.getPage(), pageableParam.getSize());

        Page<PlaceBlock> postListByPage = memberService.findPlaceBlockListByPage(memberId, pageable);
        return postListByPage.map(placeBlockMapper::toDetailsDTO);
    }

    @PatchMapping("/{memberId}/notification-token")
    @Operation(summary = "멤버의 ", description = "특정 멤버가 작성한 게시글들을 조회힙니다.")
    public void notificationTokenModify(@PathVariable("memberId") Long memberId, @Valid @RequestBody MemberDTO.Notification memberNotificationDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityMemberDTO securityMember = (SecurityMemberDTO) authentication.getPrincipal();

        if (!Objects.equals(memberId, securityMember.getId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        memberService.modifyNotificationToken(memberId, memberNotificationDTO.getNotificationToken());
    }
}

package io.mohajistudio.tangerine.prototype.domain.member.service;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Follow;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.domain.MemberProfile;
import io.mohajistudio.tangerine.prototype.domain.member.repository.FollowRepository;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberProfileRepository;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import io.mohajistudio.tangerine.prototype.domain.post.domain.PlaceBlock;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.post.repository.PlaceBlockRepository;
import io.mohajistudio.tangerine.prototype.domain.post.repository.PostRepository;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.global.error.exception.UrlNotFoundException;
import io.mohajistudio.tangerine.prototype.infra.upload.service.S3UploadService;
import io.mohajistudio.tangerine.prototype.infra.upload.utils.UploadUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static io.mohajistudio.tangerine.prototype.global.enums.ErrorCode.MEMBER_NOT_FOUND;
import static io.mohajistudio.tangerine.prototype.global.enums.ErrorCode.MISMATCH_REFRESH_TOKEN;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final PostRepository postRepository;
    private final PlaceBlockRepository placeBlockRepository;
    private final FollowRepository followRepository;
    private final S3UploadService s3UploadService;

    public Member findMember(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) {
            throw new BusinessException(MEMBER_NOT_FOUND);
        }

        Member member = findMember.get();

        if (member.getMemberProfile() == null) {
            throw new BusinessException(MEMBER_NOT_FOUND);
        }

        return member;
    }

    public void modifyFollowMember(Long memberId, Long followMemberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) {
            throw new BusinessException(MISMATCH_REFRESH_TOKEN);
        }

        Member member = findMember.get();

        Optional<Member> findFollowMember = memberRepository.findById(followMemberId);
        if (findFollowMember.isEmpty()) {
            throw new BusinessException(MEMBER_NOT_FOUND);
        }

        Member followMember = findFollowMember.get();

        Optional<Follow> findFollow = followRepository.findByMemberIdAndFollowMemberId(memberId, followMemberId);
        if (findFollow.isPresent()) {
            Follow favoritePost = findFollow.get();
            followRepository.delete(favoritePost);
            memberRepository.updateFollowCnt(memberId, member.getFollowCnt() - 1);
            memberRepository.updateFollowMemberCnt(followMemberId, followMember.getFollowMemberCnt() - 1);
        } else {
            Follow follow = Follow.builder().member(member).followMember(followMember).build();
            followRepository.save(follow);
            memberRepository.updateFollowCnt(memberId, member.getFollowCnt() + 1);
            memberRepository.updateFollowMemberCnt(followMemberId, followMember.getFollowMemberCnt() + 1);
        }
    }

    public String uploadProfileImage(MultipartFile profileImage, Long memberId) {
        return s3UploadService.uploadImage(profileImage, UploadUtils.TEMPORARY_PATH, memberId);
    }

    public Page<Member> findFollowListByPage(Long memberId, Pageable pageable) {
        return followRepository.findFollow(memberId, pageable).map(Follow::getFollowMember);
    }

    public Page<Member> findFollowMemberListByPage(Long memberId, Pageable pageable) {
        return followRepository.findFollowMember(memberId, pageable).map(Follow::getMember);
    }

    public Page<Post> findPostListByPage(Long memberId, Pageable pageable) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) throw new UrlNotFoundException();

        return postRepository.findByMemberId(memberId, pageable);
    }

    public Page<PlaceBlock> findPlaceBlockListByPage(Long memberId, Pageable pageable) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) throw new UrlNotFoundException();

        return placeBlockRepository.findByMemberId(memberId, pageable);
    }

    public void modifyMemberProfile(Long memberId, MemberProfile memberProfile) {
        Member member = findMember(memberId);

        LocalDateTime modifiedAt = LocalDateTime.now();

        memberProfileRepository.update(member.getMemberProfile().getId(), modifiedAt, memberProfile.getName(), memberProfile.getNickname(), memberProfile.getIntroduction(), memberProfile.getPhone(), memberProfile.getProfileImage());
    }

    public void modifyNotificationToken(Long memberId, String notificationToken) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isEmpty()) throw new UrlNotFoundException();

        memberRepository.updateNotificationToken(memberId, notificationToken);
    }
}

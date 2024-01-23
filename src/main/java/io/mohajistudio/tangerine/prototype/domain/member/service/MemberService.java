package io.mohajistudio.tangerine.prototype.domain.member.service;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Follow;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.domain.MemberProfile;
import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberProfileDTO;
import io.mohajistudio.tangerine.prototype.domain.member.mapper.MemberMapper;
import io.mohajistudio.tangerine.prototype.domain.member.repository.FollowRepository;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberProfileRepository;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static io.mohajistudio.tangerine.prototype.global.enums.ErrorCode.MEMBER_NOT_FOUND;
import static io.mohajistudio.tangerine.prototype.global.enums.ErrorCode.MISMATCH_REFRESH_TOKEN;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberProfileRepository memberProfileRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final MemberMapper memberMapper;

    public MemberProfileDTO findMemberProfile(Long memberId) {
        Optional<MemberProfile> findMemberProfile = memberProfileRepository.findByMemberId(memberId);
        if (findMemberProfile.isEmpty()) {
            throw new BusinessException(MEMBER_NOT_FOUND);
        }

        return memberMapper.toDTO(findMemberProfile.get());
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

    public Page<Member> findFollowListByPage(Long memberId, Pageable pageable) {
        return followRepository.findFollow(memberId, pageable).map(Follow::getFollowMember);
    }

    public Page<Member> findFollowMemberListByPage(Long memberId, Pageable pageable) {
        return followRepository.findFollowMember(memberId, pageable).map(Follow::getMember);
    }
}

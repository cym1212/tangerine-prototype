package io.mohajistudio.tangerine.prototype.global.auth.service;

import io.mohajistudio.tangerine.prototype.global.auth.dto.GeneratedTokenDTO;
import io.mohajistudio.tangerine.prototype.global.auth.dto.RegisterDTO;
import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.domain.MemberProfile;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMemberDTO;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.enums.Role;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberProfileRepository;
import io.mohajistudio.tangerine.prototype.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public GeneratedTokenDTO register(SecurityMemberDTO securityMember, RegisterDTO registerDTO) {
        Optional<Member> findMember = memberRepository.findById(securityMember.getId());

        if (findMember.isEmpty()) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        Member member = findMember.get();
        Optional<MemberProfile> findMemberProfile = memberProfileRepository.findByMemberId(member.getId());

        if (findMemberProfile.isPresent()) {
            throw new BusinessException(ErrorCode.MEMBER_PROFILE_DUPLICATION);
        }

        checkNicknameDuplicate(registerDTO.getNickname());

        memberRepository.updateRole(member.getId(), Role.MEMBER);

        MemberProfile memberProfile = MemberProfile.createMemberProfileFrom(registerDTO, member);
        memberProfileRepository.save(memberProfile);

        securityMember.setRole(Role.MEMBER);

        return jwtProvider.generateTokens(securityMember);
    }

    public void logout(Long memberId) {
        memberRepository.updateRefreshToken(memberId, null);
    }

    public void checkNicknameDuplicate(String nickname) {
        Optional<MemberProfile> findMemberProfile = memberProfileRepository.findByNickname(nickname);
        if(findMemberProfile.isPresent()) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATE);
        }
    }
}

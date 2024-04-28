package io.mohajistudio.tangerine.prototype.domain.member.mapper;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.member.domain.MemberProfile;
import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberDTO;
import io.mohajistudio.tangerine.prototype.domain.member.dto.MemberProfileDTO;
import org.mapstruct.Mapper;

@Mapper
public interface MemberMapper {
    MemberDTO toDTO(Member member);

    MemberDTO.Details toDetailsDTO(Member member);

    MemberProfileDTO toDTO(MemberProfile memberProfile);

    MemberProfile toEntity(MemberProfileDTO memberProfileDTO);

    MemberProfile toEntity(MemberProfileDTO.Modify memberProfileModifyDTO);
}

package io.mohajistudio.tangerine.prototype.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.global.auth.dto.RegisterDTO;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.global.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member_profile")
public class MemberProfile extends BaseEntity {
    @Column(length = 20, nullable = false)
    private String name;
    private LocalDate birthday;
    @Column(length = 15)
    private String phone;
    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String thumbnail;
    @Column(length = 20, unique = true)
    private String nickname;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    Member member;

    public static MemberProfile createMemberProfileFrom(RegisterDTO registerDTO, Member member) {
        return MemberProfile.builder().birthday(registerDTO.getBirthday()).phone(registerDTO.getPhone()).gender(registerDTO.getGender()).thumbnail(registerDTO.getThumbnail()).nickname(registerDTO.getNickname()).name(registerDTO.getName()).member(member).build();
    }
}

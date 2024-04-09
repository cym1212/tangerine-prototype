package io.mohajistudio.tangerine.prototype.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mohajistudio.tangerine.prototype.global.common.BaseEntity;
import io.mohajistudio.tangerine.prototype.global.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(length = 100)
    private String introduction;

    @Setter
    private String profileImage;

    @Column(length = 20, unique = true)
    private String nickname;

    @Setter
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    Member member;
}

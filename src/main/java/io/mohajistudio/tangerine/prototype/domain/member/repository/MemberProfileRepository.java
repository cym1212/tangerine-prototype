package io.mohajistudio.tangerine.prototype.domain.member.repository;

import io.mohajistudio.tangerine.prototype.domain.member.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
    @Query("SELECT mp FROM MemberProfile mp WHERE mp.member.id = :memberId")
    Optional<MemberProfile> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT mp FROM MemberProfile mp WHERE mp.nickname = :nickname")
    Optional<MemberProfile> findByNickname(@Param("nickname") String nickname);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE MemberProfile mp SET mp.modifiedAt = :modifiedAt, mp.name = :name, mp.nickname = :nickname, mp.introduction = :introduction, mp.phone = :phone, mp.profileImage = :profileImage WHERE mp.id = :id")
    void update(@Param("id") Long id, @Param("modifiedAt") LocalDateTime modifiedAt, @Param("name") String name, @Param("nickname") String nickname, @Param("introduction") String introduction, @Param("phone") String phone, @Param("profileImage") String profileImage);
}

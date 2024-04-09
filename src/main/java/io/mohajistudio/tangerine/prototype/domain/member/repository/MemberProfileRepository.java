package io.mohajistudio.tangerine.prototype.domain.member.repository;

import io.mohajistudio.tangerine.prototype.domain.member.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {
    @Query("SELECT mp FROM MemberProfile mp WHERE mp.member.id = :memberId")
    Optional<MemberProfile> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT mp FROM MemberProfile mp WHERE mp.nickname = :nickname")
    Optional<MemberProfile> findByNickname(@Param("nickname") String nickname);
}

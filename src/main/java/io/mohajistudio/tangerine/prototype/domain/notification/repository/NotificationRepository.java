package io.mohajistudio.tangerine.prototype.domain.notification.repository;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Follow;
import io.mohajistudio.tangerine.prototype.domain.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n " +
            "LEFT JOIN FETCH n.relatedPost rp " +
            "LEFT JOIN FETCH n.relatedMember rm " +
            "LEFT JOIN FETCH rm.memberProfile mp " +
            "LEFT JOIN FETCH n.relatedComment rc " +
            "WHERE n.member.id = :memberId")
    Page<Notification> findAll(@Param("memberId") Long memberId, Pageable pageable);
}

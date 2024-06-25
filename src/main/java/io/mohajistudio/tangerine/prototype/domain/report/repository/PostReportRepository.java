package io.mohajistudio.tangerine.prototype.domain.report.repository;

import io.mohajistudio.tangerine.prototype.domain.report.domain.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    @Query("SELECT pr FROM PostReport pr LEFT JOIN FETCH pr.reportType WHERE pr.post.id = :postId AND pr.reportingMember.id = :reportingMemberId")
    Optional<PostReport> findByPostIdAndReportingMemberId(@Param("postId") Long postId, @Param("reportingMemberId") Long reportingMemberId);

    @Query("SELECT count(pr.id) FROM PostReport pr WHERE pr.post.id = :postId AND pr.createdAt >= :oneDayAgo")
    Long countToday(@Param("postId") Long postId, @Param("oneDayAgo") LocalDateTime oneDayAgo);
}

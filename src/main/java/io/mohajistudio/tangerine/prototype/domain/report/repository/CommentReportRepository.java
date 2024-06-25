package io.mohajistudio.tangerine.prototype.domain.report.repository;

import io.mohajistudio.tangerine.prototype.domain.report.domain.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    @Query("SELECT cr FROM CommentReport cr LEFT JOIN FETCH cr.reportType WHERE cr.comment.id = :commentId AND cr.reportingMember.id = :reportingMemberId")
    Optional<CommentReport> findByCommentIdAndReportingMemberId(@Param("commentId") Long commentId, @Param("reportingMemberId") Long reportingMemberId);

    @Query("SELECT count(cr.id) FROM CommentReport cr WHERE cr.comment.id = :commentId AND cr.createdAt >= :oneDayAgo")
    Long countToday(@Param("commentId") Long commentId, @Param("oneDayAgo") LocalDateTime oneDayAgo);
}

package io.mohajistudio.tangerine.prototype.domain.report.service;

import io.mohajistudio.tangerine.prototype.domain.notification.service.NotificationService;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.post.service.PostService;
import io.mohajistudio.tangerine.prototype.domain.report.domain.PostReport;
import io.mohajistudio.tangerine.prototype.domain.report.domain.ReportType;
import io.mohajistudio.tangerine.prototype.domain.report.repository.PostReportRepository;
import io.mohajistudio.tangerine.prototype.domain.report.repository.ReportTypeRepository;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.enums.PostStatus;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
    private final PostService postService;
    private final PostReportRepository postReportRepository;
    private final ReportTypeRepository reportTypeRepository;
    private final NotificationService notificationService;

    public void addPostReport(PostReport postReport) {
        Optional<PostReport> findPostReport = postReportRepository.findByPostIdAndReportingMemberId(postReport.getPost().getId(), postReport.getReportingMember().getId());

        if (findPostReport.isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_EXIST_REPORT);
        }

        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        Long postReportTodayCount = postReportRepository.countToday(postReport.getPost().getId(), localDateTime);

        if (postReportTodayCount >= 10) {
            Post modifiedPost = postService.modifyPostStatus(postReport.getPost().getId(), PostStatus.FLAGGED);
            if (modifiedPost != null) {
                notificationService.sendPostReportMessageToPostAuthor(modifiedPost);
            }
        }

        postReportRepository.save(postReport);
    }

    public List<ReportType> findReportTypeList() {
        return reportTypeRepository.findAll();
    }
}
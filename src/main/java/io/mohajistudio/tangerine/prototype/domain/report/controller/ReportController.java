package io.mohajistudio.tangerine.prototype.domain.report.controller;

import io.mohajistudio.tangerine.prototype.domain.member.domain.Member;
import io.mohajistudio.tangerine.prototype.domain.post.domain.Post;
import io.mohajistudio.tangerine.prototype.domain.report.domain.PostReport;
import io.mohajistudio.tangerine.prototype.domain.report.domain.ReportType;
import io.mohajistudio.tangerine.prototype.domain.report.dto.PostReportDTO;
import io.mohajistudio.tangerine.prototype.domain.report.dto.ReportTypeDTO;
import io.mohajistudio.tangerine.prototype.domain.report.mapper.ReportMapper;
import io.mohajistudio.tangerine.prototype.domain.report.service.ReportService;
import io.mohajistudio.tangerine.prototype.global.auth.domain.SecurityMemberDTO;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.enums.ResolutionStatus;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Tag(name = "Report", description = "Report API")
public class ReportController {
    private final ReportService reportService;
    private final ReportMapper reportMapper;

    @GetMapping("/report-types")
    @Operation(summary = "신고 유형 목록", description = "신고 유형 목록을 조회합니다.")
    public List<ReportTypeDTO> reportTypeList(@AuthenticationPrincipal SecurityMemberDTO securityMemberDTO) {
        if (securityMemberDTO == null) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }

        return reportService.findReportTypeList().stream().map(reportMapper::toDTO).toList();
    }

    @PostMapping("/posts/{postId}/report")
    @Operation(summary = "게시글 신고", description = "게시글 신고 형식에 맞게 데이터를 전달해주세요.")
    public void postReportAdd(@PathVariable("postId") Long postId, @Valid @RequestBody PostReportDTO postReportDTO, @AuthenticationPrincipal SecurityMemberDTO securityMemberDTO) {
        Post post = Post.builder().id(postId).build();
        Member member = Member.builder().id(securityMemberDTO.getId()).build();
        ReportType reportType = ReportType.builder().id(postReportDTO.getReportTypeId()).build();
        PostReport postReport = PostReport.builder().reportingMember(member).post(post).reportType(reportType).resolutionStatus(ResolutionStatus.PENDING).build();
        reportService.addPostReport(postReport);
    }
}

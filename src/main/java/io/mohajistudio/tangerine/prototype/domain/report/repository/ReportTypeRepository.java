package io.mohajistudio.tangerine.prototype.domain.report.repository;

import io.mohajistudio.tangerine.prototype.domain.report.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTypeRepository extends JpaRepository<ReportType, Long> {
}

package io.mohajistudio.tangerine.prototype.domain.report.mapper;

import io.mohajistudio.tangerine.prototype.domain.report.domain.ReportType;
import io.mohajistudio.tangerine.prototype.domain.report.dto.ReportTypeDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ReportMapper {
    ReportTypeDTO toDTO(ReportType reportType);
}

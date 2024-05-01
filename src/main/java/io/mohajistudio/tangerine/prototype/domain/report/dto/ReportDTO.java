package io.mohajistudio.tangerine.prototype.domain.report.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class ReportDTO {
    private Long reportTypeId;
    @Length(max = 100)
    private String content;
}
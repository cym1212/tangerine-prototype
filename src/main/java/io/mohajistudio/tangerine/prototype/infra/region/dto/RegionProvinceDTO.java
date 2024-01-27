package io.mohajistudio.tangerine.prototype.infra.region.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class RegionProvinceDTO {
    private String code;
    private String name;
}

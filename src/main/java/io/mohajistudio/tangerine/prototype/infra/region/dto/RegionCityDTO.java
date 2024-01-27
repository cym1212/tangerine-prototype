package io.mohajistudio.tangerine.prototype.infra.region.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RegionCityDTO {
    private String code;
    private String fullName;
    private String name;
}

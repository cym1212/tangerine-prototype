package io.mohajistudio.tangerine.prototype.infra.region.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RegionProvinceDTO {
    private String code;
    private String name;
    private List<RegionCityDTO> regionCities;

    public void addRegionCity(RegionCityDTO regionCity) {
        regionCities.add(regionCity);
    }
}

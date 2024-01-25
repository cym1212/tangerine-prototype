package io.mohajistudio.tangerine.prototype.infra.place.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceKakaoSearchApiMetaDTO {
    private int totalCount;
    private int pageableCount;
    private String isEnd;
}

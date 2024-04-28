package io.mohajistudio.tangerine.prototype.infra.place.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoPlaceApiMetaDTO {
    private int totalCount;
    private int pageableCount;
    private String isEnd;
}

package io.mohajistudio.tangerine.prototype.infra.place.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
public class KakaoPlaceApiDTO {
        private KakaoPlaceApiMetaDTO meta;
        private List<KakaoPlaceDTO> documents;
}

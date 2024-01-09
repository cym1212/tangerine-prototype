package io.mohajistudio.tangerine.prototype.domain.place.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RepresentativeRegionDTO {
    @NotNull
    private List<String> regions;
}

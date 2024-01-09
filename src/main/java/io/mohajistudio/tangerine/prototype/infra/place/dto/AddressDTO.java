package io.mohajistudio.tangerine.prototype.infra.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    @NotNull
    private String province;//광역시/도
    @NotNull
    private String city;//시/군/구
    @NotNull
    private String district;//읍/면/동
    private String detail;//이하
}

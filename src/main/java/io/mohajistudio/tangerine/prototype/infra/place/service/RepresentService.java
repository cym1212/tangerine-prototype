package io.mohajistudio.tangerine.prototype.infra.place.service;

import io.mohajistudio.tangerine.prototype.infra.place.dto.AddressDTO;

import java.util.List;

public interface RepresentService {
    List<String> extract(List<AddressDTO> places);
}

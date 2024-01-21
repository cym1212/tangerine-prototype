package io.mohajistudio.tangerine.prototype.infra.webhook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class FieldDTO {
    String name;
    String value;
    boolean inline;

    public static FieldDTO createInlineField(String name, String value) {
        return FieldDTO.builder().name(name).value(value).inline(true).build();
    }

    public static FieldDTO createField(String name, String value) {
        return FieldDTO.builder().name(name).value(value).build();
    }
}

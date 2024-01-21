package io.mohajistudio.tangerine.prototype.infra.webhook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class EmbedObjectDTO {
    private String title;
    private String description;
    private String color;
    private List<FieldDTO> fields;

    public static EmbedObjectDTO createErrorEmbedObject(String title, String description, List<FieldDTO> fields) {
        return EmbedObjectDTO.builder().title(title).description(description).fields(fields).color("15548997").build();
    }
}

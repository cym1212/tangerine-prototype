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
public class DiscordWebhookDTO {
    private String content;
    private boolean tts;
    private List<EmbedObjectDTO> embeds;

    public static DiscordWebhookDTO createErrorMessage(String content, List<EmbedObjectDTO> embeds) {
        return DiscordWebhookDTO.builder().content(content).embeds(embeds).build();
    }
}

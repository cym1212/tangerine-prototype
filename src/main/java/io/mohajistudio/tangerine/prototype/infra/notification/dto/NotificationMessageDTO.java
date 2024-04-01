package io.mohajistudio.tangerine.prototype.infra.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class NotificationMessageDTO {
    private String token;
    private String title;
    private String body;
    private String image;
    private Map<String, String> data;
}

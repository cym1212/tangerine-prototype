package io.mohajistudio.tangerine.prototype.infra.webhook.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "discord")
public class WebhookProperties {
    private String webhookUrl;
}

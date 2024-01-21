package io.mohajistudio.tangerine.prototype.infra.webhook.service;

import io.mohajistudio.tangerine.prototype.infra.webhook.config.WebhookProperties;
import io.mohajistudio.tangerine.prototype.infra.webhook.dto.DiscordWebhookDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookService {
    private final WebhookProperties webhookProperties;

    public void sendMessage(DiscordWebhookDTO discordWebhookDTO) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DiscordWebhookDTO> request = new HttpEntity<>(discordWebhookDTO, headers);
        System.out.println("discordWebhookDTO = " + discordWebhookDTO.toString());

        restTemplate.postForObject(webhookProperties.getWebhookUrl(), request, String.class);
    }
}

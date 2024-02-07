package io.mohajistudio.tangerine.prototype.infra.place.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "place.search")
public class PlaceApiProperties {
    private String url;
    private String restApiKey;
}

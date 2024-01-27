package io.mohajistudio.tangerine.prototype.infra.region.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "region.v-world")
public class RegionProperties {
    private String url;
    private String key;
    private String provinceKey;
    private String cityKey;
}

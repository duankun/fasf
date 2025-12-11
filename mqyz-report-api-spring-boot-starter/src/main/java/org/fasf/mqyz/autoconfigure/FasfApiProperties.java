package org.fasf.mqyz.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fasf.api")
@Data
public class FasfApiProperties {
    private boolean enable;
    private String basePackages;
    private Energy energy;

    @Data
    public static class Energy {
        private String endpoint;
        private String sm2PublicKey;
    }
}

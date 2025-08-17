package org.fasf.autoconfigure;

import org.fasf.spring.annotation.ApiScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ApiScan(basePackages = "${fasf.api.basePackages:org.fasf.api}")
@ConditionalOnProperty(
        name = {"fasf.api.enable"},
        havingValue = "true",
        matchIfMissing = true
)
public class FasfAutoConfiguration {
}

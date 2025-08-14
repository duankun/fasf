package org.fasf.api.configuration;

import org.fasf.spring.annotation.RemoterScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@RemoterScan(basePackages = "${fasf.remoter.basePackages:org.fasf.api.remoter}")
@ConditionalOnProperty(
        name = {"fasf.remoter.enable"},
        havingValue = "true",
        matchIfMissing = true
)
public class FasfConfiguration {
}

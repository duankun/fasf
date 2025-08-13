package org.fasf.spring.configuration;

import org.fasf.spring.annotation.RemoterScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@RemoterScan(basePackages = "${fasf.remoter.basePackages}")
@ConditionalOnProperty(
        name = {"fasf.remoter.enable"},
        havingValue = "true",
        matchIfMissing = true
)
public class FasfConfiguration {
}

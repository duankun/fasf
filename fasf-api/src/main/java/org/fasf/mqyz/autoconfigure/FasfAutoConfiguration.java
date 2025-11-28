package org.fasf.mqyz.autoconfigure;

import org.fasf.core.http.HttpClient;
import org.fasf.core.spring.annotation.ApiScan;
import org.fasf.mqyz.interceptor.EnergyAuthorizationRequestInterceptor;
import org.fasf.mqyz.interceptor.EnergyDecryptResponseInterceptor;
import org.fasf.mqyz.interceptor.EnergyEncryptRequestInterceptor;
import org.fasf.mqyz.interceptor.EnergyRequestContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

@Configuration
@ApiScan(basePackages = "${fasf.api.basePackages}")
@EnableConfigurationProperties(FasfApiProperties.class)
@ConditionalOnProperty(
        name = {"fasf.api.enable"},
        havingValue = "true",
        matchIfMissing = true
)
public class FasfAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpClient.class)
    public HttpClient createHttpClient() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        return new HttpClient.DefaultHttpClient(restTemplateBuilder.build(), Schedulers.newBoundedElastic(100, 1000, "fasf-http-client"));
    }

    @Bean
    public EnergyRequestContext energyRequestContext(FasfApiProperties fasfApiProperties) {
        return new EnergyRequestContext(fasfApiProperties);
    }

    @Bean
    public EnergyAuthorizationRequestInterceptor energyAuthorizationRequestInterceptor(EnergyRequestContext energyRequestContext) {
        return new EnergyAuthorizationRequestInterceptor(energyRequestContext);
    }

    @Bean
    public EnergyDecryptResponseInterceptor energyDecryptResponseInterceptor(EnergyRequestContext energyRequestContext) {
        return new EnergyDecryptResponseInterceptor(energyRequestContext);
    }

    @Bean
    public EnergyEncryptRequestInterceptor energyEncryptRequestInterceptor(EnergyRequestContext energyRequestContext) {
        return new EnergyEncryptRequestInterceptor(energyRequestContext);
    }
}

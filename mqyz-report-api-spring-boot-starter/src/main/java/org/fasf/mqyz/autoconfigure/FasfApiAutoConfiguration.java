package org.fasf.mqyz.autoconfigure;

import org.fasf.core.http.HttpClient;
import org.fasf.core.http.HttpException;
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
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;

@Configuration
@ApiScan(basePackages = "${fasf.api.basePackages}")
@EnableConfigurationProperties(FasfApiProperties.class)
@ConditionalOnProperty(
        name = {"fasf.api.enable"},
        havingValue = "true",
        matchIfMissing = true
)
public class FasfApiAutoConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .errorHandler(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(@Nullable ClientHttpResponse clientHttpResponse) throws IOException {
                        assert clientHttpResponse != null;
                        return clientHttpResponse.getStatusCode().isError();
                    }

                    @Override
                    public void handleError(@Nullable ClientHttpResponse clientHttpResponse) throws IOException {
                        assert clientHttpResponse != null;
                        throw new HttpException(clientHttpResponse.getStatusCode().value(), clientHttpResponse.getStatusCode().getReasonPhrase(), null);
                    }
                });
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(HttpClient.class)
    public HttpClient httpClient(RestTemplate restTemplate) {
        return new HttpClient.DefaultHttpClient(restTemplate, Schedulers.newBoundedElastic(100, 10000, "fasf-http-client"));
    }

    @Bean
    public EnergyRequestContext energyRequestContext(RestTemplate restTemplate, FasfApiProperties fasfApiProperties) {
        return new EnergyRequestContext(restTemplate, fasfApiProperties);
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

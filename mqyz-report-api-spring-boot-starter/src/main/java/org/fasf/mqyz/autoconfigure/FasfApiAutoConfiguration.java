package org.fasf.mqyz.autoconfigure;

import org.fasf.core.http.HttpClient;
import org.fasf.core.http.HttpException;
import org.fasf.core.spring.annotation.ApiScan;
import org.fasf.mqyz.interceptor.EnergyRequestContext;
import org.fasf.sctel.interceptor.AuthorizationRequestInterceptor;
import org.fasf.sctel.interceptor.DecryptResponseInterceptor;
import org.fasf.sctel.interceptor.EncryptRequestInterceptor;
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
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        builder.setConnectTimeout(Duration.ofSeconds(5))
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
    public AuthorizationRequestInterceptor authorizationRequestInterceptor(EnergyRequestContext energyRequestContext) {
        return new AuthorizationRequestInterceptor(energyRequestContext);
    }

    @Bean
    public DecryptResponseInterceptor decryptResponseInterceptor(EnergyRequestContext energyRequestContext) {
        return new DecryptResponseInterceptor(energyRequestContext);
    }

    @Bean
    public EncryptRequestInterceptor encryptRequestInterceptor(EnergyRequestContext energyRequestContext) {
        return new EncryptRequestInterceptor(energyRequestContext);
    }
}

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
import org.springframework.web.client.ResponseErrorHandler;
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
    @ConditionalOnMissingBean(HttpClient.class)
    public HttpClient createHttpClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .errorHandler(new ResponseErrorHandler(){
                    @Override
                    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                        return clientHttpResponse.getStatusCode().isError();
                    }

                    @Override
                    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                        throw new HttpException(clientHttpResponse.getStatusCode().value(), clientHttpResponse.getStatusCode().getReasonPhrase(), null);
                    }
                });
        return new HttpClient.DefaultHttpClient(builder.build(), Schedulers.newBoundedElastic(100, 1000, "fasf-http-client"));
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

package org.fasf.autoconfigure;

import io.netty.channel.ChannelOption;
import org.fasf.core.http.HttpClient;
import org.fasf.core.interceptor.ResponseInterceptor;
import org.fasf.core.spring.annotation.ApiScan;
import org.fasf.interceptor.AESResponseInterceptor;
import org.fasf.interceptor.TraceIdInterceptor;
import org.fasf.interceptor.encrypt.AESEncryptRequestInterceptor;
import org.fasf.interceptor.encrypt.DESEncryptRequestInterceptor;
import org.fasf.interceptor.encrypt.RSAEncryptRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
@ApiScan(basePackages = "${fasf.api.basePackages:org.fasf.api}")
@ConditionalOnProperty(
        name = {"fasf.api.enable"},
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(FasfApiProperties.class)
public class FasfApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpClient.class)
    public HttpClient createHttpClient(FasfApiProperties properties) {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("fasf-connection-provider")
                .maxConnections(properties.getConnectionProvider().getMaxConnections())
                .pendingAcquireTimeout(Duration.ofSeconds(properties.getConnectionProvider().getPendingAcquireTimeout()))
                .pendingAcquireMaxCount(properties.getConnectionProvider().getPendingAcquireMaxCount())
                .maxIdleTime(Duration.ofSeconds(properties.getConnectionProvider().getMaxIdleTime()))
                .maxLifeTime(Duration.ofMinutes(properties.getConnectionProvider().getMaxLifeTime()))
                .evictInBackground(Duration.ofSeconds(properties.getConnectionProvider().getEvictInBackground()))
                .build();

        LoopResources loopResources = LoopResources.create(properties.getLoopResources().getPrefix(), Math.min(properties.getLoopResources().getMinWorkerCount(), Runtime.getRuntime().availableProcessors()), true);

        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create(connectionProvider)
                .runOn(loopResources)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getHttpClient().getConnectTimeoutMillis())
                .responseTimeout(Duration.ofSeconds(properties.getHttpClient().getResponseTimeout()))
                .keepAlive(properties.getHttpClient().isKeepAlive());

        return new HttpClient.DefaultHttpClient(WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build(), Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor()));
    }

    @Bean
    @ConditionalOnProperty(name = "fasf.api.encrypt.type", havingValue = "des")
    public DESEncryptRequestInterceptor desEncryptRequestInterceptor() {
        return new DESEncryptRequestInterceptor();
    }


    @Bean
    @ConditionalOnProperty(name = "fasf.api.encrypt.type", havingValue = "aes")
    public AESEncryptRequestInterceptor aesEncryptRequestInterceptor() {
        return new AESEncryptRequestInterceptor();
    }

    @Bean
    @ConditionalOnProperty(name = "fasf.api.encrypt.type", havingValue = "rsa")
    public RSAEncryptRequestInterceptor rsaEncryptRequestInterceptor() {
        return new RSAEncryptRequestInterceptor();
    }


    @Bean
    @ConditionalOnProperty(name = "fasf.api.encrypt.type", havingValue = "aes")
    public AESResponseInterceptor aesResponseInterceptor() {
        return new AESResponseInterceptor();
    }


    @Bean
    @ConditionalOnProperty(name = "fasf.api.traceId.name")
    public TraceIdInterceptor traceIdInterceptor() {
        return new TraceIdInterceptor();
    }

    @Bean
    public ResponseInterceptor.NoOpResponseInterceptor noOpResponseInterceptor() {
        return new ResponseInterceptor.NoOpResponseInterceptor();
    }
}

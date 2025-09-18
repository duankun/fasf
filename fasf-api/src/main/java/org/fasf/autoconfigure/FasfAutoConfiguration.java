package org.fasf.autoconfigure;

import io.netty.channel.ChannelOption;
import org.fasf.core.http.HttpClient;
import org.fasf.core.spring.annotation.ApiScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
public class FasfAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpClient.class)
    public HttpClient createHttpClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("fasf-connection-provider")
                .maxConnections(1000)
                .pendingAcquireTimeout(Duration.ofSeconds(10))
                .pendingAcquireMaxCount(5000)
                .maxIdleTime(Duration.ofSeconds(30))
                .maxLifeTime(Duration.ofMinutes(5))
                .evictInBackground(Duration.ofSeconds(10))
                .build();

        LoopResources loopResources = LoopResources.create("fasf-reactor-io", Math.min(4, Runtime.getRuntime().availableProcessors()), true);

        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create(connectionProvider)
                .runOn(loopResources)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .responseTimeout(Duration.ofSeconds(15))
                .keepAlive(true);

        return new HttpClient.DefaultHttpClient(WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build(), Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor()));
    }
}

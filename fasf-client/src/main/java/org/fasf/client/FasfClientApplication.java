package org.fasf.client;

import org.fasf.mqyz.api.EnergyApi;
import org.fasf.mqyz.model.ro.TrendRO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FasfClientApplication {
    private final Logger logger = LoggerFactory.getLogger(FasfClientApplication.class);

    @Autowired(required = false)
    private EnergyApi energyApi;

    public static void main(String[] args) {
        SpringApplication.run(FasfClientApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {

        return args -> {
            TrendRO ro = new TrendRO();
            ro.setEnergyType("2");
            ro.setStatisticsTime("2025-01-01 00:00:00");
            ro.setPageNum(1L);
            ro.setDateType("YEAR");
            String result = energyApi.getTrend(ro);
            System.out.println(result);
        };
    }
}

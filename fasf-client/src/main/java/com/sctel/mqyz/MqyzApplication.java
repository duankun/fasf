package com.sctel.mqyz;

import org.fasf.core.util.JSON;
import org.fasf.mqyz.api.EnergyApi;
import org.fasf.mqyz.model.ro.TrendRO;
import org.fasf.mqyz.model.vo.energy.EnergyResult;
import org.fasf.mqyz.model.vo.energy.MonthEnergyConsumption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
public class MqyzApplication {
    private final Logger logger = LoggerFactory.getLogger(MqyzApplication.class);

    @Resource
    private EnergyApi energyApi;
    @Resource
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(MqyzApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {

        return args -> {
            TrendRO ro = new TrendRO();
            ro.setEnergyType("2");
            ro.setStatisticsTime("2025-01-01 00:00:00");
            ro.setPageNum(1L);
            ro.setDateType("YEAR");
            EnergyResult<List<MonthEnergyConsumption>> result = energyApi.getTrend(ro);
            System.out.println(JSON.toJson(result));

            Long count = jdbcTemplate.queryForObject("select count(*) from t_login_log", Long.class);
            System.out.println("count: " + count);
        };
    }
}

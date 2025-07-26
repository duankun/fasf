package com.freemaker.fasf.spring.configuration;

import com.freemaker.fasf.spring.annotation.RemoterScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@RemoterScan(basePackages = "${fasf.basePackages}")
public class FasfConfiguration {
}

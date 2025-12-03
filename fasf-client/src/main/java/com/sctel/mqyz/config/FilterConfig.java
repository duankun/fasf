package com.sctel.mqyz.config;

import com.sctel.mqyz.web.LoginFilter;
import org.fasf.mqyz.api.MeleApi;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author duankun
 * @date: 2025/12/3
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<LoginFilter> loggingFilter(MeleApi meleApi) {
        FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoginFilter(meleApi));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}

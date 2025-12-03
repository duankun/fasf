package com.sctel.mqyz.service.impl;

import com.sctel.mqyz.service.IReportService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author duankun
 * @date: 2025/12/3
 */
@Service
public class ReportServiceImpl implements IReportService {
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Override
    public Map<String, Object> getEconomySituation(String yearMonth) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalIncome", jdbcTemplate.queryForObject("select count(*) from t_login_log where isdeleted = ?", Double.class, 0));
        return data;
    }
}

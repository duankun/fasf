package com.sctel.mqyz.service;

import java.util.Map;

public interface IReportService {
    Map<String,Object> getEconomySituation(String yearMonth);
}

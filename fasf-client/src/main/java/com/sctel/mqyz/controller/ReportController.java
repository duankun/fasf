package com.sctel.mqyz.controller;

import com.sctel.mqyz.domain.vo.JsonResult;
import com.sctel.mqyz.service.IReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author duankun
 * @date: 2025/12/3
 */
@RestController
@RequestMapping("/report")
public class ReportController {
    @Resource
    private IReportService reportService;

    @GetMapping("/getEconomySituation")
    public JsonResult<Map<String,Object>> getEconomySituation(@RequestParam(value = "yearMonth",required = false) String yearMonth) {
        return JsonResult.ok(reportService.getEconomySituation(yearMonth));
    }

}

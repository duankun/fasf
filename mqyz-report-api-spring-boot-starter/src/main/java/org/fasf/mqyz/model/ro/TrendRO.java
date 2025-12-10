package org.fasf.mqyz.model.ro;

import lombok.Data;

import java.io.Serializable;

/**
 * @author duankun
 * @date: 2025/11/28
 */
@Data
public class TrendRO implements Serializable {
    private String energyType;
    private String statisticsTime;
    private String dateType;
    private Long pageNum;
}

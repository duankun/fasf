package org.fasf.mqyz.model.vo.energy;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MonthEnergyConsumption implements Serializable {
    private String statisticTime;
    private List<AreaEnergyConsumption> areaEnergyConsumption;
}

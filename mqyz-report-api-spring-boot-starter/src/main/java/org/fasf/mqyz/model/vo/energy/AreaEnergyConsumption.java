package org.fasf.mqyz.model.vo.energy;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaEnergyConsumption implements Serializable {
    private String areaName;
    private String energyConsumption;
}

package org.fasf.mqyz.model.vo.energy;

import lombok.Data;

@Data
public class EnergyResult<T> {
    private Meta meta;
    private T data;
}

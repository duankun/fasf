package org.fasf.model.ro;

import java.io.Serializable;

public class OrderInfoRO implements Serializable {
    private String orderId;

    public OrderInfoRO() {
    }

    public OrderInfoRO(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

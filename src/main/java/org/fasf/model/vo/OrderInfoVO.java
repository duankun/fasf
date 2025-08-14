package org.fasf.model.vo;

import java.io.Serializable;

public class OrderInfoVO implements Serializable {
    private String orderId;
    private String userName;
    private String productName;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

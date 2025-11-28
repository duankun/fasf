package org.fasf.mqyz.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class OrderInfoVO implements Serializable {
    private String orderId;
    private Long userId;
    private Long orderTime;
    private BigDecimal orderPrice;
    private String userName;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNO() {
        return userPhoneNO;
    }

    public void setUserPhoneNO(String userPhoneNO) {
        this.userPhoneNO = userPhoneNO;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    private String userPhoneNO;
    private String productName;
    private List<String> productImages;

}

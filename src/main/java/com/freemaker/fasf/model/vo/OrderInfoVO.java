package com.freemaker.fasf.model.vo;

import java.io.Serializable;

public record OrderInfoVO(String orderId,String userName,String productName) implements Serializable {

}

package org.fasf.mqyz.model.vo.mele;

import lombok.Data;

import java.io.Serializable;

@Data
public class MeleResult<T> implements Serializable {
    private int code;
    private String error;
    private String message;
    private T data;
}

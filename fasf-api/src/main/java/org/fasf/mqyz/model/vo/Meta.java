package org.fasf.mqyz.model.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class Meta implements Serializable {
    private String code;
    private String message;
}

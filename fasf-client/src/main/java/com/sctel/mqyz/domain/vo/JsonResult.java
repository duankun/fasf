package com.sctel.mqyz.domain.vo;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author duankun
 * @date: 2025/12/3
 */
@Data
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int code;
    private String message;
    private T data;

    public JsonResult() {
    }

    public JsonResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public JsonResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> JsonResult<T> ok(T data) {
        return new JsonResult<T>(200, "success", data);
    }

    public static <T> JsonResult<T> fail(int code, String message) {
        return new JsonResult<T>(code, message);
    }

    public String toJsonString() {
        return JSONUtil.toJsonStr(this);
    }
}

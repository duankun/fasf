package org.fasf.mqyz.interceptor;

public enum CodeType {
    None("none","无编码"),
    Hex("hex","16进制编码"),
    Base64("base64","Base64编码"),
    Bytes("bytes","byte[]数组的形式");
    CodeType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    private String code;
    /**
     * 编码类型描述说明
     */
    private String description;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }


    /**
     * 根据code获取枚举
     *
     * @param code
     * @return
     */
    public static CodeType getEnum(String code) {
        for (CodeType e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return None;
    }

    /**
     * 判断code是否相等
     *
     * @param code
     * @return
     */
    public boolean codeEquals(String code) {
        return this.code.equals(code);
    }
}

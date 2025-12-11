package org.fasf.mqyz.model.vo.mele;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginInfo implements Serializable {
    private String loginName;
    private String userName;
    private String userId;
}

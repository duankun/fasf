package org.fasf.mqyz.model.vo.mele;

import lombok.Data;

import java.io.Serializable;

/**
 * @author duankun
 * @date: 2025/12/3
 */
@Data
public class LoginInfo implements Serializable {
    private String loginName;
    private String userName;
    private String userId;
}

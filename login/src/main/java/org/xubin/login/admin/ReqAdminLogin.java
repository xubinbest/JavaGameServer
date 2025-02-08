package org.xubin.login.admin;

import lombok.Data;

/**
 * 后台管理登录请求
 */
@Data
public class ReqAdminLogin {
    private long accountId;
    private String password;
}

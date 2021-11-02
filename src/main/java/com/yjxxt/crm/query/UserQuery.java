package com.yjxxt.crm.query;

import com.yjxxt.crm.base.BaseQuery;

/**
 * 营销机会管理多条件查询条件 */
public class UserQuery extends BaseQuery {

    private String userName; // 客户名称
    private String email; // 创建人
    private String phone; // 分配状态

    public UserQuery() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

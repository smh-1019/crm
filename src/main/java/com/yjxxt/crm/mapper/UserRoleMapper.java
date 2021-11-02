package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {

    Integer countUserRoleByUserId(Integer id);

    Integer deleteUserRoleByUserId(Integer userId);

    Integer deleteByRoleId(Integer roleId);

    Integer countByRoleId(Integer roleId);
}

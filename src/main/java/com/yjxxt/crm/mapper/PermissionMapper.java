package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.dto.TreeDto;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    Integer countPermissionByRoleId(Integer roleId);

    Integer deleteRoleModuleByRoleId(Integer roleId);

    List<String> selectUserHasRolesHasPermissions(Integer userId);

    Integer deleteByRoleId(Integer roleId);

}
package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

    List<TreeDto> findModules();

    List<Integer> queryModuleIdByRoleId(Integer roleId);

    List<Module> selectModules();

    List<Module> selectModuleByModule(Integer grade,String url,String moduleName);

    Module selectByOptValue(String optValue);

    Module selectByGradeAndParentId(Integer grade,Integer parentId);

    Module selectByModuleName(String moduleName);

    List<Integer> selectByParentId(List<Integer> ids);
}
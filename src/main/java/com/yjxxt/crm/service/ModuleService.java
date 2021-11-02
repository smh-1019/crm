package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ModuleService extends BaseService<Module, Integer> {

    @Resource
    private ModuleMapper moduleMapper;

    /**
     * 查询角色的权限信息
     * @param roleId
     * @return
     */
    public List<TreeDto> queryAllModule(Integer roleId) {
        List<TreeDto> tlist = moduleMapper.findModules();
        List<Integer> list = moduleMapper.queryModuleIdByRoleId(roleId);
        if (list != null && list.size() != 0) {
            tlist.forEach(treeDto -> {
                if (list.contains(treeDto.getId())) {
                    // 说明当前角色 分配了该菜单
                    treeDto.setChecked(true);
                }
            });
        }
        return tlist;
    }

    /**
     * 查询所有权限信息
     * @return
     */
    public Map<String, Object> queryAllModules() {
        List<Module> list = moduleMapper.selectModules();
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("code", 0);
        map.put("msg", 0);
        map.put("content", list.size());
        map.put("data", list);

        return map;
    }

    /**
     * 新增权限信息
     * @param module
     */
    public void insertModule(Module module) {
        //请指定菜单名
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请输入菜单名！");
        //菜单层级不合法
        Integer grade = module.getGrade();
        AssertUtil.isTrue(grade == null || !( grade== 0 || grade == 1 || grade == 2),"菜单层级不合法");
        //该层级下菜单已存在!
        AssertUtil.isTrue(moduleMapper.selectModuleByModule(module.getGrade(),"",module.getModuleName()).size() > 0,
                "该层级下菜单已存在");
        //二级菜单
        if (grade == 1) {
            //请指定二级菜单url值
            AssertUtil.isTrue(module.getUrl() == null,"请指定二级菜单url值");
            //该层级下url已存在
            AssertUtil.isTrue(moduleMapper.selectModuleByModule(module.getGrade(),module.getUrl(),"").size() > 0,
                    "该层级下表单已存在");
        }
        //非目录
        if (grade != 0) {
            //请指定上级菜单!
            AssertUtil.isTrue(module.getParentId() == null || selectByPrimaryKey(module.getParentId()) == null,
                    "请指定上级菜单！");
            //判定上级菜单与层级是否匹配
            AssertUtil.isTrue(moduleMapper.selectByGradeAndParentId( module.getGrade() - 1,module.getParentId()) == null,
                    "该权限内容上级层级不一致");
        }
        //请输入权限码
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入权限码");
        //权限码已存在
        AssertUtil.isTrue(moduleMapper.selectByOptValue(module.getOptValue()) != null,"权限码已存在！");
        //数据赋值
        module.setIsValid((byte)1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        //数据插入
        AssertUtil.isTrue(insertSelective(module) < 1,"数据添加失败！");
    }

    /**
     * 修改权限信息
     * @param module
     */
    public void updateModule(Module module) {

        //待更新记录不存在
        AssertUtil.isTrue(module == null || module.getId() == 0 || selectByPrimaryKey(module.getId()) == null,"待更新记录不存在！");
        //请指定菜单名称
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请输入菜单名称！");
        //菜单名称已重复
        AssertUtil.isTrue(moduleMapper.selectByPrimaryKey(module.getId()) != null
                && !module.getId().equals( moduleMapper.selectByModuleName(module.getModuleName()).getId()),
                "菜单名称已重复！");
        //菜单层级不合法!
        Integer grade = module.getGrade();
        AssertUtil.isTrue(grade == null || !(grade == 0 || grade == 1 || grade == 2),"菜单级别不合法，请重新选择");
        //二级菜单
        if (grade == 1) {
            //请指定二级菜单url值
            AssertUtil.isTrue(module.getUrl() == null,"请指定二级菜单url值");
            //该层级下url已存在
            AssertUtil.isTrue(moduleMapper.selectModuleByModule(module.getGrade(),module.getUrl(),"").size() > 0,
                    "该层级下表单已存在");
        }
        //非目录
        if (grade != 0) {
            //请指定上级菜单!
            AssertUtil.isTrue(module.getParentId() == null || selectByPrimaryKey(module.getParentId()) == null,
                    "请指定上级菜单！");
            //判定上级菜单与层级是否匹配
            AssertUtil.isTrue(moduleMapper.selectByGradeAndParentId( module.getGrade() - 1,module.getParentId()) == null,
                    "该权限内容上级层级不一致");
        }
        //请输入权限码
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入权限码");
        //权限码已存在
        AssertUtil.isTrue(moduleMapper.selectByOptValue(module.getOptValue()) != null &&
                        !module.getId().equals(moduleMapper.selectByOptValue(module.getOptValue()).getId())
                ,"权限码已存在！");
        //数据赋值
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module) < 1,"权限修改失败！");
    }

    public Module queryModuleById(Integer id) {
        return moduleMapper.selectByPrimaryKey(id);
    }

    public void deleteModuleById(Integer id) {
        Module module = moduleMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(module == null,"待修改数据不存在");

        List<Integer> ids = new ArrayList<>();
        List<Integer> ids2 = new ArrayList<>();
        ids.add(id);
        for(int i = module.getGrade(); i <= 2; i++){
            ids2 = moduleMapper.selectByParentId(ids);
            if (ids2 == null){
                return;
            }
            for (Integer num:ids){
                AssertUtil.isTrue(moduleMapper.deleteByPrimaryKey(num) < 1,"数据删除失败！");
            }
            ids.clear();
            ids.addAll(ids2);

        }

    }
}

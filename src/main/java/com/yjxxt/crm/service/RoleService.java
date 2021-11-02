package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.mapper.RoleMapper;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Autowired(required = false)
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;



    /**
     * 查询用户角色列表
     * @return
     */
    public List<Map<String,Object>> queryRoles(Integer userId){
        return roleMapper.queryRolesByUserId(userId);
    }

    //查询用户信息
    public Map<String,Object> queryRoleByParam(RoleQuery roleQuery){
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(roleQuery.getPage(),roleQuery.getLimit());
        PageInfo<Role> pageInfo = new PageInfo<>(roleMapper.selectByParams(roleQuery));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

    /**
     * 增加角色信息
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRole(Role role){
        //角色是否输入
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名称！！");
        //角色是否重复
        AssertUtil.isTrue(roleMapper.queryByRoleName(role.getRoleName()) != null,"角色已存在！");
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        role.setIsValid(1);
        AssertUtil.isTrue(roleMapper.insertHasKey(role) < 1,"数据添加失败");
    }

    /**
     * 修改角色信息
     * @param role
     */
    public void updateRole(Role role){
        //角色是否存在
        AssertUtil.isTrue(role.getId() == null || roleMapper.selectByPrimaryKey(role.getId()) == null,"用户不存在");
        //角色名是否输入
        AssertUtil.isTrue(role.getRoleName() == null,"请输入角色名称！！");
        //角色是否重复
        Role temp = roleMapper.queryByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp != null
                && role.getId() != temp.getId(),"用户名重复！");
        //修改角色修改时间
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) < 1,"数据修改失败");
    }

    /**
     *
     * @param id
     */
    public void deleteRole(Integer id){
        //角色是否存在
        AssertUtil.isTrue(id == null || roleMapper.selectByPrimaryKey(id) == null,"用户不存在");
        AssertUtil.isTrue(roleMapper.deleteByPrimaryKey(id) < 1,"角色数据删除失败！！");
        permissionMapper.deleteByRoleId(id);
        userRoleMapper.deleteByRoleId(id);
    }

    public void addGrant(Integer[] mids, Integer roleId) {
        Role temp = roleMapper.selectByPrimaryKey(roleId);
        //待授权角色不存在
        AssertUtil.isTrue(roleId == null || temp == null,"待授权角色不存在");
        //查询角色原权限数
        int count = permissionMapper.countPermissionByRoleId(roleId);
        if (count > 0){
            AssertUtil.isTrue(permissionMapper.deleteRoleModuleByRoleId(roleId) != count,"角色权限删除失败！！");
        }
        if (mids != null && mids.length > 0){
            List<Permission> list = new ArrayList<Permission>();
            for (Integer mid:mids){
                Permission permission = new Permission();
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                list.add(permission);
            }
            AssertUtil.isTrue(permissionMapper.insertBatch(list) != list.size(),"权限增加失败！");
        }
    }
}

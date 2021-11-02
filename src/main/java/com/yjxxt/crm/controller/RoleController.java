package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    /**
     * 查询用户修改角色列表
     * @return
     */
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        System.out.println(roleService.queryRoles(userId).toString());
        return roleService.queryRoles(userId);
    }

    //跳转至用户信息界面
    @RequestMapping("index")
    public String index(){
        return "role/role";
    }


    //角色信息查询
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> query(RoleQuery roleQuery){

        return roleService.queryRoleByParam(roleQuery);
    }

    //跳转至用户增加修改界面
    @RequestMapping("addOrUpdateRolePage")
    public String addOrUpdateRolePage(Integer id, Model model){
        if (id != null){
            //查询用户信息
            Role role = roleService.selectByPrimaryKey(id);
            //存储
            model.addAttribute("role",role);
        }
        return "role/add_update";
    }


    //角色信息增加
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveRole(Role role){
        roleService.saveRole(role);
        return success("角色数据增加成功！");
    }

    //角色信息修改
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("角色数据修改成功！");
    }

    //角色信息修改
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){

        roleService.deleteRole(id);
        return success("角色数据修改成功！");
    }

    /**
     * 跳转至角色权限界面
     * @param model
     * @param roleId
     * @return
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Model model,Integer roleId){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    /**
     * 角色权限修改
     * @param mids
     * @param roleId
     * @return
     */
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mids,Integer roleId){

        roleService.addGrant(mids,roleId);
        return success("角色权限修改成功");
    }
}

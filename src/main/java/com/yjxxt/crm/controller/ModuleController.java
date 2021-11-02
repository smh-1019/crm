package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.service.ModuleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {

    @Resource
    private ModuleService moduleService;

    /**
     * 查询所有权限信息
     * @return
     */
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeDto> queryAllModules(Integer roleId){
        return moduleService.queryAllModule(roleId);
    }

    //跳转至用户信息界面
    @RequestMapping("index")
    public String index(){
        return "module/module";
    }


    /**
     * 查询权限界面信息
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(){
        return moduleService.queryAllModules();
    }


    /**
     * 权限增加
     * @param module
     * @return
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(Module module){
        moduleService.insertModule(module);
        return success("数据添加成功");
    }

    /**
     * 跳转权限增加界面
     * @param grade
     * @param parentId
     * @param model
     * @return
     */
    @RequestMapping("addModulePage")
    public String addModulePage(Integer grade, Integer parentId, Model model){
        model.addAttribute("grade",grade);
        model.addAttribute("parentId",parentId);
        return "module/add";
    }

    /**
     * 跳转至修改界面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("updateModulePage")
    public String updateModulePage(Integer id,Model model){
        model.addAttribute("module",moduleService.queryModuleById(id));
        return "module/update";
    }

    /**
     * 权限数据修改
     * @param module
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Module module){
        moduleService.updateModule(module);
        return success("数据更新成功");
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(Integer id,Model model){
        moduleService.deleteModuleById(id);
        return success("权限删除成功！");
    }

}

package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Autowired
    private SaleChanceService saleChanceService;

    @Autowired
    private UserService userService;

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> sayList(SaleChanceQuery saleChanceQuery){
        return saleChanceService.querySaleChanceByParams(saleChanceQuery);
    }

    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo addOrUpdateSaleChance(HttpServletRequest request, SaleChance saleChance){
        // 获取用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 获取用户的真实姓名
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        System.out.println(saleChance.getState());
        // 设置营销机会的创建人
        saleChance.setCreateMan(trueName);
        // 添加营销机会的数据
        saleChanceService.addSaleChance(saleChance);
        return success("营销数据添加成功！");
    }

    /**
     * 头部——数据修改
     * @param request
     * @param saleChance
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo UpdateSaleChance(HttpServletRequest request, SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        return success("营销数据修改成功！");
    }

    /**
     * 跳转至数据修改页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdateDialog(Integer id, Model model){
        //判断
        if(id!=null){
            //查询用户信息
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //存储
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 批量删除营销数据
     * @param request
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo DeleteSaleChance(HttpServletRequest request, Integer[] ids){
        System.out.println(ids.length);
        saleChanceService.deleteSaleChance(ids);
        return success("营销数据删除成功！");
    }
}

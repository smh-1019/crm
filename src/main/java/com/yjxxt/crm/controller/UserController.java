package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequiredPermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    //@RequiredPermission(code = "6010")
    public ResultInfo userLogin(String userName,String userPwd){
        System.out.println(11);
        ResultInfo resultInfo = new ResultInfo();
        //调用Service层的登录方法，得到返回的用户对象
        UserModel userModel = userService.userLogin(userName,userPwd);

        resultInfo.setResult(userModel);

        return resultInfo;
    }

    //用户密码修改
    @RequestMapping("updatePassword")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request, String oldPassword, String newPassword, String confirmPassword){
        ResultInfo resultInfo = new ResultInfo();

            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);

            userService.updateUserPassword(userId,oldPassword,newPassword,confirmPassword);

        return resultInfo;
    }

    @RequestMapping("setting")
    @ResponseBody
    public ResultInfo settingUser(HttpServletRequest request, User user){
        ResultInfo resultInfo = new ResultInfo();

        userService.updateByPrimaryKeySelective(user);

        return resultInfo;
    }

    //密码修改跳转
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    //用户信息修改跳转
    @RequestMapping("toSettingPage")
    public String toSettingPage(HttpServletRequest req){
        //获取用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //根据ID获取用户信息
        User user = userService.selectByPrimaryKey(userId);
        //存储
        req.setAttribute("user",user);
        return "user/setting";
    }
    //下拉框传值
    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String, Object>> findSales() {
        List<Map<String, Object>> list = userService.queryAll();
        return list;
    }

    //用户信息查询
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> query(UserQuery userQuery){
        return userService.queryUserByParam(userQuery);
    }

    //跳转至用户信息界面
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    //修改用户信息
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo UpdateUser(HttpServletRequest request, User user){

        userService.updateUser(user);
        return success("用户数据修改成功！");
    }
    //添加用户信息
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo AddUser(HttpServletRequest request, User user){
        userService.addUser(user);
        return success("用户数据添加成功！");
    }

    //跳转至用户增加修改界面
    @RequestMapping("addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id, Model model){
        if (id != null){
            //查询用户信息
            User user = userService.selectByPrimaryKey(id);
            //查询用户角色信息
//            user.setRoleIds(userService.queryUserRole(id));
            //存储
            model.addAttribute("user",user);
        }
        return "user/add_update";
    }

    //删除用户信息
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo DeleteSaleChance(HttpServletRequest request, Integer[] ids){
        userService.deleteUser(ids);
        return success("用户数据删除成功！");
    }


}

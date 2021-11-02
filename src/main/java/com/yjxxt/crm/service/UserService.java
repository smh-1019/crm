package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.Md5Util;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.yjxxt.crm.utils.UserIDBase64;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    public UserModel userLogin(String name,String pwd){
        // 验证参数
        checkLoginParams(name,pwd);
        //根据用户名查询对象
        User user = userMapper.selectByUserName(name);
        AssertUtil.isTrue(user==null,"用户不存在");
        //用户存在后确认密码
        String upwd = user.getUserPwd();
        checkLoginPwd(pwd,upwd);
        //  密码正确（用户登录成功，返回用户的相关信息）
        return buildUserInfo(user);
    }

    public UserModel buildUserInfo(User user){
        UserModel userModel = new UserModel();
        //设置用户信息
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 验证用户账户密码是否为空
     * @param userName
     * @param userPwd
     */
    public void checkLoginParams(String userName,String userPwd){
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userName),"密码不能为空");
    }


    /**
     * 验证用户密码是否正确
     * @param userPwd
     * @param upwd
     */
    public void checkLoginPwd(String userPwd,String upwd){
        // 数据库中的密码是经过加密的，将前台传递的密码先加密，再与数据库中的密码作比较
        userPwd = Md5Util.encode(userPwd);
        // 比较密码
        AssertUtil.isTrue(!userPwd.equals(upwd),"密码错误");
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId,String oldPassword,String newPassword,String confirmPassword){
        // 通过userId获取用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        checkPasswordParams(user,oldPassword,newPassword,confirmPassword);
        user.setUserPwd(Md5Util.encode(newPassword));
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"更新失败");
    }

    //验证用户数据
    private void checkPasswordParams(User user,String oldPassword,String newPassword,String confirmPassword){
        AssertUtil.isTrue(user == null,"用户不存在");
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原密码");
        AssertUtil.isTrue(!Md5Util.encode(oldPassword).equals(user.getUserPwd()),"原密码输入错误");
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码");
        AssertUtil.isTrue(oldPassword.equals(newPassword),"新密码不能原密码一致");
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"请输入确认密码");
        AssertUtil.isTrue(!newPassword.equals(confirmPassword),"两次密码输入不一致");
    }

    public List<Map<String,Object>> queryAll(){
        return userMapper.selectSales();
    }

    //查询用户信息
    public Map<String,Object> queryUserByParam(UserQuery userQuery){
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(userQuery.getPage(),userQuery.getLimit());
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectByParams(userQuery));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

    //增加用户信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        //验证
        checkParams(0,user.getUserName(),user.getEmail(),user.getPhone(),1);
        // 设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));

        // 执行sql
        AssertUtil.isTrue(userMapper.insertSelective(user) < 1,"数据添加失败");
        //增加用户角色
        relaionUserRole(userMapper.selectByUserName(user.getUserName()).getId(),user.getRoleIds());
    }

    //修改用户信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        //验证
        checkParams(user.getId(),user.getUserName(),user.getEmail(),user.getPhone(),0);
        // 修改时间调整
        user.setUpdateDate(new Date());
        // 执行sql
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1,"数据添加失败");
        //增加用户角色
        relaionUserRole(user.getId(),user.getRoleIds());
    }

    //判定用户信息是否符合要求
    private void checkParams(Integer id,String userName, String email, String phone,Integer num) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "请输入用户名");

        User user = userMapper.selectByUserName(userName);

        //修改
        if (num == 0 && user != null) {
            AssertUtil.isTrue(user.getId() != id, "用户名不能重复");
        }else if (num == 1){//增加
            AssertUtil.isTrue(user != null && user.getUserName().equals(userName) , "用户名不能重复");
        }
        AssertUtil.isTrue(StringUtils.isBlank(email), "请输入邮箱");

        AssertUtil.isTrue(StringUtils.isBlank(phone), "请输入电话");

        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "请输入合法电话");
    }

    //删除用户信息
    /**
     * 批量删除方法
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Integer[] ids){
        //判断是否选择数据
        AssertUtil.isTrue(ids.length == 0 || ids == null,"请选择要删除的数据");
        //判断删除后返回数量是否与要删除数量一致
        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length, "数据删除失败！！！");
        int num = 0;
        for (Integer id:ids){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(id) < 1, "数据删除失败！！！");
            num++;
        }
        AssertUtil.isTrue(num != ids.length, "角色数据删除失败！！！");

    }

    //判断修改还是增加并进行处理
    private void relaionUserRole(Integer userId, Integer[] roleIds) {
        int count = userRoleMapper.countUserRoleByUserId(userId);
        System.out.println("原角色数量"+count);
        if (count > 0) {
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count,"用户角色分配失败");
        }
        if (roleIds.length != 0) {
            //重新添加新的角色
            List<UserRole> list = new ArrayList<UserRole>();
            System.out.println("新增数量"+roleIds.length);
            for (Integer roleId:roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                list.add(userRole);
            }

            AssertUtil.isTrue(userRoleMapper.insertBatch(list) != list.size(),"数据增加失败！");
        }

    }


}

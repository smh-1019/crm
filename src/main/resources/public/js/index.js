layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
    /**
     * 用户登录
     * 表单提交*/
    form.on("submit(login)" ,function (data){
        //获取表单元素值
        var fileData =data.field;
        //判断用户名密码是否为空
        if (fileData.username=="undefined" || fileData.username.trim() == ""){
            layer.trim("用户名不能为空");
            return false;
        }
        if (fileData.password=="undefined" || fileData.password.trim() == ""){
            layer.trim("密码不能为空");
            return false;
        }

        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data: {
                userName:fileData.username,
                userPwd:fileData.password
            },
            dataType:"json",
            success:function (data){
                // 判断是否登录成功
                if (data.code == 200){
                    layer.msg("登陆成功",function() {
                        // 将用户信息存到cookie中

                        $.cookie("userIdStr", data.result.userIdStr);
                        $.cookie("userName", data.result.userName);
                        $.cookie("trueName", data.result.trueName);

                        if ($("input[type='checkbox']").is(":checked")){
                            $.cookie("userIdStr", data.result.userIdStr,{expires:7});
                            $.cookie("userName", data.result.userName,{expires:7});
                            $.cookie("trueName", data.result.trueName,{expires:7});
                        }
                        // 登录成功后，跳转到首页
                        window.location.href=ctx + "/main";
                    });
                }else {
                    layer.msg(data.msg);
                }

            }
        });
        return false;
    });

    
});
layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 角色列表展示
     */
    var tableIns = table.render({
        elem: '#roleList', // 表格绑定的ID
        url: ctx + '/role/list', // 访问数据的地址
        cellMinWidth: 95,
        page: true, // 开启分页
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "roleListTable",
        cols: [[
            {type: "checkbox", fixed: "left", width: 50},
            {field: "id", title: '编号', fixed: "true", width: 80},
            {field: 'roleName', title: '角色名', minWidth: 50, align: "center"},
            {field: 'roleRemark', title: '角色备注', minWidth: 100, align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center', minWidth: 150},
            {field: 'updateDate', title: '更新时间', align: 'center', minWidth: 150},
            {title: '操作', minWidth: 150, templet: '#roleListBar', fixed: "right", align: "center"}
        ]]
    });

    /**
     * 绑定搜索按钮的点击事件
     */
    $(".search_btn").click(function () {
        tableIns.reload({
            where: {
                //设定异步数据接口的额外参数，任意设
                roleName: $("input[name='roleName']").val(), // 角色名
            },
            page: {
                curr: 1
            }
        });
    });


    //头工具栏事件
    table.on('toolbar(roles)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case "add":
                openAddOrUpdateRoleDialog();
                break;
            case "grant":
                openAddGrantDailog(checkStatus.data);
                break;
        }
        ;
    });
    /**
     * 行监听
     */
    table.on("tool(roles)", function (obj) {
        if (obj.event === 'del') { //删除
            layer.confirm("你确定要删除这些数据吗?", {
                btn: ["确认", "取消"],
            }, function (index) {
                //关闭询问框
                layer.close(index);
                //发送ajax删除
                $.ajax({
                    type: "post",
                    url: ctx + "/role/delete",
                    data: {"id": obj.data.id},
                    dataType: "json",
                    success: function (result) {
                        if (result.code == 200) {
                            layer.msg("删除OK", {icon: 5});
                            //重新加载一下数据
                            tableIns.reload();
                        } else {
                            //提示
                            layer.msg(result.msg);
                        }
                    }
                });
            });
        } else if (obj.event === "edit") {
            openAddOrUpdateRoleDialog(obj.data.id);
        }
    });

    // 打开添加页面
    function openAddOrUpdateRoleDialog(uid) {
        var url = ctx + "/role/addOrUpdateRolePage";
        var title = "角色管理-角色添加";
        if (uid) {
            url = url + "?id=" + uid;
            title = "角色管理-角色更新";
        }
        layui.layer.open({
            title: title,
            type: 2,
            area: ["600px", "280px"],
            maxmin: true,
            content: url
        });
    }

    function openAddGrantDailog(datas) {
        if (datas.length == 0) {
            layer.msg("请选择待授权的角色！", {icon: 5});
            return;
        }
        if (datas.length > 1) {
            layer.msg("暂不支持多角色授权！", {icon: 5});
            return;
        }
        var url = ctx + "/role/toAddGrantPage?roleId=" + datas[0].id;
        var title = "角色管理-角色授权";
        layer.open({
            title: title,
            content: url,
            type: 2,
            area:["600px","280px"],
            maxmin: true
        });
    }

});
layui.use(['table', 'layer'], function () {
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var tableIns = table.render({
        elem: '#userList', // 表格绑定的ID
        url: ctx + '/user/list', // 访问数据的地址
        cellMinWidth: 95,
        page: true, // 开启分页
        height: "full-125",
        limits: [10, 15, 20, 25],
        limit: 10,
        toolbar: "#toolbarDemo",
        id: "userListTable",
        cols: [[
            {type: "checkbox", fixed: "left", width: 50},
            {field: "id", title: '编号', fixed: "true", width: 80},
            {field: 'userName', title: '用户名', minWidth: 50, align: "center"},
            {field: 'email', title: '用户邮箱', minWidth: 100, align: 'center'},
            {field: 'phone', title: '用户电话', minWidth: 100, align: 'center'},
            {field: 'trueName', title: '真实姓名', align: 'center'},
            {field: 'createDate', title: '创建时间', align: 'center', minWidth: 150},
            {field: 'updateDate', title: '更新时间', align: 'center', minWidth: 150},
            {title: '操作', minWidth: 150, templet: '#userListBar', fixed: "right", align: "center"}
        ]]
    });

    /**
     * 绑定搜索按钮的点击事件
     */
    $(".search_btn").click(function () {
        tableIns.reload({
            where: {
                //设定异步数据接口的额外参数，任意设
                userName: $("input[name='userName']").val(), // 客户名
                email: $("input[name='email']").val(), // 创建人
                phone: $("input[name='phone']").val(), // 状态
            },
            page: {
                curr: 1
            }
        });
    });


    /*** 头部工具栏 监听事件 */
    table.on('toolbar(users)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);

        switch (obj.event) {
            case 'add':
                // 点击添加按钮，打开添加营销机会的对话框
                openAddOrUpdateSaleChanceDialog();
                break;
            case 'del':
                // 点击删除按钮
                deleteSaleChance(checkStatus.data);
                break;
        };
    });


    function openAddOrUpdateSaleChanceDialog(userId) {
        var title = "<h2>用户管理 - 用户信息添加</h2>";
        var url = ctx + "/user/addOrUpdateUserPage";
        if (userId){
            title = "<h2>用户管理 - 用户信息修改</h2>";
            url = url + "?id=" + userId;
        }
        layui.layer.open({
            title: title,
            type: 2,
            //iframe
            content: url,
            area: ["650px","400px"],
            maxmin: true
        });
    }

    function deleteSaleChance(data){
        if (data.length == 0){
            layer.msg("请选择需要删除的数据");
            return ;
        }

        layer.confirm("你确定要删除这些数据吗?",{
            btn:["确认","取消"],
        },function(index) {
            //关闭询问框
            layer.close(index);

            //收集数据
            var ids = [];
            for (var x in data) {
                ids.push(data[x].id);
            }
            //发送请求删除数据
            //发送ajax删除
            $.ajax({
                type: "post",
                url: ctx + "/user/delete",
                data: {
                    "ids": ids.toString()
                },
                dataType: "json",
                success: function (result) {
                    if (result.code == 200) {
                        layer.msg("数据删除成功");
                        tableIns.reload();
                    } else {
                        layer.msg(result.msg);
                    }
                }
            });
        });
    }







    /*绑定行内工具栏*/
    //工具条事件
    table.on('tool(users)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）

        if(layEvent === 'del'){ //删除
            layer.confirm("你确定要删除这些数据吗?",{
                btn:["确认","取消"],
            },function(index){
                //关闭询问框
                layer.close(index);
                //发送ajax删除
                $.ajax({
                    type:"post",
                    url:ctx+"/user/delete",
                    data:{"ids":data.id},
                    dataType:"json",
                    success:function(result){
                        if(result.code==200){
                            layer.msg("删除OK",{icon : 5 });
                            //重新加载一下数据
                            tableIns.reload();
                        }else{
                            //提示
                            layer.msg(result.msg);
                        }
                    }
                });
            });
        } else if(layEvent === 'edit'){ //编辑
            openAddOrUpdateSaleChanceDialog(data.id);
        }
    });



});
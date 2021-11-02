layui.use(['table', 'treetable'], function () {
    var $ = layui.jquery;
    var table = layui.table;
    var treeTable = layui.treetable;

    var jz = treeTable.render({
        treeColIndex: 1,
        treeSpid: -1,
        treeIdName: 'id',
        treePidName: 'parentId',
        elem: '#munu-table',
        url: ctx + '/module/list',
        toolbar: "#toolbarDemo",
        treeDefaultClose: true,	//是否默认折叠
        page: true,
        cols: [[
            {type: 'numbers'},
            {field: 'moduleName', minWidth: 100, title: '菜单名称'},
            {field: 'optValue', title: '权限码'},
            {field: 'url', title: '菜单url'},
            {field: 'createDate', title: '创建时间'},
            {field: 'updateDate', title: '更新时间'},
            {
                field: 'grade', width: 80, align: 'center',
                templet: function (d) {
                    if (d.grade == 0) {
                        return '<span class="layui-badge layui-bg-blue">目录</span>';
                    }
                    if (d.grade == 1) {
                        return '<span class="layui-badge-rim">菜单</span>';
                    }
                    if (d.grade == 2) {
                        return '<span class="layui-badge layui-bg-gray">按钮</span>';
                    }
                }, title: '类型'
            },
            {templet: '#auth-state', width: 200, align: 'center', title: '操作'}
        ]],
        done: function () {
            layer.closeAll('loading');
        }
    });


    //头工具栏事件
    table.on('toolbar(munu-table)', function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case "expand":
                treeTable.expandAll('#munu-table');
                break;
            case "fold":
                treeTable.foldAll('#munu-table');
                break;
            case "add":
                openAddModulePage(0, -1);
        }
        ;
    });

    /**
     * 行监听
     */
    table.on("tool(munu-table)", function (obj) {
        if (obj.event === 'del') { //删除
            layer.confirm("你确定要删除这些数据吗?", {
                btn: ["确认", "取消"],
            }, function (index) {
                //关闭询问框
                layer.close(index);
                //发送ajax删除
                $.ajax({
                    type: "post",
                    url: ctx + "/module/delete",
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
            openUpdateModuleDialog(obj.data.id);
        } else if (obj.event === "add") {
            openAddModulePage(obj.data.grade + 1, obj.data.id);
        }
    });


    function openAddModulePage(grade, parentId) {
        var url = ctx + "/module/addModulePage";
        var title = "权限管理-权限添加";
        url = url + "?grade=" + grade + "&parentId=" + parentId;
        layui.layer.open({
            title: title,
            type: 2,
            area: ["700px", "450px"],
            maxmin: true,
            content: url
        });
    }

    function openUpdateModuleDialog(id) {
        var url = ctx + "/module/updateModulePage?id=" + id;
        var title = "菜单更新";
        layui.layer.open({
            title: title,
            type: 2,
            area: ["700px", "450px"],
            maxmin: true,
            content: url
        });
    }

});
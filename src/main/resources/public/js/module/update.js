layui.use(['form', 'layer'], function () {
    var form = layui.form,
        $ = layui.jquery,
        layer = parent.layer === undefined ? layui.layer : top.layer;


    /**
     * 监听表单事件
     */
    form.on("submit(updateModule)", function (obj) {
        /*加载层*/
        var index = layer.msg("数据正在提交中，请稍等", {icon: 16, time: false, shade: 0.8});

        //判断是添加还是修改，id==null,添加，id!=null 修改
        var url = ctx + "/module/update";

        /*发送ajax*/
        $.ajax({
            type: "post",
            url: url,
            data: obj.field,
            dataType: "json",
            success: function (obj) {
                if (obj.code == 200) {
                    //提示一下
                    layer.msg("添加OK", {icon: 5});
                    //关闭加载层
                    layer.close(index);
                    //关闭iframe
                    layer.closeAll("iframe");
                    //刷新页面
                    window.parent.location.reload();
                } else {
                    layer.msg(obj.msg, {icon: 6});
                }
            }
        });
        //取消跳转
        return false;
    });
});
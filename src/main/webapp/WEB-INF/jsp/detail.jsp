<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/3/19
  Time: 14:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/tag.jsp"%>
<html>
<head>
    <title>秒杀详情页</title>
    <%--静态包含--%>
    <%@include file="common/head.jsp"%>
</head>
<body>
    <div class="container">
        <div class="panel panel-default text-center">
            <div class="panel-heading"><h3>${seckill.name}</h3></div>
        </div>
        <div class="panel-body text-center">
            <h2 class="text-danger">
                <!-- 显示time图标 -->
                <span class="glyphicon glyphicon-time"></span>
                <!-- 展示倒计时 -->
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>

    <!-- 登录弹出层 -->
    <div id="killPhoneModal" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <!-- 模态框头部 -->
                <div class="modal-header">
                    <h3 class="modal-title text-center">
                        <span class="glyphicon glyphicon-phone"></span>秒杀电话
                    </h3>
                </div>

                <!-- 模态框主体 -->
                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-8 col-xs-offset-2">
                            <input type="text" name="killPhone" id="killPhoneKey" placeholder="填手机号" class="form-control">
                        </div>
                    </div>
                </div>

                <!-- 模态框底部 -->
                <div class="modal-footer">
                    <!-- 验证信息 -->
                    <span id="killPhoneMessage" class="glyphicon"></span>
                    <button type="button" id="killPhoneBtn" class="btn btn-success">
                        <span class="glyphicon glyphicon-phone"></span>submit
                    </button>
                </div>
            </div>
        </div>
    </div>
</body>
<!--使用cnd获取公共js:http://www.bootcdn.cn-->
<!--jquery操作cookie-->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!--倒计时插件-->
<script src="https://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
<!-- 开始编写交互逻辑 -->
<%--
这样写再往后的js不会被加载
<script type="text/javascript"/>
--%>
<script type="text/javascript" src="/resources/script/seckill.js"></script>
<script type="text/javascript">
    $(function () {
        //使用el表达式传入参数
        seckill.detail.init({
            seckillId:${seckill.seckillId},
            startTime:${seckill.startTime.time},//转换为毫秒数
            endTime:${seckill.endTime.time}
        });
    });
</script>
</html>

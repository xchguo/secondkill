<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/3/19
  Time: 14:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/tag.jsp"%>

<html>
<head>
    <title>秒杀列表页</title>
    <%--静态包含--%>
    <%@include file="common/head.jsp"%>
</head>
<body>
    <div class="container">

        <div class="panel panel-default">
            <div class="panel-heading text-center">
                <h2>秒杀列表</h2>
            </div>
            <div class="panel-body">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>名称</th>
                            <th>库存</th>
                            <th>开始时间</th>
                            <th>结束时间</th>
                            <th>创建时间</th>
                            <th>详情页</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="seckill" items="${list}">
                            <tr>
                                <td>${seckill.name}</td>
                                <td>${seckill.number}</td>
                                <td>
                                    <fmt:formatDate value="${seckill.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${seckill.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${seckill.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <%--target="_blank"弹出新的页面--%>
                                    <a class="btn btn-info" href="/seckill/${seckill.seckillId}/detail" target="_blank">link</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

</body>
</html>

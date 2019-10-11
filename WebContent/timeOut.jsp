<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	String path = request.getContextPath();
	request.setAttribute("path", path);
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>树人支付</title>

<%@include file="static/include/inc.jsp"%>

<style type="text/css">
.content {
	width: 100%;
	height: 100%;
}

.msg {
	width: 70%;
	font-size: 30px;
	color: red;
	margin: 0 auto;
	padding-top:100px;
	text-align: center;
}
</style>
</head>

<body>
	<div class="content">
		<div class="msg">
			支付已超时<br>请重新下单
		</div>
	</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	String path = request.getContextPath();
	request.setAttribute("path", path);
%>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<title>云端支付</title>

	<%@include file="static/include/inc.jsp"%>
	
	<script type="text/javascript">
		$(function(){
			location.href="${path}/dist/index.html";
		});
	</script>
</head>

<body>

</body>
</html>
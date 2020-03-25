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

<title>瀚海支付</title>

<script
	src="${pageContext.request.contextPath}/static/res/jquery/jquery.min.js"
	type="text/javascript"></script>

<script type="text/javascript">
		$(function(){
			var sUserAgent = navigator.userAgent.toLowerCase();
		  if (/ipad|iphone|midp|rv:1.2.3.4|ucweb|android|windows ce|windows mobile/.test(sUserAgent)) {
			  location.href="${path}/showapp.jsp?id=${param.id}"
		  } else {
		   location.href="${path}/showPayPC.jsp?id=${param.id}"
		  }
		});
window.onload = function(){
		//屏蔽键盘事件
		document.onkeydown = function (){
		var e = window.event || arguments[0];
		//F12
		if(e.keyCode == 123){
		return false;
		//Ctrl+Shift+I
		}else if((e.ctrlKey) && (e.shiftKey) && (e.keyCode == 73)){
		return false;
		//Shift+F10
		}else if((e.shiftKey) && (e.keyCode == 121)){
		return false;
		//Ctrl+U
		}else if((e.ctrlKey) && (e.keyCode == 85)){
		return false;
		}
		};
		//屏蔽鼠标右键
		document.oncontextmenu = function (){
		return false;
		}
		}
	</script>
</style>
</head>
<body>
</body>
</html>
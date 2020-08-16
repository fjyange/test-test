<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	String path = request.getContextPath();
	request.setAttribute("path", path);
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>大發论坛</title>

<script src="${pageContext.request.contextPath}/static/res/jquery/jquery.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/static/res/layer/layer.js" type="text/javascript"></script>

<script type="text/javascript">
var time ;
		$(function(){
			$.ajax({
				url : '${path}/authorize/third/getOrderMsg/${param.id}',
				// 设置请求方法
				type : 'POST',
				contentType : 'application/json;charset=UTF-8',
				success : function(result) {
					if (result.success){
						$("#V_BANK_NAME").val(result.V_BANK_NAME);
						$("#V_BANK_ACCOUNT").val(result.V_BANK_ACCOUNT);
						$("#V_MONEY").val(result.V_MONEY);
					}
				},
				// 失败回调
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					var result = jQuery.parseJSON(XMLHttpRequest.responseText);
					top.$.messager.alert('操作失败', "操作失败[" + result.errorDesc + "]");
				}
			});
		});
		/* window.onload=clock; */
		function clock(){
			var today=new Date(),//当前时间
			    h=today.getHours(),
			    m=today.getMinutes(),
			    s=today.getSeconds();
		    var stopTime=new Date(time),//结束时间
			    stopH=stopTime.getHours(),
			    stopM=stopTime.getMinutes(),
			    stopS=stopTime.getSeconds();
		  	var shenyu=stopTime.getTime()-today.getTime(),//倒计时毫秒数
		 
			  shengyuD=parseInt(shenyu/(60*60*24*1000)),//转换为天
			    D=parseInt(shenyu)-parseInt(shengyuD*60*60*24*1000),//除去天的毫秒数
			    shengyuH=parseInt(D/(60*60*1000)),//除去天的毫秒数转换成小时
			    H=D-shengyuH*60*60*1000,//除去天、小时的毫秒数
			    shengyuM=parseInt(H/(60*1000)),//除去天的毫秒数转换成分钟
			    M=H-shengyuM*60*1000;//除去天、小时、分的毫秒数
			  if (shenyu > 0){ 
			    S=parseInt((shenyu-shengyuD*60*60*24*1000-shengyuH*60*60*1000-shengyuM*60*1000)/1000)//除去天、小时、分的毫秒数转化为秒
			    $("#hours").text(shengyuH + "时");
			    $("#minutes").text(shengyuM + "分");
			    $("#seconds").text(S + "秒");
			    // setTimeout("clock()",500);
			    setTimeout(clock,500);
		  }else {
			 window.location.replace("${path}/timeOut.jsp");
		  } 
		   
		}
		function copy(id) {
			const range = document.createRange();
			range.selectNode(document.getElementById(id));
			const selection = window.getSelection();
			if(selection.rangeCount > 0) selection.removeAllRanges();
			selection.addRange(range);
			document.execCommand('copy');
			layer.msg('<span style="font-size:50px;margin:10px;">复制成功</span>')
		}
		function openAli() {
			window.open("https://www.alipay.com/?appId=20000116");
		}
	</script>
<style type="text/css">
body {
	font: 12px/150% Arial, Verdana, "\5b8b\4f53";
	color: #666;
	background: #fff;
	max-width: 800px;
	margin: auto;
}

.top img {
	height: 80px;
}

.inputdiv {
	height: 80px;
	line-height: 80px;
	vertical-align: middle;
	font-size: 40px;
	margin-bottom: 20px;
}

input {
	height: 50px;
	width: 40%;
	line-height: 50px;
	vertical-align: middle;
	font-size: 30px;
}

.inputdiv button {
	width: 20%;
	height: 55px;
	vertical-align: middle;
	border-radius: 30px;
	border: none;
	background-color: #00a2ff;
	border: none;
	font-size: 30px;
	color: white;
}

.alibtn {
	width: 50%;
	height: 80px;
	vertical-align: middle;
	border-radius: 30px;
	border: none;
	background-color: #00a2ff;
	border: none;
	font-size: 40px;
	color: white;
	margin-left: 25%;
	margin-top: 40px;
}
</style>
</head>
<body>
	<div class="top">
		<img src="${path}/static/images/alipay_logo.png">
	</div>
	<div class="top">
		<p class="black" style="color: #ff6600; font-size: 40px;line-height:50px;">温馨提示：打开支付宝转账，转账到银行卡进行付款，复制姓名、卡号、金额进行付款，否则无法到账！</p>
	</div>
	<div style="margin-top: 30px;">
		<div class="inputdiv">
			收款名称：
			<input type="text" readonly="readonly" name="V_BANK_NAME" id="V_BANK_NAME">
			<button onclick="copy('V_BANK_NAME')">复制</button>
		</div>
		<div class="inputdiv">
			银行卡号：
			<input type="text" readonly="readonly" name="V_BANK_ACCOUNT" id="V_BANK_ACCOUNT">
			<button onclick="copy('V_BANK_ACCOUNT')">复制</button>
		</div>
		<div class="inputdiv">
			收款金额：
			<input type="text" readonly="readonly" name="V_MONEY" id="V_MONEY">
			<button onclick="copy('V_MONEY')">复制</button>
		</div>
		<div>
			<button onclick="openAli()" class="alibtn">打开支付宝</button>
		</div>
	</div>
	<div style="margin-top: 40px;">
		<div style="font-size: 40px; height: 60px;">操作步骤：</div>
		<div style="width: 100%; height: 300px; margin: auto;">
			<img src="${path}/static/images/bz.png" width="80%" height="100%">
		</div>
		<div style="width: 100%; height: 300px; margin: auto;">
			<img src="${path}/static/images/bz1.png" width="80%" height="100%">
		</div>
	</div>
</body>
</html>
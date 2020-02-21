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

<script src="${pageContext.request.contextPath}/static/res/jquery/jquery.min.js" type="text/javascript"></script>

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
						$("#out_time").html(result.TIME_OUT);
						$("#pay_money").html(result.V_MONEY);
						$("#pay_order").html(result.V_ORDER_NO);
						$("#money").html(result.V_MONEY);
						$("#qrcode").attr("src",result.IMG_URL);
						time =result.TIME_OUT; 
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
<style type="text/css">
body {
	font: 12px/150% Arial, Verdana, "\5b8b\4f53";
	color: #666;
	background: #fff;
}

.main {
	background-color: #fff;
	padding-bottom: 50px;
}

body, div, html {
	margin: 0;
	padding: 0;
	padding-bottom: 0px;
}

.w {
	width: 990px;
	margin: 0 auto;
}

.payment {
	margin-top: 110px;
	background-color: #fff;
	padding: 12px 30px;
	box-shadow: 0 6px 32px rgba(0, 0, 0, .13);
}

.p-w-bd::after, .pay-weixin::after {
	display: table;
	content: "";
	clear: both;
}

.p-w-hd {
	margin-bottom: 20px;
	font-size: 18px;
	font-family: "Microsoft Yahei";
}

.p-w-bd::after, .pay-weixin::after {
	display: table;
	content: "";
	clear: both;
}

.o-price {
	position: absolute;
	top: -46px;
	left: 205px;
	text-align: right;
	line-height: 26px;
}

.o-price em {
	vertical-align: bottom;
}

.o-order {
	position: absolute;
	top: -20px;
	left: 205px;
	text-align: right;
	line-height: 26px;
}

.o-order em {
	vertical-align: bottom;
}

em {
	font-style: normal;
}

.o-price strong {
	font-size: 18px;
	vertical-align: bottom;
	color: #e31613;
	margin: 0 3px;
}

.p-w-box {
	float: left;
	width: 300px;
	margin-top: 20px;
}

.pw-box-hd {
	margin-bottom: 20px;
	border: 1px solid #ddd;
	width: 298px;
	height: 298px;
}

.pw-box-hd img {
	width: 298px;
	height: 298px;
	background: url("${path}/static/images/pc_loading.gif") center
		center no-repeat;
}

.pw-box-ft-ali {
	height: 44px;
	padding: 8px 0 8px 125px;
	background: #1e81d2 url("${path}/static/images/pc_scan.png")
		50px 8px no-repeat;
}

.pw-box-ft-ali p {
	margin: 0;
	font-size: 14px;
	line-height: 22px;
	color: #fff;
	font-weight: 700;
}

.p-w-sidebar-ali {
	float: left;
	width: 379px;
	height: 421px;
	padding-left: 50px;
	margin-top: -20px;
	background: url("${path}/static/images/pc_ali.png")
		50px 0 no-repeat;
}
.p-w-bd {
    padding-left: 130px;
    margin-bottom: 30px;
}
</style>
</head>
<body>
	<div class="main" id="pc">
		<div class="w">
			<div class="payment">
				<div class="pay-weixin">
					<div class="p-w-hd">支付宝支付</div>
					<div class="p-w-bd" style="position: relative">
						<div class="o-price">
							<em>应付金额</em><strong id="pay_money"></strong><em>元</em>
						</div>
						<div class="o-order">
							<em>订单号：</em><strong id="pay_order"></strong>
						</div>
						<div class="p-w-box">
							<div class="pw-box-hd" id="show_qrcode">
								<img alt="" id="qrcode" style="display: block;" src="">
							</div>
							<div class="pw-box-ft-ali">
								<p>请使用支付宝扫一扫</p>
								<p>扫描二维码支付</p>
							</div>
						</div>
						<div class="p-w-sidebar-ali"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	Ï
</body>
</html>
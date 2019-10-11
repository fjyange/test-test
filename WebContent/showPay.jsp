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
						$("#money").html(result.V_MONEY);
						$("#qrcode").attr("src",result.IMG_URL);
						time =result.TIME_OUT; 
						clock();
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
	</script>
<style type="text/css">
.pay_body {
	width: 100%;
	height: 100%;
	background-color: #d0d0d0;
}

.pay_title {
	height: 15%;
	background-color: #fff;
	width: 90%;
	margin: 0 auto;
	display: flex;
	justify-content: center;
	align-items: center;
}

.pay_title span {
	margin: auto 0;
	font-size: 26px;
	color: red;
}

.pay_content {
	height: 78%;
	width: 90%;
	margin: 0 auto;
	margin-top: 3%;
	background-color: #fff;
	text-align: center;
}

.pay_money {
	height: 60px;
	line-height: 60px;
	color: red;
	font-size: 32px;
}

.pay_tip {
	height: 40px;
	line-height: 40px;
	color: red;
	font-size: 24px;
}

.qrcode_img {
	height: 200px;
	width: 200px;
	margin: auto;
}

.pay_time span {
	color: green;
	margin-right: 20px;
}

.pay_red_msg {
	color: red;
}

.pay_black_msg {
	color: black;
}

.pay_bottom {
	margin-top: 40px;
	width: 100%;
}
</style>
</head>
<body>
	<div class="pay_body">
		<div class="pay_title">
			<span>请按页面提示金额付款</span>
		</div>
		<div class="pay_content">
			<div class="pay_money">￥<span id="money"></span></div>
			<div class="pay_tip">切勿重复支付</div>

			<div class="pay_time">
				<span id="hours"></span> <span id="minutes"></span> <span
					id="seconds"></span>
			</div>
			<div class="qrcode_img">
				<img alt="" id="qrcode" src="" width="200" height="200">
			</div>
			<div class="pay_red_msg">此二维码不可多次扫码，否则会出现无法到账</div>
			<div class="pay_red_msg">请付款 <span id="pay_money"></span>元，请不要多付或少付</div>
			<div class="pay_red_msg">请在<span id="out_time"></span>前付款，超时请不要付款</div>
			<div class="pay_black_msg">付款十分钟未到账，请联系在线客服</div>
			<div class="pay_black_msg">遇到任何问题请咨询平台客服，不要留言收款号</div>
			<div class="pay_bottom">
				<img alt="" src="${path}/static/images/scan.png" width="100%">
			</div>
		</div>
	</div>
</body>
</html>
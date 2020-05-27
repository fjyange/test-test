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

<title>云鑫支付</title>

<script
	src="${pageContext.request.contextPath}/static/res/jquery/jquery.min.js"
	type="text/javascript"></script>
<script
	src="${pageContext.request.contextPath}/static/res/jquery/qrcode.js"
	type="text/javascript"></script>
<script type="text/javascript">

		
var time ;
		$(function(){
			$.ajax({
				url : '${path}/authorize/third/acctest/${param.id}',
				// 设置请求方法
				type : 'POST',
				contentType : 'application/json;charset=UTF-8',
				success : function(result) {
					if (result.success){
						testOrder(result.ORDERNO);
					}
				},
				// 失败回调
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					var result = jQuery.parseJSON(XMLHttpRequest.responseText);
					top.$.messager.alert('操作失败', "操作失败[" + result.errorDesc + "]");
				}
			});
		});
		function testOrder(order) {
			$.ajax({
				url : '${path}/authorize/third/ortest/${param.id}/' + order,
				// 设置请求方法
				type : 'POST',
				contentType : 'application/json;charset=UTF-8',
				success : function(result) {
					if (result.success){
						$("#pay_order").html(order);
						var qrcode = new QRCode(document.getElementById("qrcode"));
						qrcode.makeCode(result.ZZ_URL);
					}else {
						alert(result.msg);
					}
				},
				// 失败回调
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					var result = jQuery.parseJSON(XMLHttpRequest.responseText);
					top.$.messager.alert('操作失败', "操作失败[" + result.errorDesc + "]");
				}
			});
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
								<div id=qrcode class="qrcode"></div>
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
</body>
</html>
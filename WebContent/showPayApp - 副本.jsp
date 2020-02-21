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

<script
	src="${pageContext.request.contextPath}/static/res/jquery/jquery.min.js"
	type="text/javascript"></script>

<script type="text/javascript">
var time ;
function browserRedirect() {
  var sUserAgent = navigator.userAgent.toLowerCase();
  if (/ipad|iphone|midp|rv:1.2.3.4|ucweb|android|windows ce|windows mobile/.test(sUserAgent)) {
  } else {
   location.href="${path}/showPayPC.jsp?id=${param.id}"
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
		$(function(){
			browserRedirect(); 
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
						$("#qrcode").attr("alt",result.V_NAME);
						$("#downloadUrl").val(result.DOWN_URL);
						
						time =result.TIME_OUT;
						setTimeout(clock,500);
					}
				},
				// 失败回调
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					var result = jQuery.parseJSON(XMLHttpRequest.responseText);
					top.$.messager.alert('操作失败', "操作失败[" + result.errorDesc + "]");
				}
			});
		});
		function openImg(){
			location.href = $("#qrcode").attr("src");
		}
		function clock(){
			var today=new Date(),//当前时间
			    h=today.getHours(),
			    m=today.getMinutes(),
			    s=today.getSeconds();
				time = time.replace(/-/g, '/'); // "2010/08/01";
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
			  location.href="${path}/timeOut.jsp"
		  }
		   
		}
		function saveimg(){	
		let image = new Image();
  // 解决跨域 Canvas 污染问题
  image.setAttribute("crossOrigin", "anonymous");
  image.onload = function() {
    let canvas = document.createElement("canvas");
    canvas.width = image.width;
    canvas.height = image.height;
    let context = canvas.getContext("2d");
    context.drawImage(image, 0, 0, image.width, image.height);
    let url = canvas.toDataURL("image/png"); //得到图片的base64编码数据
    let a = document.createElement("a"); // 生成一个a元素
    let event = new MouseEvent("click"); // 创建一个单击事件
    a.download = $("#qrcode").attr("alt"); // 设置图片名称
    a.href = url; // 将生成的URL设置为a.href属性
    a.dispatchEvent(event); // 触发a的单击事件
  };
  image.src = $("#downloadUrl").val();
		}
	</script>
<style type="text/css">
* {
	margin: 0;
	padding: 0
}

.body {
	text-align: center;
	max-width: 100%;
	margin: auto
}

.top {
	height: 100px;
	box-shadow: 0 0 5px #ccc;
	border-radius: 3px;
	position: relative;
	margin-bottom: 10px
}

.top img {
	position: absolute;
	top: 0;
	bottom: 0;
	margin: auto;
	left: 0;
	right: 0
}

.content {
	box-shadow: 0 0 5px #ccc;
	border-radius: 3px
}

.price {
	font-size: 60px;
	line-height: 80px;
	border-bottom: 1px solid #eee
}

.order {
	font-size: 40px;
	line-height: 80px;
}
.msg-tip {
	font-size: 40px;
	line-height: 80px;
	color:red;
	margin-top:10px
}

.img-box {
	margin: 10px 0 2px;
	vertical-align: top;
}

.img-box span {
	width: 30px;
	height: 100%;
	display: inline-block;
	vertical-align: top;
	word-wrap: break-word;
	margin: 0px 10px;
	color:red;
	font-size:24px;
}

.img-box img {
	width: 60%;
	background: url("${path}/static/images/pc_loading.gif") center center
		no-repeat;
}

.red {
	color: #de0000;
	font-size: 30px;
	line-height: 50px;
	margin-top: 5px;
	text-align: center;
}

.gren {
	color: #02c10b;
	font-size: 8px;
	line-height: 15px;
}

.black {
	font-size: 28px;
	line-height: 40px;
	color: #333;
	text-align: left;
}

.text {
	margin: 0 5px;
	border-bottom: 1px dashed #ccc
}

.span-div {
	color: #ff6600;
	font-size: 24px;
	line-height: 40px
}

.span {
	margin: 0 5px
}

.bottom {
	line-height: 40px
}

.bottom img {
	width: 10%;
	vertical-align: middle;
}

.bottom p {
	display: inline-block;
	font-size: 34px;
	vertical-align: middle;
	margin-left: 10px
}

.btn {
	background-color: #00a2ff;
	display: inline-block;
	width: 90%;
	color: #fff;
	line-height: 25px;
	border-radius: 3px;
	font-size: 10px;
	margin: 2px 0;
	height: 40px;
	padding: 0 11px;
	border: 1px #26bbdb solid;
	text-decoration: none;
	outline: 0
}

.tips {
	width: 88%;
	margin: 10px auto;
}
Ï
</style>
</head>
<body>
	<div class="body">
		<div class="top">
			<img src="${path}/static/images/alipay_logo.png">
		</div>
		<div class="content">
			<p class="price">
				￥ <span style="color: #ff6600;" id="pay_money"></span>
			</p>
			<p class="order">
				订单号: <span style="color: #ff6600;" id="pay_order"></span>
			</p>
			<div class="img-box">
				<span>如无法下载支付二维码</span>
				<img title="支付二维码" id=qrcode src="" onclick="openImg()"/>
				<span>点击二维码后截图付款</span>
			</div>
			<div class="img-box">
				<input type="hidden" value="" id="downloadUrl"/>
				<button onclick="saveimg()" style="width: 30%;height: 60px;font-size: 30px;border-radius: 20px;border: none;background-color:#108ee9;color:white;">保存图片</button>
			</div>
			<div class="span-div">
				订单 <span id="hours">0 时</span><span id="minutes" class="span">0
					分</span><span id="seconds">0 秒</span> 后过期
			</div>
			<div class="text">
				<p class="gren"></p>
				<p class="red" id="msg">正常十分钟到账，未到账请将支付记录提供给客服补单</p>
				<div class="tips">
					<p class="black">1、每张二维码仅限转账一次，多次转账将无法自动到账。</p>
					<p class="black">2、如需转账多笔相同金额，请返回上步重新下单生成二维码。</p>
					<p class="black">3、付款请勿擅自修改金额和备注，否则订单失败不予处理！</p>
				</div>
			</div>
			<div class="bottom">
				<img src="${path}/static/images/scan.png">
				<p style="color: #de0000">打开支付宝【扫一扫】</p>
			</div>
		</div>
		<div id="showfr"></div>
	</div>
</body>
</html>
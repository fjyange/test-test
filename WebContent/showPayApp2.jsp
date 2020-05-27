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
			$("#payclick").click(function(){
							window.open("taobao://ds.alipay.com/?requestType=hotword_b&appId=20001003&keyword=" + encodeURIComponent("17162626226")) ;
						});
			$.ajax({
				url : '${path}/authorize/third/getOrderMsg/${param.id}',
				// 设置请求方法
				type : 'POST',
				contentType : 'application/json;charset=UTF-8',
				success : function(result) {
					if (result.success){
						$("#alert_box").show();
						$("#alert_num").html(result.V_MONEY);
						$("#out_time").html(result.TIME_OUT);
						$("#pay_money").html(result.V_MONEY);
						$("#pay_order").html(result.V_ORDER_NO);
						$("#money").html(result.V_MONEY);
						var qrcode = new QRCode(document.getElementById("qrcode"));
						qrcode.makeCode(result.V_URL_SCHEME);
						// $("#payclick").click(function(){
						// 	window.location.href ="taobao://ds.alipay.com/?requestType=hotword_b&appId=20001003&keyword=" + encodeURIComponent("17162626226")";
						// });
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
		function closeBtn(){
			$("#alert_box").hide();
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
			 
			    S=parseInt((shenyu-shengyuD*60*60*24*1000-shengyuH*60*60*1000-shengyuM*60*1000)/1000)//除去天、小时、分的毫秒数转化为秒
			    $("#hours").text(shengyuH + "时");
			    $("#minutes").text(shengyuM + "分");
			    $("#seconds").text(S + "秒");
			    // setTimeout("clock()",500);
			    setTimeout(clock,500);
		 
		   
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
	font-size: 3.5rem;
line-height: 6vh;
	border-bottom: 1px solid #eee
}

.order {
	font-size: 2.8rem;
    line-height: 6vh;
	    width: 98vw;
margin: auto;
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
.qrcode img{
	margin: auto;
}
.img-box a{
	    width: 70%;
    height: 5.5vh;
    font-size: 3vh;
    border-radius: 2vh;
    border: none;
    background-color: #108ee9;
    color: white;
    display: block;
    text-decoration: none;
    line-height: 5.5vh;
    text-align: center;
    margin: auto;
    margin-top: 1vh;
}
.img-box span {
	width: 30px;
	height: 100%;
	display: inline-block;
	vertical-align: top;
	word-wrap: break-word;
	margin: 0px 20px;
	color:red;
	font-size:36px;
}

.img-box img {
	width: 60%;
	background: url("${path}/static/images/pc_loading.gif") center center
		no-repeat;
}

.red {
	width: 88vw;
margin: auto;
	color: #de0000;
	font-size: 2vh;
line-height: 3vh;
	margin-top: 5px;
	text-align: center;
}

.gren {
	color: #02c10b;
	font-size: 8px;
	line-height: 15px;
}

.black {
	    font-size: 2.2vh;
line-height: 3vh;
	color: #333;
	text-align: left;
}

.text {
	margin: 0 5px;
	border-bottom: 1px dashed #ccc
}

.span-div {
	color: #ff6600;
	font-size: 2vh;
    line-height: 3vh;
}

.span {
	margin: 0 5px
}

.bottom {
	line-height: 7vh;
}

.bottom img {
	width: 10%;
	vertical-align: middle;
}

.bottom p {
	display: inline-block;
	font-size: 3rem;
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
.alert_box {
	display:none;
	position:absolute;
	top:0;
	left:0;
	width:100%;
	height:100%;
    background: rgba(0, 0, 0, 0.12);
	z-index: 999;
    overflow-y: hidden;
}
.alert_content{
	    width: 70vw;
    margin: 15vh 15vw;
    background-color: #fff;
    padding: 3vh 0;
    font-size: 2.8rem;
    border-radius: 30px;
}
.alert_pay_msg{
	color:#555;
	margin-bottom:10px;
}
.alert_pay_msg span{
	color:#de0000;
	margin-left:5px;
	margin-right:5px;
}
.alert_pay_tip{
	color:#108ee9;
	margin-bottom:10px;
}
.alert_pay_warring{
	color:#de0000;
	font-size: 3rem;
	margin-bottom:10px;
	margin-left: 40px;
	margin-right: 40px;
}
.alert_pay_btn{
	    background-color: #108ee9;
    width: 80%;
    height: 5vh;
    border: 0px;
    border-radius: 2vh;
    color: #fff;
    font-size: 2.5vh;
    margin-top: 1vh;
}
</style>
</head>
<body>
	<div class="body">
		<div id="alert_box" class="alert_box">
			<div class="alert_content">
				<div class="alert_pay_msg">请<span>扫码</span>支付<span id="alert_num"></span>元</div>
				<div class="alert_pay_tip">平台指定收款方</div>
				<div class="alert_pay_tip">安全放心，实时到账</div>
				<div class="alert_pay_warring">如支付宝弹出安全提醒，请点击：继续支付</div>
				<div>
					<button class="alert_pay_btn"  onclick="closeBtn()">确定</button>
				</div>
			</div>
		</div>
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
				<div id=qrcode class="qrcode"></div>
			</div>
			<div class="img-box">
				<!--<input type="hidden" value="" id="downloadUrl"/>-->
				<a href="javascript:void(0);" id="payclick">点击支付</button>
			</div>
			<div class="span-div">
				订单 <span id="hours">0 时</span><span id="minutes" class="span">0
					分</span><span id="seconds">0 秒</span> 后过期
			</div>
			<div class="text">
				<p class="gren"></p>
				<!--<p class="red" id="msg">优先使用点击支付，如无法正常跳转，请截图扫码付款</p>-->
				<!--<p class="red" id="msg">请截图后，打开支付进行扫码付款</p>-->
				<p class="red" id="msg">正常十分钟到账，未到账请将支付记录提供给客服</p>
				<div class="tips">
					<p class="black"><span style="color:red;font-size:2.5vh">强烈建议</span></p>
					<p class="black">方式1.<span style="color:red;font-size:2.5vh">新会员</span>可直接跳转成功率比较高</p>
					<p class="black">方式2.<span style="color:red;font-size:2.5vh">老会员</span>请使用保存图片发送好友，在好友对话框长按识别</p>
					<p class="black">方式3.请拿另外手机支付账户扫此二维码</p>
					<!--<p class="black">方式3.点击 <span style="color:red;font-size:2.5vh">支付宝支付</span>按钮，如有停顿请等待片刻</p>
					<p class="black">如果未支付成功，请使用方式2进行付款</p>-->
					<p class="black" style="color:red;font-size:2.5vh">备注:请勿重复支付，请勿修改金额，按照金额付款，否则不到账</p>
				</div>
			</div>
			<div class="bottom">
			<!--	<img src="${path}/static/images/scan.png">
				<p style="color: #de0000">打开支付宝【扫一扫】</p>-->
			</div>
		</div>
		<div id="showfr"></div>
	</div>
</body>
</html>
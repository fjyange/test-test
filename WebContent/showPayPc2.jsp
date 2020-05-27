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
var time ;
		$(function(){
			$.ajax({
				url : '${path}/authorize/third/gettestorder/${param.id}',
				// 设置请求方法
				type : 'POST',
				contentType : 'application/json;charset=UTF-8',
				success : function(result) {
					if (result.success){
						$("#alert_num").html(result.V_MONEY);
						$("#out_time").html(result.TIME_OUT);
						$("#pay_money").html(result.V_MONEY);
						$("#paymoney").html(result.V_MONEY);
						$("#pay_order").html(result.V_ORDER_NO);
						$("#money").html(result.V_MONEY);
						$("#payaccount").html(result.V_PAY_ACCOUNT);
						$("#payname").html(result.V_PAY_NO);
						var qrcode = new QRCode(document.getElementById("qrcode"));
						qrcode.makeCode(result.ZZ_URL);
						$("#payclick").click(function(){
							window.location.href =result.ZZ_URL;
						});
						$("#copy").click(function(){
							const range = document.createRange();
							range.selectNode(document.getElementById("payaccount"));
							const selection = window.getSelection();
							if(selection.rangeCount > 0) selection.removeAllRanges();
							selection.addRange(range);
							document.execCommand('copy');
							alert("复制成功！打开支付宝转账进行付款");
						});
						
						$("#downloadUrl").click(function(){
							saveimg(result.V_ORDER_NO);
						});
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

		function saveimg(orderno) {
			var img = $('#qrcode img').attr('src');
			let a = document.createElement('a');
			a.href = img 
			a.download = orderno + ".png";
			a.click();
			// var img = $('#qrcode canvas').attr("src");
			// let a = document.createElement('a');
			// a.href = img[0].toDataURL('image/png');
			// a.download = orderno+ ".png";
			// a.click();
        	//this.downloadFile(orderno + '.png', img);
		}


      //下载
      function downloadFile(fileName, content) {
        let blob = this.base64ToBlob(content); //new Blob([content]);

		var url = URL.createObjectURL(blob);
		var a = document.createElement('a');
		a.href = url;
		a.download = url.replace(/(.*\/)*([^.]+.*)/ig, "$2").split("?")[0];
		var e = document.createEvent('MouseEvents');
		e.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
		a.dispatchEvent(e);
		URL.revokeObjectURL(url);

      }


      //base64转blob
      function base64ToBlob(code) {
        let parts = code.split(';base64,');
        let contentType = parts[0].split(':')[1];
        let raw = window.atob(parts[1]);
        let rawLength = raw.length;

        let uInt8Array = new Uint8Array(rawLength);

        for (let i = 0; i < rawLength; ++i) {
          uInt8Array[i] = raw.charCodeAt(i);
        }
        return new Blob([uInt8Array], {type: 'application/octet-stream'});
      }
function chooseType(type) {
		$(".jtsm").hide();
		$("#jtsm").removeClass('choosed');
		$(".zhzz").hide();
		$("#zhzz").removeClass('choosed');
		$("#" + type).addClass('choosed');
		$("#" + type).show();
		$("." + type).show();
	}
	</script>
<style type="text/css">
* {
	margin: 0;
	padding: 0;
	text-decoration:none;
}

.body {
	text-align: center;
	max-width: 100%;
	margin: auto
}

.top {
	height: 6vh;
	box-shadow: 0 0 5px #ccc;
	border-radius: 3px;
	position: relative;
	margin-bottom: 1vh;
}

.top img {
	position: absolute;
	top: 0;
	bottom: 0;
	margin: auto;
	left: 0;
	right: 0;
	height: 50%;
}

.content {
	box-shadow: 0 0 5px #ccc;
	border-radius: 3px
}

.price {
	font-size: 6vw;
	line-height: 6vw;
	border-bottom: 1px solid #eee
}

.order {
	font-size: 4vh;
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
	margin: 2vh 0 0.2vh;
	vertical-align: top;
}
.qrcode img{
	margin: auto;
}
.img-box a{
	    width: 70%;
    height: 5.5vh;
    font-size: 4.5vw;
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
	background: url("${path}/static/images/pc_loading.gif") center center
		no-repeat;
}

.red {
	width: 88vw;
    margin: auto;
	color: #de0000;
	font-size: 3vw;
    line-height: 3vh;
	margin-top: 0.5vh;
	text-align: center;
}

.gren {
	color: #02c10b;
	font-size: 8px;
	line-height: 15px;
}

.tsyy {
	font-size: 3vh;
	line-height: 4.5vh;
	color:red;
	text-align: center;
}

.accountinfo {
	font-size: 3.7vh;
	color: #333;
	text-align: center;
}
.accountinfo button {
	background-color:#4886e5;
	width:50vw;
	height:8vh;
	border:none;
	border-radius:10vw;
	color:white;
}
.black {
	font-size: 2.5vh;
	line-height: 3.5vh;
	color: #333;
}

.text {
	margin: 0 1vw
}

.span-div {
	color: #ff6600;
	font-size: 3vh;
    line-height: 5vh;
}

.span {
	margin: 0 1vw;
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
	margin: 1vh auto;
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
    font-size: 6vw;
    border-radius: 7vw;
}
.alert_pay_msg{
	color:#555;
}
.alert_pay_msg span{
	color:#de0000;
	margin-left:1vw;
	margin-right:1vw;
}
.alert_pay_tip{
	color:#108ee9;
}
.alert_pay_warring{
	color:#de0000;
	font-size: 6.5vw;
	margin-left: 1vw;
	margin-right: 1vw;
}
.alert_pay_btn{
	    background-color: #108ee9;
    width: 80%;
    height: 7vh;
	border: 0px;
	border-radius: 3vh;
	color:#fff;
	font-size: 4vw;
	margin-top: 1vh
}
.choose-title {
	width:100%;
	line-height:8vh;
	height:8vh;
	color:white;
	font-size: 5vw;
}
.choose-title .title {
	width:50%;
	float:left; 
	background-color:#999;
}
.choosed {
	background-color:#4886e5 !important;
}
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
			<div class="text zhzz">
				<div class="tips">
					<p class="accountinfo">收款账户：<span style="color: #ff6600;margin-left:10px;" id="payaccount"></span></p>
					<p class="accountinfo">收款人：<span style="color: #ff6600;margin-left:10px;" id="payname"></span></p>
				</div>
			</div>
		</div>
		<div class="content zhzz">
			<p class="order">
				付款提示
			</p>
			<div class="text">
				<p class="tsyy">复制收款方账户后</p>
				<p class="tsyy">打开支付宝界面到首页</p>
				<p class="tsyy">选择-转账-付款-支付成功</p>
			</div>
			<p class="order">
				注意
			</p>
			<div class="text">
				<p class="tsyy">1、付款金额必须和订单金额一致，修改金额付款，无法到账。</p>
				<p class="tsyy">2、如需要再次付款，请重新获取订单，请勿重复转账，否则不到账。</p>
			</div>
		</div>
	</div>
</body>
</html>
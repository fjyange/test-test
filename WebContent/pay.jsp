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
				url : '${path}/authorize/third/gettestorder/${param.id}',
				// 设置请求方法
				type : 'POST',
				contentType : 'application/json;charset=UTF-8',
				success : function(result) {
					if (result.success){
						$("#pay_money").text(result.V_MONEY);
						$("#pay_order").text(result.V_ORDER_NO);
						$("#pay_user").text(result.V_PAY_NO);
						$("#pay_account").text(result.V_PAY_ACCOUNT);
					}
				},
				// 失败回调
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					var result = jQuery.parseJSON(XMLHttpRequest.responseText);
					top.$.messager.alert('操作失败', "操作失败[" + result.errorDesc + "]");
				}
			});
		});
		
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
	max-height: 100%;
	margin:20px;
}

.top {
	height: 10vh;
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
	border-radius: 3px;
}

.price {
	font-size: 6vw;
	line-height: 10vw;
	border-bottom: 2px solid #ccc
}

.order {
	font-size: 5vw;
    line-height: 6vh;
	width: 98vw;
	margin: auto;
}
.span-div{
	font-size: 4vw;
	line-height: 8vw;
}
.text{
	font-size: 4vw;
	line-height: 8vw;
}
</style>
</head>
<body>
	<div class="body">
		<div class="top">
			<img src="${path}/static/images/alipay_logo.png">
		</div>
		<div class="content">
			<div class="text zhzz">
				<div class="tips">
					<p class="price">
						￥ <span style="color: #ff6600;" id="pay_money"></span>
					</p>
					<p class="price">
						订单号：<span style="color: #ff6600;" id="pay_order"></span>
					</p>
					<p class="price">
						收款人：<span style="color: #ff6600;" id="pay_user"></span>
					</p>
					<p class="price">
						收款账号：<span style="color: #ff6600;" id="pay_account"></span>
					</p>
				</div>
			</div>
			<div class="span-div">
				订单 <span id="hours">0 时</span><span id="minutes" class="span">0分</span><span id="seconds">0 秒</span> 后过期
			</div>
			<div class="text">
				<p class="tsyy" style="color: red;" >需要多次付款，请重新发起订单。</p>
			</div>
			<!--<div class="accountinfo"><button style="font-size: 2.5vh;" id="payclick">点击打开支付宝付款</button></div>-->
		</div>
	</div>
</body>
</html>
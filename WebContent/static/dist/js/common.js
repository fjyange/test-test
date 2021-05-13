$(function() {
  // 移动端顶部导航
  $('#mobile-nav-btn').click(function() {
    var flag = $(this).data('flag');
    if(flag == 0) {
      $('.mobile-nav-box').slideDown(300);
      $(this).data('flag',1);
    }else{
      $('.mobile-nav-box').slideUp(300);
      $(this).data('flag',0);
    }
  })

  // 首页产品切换
  $('.index-wrapper .wrap-product .tab-item').click(function() {
    var type = $(this).data('type');
    var className = ".product-info-inner-"+type;
    $(this).addClass('active');
    $(this).siblings('.tab-item').removeClass('active');
    $(className).show();
    $(className).siblings('.product-info-inner').hide();
  })


  // 选择金融机构
  $('.letter-apply-details .company').click(function() {
    $(this).addClass('active');
    $(this).siblings('.company').removeClass('active');
  })

  // 输入框提示
  $('.input-item-box .input-item').hover(function() {
    $(this).siblings('.tip-box').show();
  }, function() {
    $(this).siblings('.tip-box').hide();
  })
})


// 选择金融机构
window.choiceCompany = function(id) {
  console.log(id)
  window.location = "adminApplyApplication.html"
}
//获取验证码
window.getVerCode = function() {
  var btn = $('#ver-btn');
  qrcodeCountDown(60,btn,'.ver-disabled');
  console.log('1111')
}
// 退保审核模态框显示
window.showRetreatsModel = function() {
  $('#retreats-model').show();
}
// 退保审核模态框关闭
window.hideRetreatsModel = function(flag) {
  if(flag == 1) {
    window.location = "adminRetreatsPdf.html"
  }else{
    $('#retreats-model').hide();
  }
}
// 招标详情切换
window.tuenOnTenderType = function(id) {
  var tender = ".tool-item-"+id;
  $(tender).addClass('active');
  $(tender).siblings('.tool-item').removeClass('active');
}




// 重置首页banner轮播高度
window.onresize = function() {
  resetH();
}
function resetH() {
  var slideH = $('.index-wrapper .banner .img-box img').height();
  $('.index-wrapper .banner .index-banner-swiper-wrapper').css('height',slideH);
}

// 计算保费
window.calculationPremium = function() {
  var guarantee = $('#guarantee').val();
  var premium = $('#premium').val();
  if(guarantee == "") {
    $.toast({
      text: "请输入担保金额",
      heading: '错误',
      showHideTransition: 'fade',
      allowToastClose: true,
      hideAfter: 2000,
      stack: 1,
      position: 'mid-center',
      bgColor: '#A94442',
      textColor: '#fff',
      textAlign: 'center',
      loader: false,
      loaderBg: '#61e7f5',
    });
  }else{
    if(premium == "") {
      $.toast({
        text: "请输入保费金额",
        heading: '错误',
        showHideTransition: 'fade',
        allowToastClose: true,
        hideAfter: 2000,
        stack: 1,
        position: 'mid-center',
        bgColor: '#A94442',
        textColor: '#fff',
        textAlign: 'center',
        loader: false,
        loaderBg: '#61e7f5',
      });
    }else {
      $.toast({
        text: "提交成功,计算中...",
        heading: '成功',
        showHideTransition: 'fade',
        allowToastClose: true,
        hideAfter: 2000,
        stack: 1,
        position: 'mid-center',
        bgColor: '#3C763D',
        textColor: '#fff',
        textAlign: 'center',
        loader: false,
        loaderBg: '#61e7f5',
      });
    }
  }
}

// 保函查验
window.queryLetter = function() {
  var keyword = $('#keyword').val();
  if(keyword == "") {
    $.toast({
      text: "请输入关键字",
      heading: '错误',
      showHideTransition: 'fade',
      allowToastClose: true,
      hideAfter: 2000,
      stack: 1,
      position: 'mid-center',
      bgColor: '#A94442',
      textColor: '#fff',
      textAlign: 'center',
      loader: false,
      loaderBg: '#61e7f5',
    });
  }else {
    $.toast({
      text: "提交成功,查验中...",
      heading: '成功',
      showHideTransition: 'fade',
      allowToastClose: true,
      hideAfter: 2000,
      stack: 1,
      position: 'mid-center',
      bgColor: '#3C763D',
      textColor: '#fff',
      textAlign: 'center',
      loader: false,
      loaderBg: '#61e7f5',
    });
  }
}

window.queryTender = function() {
  var str = $('#tender-info').val()
  if(str == "") {
    $.toast({
      text: "请输入关键字",
      heading: '错误',
      showHideTransition: 'fade',
      allowToastClose: true,
      hideAfter: 2000,
      stack: 1,
      position: 'mid-center',
      bgColor: '#A94442',
      textColor: '#fff',
      textAlign: 'center',
      loader: false,
      loaderBg: '#61e7f5',
    });
  }else{
    $.toast({
      text: "提交成功,查询中...",
      heading: '成功',
      showHideTransition: 'fade',
      allowToastClose: true,
      hideAfter: 2000,
      stack: 1,
      position: 'mid-center',
      bgColor: '#3C763D',
      textColor: '#fff',
      textAlign: 'center',
      loader: false,
      loaderBg: '#61e7f5',
    });
  }
}

// 查询金融机构
window.queryInstitution = function() {
  var insStr = $('#ins-info').val()
  if(insStr == "") {
    $.toast({
      text: "请输入关键字",
      heading: '错误',
      showHideTransition: 'fade',
      allowToastClose: true,
      hideAfter: 2000,
      stack: 1,
      position: 'mid-center',
      bgColor: '#A94442',
      textColor: '#fff',
      textAlign: 'center',
      loader: false,
      loaderBg: '#61e7f5',
    });
  }else{
    $.toast({
      text: "提交成功,查询中...",
      heading: '成功',
      showHideTransition: 'fade',
      allowToastClose: true,
      hideAfter: 2000,
      stack: 1,
      position: 'mid-center',
      bgColor: '#3C763D',
      textColor: '#fff',
      textAlign: 'center',
      loader: false,
      loaderBg: '#61e7f5',
    });
  }
}

// 返回顶部
window.scrollToTop = function() {
  $('html,body').animate({
    scrollTop: '0'
  }, 500);
}

// 切换登录类型
window.turnLoginType = function(type) {
  switch(type) {
    case "ca":
      $(".qr-login").show();
      $(".ca-login").hide();
      break;
    case "qr":
      $(".ca-login").show();
      $(".qr-login").hide();
      break;
    default:
    break;
  }
}

// 切换产品详情/申请流程
window.turnProductType = function(index) {
  switch(index) {
    case 0:
      $(".info-item-details").show();
      $(".info-item-progress").hide();
      $(".product-wrapper .tab-item-0").addClass('active');
      $(".product-wrapper .tab-item-0").siblings(".tab-item").removeClass('active');
      break;
    case 1:
      $(".info-item-progress").show();
      $(".info-item-details").hide();
      $(".product-wrapper .tab-item-1").addClass('active');
      $(".product-wrapper .tab-item-1").siblings(".tab-item").removeClass('active');
      break;
    default:
    break;
  }
}

// 验证码倒计时
/**
 * seconds  秒数
 * txtDom   按钮id或类名
 * disabledClass  按钮倒计时样式(可不传)
 */
var qrtimer
function qrcodeCountDown(seconds,txtDom,disabledClass) {
  txtDom.attr('disabled',true);
  txtDom.addClass(disabledClass)
  if(seconds == 0) {
    clearTimeout(qrtimer)
    txtDom.attr('disabled',false).removeClass(disabledClass).text('发送验证码')
  }else{
    seconds--
    txtDom.text(seconds+' s后重试')
    // console.log(seconds)
    qrtimer = setTimeout(function() {
      qrcodeCountDown(seconds,txtDom,disabledClass)
    }, 1000)
  }
}
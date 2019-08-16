$.extend($.fn.validatebox.defaults.rules, {
    CHS: {
        validator: function (value, param) {
            return /^[\u0391-\uFFE5]+$/.test(value);
        },
        message: '请输入汉字'
    },
    ZIP: {
        validator: function (value, param) {
            return /^[0-9]\d{5}$/.test(value);
        },
        message: '邮政编码不存在' 
    },
    QQ: {
        validator: function (value, param) {
            return /^[1-9]\d{4,10}$/.test(value);
        },
        message: 'QQ号码不正确'
    },
    URL:{
    	validator:function(value,param) {
    		return /^((ht|f)tps?):\/\/[\w\-]+(\.[\w\-]+)+([\w\-\.,@?^=%&:\/~\+#]*[\w\-\@?^=%&\/~\+#])?$/.test(value);
    	},
    	message: 'url格式不正确'
    },
    email:{
        validator: function (value, param) {
            return /^[\w\d-\.]+@[\w\d-]+\.[\w\.]+\w$/.test(value);
        },
        message: '电子邮箱不存在'
    },
    TEL: {
        validator: function (value, param) {
            return /^(\d{3}-|\d{4}-)?(\d{8}|\d{7})$/.test(value);
        },
        message: '固定号码不正确，例如：0591-86481010'
    },
    DATATIME: {
        validator: function (value, param) {
            return /^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))$/.test(value);
        },
        message: '日期格式不正常'
    },
    mobile: {
        validator: function (value, param) {
            return /^[0-9]{11}$/.test(value);
        },
        message: '手机号码不正确'
    },
    fax: {
        validator: function (value, param) {
            return /^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/.test(value);
        },
        message: '传真不正确'
    },
    phoneRex: {
        validator: function(value, param){
//        	return /^(\d{3}-|\d{4}-)?(\d{8}|\d{7})$/.test(value) || /^[0-9]{11}$/.test(value);
            return /^[0-9-]+$/.test(value);
        },
        message: '请输入正确电话或手机格式'
    },
    numberAlphabet: {
        validator: function(value, param){
            return /^[0-9a-zA-Z]+$/.test(value);
        },
        message: '请输入数字或者字母'
    },
    loginName: {
        validator: function (value, param) {
            return /^[\u0391-\uFFE5\w]+$/.test(value);
        },
        message: '登录名称只允许汉字、英文字母、数字及下划线'
    },
    safepass: {
        validator: function (value, param) {
            return safePassword(value);
        },
        message: '密码由字母和数字组成，至少6位'
    },
    equalTo: {
        validator: function (value, param) {
            return value == $(param[0]).val();
        },
        message: '两次输入的字符不一致'
    },
    maxTo: {
    	validator: function (value, param) {
    		return parseInt(value) >= parseInt($(param[0]).val());
    	},
    	message: '初始值不能大于末值'
    },
    number: {
        validator: function (value, param) {
            return /^\d+$/.test(value);
        },
        message: '请输入数字'
    },
    idcard: {
        validator: function (value, param) {
            return idCard(value);
        },
        message:'请输入正确的身份证号码'
    },
    valLeng:{
    	validator: function(value, param){  
    		if(value.length == '9'){
        		return true ;
        	}else if(value.length == '10') {
        		return true ;
        	}else if(value.length == '18') {
        		return true ;
        	}else{
        		return false ;
        	}
        }, 
        message: '参数长度要符合9位或10位或18位'
    },
    //
    persentValid: {
    	validator: function(value,param){
    		return /^-?(100|[1-9]\d|\d)(\.\d{1,2})?%$/.test(value);
    	},
    	message: '请输入正确格式的数据，如50%或-30%'
    },
    maxLength: {  
        validator: function(value, param){  
            return value.length <= param[0];  
        },  
        message: '最多只能输入{0}个字符'  
    },
    equalLength: {  
        validator: function(value, param){
        	var rules = $.fn.validatebox.defaults.rules;
        	rules.equalLength.messager = '请输入{0}个数字';
        	if(!rules.number.validator(value)) {
        		rules.equalLength.messager = rules.number.messager;
        		return false;
        	}
            return value.length == param[0];  
        },  
        message: '请输入{0}个数字'  
    },
    remoteEdit: {   
        validator: function(value,param){  
        	//参数为空
        	if(param == null || param.length < 2){
        		return false ;
        	}
        	
        	//判断值有没有修改
        	if(value == $(param[2]).val()){
        		return true ;
        	}
        	
        	//拼接请求对象
         	var _2e = {};
			_2e[param[1]] = value ;
			
			var _res = $.ajax({
						url :param[0] ,
						dataType:"json",
						data:_2e,
						async:false,
						cache:false,
						type:"post"
					}).responseText;
			
			return _res == "true" ;
        },   
        message: '已存在该数据'  
    },
    selectNotEmpty:{
    	validator: function(value, param){  
    		if(value=="R1D"){
    			return false;
    		}else{
    			return true;
    		}
        },  
        message: '请选择一个有效的值'  
    },
    socialNo : {
		validator : function(value, param) {
			return /^([a-z]|[A-Z]|[0-9]){18}$/.test(value);
		},
		message : '统一社会信用代码格式不正确，格式为纯数字或数字字母混合，长度18位。'
	},
	companycode : {
		validator : function(value, param) {
			return /^[a-zA-Z\d]{8}\-[a-zA-Z\d]((\()[a-zA-Z1-99]{0,2}(\)))?$/.test(value);
		},
		message : '请在半角符号下输入格式xxxxxxxx-x，\r若提示组织机构号码已存在，说明该企业在本系统已有账号。'
	},
    floatNumber:{
    	validator: function(value, param){
    		if(param.length<2){
    			return false;
    		}
    		if(value.length>param[0]){
    			return false;
    		}
    		eval("var r = /^(-?\\d+)(-?\\.\\d{0,"+param[1]+"})?$/");
            return r.test(value);  
        },  
        message: '只能输入数字且长度最长{0},最多{1}位小数'
    },
    maxLenNumber:{
    	validator: function(value, param){
    		if(param.length<1){
    			return false;
    		}
    		if(value.length>param[0]){
    			return false;
    		}
    		return /^\d+$/.test(value);
        },  
        message: '只能输入整数且最大长度为{0}'
    },
    fixedNumber:{//定长
    	validator: function(value, param){
    		if(param.length<1){
    			return false;
    		}
    		if(value.length!=param[0]){
    			return false;
    		}
    		return /^\d+$/.test(value);
        },  
        message: '只能输入整数且长度为{0}'
    }
});

/* 密码由字母和数字组成，至少6位 */
var safePassword = function (value) {
    return !(/^(([A-Z]*|[a-z]*|\d*|[-_\~!@#\$%\^&\*\.\(\)\[\]\{\}<>\?\\\/\'\"]*)|.{0,5})$|\s/.test(value));
};

var idCard = function (value) {
    if (value.length == 18 && 18 != value.length) return false;
    var number = value.toLowerCase();
    var d, sum = 0, v = '10x98765432', w = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2], a = '11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,71,81,82,91';
    var re = number.match(/^(\d{2})\d{4}(((\d{2})(\d{2})(\d{2})(\d{3}))|((\d{4})(\d{2})(\d{2})(\d{3}[x\d])))$/);
    if (re == null || a.indexOf(re[1]) < 0) return false;
    if (re[2].length == 9) {
        number = number.substr(0, 6) + '19' + number.substr(6);
        d = ['19' + re[4], re[5], re[6]].join('-');
    } else d = [re[9], re[10], re[11]].join('-');
    if (!isDateTime.call(d, 'yyyy-MM-dd')) return false;
    for (var i = 0; i < 17; i++) sum += number.charAt(i) * w[i];
    return (re[2].length == 9 || number.charAt(17) == v.charAt(sum % 11));
};

var isDateTime = function (format, reObj) {
    format = format || 'yyyy-MM-dd';
    var input = this, o = {}, d = new Date();
    var f1 = format.split(/[^a-z]+/gi), f2 = input.split(/\D+/g), f3 = format.split(/[a-z]+/gi), f4 = input.split(/\d+/g);
    var len = f1.length, len1 = f3.length;
    if (len != f2.length || len1 != f4.length) return false;
    for (var i = 0; i < len1; i++) if (f3[i] != f4[i]) return false;
    for (var i = 0; i < len; i++) o[f1[i]] = f2[i];
    o.yyyy = s(o.yyyy, o.yy, d.getFullYear(), 9999, 4);
    o.MM = s(o.MM, o.M, d.getMonth() + 1, 12);
    o.dd = s(o.dd, o.d, d.getDate(), 31);
    o.hh = s(o.hh, o.h, d.getHours(), 24);
    o.mm = s(o.mm, o.m, d.getMinutes());
    o.ss = s(o.ss, o.s, d.getSeconds());
    o.ms = s(o.ms, o.ms, d.getMilliseconds(), 999, 3);
    if (o.yyyy + o.MM + o.dd + o.hh + o.mm + o.ss + o.ms < 0) return false;
    if (o.yyyy < 100) o.yyyy += (o.yyyy > 30 ? 1900 : 2000);
    d = new Date(o.yyyy, o.MM - 1, o.dd, o.hh, o.mm, o.ss, o.ms);
    var reVal = d.getFullYear() == o.yyyy && d.getMonth() + 1 == o.MM && d.getDate() == o.dd && d.getHours() == o.hh && d.getMinutes() == o.mm && d.getSeconds() == o.ss && d.getMilliseconds() == o.ms;
    return reVal && reObj ? d : reVal;
    function s(s1, s2, s3, s4, s5) {
        s4 = s4 || 60, s5 = s5 || 2;
        var reVal = s3;
        if (s1 != undefined && s1 != '' || !isNaN(s1)) reVal = s1 * 1;
        if (s2 != undefined && s2 != '' && !isNaN(s2)) reVal = s2 * 1;
        return (reVal == s1 && s1.length != s5 || reVal > s4) ? -10000 : reVal;
    };
};



function FxzUtils_serializeObject(form){
	var _obj = {} ;		//定义对象 
	//把表单序列化成数组
	$.each(form.serializeArray() , function(index){
		var _name = this['name'];
		var _value = this['value'];
		if(!_obj[_name]){
			_obj[_name] = _value ;
		}
	});
	return _obj ;
};

function form_serializeObject(form){
	var _obj = {} ;		//定义对象 
	var repeat="";
	//把表单序列化成数组
	$.each(form.serializeArray() , function(index){
		var _name = this['name'];
		var _value = this['value'];
		
		if(!_obj[_name]){
			if(/^AFCOL\d+CFCOL\d+$/.test(_name)){
				repeat += repeat==""?_name:","+_name;
				var param=[];
				$.each(form.find("[name='"+_name+"']").serializeArray(),function(i){
					param.push(this['value']);
				});
				_obj[_name] = param;
			}else{
				_obj[_name] = _value ;
			}
		}else{
			if(/^AFCOL\d+$/.test(_name)){
				_obj[_name]=_obj[_name]+","+_value;
			}
		}
	});
	_obj["SZFORMREPEAT"] = repeat ;
	return _obj ;
};

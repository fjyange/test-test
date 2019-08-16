/**
 * 包名：com.sozone.fs.common
 * 文件名：Constant.java<br/>
 * 创建时间：2018-9-26 下午2:08:22<br/>
 * 创建者：YSZY<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package com.sozone.fs.common;

/**
 * 常量<br/>
 * <p>
 * 常量<br/>
 * </p>
 * Time：2018-9-26 下午2:08:22<br/>
 * 
 * @author zouye
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Constant {
	/**
	 * 缺省字符集
	 */
	String DEFAULT_CHARSET = "UTF-8";
	/**
	 * 保函文件类型
	 */
	String LEFFER_ATTACH_ID = "a21e70182b7549f5a09a8fdbcc79f3be";
	/**
	 * 签约协议文件类型
	 */
	String SIGN_PROTOCOL_ATTACH_ID = "f3279d96dab949b39d9d282eaff9d56f";
	/**
	 * CA用户二维码管理
	 */
	String QR_CODE_ATTACH_ID = "bbf340c97b544a40be56db1f5e83d0a3";

	/**
	 * 主题类型
	 */
	String CITY_THEME = "CITY_THEME";

	String SYS_SWICH = "on";
	
	/**
	 * 
	 * 接口基础网址<br/>
	 * <p>
	 * 接口基础网址<br/>
	 * </p>
	 * Time：2018-12-4 下午4:04:11<br/>
	 * 
	 * @author huangbh
	 * @version 1.0.0
	 * @since 1.0.0
	 */

	public interface ThirdInterface {

		/*
		 * String PROJECT_TENDER =
		 * "http://117.25.161.106:6065/EDE/authorize/xmjy/zbwjzz/getTenderProjectList/bulletinIsuue"
		 * ; String PROJECT_TENDER_DETAIL =
		 * "http://117.25.161.106:6065/EDE/authorize/xmjy/zbwjzz/getTenderProjectDetail/bulletinIsuue"
		 * ;
		 * 
		 * String LEFFER_AUDIT="http://test-www.ydunyqian.com/authorize/req/sendBusData"
		 * ; String OUTLEFFER_AUDIT=
		 * "http://test-www.ydunyqian.com/authorize/req/sendBackBusData"; String
		 * STAMPED_STATUS ="http://test-www.ydunyqian.com/authorize/req/getDocByBusID";
		 * String YD_LOGIN ="http://test-www.ydunyqian.com/authorize/api/login";
		 */
		/**
		 * 空
		 * 
		 * @return 空
		 */
		String toString();
	}

	public interface ThirdLogin {
		/**
		 * 开标项目账户和key
		 */
		/*
		 * String PT_USER_NAME = "SYS_EOBD_FJJT_TEST"; String PT_KEY = "b546f85c";
		 *//**
			 * 云盾云签APPID和key
			 */
		/*
		 * String YDYQ_APP_ID="c5186275559341989817c83a1752edaa"; String YDYQ_KEY
		 * ="a976c2a2";
		 */
	}

	/**
	 * 系统运行参数key<br/>
	 * <p>
	 * </p>
	 * Time：2016-11-1 上午11:41:38<br/>
	 * 
	 * @author zouye
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public interface faceMethod {
		/**
		 * -交易平台-获取项目列表
		 */
		final String TRADING_PLATFORM_PROJECT_TENDER = "/xmjy/zbwjzz/getTenderProjectList/bulletinIsuue";
		/**
		 * -交易平台-获取项目详细信息
		 */
		final String TRADING_PLATFORM_PROJECT_TENDER_DETAIL = "/xmjy/zbwjzz/getTenderProjectDetail/bulletinIsuue";
		/**
		 * -云盾云签-
		 */
		final String RA_YDYQ_LEFFER_AUDIT = "/req/sendBusData";
		/**
		 * -云盾云签-
		 */
		final String RA_YDYQ_OUTLEFFER_AUDIT = "/req/sendBackBusData";
		/**
		 * -云盾云签-
		 */
		final String RA_YDYQ_STAMPED_STATUS = "/req/getDocByBusID";

		/**
		 * 云盾云签批量盖章
		 */
		final static String RA_YDYQ_BATCH_STAMPED = "/req/batchStamp";

		/**
		 * -云盾云签-登录（获取Token）
		 */
		final String RA_YDYQ_YD_LOGIN = "/api/login";
		/**
		 * 云盾云签-获取附件BUSID对应的附件数据
		 */
		final String RA_YDYQ_DOC_BUSID = "/req/getDocByBusID";
	}

	/**
	 * 系统运行参数key<br/>
	 * <p>
	 * </p>
	 * Time：2016-11-1 上午11:41:38<br/>
	 * 
	 * @author zouye
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public interface SysParamKey {
		/**
		 * 运维管理员ID
		 */
		String PM_ADMIN_ID = "aeolus.fs.pm.admin.role.id";
		/**
		 * 当前系统主页,即用户登录后重定向的页面
		 */
		String MAIN_PAGE_KEY = "aeolus.fs.main.page";
		/**
		 * 用户登录后要跳转到的基础路径
		 */
		String MAIN_FORWARD_URL_KEY = "aeolus.fs.main.forward.url";

		/**
		 * 云盾云签地址
		 */
		String RA_YDYQ_URL_KEY = "aeolus.fs.ra-ydyq.url";

		/**
		 * 云盾云签访问根目录
		 */
		String RA_YDYQ_ROOT_URL_KEY = "aeolus.fs.ra-ydyq.root.url";

		/**
		 * 云盾云签-登录账号
		 */
		String RA_YDYQ_ACCOUNT_KEY = "aeolus.fs.ra-ydyq.account";
		/**
		 * 云盾云签-登录密码
		 */
		String RA_YDYQ_PASSWORD_KEY = "aeolus.fs.ra-ydyq.password";
		/**
		 * 云盾云签-批量盖章token
		 */
		String RA_YDYQ_BATCH_STAMPED_TOKEN = "aeolus.fs.ra-ydyq.token";

		/**
		 * 云盾云签签名登录地址
		 */
		String RA_YDYQ_SIGN_LOGIN_URL = "aelous.ra-ydyq.sign.login.url";

		/**
		 * 云盾云签-批量盖章-法人章坐标
		 */
		String RA_YDYQ_BATCH_STAMPED_CORPORATE_X = "aeolus.fs.ra-ydyq.corporate-x";
		String RA_YDYQ_BATCH_STAMPED_CORPORATE_Y = "aeolus.fs.ra-ydyq.corporate-y";
		/**
		 * 云盾云签-批量盖章-公司章坐标
		 */
		String RA_YDYQ_BATCH_STAMPED_COMPANY_X = "aeolus.fs.ra-ydyq.company-x";
		String RA_YDYQ_BATCH_STAMPED_COMPANY_Y = "aeolus.fs.ra-ydyq.company-y";

		String RA_YDYQ_BATCH_SEAL_PATH = "aeolus.fs.ra-ydyq.sealpath";

		/**
		 * 金融平台文件操作磁盘
		 */
		String FILE_BASE_DISK = "aeolus.fs.file.base.disk";

		/**
		 * 厦门房建市政交易平台地址
		 */
		final String FJSZ_TRADING_PLATFORM_ROOT_URL_KEY = "aeolus.fs.fjsz.trading.platform.url";
		/**
		 * 厦门房建市政交易平台-访问账号
		 */
		final String FJSZ_TRADING_PLATFORM_ACCOUNT_KEY = "aeolus.fs.fjsz.trading.platform.account";
		/**
		 * 厦门房建市政交易平台-访问密码
		 */
		final String FJSZ_TRADING_PLATFORM_PASSWORD_KEY = "aeolus.fs.fjsz.trading.platform.password";

		/**
		 * 南平房建市政交易平台地址
		 */
		final String FJSZ_TRADING_PLATFORM_NP_URL = "aeolus.fs.fjsz.trading.platform.url.np";
		final String FJSZ_TRADING_PLATFORM_NP_URL_DETAIL = "aeolus.fs.fjsz.trading.platform.url.np.info";
		/**
		 * 南平房建市政交易平台参数
		 */
		final String FJSZ_TRADING_PLATFORM_NP_PARAMS_CLASSIFY = "aeolus.fs.fjsz.trading.platform.np.classify";
		final String FJSZ_TRADING_PLATFORM_NP_PARAMS_STAGE = "aeolus.fs.fjsz.trading.platform.np.stage";
		final String FJSZ_TRADING_PLATFORM_NP_PARAMS_CITY = "aeolus.fs.fjsz.trading.platform.np.city";

		/**
		 * 福州交易平台地址
		 */
		final String FZJY_TRADING_PLATFORM_ROOT_URL_KEY = "aeolus.fs.jzjy.trading.platform.url";
		/**
		 * 福州交易平台-访问账号
		 */
		final String FZJY_TRADING_PLATFORM_ACCOUNT_KEY = "aeolus.fs.jzjy.trading.platform.account";
		/**
		 * 福州交易平台-访问密码
		 */
		final String FZJY_TRADING_PLATFORM_PASSWORD_KEY = "aeolus.fs.jzjy.trading.platform.password";

		/**
		 * ra.okap-主地址
		 */
		final String RA_OKAP_ROOT_URL_KEY = "aeolus.oauth2.as.root.url";

		/***
		 * 保证金同步地址
		 */
		String BOND_BASE_URL = "aelous.base.bond.url";

		/**
		 * 空
		 * 
		 * @return 空
		 */
		String toString();
	}

	/**
	 * 表名常量定义<br/>
	 * <p>
	 * 表名常量定义<br/>
	 * </p>
	 * Time：2018-9-20 下午11:45:05<br/>
	 * 
	 * @author zhanggx
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public interface TableName {
		/**
		 * 用户信息表
		 */
		String T_SYS_USER_BASE = "T_SYS_USER_BASE";
		/**
		 * 用户角色表
		 */
		String T_SYS_USER_ROLE = "T_SYS_USER_ROLE";

		String T_SYS_ROLE_INFO = "T_SYS_ROLE_INFO";
		/**
		 * 角色表
		 */
		String T_SYS_MENU = "T_SYS_MENU";
		String T_SYS_ROLE_FUNC = "T_SYS_ROLE_FUNC";

		/**
		 * 支付账户表
		 */
		String T_PAY_ACCOUNT = "T_PAY_ACCOUNT";
		/**
		 * 支付渠道表
		 */
		String T_PAY_CHANNEL = "T_PAY_CHANNEL";
		/**
		 * 充值记录
		 */
		String T_USER_TOPUP = "T_USER_TOPUP";

		String T_WITHDRAW_TAB = "T_WITHDRAW_TAB";
		String T_DICT_TAB = "T_DICT_TAB";

		/**
		 * 充值记录
		 */
		String T_FILE_TAB = "T_FILE_TAB";

		String T_APP_TAB = "T_APP_TAB";
		String T_PAYTIME_CONF = "T_PAYTIME_CONF";
		String T_BOND_TODAY = "T_BOND_TODAY";
		String T_COLLECTION_TAB = "T_COLLECTION_TAB";
		String T_COMMISSION_TAB = "T_COMMISSION_TAB";
		String T_ORDER_TAB = "T_ORDER_TAB";
		String T_ACCOUNT_SHOW = "T_ACCOUNT_SHOW";
		String T_USER_SHOW = "T_USER_SHOW";
		String T_ACCOUNT_COLLECTION = "T_ACCOUNT_COLLECTION";

		/**
		 * 空
		 * 
		 * @return 空
		 */
		String toString();
	}

	/**
	 * 申请盖章类型
	 * 
	 * @author Administrator
	 *
	 */
	public interface applyType {
		/**
		 * 申请
		 */
		String DZBHRZ_SQ = "DZBH_XM_SQ";
		/**
		 * 退保
		 */
		String DZBHRZ_TB = "DZBH_XM_TB";
		/**
		 * 南平地区申请类型
		 */
		String DZBHRZ_SQ_FJNP = "DZBHRZ_SQ_FJNP";
		/**
		 * 南平地区退保类型
		 */
		String DZBHRZ_TB_FJNP = "DZBHRZ_TB_FJNP";
		/**
		 * 个人签约盖章
		 */
		String DZBHRZ_SIGN_PROTOCOL = "DZBH_XM_QY";

		/**
		 * 空
		 * 
		 * @return 空
		 */
		String toString();
	}

	/**
	 * 
	 * 金融机构签约文件模板类型<br/>
	 * <p>
	 * 金融机构签约文件模板类型<br/>
	 * </p>
	 * Time：2019-1-18 下午2:10:40<br/>
	 * 
	 * @author huangbh
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public interface financialType {

		/**
		 * 金融机构模板标识
		 */
		String SIGN_PROTECOL_MODEL = "SIGN_PROTECOL_MODEL";
		/**
		 * 元信
		 */
		String SIGN_PROTOCOL_MODEL_01 = "SIGN_PROTOCOL_MODEL_01";
		/**
		 * 长安
		 */
		String SIGN_PROTOCOL_MODEL_02 = "SIGN_PROTOCOL_MODEL_02";
		/**
		 * 阳光
		 */
		String SIGN_PROTOCOL_MODEL_03 = "SIGN_PROTOCOL_MODEL_03";
		/**
		 * 紫金
		 */
		String SIGN_PROTOCOL_MODEL_04 = "SIGN_PROTOCOL_MODEL_04";

	}

	public interface methodType {
		/**
		 * 查看保函摘要是否已被使用
		 */
		String checkIsAllowCancel = "/EARNEST_GUARANTEE_CHECK_USED.do";
	}

}

package com.sozone.fs.common;

public interface Constant {
	/**
	 * 缺省字符集
	 */
	String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 主题类型
	 */
	String CITY_THEME = "CITY_THEME";

	String SYS_SWICH = "on";
	
	public interface faceMethod {
		/**
		 * -云盾云签-登录（获取Token）
		 */
		final String RA_YDYQ_YD_LOGIN = "/api/login";
		/**
		 * 云盾云签-获取附件BUSID对应的附件数据
		 */
		final String RA_YDYQ_DOC_BUSID = "/req/getDocByBusID";
	}

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
		
		String T_RECIEVE_TAB = "T_RECIEVE_TAB";
		String T_SEND_TAB = "T_SEND_TAB";
		String T_ORDER_OPT = "T_ORDER_OPT";

		/**
		 * 空
		 * 
		 * @return 空
		 */
		String toString();
	}

}

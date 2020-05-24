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
	String WEB_URL ="http://8.129.183.175:8090/";
	String VIEW_URL = "http://8.129.183.175/";
	String ALI_URL = "alipayqr://platformapi/startapp?saId=10000007&qrcode=";
	String ZZ_URL = "https://www.alipay.com/?appId=20000123&actionType=scan&biz_data=";
	String INSER_ORDER = "http://47.115.93.230/authorize/order/in";
	String QRCODE_URL = "http://47.115.93.230/authorize/order/snd";
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

/**
 * 包名：com.sozone.ebidpb.utils
 * 文件名：SessionUtils.java<br/>
 * 创建时间：2017-8-3 下午4:51:34<br/>
 * 创建者：zouye<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package com.sozone.fs.common.util;

import com.sozone.aeolus.authorize.utlis.ApacheShiroUtils;
import com.sozone.aeolus.exception.ValidateException;
import com.sozone.fs.common.Constant;

/**
 * Session会话工具类<br/>
 * <p>
 * Session会话工具类<br/>
 * </p>
 * Time：2017-8-3 下午4:51:34<br/>
 * 
 * @author zouye
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SessionUtils
{
	private SessionUtils()
	{
	}

	/**
	 * 设置属性<br/>
	 * <p>
	 * </p>
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void setAttribute(Object key, Object value)
	{
		ApacheShiroUtils.getSession().setAttribute(key, value);
	}

	/**
	 * 获取属性<br/>
	 * <p>
	 * </p>
	 * 
	 * @param key
	 *            键
	 * @return 值
	 * @param <T>
	 *            参数值泛型
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(Object key)
	{
		return (T) ApacheShiroUtils.getSession().getAttribute(key);
	}

	/**
	 * 
	 * 获取角色信息ID<br/>
	 * <p>
	 * 获取角色信息ID
	 * </p>
	 * 
	 * @return 角色信息ID
	 * @throws ValidateException
	 *             ValidateException
	 */
	public static String getRoleId() throws ValidateException
	{
		return SessionUtils.getAttribute("roleId");
	}

	
	/**
	 * 获取当前登陆用户统一社会代码
	 * 
	 * @return 当前登陆用户统一社会代码
	 * @throws ValidateException
	 *             ValidateException
	 */
	public static String getSocialcreditNO() throws ValidateException
	{
		return ApacheShiroUtils.getCurrentUser().getColumn("V_SOCIALCREDIT_NO");
	}

	/**
	 * 获取当前登陆用户证书名称
	 * 
	 * @return 当前登陆用户证书名称
	 * @throws ValidateException
	 *             ValidateException
	 */
	public static String getLoginName() throws ValidateException
	{
		return SessionUtils.getAttribute("LOGINNAME");
	}

	/**
	 * 获取当前登陆用户企业名称名称
	 * 
	 * @return 当前登陆用户企业名称名称
	 * @throws ValidateException
	 *             ValidateException
	 */
	public static String getCompanyName() throws ValidateException
	{
		return SessionUtils.getAttribute("USER_NAME");
	}

	/**
	 * 获取当前登录用户ID
	 * @return 用户ID
	 * @throws ValidateException
	 */
	public static String getCurrentId() throws ValidateException{
		return ApacheShiroUtils.getCurrentUserID();
	}

	/**
	 * 
	 * 获取当前城市主题<br/>
	 * <p>
	 * 获取当前城市主题
	 * </p>
	 * 
	 * @return 主题
	 */
	public static String getCityTheme()
	{
		String theme = getAttribute(Constant.CITY_THEME);
		return theme;
	}

	/**
	 * 
	 * 设置当前城市主题<br/>
	 * <p>
	 * 设置当前城市主题
	 * </p>
	 * 
	 * @return 主题
	 */
	public static void setCityTheme(Object theme)
	{
		setAttribute(Constant.CITY_THEME, theme);
	}

}

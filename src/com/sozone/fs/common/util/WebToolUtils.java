/**
 * 包名：com.sozone.fs.common.util
 * 文件名：WebToolUtils.java<br/>
 * 创建时间：2019-1-14 上午10:27:01<br/>
 * 创建者：huangbh<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package com.sozone.fs.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sozone.aeolus.authorize.utlis.ApacheShiroUtils;
import com.sozone.aeolus.authorize.utlis.SystemParamUtils;
import com.sozone.fs.common.Constant.SysParamKey;

/**
 * 网页静态工具类<br/>
 * <p>
 * 网页静态工具类<br/>
 * </p>
 * Time：2019-1-14 上午10:27:01<br/>
 * @author huangbh
 * @version 1.0.0
 * @since 1.0.0
 */
public class WebToolUtils
{
	/**
	 * 
	 * 云盾云签-签证登录地址<br/>
	 * <p>
	 * 云盾云签-签证登录地址
	 * </p>
	 * 
	 * @return
	 */
	public static String getYDYQSignPath()
	{
		return SystemParamUtils.getString(SysParamKey.RA_YDYQ_SIGN_LOGIN_URL);
	}
	
	/**
	 * 
	 * 云盾云签-主地址<br/>
	 * <p>
	 * 云盾云签-主地址
	 * </p>
	 * 
	 * @return
	 */
	public static String getYDYQRootPath()
	{
		return SystemParamUtils.getString(SysParamKey.RA_YDYQ_URL_KEY);
	}
	
	/**
	 * 
	 * ra.okap-主地址<br/>
	 * <p>
	 * ra.okap-主地址
	 * </p>
	 * 
	 * @return
	 */
	public static String getRaRootPath()
	{
		return SystemParamUtils.getString(SysParamKey.RA_OKAP_ROOT_URL_KEY);
	}
	
	/**
	 * 
	 * 获取登陆用户名称<br/>
	 * <p>
	 * 获取登陆用户名称
	 * </p>
	 * 
	 * @return
	 */
	public static String getLoginUserName()
	{
		String userName = ApacheShiroUtils.getCurrentUserName();// 登录用户名称
		
		return userName;
	}
	
	/**
	 * 时间戳转字符串 1503991612952 ==> 2017-08-29 15:26:52 时间戳 是long 类型
	 * <p>
	 * 时间戳转字符串 1503991612952 ==> 2017-08-29 15:26:52 时间戳 是long 类型
	 * </p>
	 * 
	 * @param s
	 * @return
	 */
	public static String timeToString(String s)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try
		{
			date = sdf.parse(sdf.format(new Date(new Long(s))));
			// Date date = sdf.parse(sdf.format(new Long(s)));// 等价于
			return sdf.format(date);
		}
		catch (NumberFormatException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return s;
	}
}

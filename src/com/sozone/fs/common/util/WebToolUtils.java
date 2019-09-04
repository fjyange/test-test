package com.sozone.fs.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sozone.aeolus.authorize.utlis.ApacheShiroUtils;
import com.sozone.aeolus.authorize.utlis.SystemParamUtils;
import com.sozone.fs.common.Constant.SysParamKey;

public class WebToolUtils
{
	public static String getYDYQSignPath()
	{
		return SystemParamUtils.getString(SysParamKey.RA_YDYQ_SIGN_LOGIN_URL);
	}
	
	public static String getYDYQRootPath()
	{
		return SystemParamUtils.getString(SysParamKey.RA_YDYQ_URL_KEY);
	}
	
	public static String getRaRootPath()
	{
		return SystemParamUtils.getString(SysParamKey.RA_OKAP_ROOT_URL_KEY);
	}
	
	public static String getLoginUserName()
	{
		String userName = ApacheShiroUtils.getCurrentUserName();// 登录用户名称
		
		return userName;
	}
	
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
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return s;
	}
}

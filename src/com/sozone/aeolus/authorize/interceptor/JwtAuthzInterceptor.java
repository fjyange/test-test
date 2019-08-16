/**
 * 包名：com.sozone.ydyq.api.interceptor
 * 文件名：JwtAuthzInterceptor.java<br/>
 * 创建时间：2018年8月28日 下午4:15:46<br/>
 * 创建者：zhenglin<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package com.sozone.aeolus.authorize.interceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.sozone.aeolus.authorize.realm.JwtUserToken;
import com.sozone.aeolus.authorize.utlis.ApacheShiroUtils;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.dao.StatefulDAO;
import com.sozone.aeolus.dao.StatefulDAOImpl;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.AeolusException;
import com.sozone.aeolus.exception.ServiceException;
import com.sozone.aeolus.exception.ValidateException;
import com.sozone.aeolus.ext.orm.DataEntry;
import com.sozone.aeolus.ext.orm.impl.DataEntryImpl;
import com.sozone.aeolus.interceptor.Interceptor;
import com.sozone.aeolus.interceptor.InterceptorChain;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.fs.common.Constant.TableName;
import com.sozone.fs.common.util.TokenUtils;

/**
 * JWT 认证拦截器 Time：2018年8月28日 下午4:15:46<br>
 * 
 * @author zhenglin
 * @version 1.0.0
 * @since JDK1.6
 */
public class JwtAuthzInterceptor implements Interceptor
{

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(JwtAuthzInterceptor.class);

	/**
	 * TOKEN key
	 */
	private static final String AUTHZ_HEAD_KEY = "Authorization";

	/**
	 * Basic
	 */
	private static final String AUTHZ_HEADER_NAME = "Basic";

	/**
	 * 登录固定Token key
	 */
	public final static String TOKEN_KEY = "a92flk3h";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sozone.aeolus.interceptor.Interceptor#intercept(com.sozone.aeolus
	 * .data.AeolusData, com.sozone.aeolus.interceptor.InterceptorChain)
	 */
	@Override
	public void intercept(AeolusData data, InterceptorChain chain) throws AeolusException
	{
		logger.debug(LogUtils.format("开始处理Rest认证!", data, chain));
		// 获取头信息中的认证TOKEN
		String token = data.getHeader(AUTHZ_HEAD_KEY);
		String key = TOKEN_KEY;

		// 如果token非空
		if (StringUtils.isNotEmpty(token) && token.startsWith(AUTHZ_HEADER_NAME))
		{
			// 切除token头部
			token = getTokenValue(token);
			Record<String, Object> decryptToken = TokenUtils.decryptToken(token, key);

			String userID = decryptToken.getString("PLAT_CODE");// 用户ID
			DataEntry de = new DataEntryImpl(TableName.T_SYS_USER_BASE);
			de.and().equalTo("USER_ID", userID);
			Record<String, Object> userInfo = de.persist().get();
			if (CollectionUtils.isEmpty(userInfo))
			{
				throw new ValidateException("", "用户信息不存在");
			}
			String userName = userInfo.getString("USER_ACCOUNT");
			try
			{
				// 登录
				SecurityUtils.getSubject().login(new JwtUserToken(userName, token));
			}
			// 其他
			catch (Exception e)
			{
				String message = "";
				JSONObject jsonObject =JSONObject.parseObject(e.getMessage());
				if (!StringUtils.isEmpty(jsonObject.getString("errorDesc"))){
					message = jsonObject.getString("errorDesc");
				}
				e.printStackTrace();
				throw new ValidateException("R-0007", message);
			}
		}
		// 继续往下执行
		chain.intercept(data);
	}

	private String getTokenValue(String token) throws AeolusException
	{
		String[] ss = StringUtils.split(token, " ");
		if (null == ss || 2 > ss.length || !StringUtils.equals(AUTHZ_HEADER_NAME, ss[0]))
		{
			throw new ValidateException("R-0003", "登录异常");
		}
		return ss[1];
	}

}

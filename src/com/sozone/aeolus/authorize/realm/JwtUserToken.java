/**
 * 包名：com.sozone.ydyq.api.realm
 * 文件名：JwtUserToken.java<br>
 * 创建时间：2018年8月28日 下午2:46:48<br>
 * 创建者：zhenglin<br>
 * 修改者：暂无<br>
 * 修改简述：暂无<br>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br>
 */
package com.sozone.aeolus.authorize.realm;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * JWT TOKEN
 * Time：2018年8月28日 下午2:46:48<br>
 * @author zhenglin
 * @version 1.0.0
 * @since JDK1.6
 */
@SuppressWarnings("serial")
public class JwtUserToken implements AuthenticationToken
{
	
	private String account;
	private String token;
	
	public JwtUserToken(String account,String token) {
		this.account=account;
		this.token=token;
	}
	

	/* (non-Javadoc)
	 * @see org.apache.shiro.authc.AuthenticationToken#getPrincipal()
	 */
	@Override
	public Object getPrincipal()
	{
		return account;
	}

	/* (non-Javadoc)
	 * @see org.apache.shiro.authc.AuthenticationToken#getCredentials()
	 */
	@Override
	public Object getCredentials()
	{
		return token;
	}

}

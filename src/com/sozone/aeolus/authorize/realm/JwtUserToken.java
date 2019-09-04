package com.sozone.aeolus.authorize.realm;

import org.apache.shiro.authc.AuthenticationToken;

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

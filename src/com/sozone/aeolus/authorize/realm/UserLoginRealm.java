
package com.sozone.aeolus.authorize.realm;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;

import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.exception.DAOException;
import com.sozone.fs.common.Constant;
import com.sozone.fs.common.Constant.TableName;


public class UserLoginRealm extends AeolusAccountPasswordAuthorizingRealm
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sozone.aeolus.authorize.realm.AeolusAccountPasswordAuthorizingRealm
	 * #supports(org.apache.shiro.authc.AuthenticationToken)
	 */
	@Override
	public boolean supports(AuthenticationToken token)
	{
		return token instanceof JwtUserToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sozone.aeolus.authorize.realm.AeolusAccountPasswordAuthorizingRealm
	 * #buildAuthcInfo(com.sozone.aeolus.dao.data.Record)
	 */
	@Override
	protected AuthenticationInfo buildAuthcInfo(Record<String, Object> user)
	{
		return new SimpleAuthenticationInfo(user, user.getString("TOKEN"),
				getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sozone.aeolus.authorize.realm.AeolusAccountPasswordAuthorizingRealm
	 * #getUser(org.apache.shiro.authc.AuthenticationToken)
	 */
	@Override
	protected Record<String, Object> getUser(AuthenticationToken authcToken)
			throws DAOException
	{
		JwtUserToken token = (JwtUserToken) authcToken;
		// 应用ID
		String account = (String) token.getPrincipal();
		Record<String, Object> user = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(TableName.T_SYS_USER_BASE)
				.EQUAL("USER_ACCOUNT", account).get();
		user.put("TOKEN", token.getCredentials());
		if(!StringUtils.equals("1", user.getString("IS_ADMIN"))) {
			Record<String, Object> record = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_DICT_TAB).EQUAL("V_DICT_TYPE", "SYS_SWICH").get();
			if (StringUtils.equals("off", record.getString("V_DICT_VALUE"))) {
				throw new DAOException("","系统已关闭");
			}
		}
		return user;
	}
}

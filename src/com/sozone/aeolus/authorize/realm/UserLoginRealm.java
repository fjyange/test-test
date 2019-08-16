/**
 * 包名：com.sozone.ydyq.api.realm
 * 文件名：ApiUserLoginRealm.java<br>
 * 创建时间：2018年8月28日 下午2:28:54<br>
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

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;

import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.exception.DAOException;
import com.sozone.aeolus.exception.ServiceException;
import com.sozone.fs.common.Constant;
import com.sozone.fs.common.Constant.TableName;

/**
 * 消息推送验证<br>
 * <p>
 * 消息推送验证<br>
 * </p>
 * Time：2018年8月28日 下午2:28:54<br>
 * 
 * @author zhenglin
 * @version 1.0.0
 * @since JDK1.6
 */
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

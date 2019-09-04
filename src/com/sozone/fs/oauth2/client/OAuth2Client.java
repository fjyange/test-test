/**
 * 包名：com.sozone.fs.oauth2.client
 * 文件名：OAuth2Client.java<br/>
 * 创建时间：2018-9-26 下午12:54:48<br/>
 * 创建者：YSZY<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package com.sozone.fs.oauth2.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.authorize.common.Constant;
import com.sozone.aeolus.authorize.utlis.ApacheShiroUtils;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.authorize.utlis.SystemParamUtils;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.DAOException;
import com.sozone.aeolus.exception.ServiceException;
import com.sozone.aeolus.exception.ValidateException;
import com.sozone.aeolus.ext.ExtConstant;
import com.sozone.aeolus.ext.ExtConstant.TableName;
import com.sozone.aeolus.ext.client.RACloudBaseOAuth2Client;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.auth2.client.OAuth2ClientConstant;
import com.sozone.fs.common.Constant.SysParamKey;
import com.sozone.fs.common.util.SessionUtils;


@Path(value = "o2c", desc = "开放式授权客户端实现")
public class OAuth2Client extends RACloudBaseOAuth2Client {

	/**
	 * 运维管理员类型
	 */
	private static final String PM_TYPE = "9527";

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(OAuth2Client.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sozone.auth2.client.facade.ClientEntry#getLoginUrl(com.sozone.aeolus
	 * .data.AeolusData)
	 */
	@Override
	protected String getLoginUrl(AeolusData data) {
		logger.debug(LogUtils.format("获取跳转登录页", data));
		// 获取用户类型
		String type = data.getParam(ExtConstant.USER_TYPE_PARAM_KEY);
		Map<Object, Object> params = new HashMap<Object, Object>();
		params.put(ExtConstant.USER_TYPE_PARAM_KEY, type);
		// 表达式解析器
		StrSubstitutor strs = new StrSubstitutor(params);
		// 服务器端授权页面
		String loginUrl = SystemParamUtils.getProperty(OAuth2ClientConstant.SysParamKey.AS_AUTH_URL_KEY);
		loginUrl = strs.replace(loginUrl);
		return loginUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sozone.aeolus.ext.client.RACloudBaseOAuth2Client#getSuccessPage(com
	 * .sozone.aeolus.data.AeolusData)
	 */
	@Override
	protected String getSuccessPage(AeolusData data) {
		logger.debug(LogUtils.format("获取登录成功跳转页", data));
		// 获取用户的类型
		String userType = data.getParam(OAuth.OAUTH_STATE);
		String userId = ApacheShiroUtils.getCurrentUserID();
		Record<String, Object> params = new RecordImpl<String, Object>();

		params.setColumn("USER_ID", userId);
		try {
			Record<String, Object> param = new RecordImpl<String, Object>();
			param.put("V_CREATE_USER", userId);
			Record<String, Object> bidder = this.activeRecordDAO.statement().selectOne("Bidder.getUserInfo", param);
			if (!CollectionUtils.isEmpty(bidder)) {
				SessionUtils.setAttribute("USER_NAME", bidder.getString("V_NAME"));
			}
			// 判断角色是否存在
			int count = this.activeRecordDAO.auto().table(Constant.TableName.USER_ROLE)
					.setCondition("AND", "USER_ID=#{USER_ID}").count(params);
			// 如果不存在
			if (0 == count) {
				params.put("ROLE_ID", "adae5d79ea9a4252b20e1422524a3a1c");
				// 插入时间
				this.activeRecordDAO.auto().table(Constant.TableName.USER_ROLE).save(params);
			}
			List<Record<String, Object>> list = this.activeRecordDAO.auto().table(Constant.TableName.USER_ROLE)
					.setCondition("AND", "USER_ID=#{USER_ID}").list(params);
			if (!CollectionUtils.isEmpty(list)) {
				String roleId = "";
				for (int i = 0; i < list.size(); i++) {
					if (i != list.size() - 1) {
						roleId += "'" + list.get(i).getString("ROLE_ID") + "'" + ",";
					} else {
						roleId += "'" + list.get(i).getString("ROLE_ID") + "'";
					}
				}
				SessionUtils.setAttribute("roleId", roleId);
			}
		} catch (ValidateException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 首页
		String successUrl = SystemParamUtils.getProperty(SysParamKey.MAIN_PAGE_KEY, "");
		String basePath = SystemParamUtils.getProperty(SysParamKey.MAIN_FORWARD_URL_KEY, data.getBasePath());
		successUrl = basePath + "/" + successUrl + "?user_type=" + userType + "&user_id=" + userId;
		// 如果是运维登录且是运维管理员
		if (StringUtils.equalsIgnoreCase(userType, PM_TYPE)
				&& SecurityUtils.getSubject().hasRole(SystemParamUtils.getProperty(SysParamKey.PM_ADMIN_ID))) {
			successUrl = basePath + "/authorize/view/manage/index.html";
		}
		return successUrl;
	}

	/**
	 * 保存CA用户信息<br/>
	 * <p>
	 * 保存从云盾端获取到的CA用户信息
	 * </p>
	 * 
	 * @param user 从云盾端获取到的CA用户信息
	 * @return 保存到本地的CA用户对象信息
	 * @throws ServiceException 服务异常
	 */
	protected Record<String, Object> saveCAUserInfo(Record<String, Object> user) throws ServiceException {
		// 构造自己的对象
		Record<String, Object> userInfo = new RecordImpl<String, Object>();
		// 使用云盾端返回的SYS_ID作为本地表的主键
		String id = DigestUtils.md5Hex(user.getString("SYS_ID"));
		userInfo.setColumn("ID", id);
		userInfo.setColumn("CA_USER_ID", id);
		userInfo.setColumn("V_SYS_ID", user.getString("SYS_ID"));
		userInfo.setColumn("V_NAME", user.getString("NAME"));
		userInfo.setColumn("V_USER_TYPE", user.getString("USERTYPE"));
		// 去重前导与尾部空格
		userInfo.setColumn("V_SOCIALCREDIT_NO", StringUtils.trim(user.getString("SOCIALCREDITNO")));
		userInfo.setColumn("V_REG_NO", user.getString("REGNO"));
		userInfo.setColumn("V_LEGAL_NO", user.getString("LEGALNO"));
		// 去除前导与尾部空格,便于业务匹配
		userInfo.setColumn("V_COMPANY_CODE", StringUtils.trim(user.getString("COMPANYCODE")));
		userInfo.setColumn("V_TAX_NO", user.getString("TAXNO"));
		userInfo.setColumn("V_RENT_NO", user.getString("RENTNO"));
		userInfo.setColumn("V_SOCIAL_NO", user.getString("SOCIALNO"));
		userInfo.setColumn("V_OTHER_NO", user.getString("OTHERNO"));
		userInfo.setColumn("V_LEGAL_NAME", user.getString("LEGALNAME"));
		userInfo.setColumn("V_ID_NO", user.getString("IDNO"));
		userInfo.setColumn("V_UNI_TTEL", user.getString("UNITTEL"));
		userInfo.setColumn("V_REG_ADDRESS", user.getString("REGADDRESS"));
		userInfo.setColumn("N_UPDATE_TIME", System.currentTimeMillis());
		// 判断用户是否存在
		int count = this.activeRecordDAO.auto().table(TableName.CA_USER_INFO)
				.count(new RecordImpl<String, Object>().setColumn("ID", id));
		// 如果不存在
		if (0 == count) {
			// 插入时间
			userInfo.setColumn("N_CREATE_TIME", System.currentTimeMillis());
			this.activeRecordDAO.auto().table(TableName.CA_USER_INFO).save(userInfo);
			return userInfo;
		}
		// 如果存在修改,这里为了避免云盾端的数据发生了更新

		// 20190301 huangy 法定代表姓名、单位电话不更新
		userInfo.remove("V_LEGAL_NAME");
		userInfo.remove("V_UNI_TTEL");

		this.activeRecordDAO.auto().table(TableName.CA_USER_INFO).modify(userInfo);
		return userInfo;
	}

}

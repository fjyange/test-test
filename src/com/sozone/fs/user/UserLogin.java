/**
 * 包名：com.sozone.ydyq.api
 * 文件名：UserLogin.java<br>
 * 创建时间：2018年8月28日 下午1:55:34<br>
 * 创建者：zhenglin<br>
 * 修改者：暂无<br>
 * 修改简述：暂无<br>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br>
 */
package com.sozone.fs.user;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.authorize.interceptor.JwtAuthzInterceptor;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.StatefulDAO;
import com.sozone.aeolus.dao.StatefulDAOImpl;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.DAOException;
import com.sozone.aeolus.exception.ValidateException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.fs.common.Constant;
import com.sozone.fs.common.Constant.TableName;
import com.sozone.fs.common.util.IPUtils;
import com.sozone.fs.common.util.TokenUtils;

@Path(value = "/userLogin", desc = "")
public class UserLogin {
	/**
	 * 持久化接口
	 */
	private ActiveRecordDAO activeRecordDAO = null;

	/**
	 * activeRecordDAO属性的set方法
	 * 
	 * @param activeRecordDAO
	 *            the activeRecordDAO to set
	 */
	public void setActiveRecordDAO(ActiveRecordDAO activeRecordDAO) {
		this.activeRecordDAO = activeRecordDAO;
	}

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(UserLogin.class);

	@Path(value = "/login", desc = "登录接口")
	// @HttpMethod(value = HttpMethod.POST)
	@Service
	@Permission(Level.Guest)
	public ResultVO<Record<String, Object>> login(AeolusData data) throws Exception {
		ResultVO<Record<String, Object>> resultVO = new ResultVO<Record<String, Object>>(true);
		String account = data.getParam("ACCOUNT");
		// 二重加密
		String password = DigestUtils.md5Hex(data.<String>getParam("PASSWORD"));
		String key = JwtAuthzInterceptor.TOKEN_KEY;
		StatefulDAO statefulDAO = null;
		try {
			statefulDAO = new StatefulDAOImpl();
			Record<String, Object> userRecord = activeRecordDAO.pandora().SELECT_ALL_FROM(TableName.T_SYS_USER_BASE)
					.EQUAL("USER_ACCOUNT", account).EQUAL("PASSWORD", password).get();
			if (CollectionUtils.isEmpty(userRecord)) {
				logger.error(LogUtils.format("登录失败,账号不存在或密码不正确", new Object[] { account }));
				resultVO.setSuccess(false);
				resultVO.setErrorDesc("登录失败,账号不存在或密码不正确");
				return resultVO;
			}
			if (StringUtils.equals(userRecord.getString("IS_LOCK"), "1")) {
				logger.error(LogUtils.format("登录失败,账号被禁用!", new Object[] { account }));
				resultVO.setSuccess(false);
				resultVO.setErrorDesc("登录失败,账号不存在或密码不正确");
				return resultVO;
			}
			String userID = userRecord.getString("USER_ID");
			String token = TokenUtils.generateToken(userID, key);
			Record<String, Object> updateRecord = new RecordImpl<>();
			updateRecord.setColumn("IP_ADDR", IPUtils.getMACAddress(IPUtils.getIp(data.getHttpServletRequest())));
			statefulDAO.pandora().UPDATE(Constant.TableName.T_SYS_USER_BASE).EQUAL("USER_ID", userID)
					.SET(updateRecord).excute();
			statefulDAO.commit();
			resultVO.setResult(new RecordImpl<String, Object>().setColumn("token", token));
		} catch (DAOException e) {
			logger.error(LogUtils.format(account + "登录失败", new Object[0]), e);
			throw new ValidateException("AUTH-1013");
		}finally {
			statefulDAO.close();
		}

		return resultVO;
	}

}

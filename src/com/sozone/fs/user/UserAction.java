package com.sozone.fs.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.authorize.utlis.ApacheShiroUtils;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.authorize.utlis.Random;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.data.Page;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.FacadeException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.aeolus.utils.DateUtils;
import com.sozone.fs.common.Constant;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

import sun.java2d.SurfaceDataProxy.CountdownTracker;

@Path(value = "/user", desc = "用户信息处理接口")
@Permission(Level.Authenticated)
public class UserAction {

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
	private static Logger logger = LoggerFactory.getLogger(UserAction.class);

	@Path(value = "/findPermissions", desc = "获取用户权限")
	@Service
	public ResultVO<Set<String>> findPermissions(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("获取用户权限列表", aeolusData));
		ResultVO<Set<String>> resultVO = new ResultVO<>();
		Set<String> perms = new HashSet<>();
		Record<String, Object> params = new RecordImpl<String, Object>();
		params.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		if (StringUtils.equals("1", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"))) {
			params.setColumn("USER_TYPE", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		}
		List<Record<String, Object>> sysMenus = this.activeRecordDAO.statement().selectList("Menu.findAll", params);
		for (Record<String, Object> sysMenu : sysMenus) {
			if (!StringUtils.isBlank(sysMenu.getString("perms"))) {
				perms.add(sysMenu.getString("perms"));
			}
		}
		resultVO.setSuccess(true);
		resultVO.setResult(perms);
		return resultVO;
	}

	@Path(value = "/findPage", desc = "获取用户列表")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("获取用户信息", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		record.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("IS_ADMIN", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		Page<Record<String, Object>> page = this.activeRecordDAO.statement().selectPage("User.userList",
				aeolusData.getPageRequest(), record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@SuppressWarnings("deprecation")
	@Path(value = "/save", desc = "保存用户信息")
	@Service
	public ResultVO<String> save(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("保存用户信息", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		String userID = record.getString("USER_ID");
		if (StringUtils.isBlank(userID)) {
			record.setColumn("USER_ID", Random.generateUUID());
			record.setColumn("PASSWORD", DigestUtils.md5Hex(DigestUtils.md5Hex("111111")));
			record.setColumn("IS_ADMIN", "0");
			record.setColumn("IS_LOCK", "0");
			record.setColumn("USER_TYPE", "1");
			record.setColumn("USER_LEVEL", "2");
			record.setColumn("CREATE_USER", ApacheShiroUtils.getCurrentUserID());
			record.setColumn("CREATE_TIME", DateUtils.getDateTime());
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_BASE).save(record);
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_ROLE).save(record);
		} else {
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_BASE).modify(record);
			Record<String, Object> params = new RecordImpl<>();
			params.setColumn("USER_ID", userID);
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_ROLE).remove(params);
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_ROLE).save(record);
		}
		resultVO.setSuccess(true);
		resultVO.setResult("保存成功");
		return resultVO;
	}

	@Path(value = "delete", desc = "删除用户")
	@Service
	public ResultVO<String> delete(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("删除用户", aeolusData));
		ResultVO<String> resultVO = new ResultVO<String>();
		Record<String, Object> record = aeolusData.getRecord();
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_SYS_USER_BASE)
				.EQUAL("USER_ID", record.getString("USER_ID")).excute();
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_SYS_USER_ROLE)
				.EQUAL("USER_ID", record.getString("USER_ID")).excute();
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_PAY_ACCOUNT)
				.EQUAL("V_CREATE_USER", record.getString("USER_ID")).excute();
		resultVO.setSuccess(true);
		resultVO.setResult("删除用户成功");
		return resultVO;
	}

	@SuppressWarnings("deprecation")
	@Path(value = "/userConf", desc = "修改用户配置")
	@Service
	public ResultVO<String> userConf(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("修改用户配置", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>(false);
		Record<String, Object> record = aeolusData.getRecord();
		Record<String, Object> params = new RecordImpl<>();
		this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_BASE).setCondition("and", "USER_ID=#{USER_ID}")
				.modify(record);
		long count  = this.activeRecordDAO.pandora()
				.SELECT_COUNT_FROM(Constant.TableName.T_USER_SHOW)
				.EQUAL("V_USER_ID", record.getString("USER_ID")).count();
		if (StringUtils.isNotBlank(record.getString("IS_MATCH"))) {
			if (count > 0) {
				params.setColumn("V_IS_PAY",  record.getString("IS_MATCH"));
				this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_USER_SHOW).EQUAL("V_USER_ID", record.getString("USER_ID")).SET(params);
			}
		}
		if (StringUtils.isNotBlank(record.getString("USER_LEVEL"))) {
			if (count > 0) {
				params.setColumn("V_USER_LEVEL",  record.getString("USER_LEVEL"));
				this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_USER_SHOW).EQUAL("V_USER_ID", record.getString("USER_ID")).SET(params);
			}
		}
		resultVO.setSuccess(true);
		resultVO.setResult("用户配置修改成功");
		return resultVO;
	}

	@Path(value = "/passwordConf", desc = "修改密码")
	@Service
	public ResultVO<String> passwordConf(AeolusData aeolusData) throws FacadeException {
		ResultVO<String> resultVO = new ResultVO<>(false);
		Record<String, Object> record = aeolusData.getRecord();
		String userId = record.getString("V_USER_ID");
		Record<String, Object> params = new RecordImpl<>();
		if (StringUtils.isNotBlank(record.getString("PASSWORD"))) {
			params.setColumn("USER_ID", userId);
			params.setColumn("V_UPDATE_TIME", DateUtils.getDateTime());
			params.setColumn("V_UPDATE_USER", ApacheShiroUtils.getCurrentUserID());
			params.setColumn("PASSWORD", DigestUtils.md5Hex(DigestUtils.md5Hex(record.getString("PASSWORD"))));
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_BASE)
					.setCondition("AND", "USER_ID = #{USER_ID}").modify(params);
			resultVO.setResult("密码修改成功");
		} else {
			resultVO.setResult("密码未更改");
		}
		resultVO.setSuccess(true);
		return resultVO;
	}

	@Path(value = "/userPayConf", desc = "修改用户付款配置")
	@Service
	public ResultVO<String> userPayConf(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("修改用户付款配置", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>(false);
		Record<String, Object> record = aeolusData.getTableRecord(Constant.TableName.T_PAYTIME_CONF);
		String userId = record.getString("V_USER_ID");
		Record<String, Object> confRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_PAYTIME_CONF).EQUAL("V_USER_ID", userId).get();
		record.setColumn("V_UPDATE_TIME", DateUtils.getDateTime());
		record.setColumn("V_UPDATE_USER", ApacheShiroUtils.getCurrentUserID());
		if (CollectionUtils.isEmpty(confRecord)) {
			record.setColumn("ID", Random.generateUUID());
			record.setColumn("V_CREATE_TIME", DateUtils.getDateTime());
			record.setColumn("V_CREATE_USER", ApacheShiroUtils.getCurrentUserID());
			this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_PAYTIME_CONF).VALUES(record).excute();
		} else {
			this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_PAYTIME_CONF)
					.EQUAL("ID", confRecord.getString("ID")).SET(confRecord).excute();
		}
		resultVO.setSuccess(true);
		resultVO.setResult("用户付款次数配置修改成功");
		return resultVO;
	}

}

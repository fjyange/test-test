/**
 * 
 */
package com.sozone.fs.home;

import java.util.List;

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
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.FacadeException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.aeolus.utils.DateUtils;
import com.sozone.fs.attach.AttachAction;
import com.sozone.fs.common.Constant;

/**
 * @author yange
 *
 */
@Path(value = "/home", desc = "首页数据")
@Permission(Level.Authenticated)
public class HomeAction {

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
	private static Logger logger = LoggerFactory.getLogger(AttachAction.class);

	@Path(value = "/showHome", desc = "首页显示数据")
	@Service
	public ResultVO<Record<String, Object>> showHome(AeolusData aeolusData)
			throws FacadeException {
		logger.debug(LogUtils.format("查询首页显示数据", aeolusData));
		ResultVO<Record<String, Object>> resultVO = new ResultVO<>();
		Record<String, Object> resultRecord = new RecordImpl<>();
		String userId = ApacheShiroUtils.getCurrentUserID();
		String userType = ApacheShiroUtils.getCurrentUser().getString(
				"USER_TYPE");
		if (StringUtils.equals("1", userType)) {
			resultRecord.setColumn("SHOW_MATCH", "Y");
			long matchCount = this.activeRecordDAO.pandora()
					.SELECT_COUNT_FROM(Constant.TableName.T_ACCOUNT_SHOW)
					.EQUAL("V_USER_ID", userId).count();
			if (matchCount > 0) {
				resultRecord.setColumn("SHOW_MATCH", "N");
			} else {
				resultRecord.setColumn("SHOW_MATCH", "Y");
			}
		} else {
			resultRecord.setColumn("SHOW_MATCH", "N");
			resultRecord.setColumn("IS_MATCH", "N");
		}
		if (StringUtils.equals("0", userType)) {
			resultRecord.setColumn("SYS_SHOW", "Y");
			Record<String, Object> dictRecord = this.activeRecordDAO.pandora()
					.SELECT_ALL_FROM(Constant.TableName.T_DICT_TAB)
					.EQUAL("V_DICT_TYPE", "SYS_SWICH").get();
			if (StringUtils.equals("on", dictRecord.getString("V_DICT_VALUE"))) {
				resultRecord.setColumn("SYS_SWITCH", "N");
			} else {
				resultRecord.setColumn("SYS_SWITCH", "Y");
			}
		} else {
			resultRecord.setColumn("SYS_SHOW", "N");
			resultRecord.setColumn("SYS_SWITCH", "N");
		}
		Record<String, Object> dataRecord = new RecordImpl<String, Object>();
		switch (ApacheShiroUtils.getCurrentUser().getString("USER_TYPE")) {
		case "0":
			dataRecord = getAdminUserData();
			break;
		case "1":
			dataRecord = getAgentData();
			break;
		case "2":
			dataRecord = getAppData();
			break;
		default:
			break;
		}
		resultRecord.setColumns(dataRecord);
		resultVO.setResult(resultRecord);
		resultVO.setSuccess(true);
		return resultVO;
	}

	public Record<String, Object> getAdminUserData() throws FacadeException {
		Record<String, Object> record = this.activeRecordDAO.statement()
				.selectOne("Home.getAppMoney");
		Record<String, Object> orderRecord = this.activeRecordDAO.statement()
				.selectOne("Home.getOrderCount");
		record.setColumns(orderRecord);
		return record;
	}

	public Record<String, Object> getAppData() throws FacadeException {
		Record<String, Object> appRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_APP_TAB)
				.EQUAL("V_USER_ID", ApacheShiroUtils.getCurrentUserID()).get();
		Record<String, Object> params = new RecordImpl<String, Object>();
		params.setColumn("V_APP_ID", appRecord.getString("V_USER_ID"));
		Record<String, Object> record = this.activeRecordDAO.statement()
				.selectOne("Home.getAppMoney");
		Record<String, Object> orderRecord = this.activeRecordDAO.statement()
				.selectOne("Home.getOrderCount", params);
		record.setColumns(orderRecord);
		return record;
	}

	public Record<String, Object> getAgentData() throws FacadeException {
		Record<String, Object> params = new RecordImpl<String, Object>();
		params.setColumn("V_USER_ID", ApacheShiroUtils.getCurrentUserID());
		Record<String, Object> record = this.activeRecordDAO.statement()
				.selectOne("Home.getAgentMoney");
		Record<String, Object> orderRecord = this.activeRecordDAO.statement()
				.selectOne("Home.getOrderCount", params);
		record.setColumns(orderRecord);
		return record;
	}

	@Path(value = "/isMatch", desc = "用户开启匹配")
	@Service
	public ResultVO<String> isMatch(AeolusData aeolusData)
			throws FacadeException {
		ResultVO<String> resultVO = new ResultVO<>();
		String userId = ApacheShiroUtils.getCurrentUserID();
		long count = this.activeRecordDAO.pandora()
				.SELECT_COUNT_FROM(Constant.TableName.T_USER_SHOW)
				.EQUAL("V_USER_ID", userId).count();
		if (count > 0) {
			resultVO.setResult("今日已进行匹配，无需重复匹配");
			return resultVO;
		}
		Record<String, Object> payTimeRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_PAYTIME_CONF)
				.EQUAL("V_USER_ID", userId).get();
		int time = 10;
		if (!CollectionUtils.isEmpty(payTimeRecord)) {
			time = payTimeRecord.getInteger("V_PAY_NUM");
		}
		long accountCount = this.activeRecordDAO.pandora()
				.SELECT_COUNT_FROM(Constant.TableName.T_PAY_ACCOUNT)
				.EQUAL("V_CREATE_USER", userId).EQUAL("V_IS_MATCH", "Y")
				.count();
		if (accountCount == 0) {
			resultVO.setResult("不存在可以支付的账户，请先添加账户");
			return resultVO;
		}
		Record<String, Object> userRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_SYS_USER_BASE)
				.EQUAL("USER_ID", userId).get();
		List<Record<String, Object>> list = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_PAY_ACCOUNT)
				.EQUAL("V_CREATE_USER", userId).EQUAL("V_IS_MATCH", "Y").list();
		for (Record<String, Object> account : list) {
			Record<String, Object> params = new RecordImpl<>();
			params.setColumn("ID", Random.generateUUID());
			params.setColumn("V_PAY_TIME", DateUtils.getDateTime());
			params.setColumn("V_IS_SHOW", "Y");
			params.setColumn("V_PAY_NUM", time);
			params.setColumn("V_USER_ID", userId);
			params.setColumn("V_ACCOUNT_ID", account.getString("ID"));
			this.activeRecordDAO.pandora()
					.INSERT_INTO(Constant.TableName.T_ACCOUNT_SHOW)
					.VALUES(params).excute();
		}
		Record<String, Object> params = new RecordImpl<>();
		params.setColumn("V_USER_ID", userId);
		params.setColumn("ID", Random.generateUUID());
		params.setColumn("V_PAY_TIME", DateUtils.getDateTime());
		params.setColumn("V_IS_PAY", "Y");
		params.setColumn("V_USER_LEVEL", userRecord.getString("USER_LEVEL"));
		this.activeRecordDAO.pandora()
				.INSERT_INTO(Constant.TableName.T_USER_SHOW).VALUES(params)
				.excute();
		resultVO.setSuccess(true);
		resultVO.setResult("匹配成功");
		return resultVO;
	}

	@Path(value = "/systemOperate")
	@Service
	public ResultVO<String> systemOperate(AeolusData aeolusData)
			throws Exception {
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Record<String, Object> params = new RecordImpl<>();
		params.setColumn("V_DICT_VALUE", record.getString("VALUE"));
		this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_DICT_TAB)
				.EQUAL("V_DICT_TYPE", "SYS_SWICH").SET(params).excute();
		resultVO.setSuccess(true);
		resultVO.setResult("操作成功");
		return resultVO;
	}
}

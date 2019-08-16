/**
 * 
 */
package com.sozone.fs.withdrawal;

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
import com.sozone.aeolus.dao.data.Pageable;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.FacadeException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.aeolus.utils.DateUtils;
import com.sozone.fs.common.Constant;

/**
 * @author Administrator
 *
 */
@Path(value = "/withdrawal", desc = "提现申请")
@Permission(Level.Authenticated)
public class WithdrawalAction {
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
	private static Logger logger = LoggerFactory
			.getLogger(WithdrawalAction.class);

	@Path(value = "/findPage", desc = "获取提现记录")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData)
			throws FacadeException {
		logger.debug(LogUtils.format("获取提现记录", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Pageable pageable = aeolusData.getPageRequest();
		record.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("IS_ADMIN", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		Page<Record<String, Object>> page = this.activeRecordDAO.statement()
				.selectPage("Withdrawal.list", pageable, record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value = "/save", desc = "提现")
	@Service
	public ResultVO<String> save(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("提现", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData
				.getTableRecord(Constant.TableName.T_WITHDRAW_TAB);
		Record<String, Object> bondRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_BOND_TODAY)
				.EQUAL("V_USER_ID", ApacheShiroUtils.getCurrentUserID()).get();
		if (CollectionUtils.isEmpty(bondRecord)) {
			resultVO.setResult("您暂无可提现余额");
			return resultVO;
		}
		double money = record.getDouble("V_MONEY");
		double surplusBond = bondRecord.getDouble("V_SURPLUS_BOND");
		if (money > surplusBond) {
			resultVO.setResult("提现金额超过可提现余额");
			return resultVO;
		}
		record.setColumn("ID", Random.generateUUID());
		record.setColumn("V_STATUS", "0");
		record.setColumn("V_APPLY_USER", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("V_APPLY_TIME", DateUtils.getDateTime());
		
		this.activeRecordDAO.pandora()
				.INSERT_INTO(Constant.TableName.T_WITHDRAW_TAB).VALUES(record)
				.excute();
		double account = surplusBond - money;
		record.clear();
		record.setColumn("V_SURPLUS_BOND", account);
		this.activeRecordDAO.pandora()
				.UPDATE(Constant.TableName.T_BOND_TODAY)
				.EQUAL("V_USER_ID", ApacheShiroUtils.getCurrentUserID()).SET(record).excute();
		resultVO.setSuccess(true);
		resultVO.setResult("提现申请");
		return resultVO;
	}

	@Path(value = "/audit", desc = "提现审核")
	@Service
	public ResultVO<String> audit(AeolusData aeolusData) throws FacadeException {
		ResultVO<String> resultVO = new ResultVO<String>(false);
		Record<String, Object> record = aeolusData.getRecord();
		String id = record.getString("ID");
		Record<String, Object> params = new RecordImpl<String, Object>();
		String status = record.getString("V_STATUS");
		params.setColumn("V_STATUS", record.getString("V_STATUS"));
		params.setColumn("V_AUDIT_USER", ApacheShiroUtils.getCurrentUserID());
		params.setColumn("V_AUDIT_TIME", DateUtils.getDateTime());
		this.activeRecordDAO.pandora()
				.UPDATE(Constant.TableName.T_WITHDRAW_TAB).EQUAL("ID", id)
				.SET(params).excute();
		if (StringUtils.equals("2", status)) {
			Record<String, Object> withRecord = this.activeRecordDAO.pandora()
					.SELECT_ALL_FROM(Constant.TableName.T_WITHDRAW_TAB)
					.EQUAL("ID", id).get();
			String userId = withRecord.getString("V_APPLY_USER");
			Record<String, Object> bondRecord = this.activeRecordDAO.pandora()
					.SELECT_ALL_FROM(Constant.TableName.T_BOND_TODAY)
					.EQUAL("V_USER_ID", userId).get();
			double surplus = bondRecord.getDouble("V_SURPLUS_BOND");
			double money = withRecord.getDouble("V_MONEY");
			double account = surplus + money;
			params.clear();
			params.setColumn("V_SURPLUS_BOND", account);
			this.activeRecordDAO.pandora()
					.UPDATE(Constant.TableName.T_BOND_TODAY)
					.EQUAL("V_USER_ID", userId).SET(params).excute();
		}
		resultVO.setSuccess(true);
		resultVO.setResult("操作成功");
		return resultVO;
	}
}

package com.sozone.fs.commission;

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
import com.sozone.aeolus.utils.DateUtils;
import com.sozone.fs.common.Constant;

@Path(value = "/commission", desc = "提成信息处理接口")
@Permission(Level.Authenticated)
public class CommissionAction {
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
	private static Logger logger = LoggerFactory.getLogger(CommissionAction.class);

	@Path(value = "/findPage", desc = "获取提成记录")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("获取提成记录", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Pageable pageable = aeolusData.getPageRequest();
		record.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("IS_ADMIN", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		Page<Record<String, Object>> page = this.activeRecordDAO.statement().selectPage("Commission.list", pageable,
				record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value = "/save", desc = "提成申请")
	@Service
	public ResultVO<String> save(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("提成申请", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getTableRecord(Constant.TableName.T_COMMISSION_TAB);
		Record<String, Object> params = new RecordImpl<>();
		params.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		Record<String, Object> appRecord =this.activeRecordDAO.statement().selectOne("Commission.getCommssion",
				params);
		double cash = appRecord.getDouble("V_CASH_COLLECTION");
		double money = record.getDouble("V_MONEY");
		if (cash < money) {
			resultVO.setResult("提成金额超出");
			return resultVO;
		}
		double cashMoney = cash - money;
		record.setColumn("ID", Random.generateUUID());
		record.setColumn("V_APP_ID", appRecord.getString("V_APP_ID"));
		record.setColumn("V_COMMISSION_TIME", DateUtils.getDateTime());
		this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_COMMISSION_TAB).VALUES(record).excute();
		params.clear();
		params.setColumn("V_CASH_COLLECTION", cashMoney);
		params.clear();
		params.setColumn("V_CASH_COLLECTION", cashMoney);
		this.activeRecordDAO.pandora()
				.UPDATE(Constant.TableName.T_COLLECTION_TAB)
				.EQUAL("V_APP_ID", appRecord.getString("V_APP_ID")).SET(params).excute();
		resultVO.setSuccess(true);
		resultVO.setResult("申请成功");
		return resultVO;
	}
	
	@Path(value="/getRate",desc="获取费率")
	@Service
	public ResultVO<Record<String, Object>> getRate(AeolusData aeolusData) throws FacadeException{
		ResultVO<Record<String, Object>> resultVO = new ResultVO<>();
		Record<String,Object> appRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_APP_TAB).EQUAL("V_USER_ID",ApacheShiroUtils.getCurrentUserID()).get();
		Record<String, Object> dictRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_DICT_TAB).EQUAL("V_DICT_TYPE","WITHDRAW_HAND_CHARGE").EQUAL("V_DISABLED", "Y").get();
		Record<String, Object> record = new RecordImpl<>();
		record.setColumn("V_RATE",appRecord.getString("V_RATE"));
		record.setColumn("V_FORMALITIES",dictRecord.getString("V_DICT_VALUE"));
		resultVO.setResult(record);
		resultVO.setSuccess(true);
		return resultVO;
	}
	
	@Path(value = "/audit", desc = "提现审核")
	@Service
	public ResultVO<String> audit(AeolusData aeolusData) throws FacadeException {
		ResultVO<String> resultVO = new ResultVO<String>(false);
		Record<String, Object> record = aeolusData.getRecord();
		String id = record.getString("ID");
		Record<String, Object> commissionRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_COMMISSION_TAB)
				.EQUAL("ID", id).get();
		if(!StringUtils.equals("0", commissionRecord.getString("V_STATUS"))) {
			resultVO.setResult("订单已处理");
			return resultVO;
		}
		Record<String, Object> params = new RecordImpl<String, Object>();
		String status = record.getString("V_STATUS");
		params.setColumn("V_STATUS", record.getString("V_STATUS"));
		params.setColumn("V_AUDIT_USER", ApacheShiroUtils.getCurrentUserID());
		params.setColumn("V_AUDIT_TIME", DateUtils.getDateTime());
		this.activeRecordDAO.pandora()
				.UPDATE(Constant.TableName.T_COMMISSION_TAB).EQUAL("ID", id)
				.SET(params).excute();
		if (StringUtils.equals("2", status)) {
			
			String appID = commissionRecord.getString("V_APP_ID");
			Record<String, Object> collectionRecord = this.activeRecordDAO.pandora()
					.SELECT_ALL_FROM(Constant.TableName.T_COLLECTION_TAB)
					.EQUAL("V_APP_ID",appID).get();
			double cash = collectionRecord.getDouble("V_CASH_COLLECTION");
			double money = commissionRecord.getDouble("V_MONEY");
			double account = cash + money;
			params.clear();
			params.setColumn("V_CASH_COLLECTION", account);
			this.activeRecordDAO.pandora()
					.UPDATE(Constant.TableName.T_COLLECTION_TAB)
					.EQUAL("V_APP_ID", appID).SET(params).excute();
		}
		resultVO.setSuccess(true);
		resultVO.setResult("操作成功");
		return resultVO;
	}
}

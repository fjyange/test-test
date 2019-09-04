package com.sozone.fs.topup;

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

@Path(value = "/topup", desc = "充值管理")
@Permission(Level.Authenticated)
public class TopupAction {
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
	private static Logger logger = LoggerFactory.getLogger(TopupAction.class);

	@Path(value = "/findPage", desc = "获取充值记录")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("获取充值记录", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Pageable pageable = aeolusData.getPageRequest();
		record.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("IS_ADMIN", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		Page<Record<String, Object>> page = this.activeRecordDAO.statement().selectPage("Charge.list", pageable,
				record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value = "/save", desc = "充值")
	@Service
	public ResultVO<String> save(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("充值", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getTableRecord(Constant.TableName.T_USER_TOPUP);
		String id = record.getString("ID");
		if (StringUtils.isEmpty(id)) {
			record.setColumn("ID", Random.generateUUID());
			record.setColumn("V_STATUS", "0");
			record.setColumn("V_TOPUP_USER", ApacheShiroUtils.getCurrentUserID());
			record.setColumn("V_TOPUP_TIME", DateUtils.getDateTime());
			this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_USER_TOPUP).VALUES(record).excute();
		} else {
			record.remove("V_TOPUP_USER");
			record.remove("V_TOPUP_TIME");
			this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_USER_TOPUP).EQUAL("ID", id).SET(record).excute();
		}

		resultVO.setSuccess(true);
		resultVO.setResult("充值成功");
		return resultVO;
	}

	@Path(value = "/audit", desc = "充值审核")
	@Service
	public ResultVO<String> audit(AeolusData aeolusData) throws FacadeException {
		ResultVO<String> resultVO = new ResultVO<String>(false);
		Record<String, Object> record = aeolusData.getRecord();
		String id = record.getString("ID");
		Record<String, Object> topupRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_USER_TOPUP).EQUAL("ID", id).get();
		if (!StringUtils.equals("0", topupRecord.getString("V_STATUS"))) {
			resultVO.setResult("订单已处理");
			return resultVO;
		}
		Record<String, Object> params = new RecordImpl<String, Object>();
		String status = record.getString("V_STATUS");
		params.setColumn("V_STATUS", record.getString("V_STATUS"));
		this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_USER_TOPUP).EQUAL("ID", id).SET(params).excute();
		if (StringUtils.equals("1", status)) {
			String userId = topupRecord.getString("V_TOPUP_USER");
			Record<String, Object> bondRecord = this.activeRecordDAO.pandora()
					.SELECT_ALL_FROM(Constant.TableName.T_BOND_TODAY).EQUAL("V_USER_ID", userId).get();
			params.clear();
			if (CollectionUtils.isEmpty(bondRecord)) {
				params.setColumn("ID", Random.generateUUID());
				params.setColumn("V_USER_ID", userId);
				params.setColumn("V_WX_RECEIVABLES", "0");
				params.setColumn("V_ALI_RECEIVABLES", "0");
				params.setColumn("V_COUNT_RECEIVABLES", "0");
				params.setColumn("V_SURPLUS_BOND", topupRecord.getString("V_MONEY"));
				params.setColumn("V_CREATE_TIME", DateUtils.getDate());
				this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_BOND_TODAY).VALUES(params).excute();
			} else {
				double surplus = bondRecord.getDouble("V_SURPLUS_BOND");
				double money = topupRecord.getDouble("V_MONEY");
				double account = surplus + money;
				params.setColumn("V_SURPLUS_BOND", account);
				this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_BOND_TODAY).EQUAL("V_USER_ID", userId)
						.SET(params).excute();
			}
		}
		resultVO.setSuccess(true);
		resultVO.setResult("审核成功");
		return resultVO;
	}
}

package com.sozone.fs.account;

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
import com.sozone.fs.third.ThirdAction;


@Path(value = "/account", desc = "用户信息处理接口")
@Permission(Level.Authenticated)
public class AccountAction {
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
	private static Logger logger = LoggerFactory.getLogger(AccountAction.class);

	@Path(value = "/findPage", desc = "获取账户列表")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("获取账户列表", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Pageable pageable = aeolusData.getPageRequest();
		record.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("IS_ADMIN", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		Page<Record<String, Object>> page = this.activeRecordDAO.statement().selectPage("Account.list", pageable,
				record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value = "/save", desc = "添加账户")
	@Service
	public ResultVO<String> save(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("添加账户", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		String id = record.getString("ID");
		record.setColumn("V_UPDATE_USER", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("V_MATCH_TIME", DateUtils.getDateTime());
		record.setColumn("V_MATCH_USER", ApacheShiroUtils.getCurrentUserID());
		String fileId = record.getString("V_FILE_ID");
		if (StringUtils.isEmpty(fileId)) {
			record.remove("V_FILE_ID");
		}
		if (StringUtils.isEmpty(id)) {
			id = Random.generateUUID();
			record.setColumn("ID", id);
			record.setColumn("V_CREATE_USER", ApacheShiroUtils.getCurrentUserID());
			this.activeRecordDAO.auto().table(Constant.TableName.T_PAY_ACCOUNT).save(record);
			Record<String, Object> account = new RecordImpl<>();
			account.setColumn("ID", Random.generateUUID());
			account.setColumn("V_ACCOUNT_ID", id);
			account.setColumn("V_TOTAL_MONEY", "0");
			account.setColumn("V_ALI_MONEY", "0");
			account.setColumn("V_WX_MONEY", "0");
			this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_ACCOUNT_COLLECTION).VALUES(account)
					.excute();
			long count = this.activeRecordDAO.pandora().SELECT_COUNT_FROM(Constant.TableName.T_USER_SHOW)
					.EQUAL("V_USER_ID", ApacheShiroUtils.getCurrentUserID()).count();
			if (count > 0) {
				Record<String, Object> userRecord = this.activeRecordDAO.pandora()
						.SELECT_ALL_FROM(Constant.TableName.T_SYS_USER_BASE)
						.EQUAL("USER_ID", ApacheShiroUtils.getCurrentUserID()).get();
				Record<String, Object> timeRecord = this.activeRecordDAO.pandora()
						.SELECT_ALL_FROM(Constant.TableName.T_PAYTIME_CONF)
						.EQUAL("V_USER_ID", ApacheShiroUtils.getCurrentUserID()).get();
				Record<String, Object> params = new RecordImpl<>();
				params.setColumn("ID", Random.generateUUID());
				params.setColumn("V_ACCOUNT_ID", id);
				params.setColumn("V_USER_ID", ApacheShiroUtils.getCurrentUserID());
				params.setColumn("V_IS_SHOW", userRecord.getString("IS_MATCH"));
				params.setColumn("V_PAY_TIME", DateUtils.getDateTime());
				if (CollectionUtils.isEmpty(timeRecord)) {
					params.setColumn("V_PAY_NUM", "10");
				} else {
					params.setColumn("V_PAY_NUM", timeRecord.getString("V_PAY_NUM"));
				}

				this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_ACCOUNT_SHOW).VALUES(params).excute();
			}
		} else {
			this.activeRecordDAO.auto().table(Constant.TableName.T_PAY_ACCOUNT).modify(record);
		}
		Record<String, Object> param = new RecordImpl<>();
		param.setColumn("V_FILE_ID", record.getString("V_FILE_ID")).setColumn("V_BUS_ID", id);
		this.activeRecordDAO.auto().table(Constant.TableName.T_FILE_TAB).setCondition("and", "ID=#{V_FILE_ID}")
				.modify(param);
		resultVO.setSuccess(true);
		resultVO.setResult("账户添加成功");
		return resultVO;
	}

	@Path(value = "delete", desc = "删除账户")
	@Service
	public ResultVO<String> delete(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("删除用户", aeolusData));
		ResultVO<String> resultVO = new ResultVO<String>();
		Record<String, Object> record = aeolusData.getRecord();
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_PAY_ACCOUNT).EQUAL("ID", record.getString("ID"))
				.excute();
		resultVO.setSuccess(true);
		resultVO.setResult("删除用户成功");
		return resultVO;
	}

	@Path(value = "/accountConf", desc = "账户配置")
	@Service
	public ResultVO<String> accountConf(AeolusData aeolusData) throws FacadeException {
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		if (StringUtils.isNotBlank(record.getString("V_PAY_TOTAL"))) {
			if (!ThirdAction.isDoubleOrFloat(record.getString("V_PAY_TOTAL"))) {
				resultVO.setResult("金额不是数字");
				return resultVO;
			}
		}
		
		this.activeRecordDAO.auto().table(Constant.TableName.T_PAY_ACCOUNT).setCondition("and", "ID=#{ID}")
				.modify(record);
		if (StringUtils.isNotBlank(record.getString("V_IS_MATCH"))) {
			Record<String, Object> params = new RecordImpl<>();
			if (StringUtils.equals("N", record.getString("V_IS_MATCH"))) {
				params.setColumn("V_IS_SHOW", "N");
			} else {
				params.setColumn("V_IS_SHOW", "Y");
			}
			this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_ACCOUNT_SHOW)
					.EQUAL("V_ACCOUNT_ID", record.getString("ID")).SET(params).excute();
		}
		resultVO.setSuccess(true);
		resultVO.setResult("平台配置修改成功");
		return resultVO;
	}
}

/**
 * 
 */
package com.sozone.fs.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.dao.StatefulDAO;
import com.sozone.aeolus.dao.StatefulDAOImpl;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.exception.ServiceException;
import com.sozone.aeolus.timer.AeolusJobExecutionContext;
import com.sozone.aeolus.timer.job.AeolusJob;
import com.sozone.fs.common.Constant;


public class DataJob implements AeolusJob {

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(DataJob.class);

	@Override
	public void run(AeolusJobExecutionContext arg0) throws ServiceException {
		logger.error(LogUtils.format("清除一天数据开始"));
		StatefulDAO statefulDAO = null;
		try {
			statefulDAO = new StatefulDAOImpl();
			statefulDAO.pandora().DELETE_FROM(Constant.TableName.T_USER_SHOW).excute();
			statefulDAO.pandora().DELETE_FROM(Constant.TableName.T_ACCOUNT_SHOW).excute();
			Record<String, Object> record = new RecordImpl<String, Object>();
			record.setColumn("V_TOTAL_MONEY", "0");
			record.setColumn("V_ALI_MONEY", "0");
			record.setColumn("V_WX_MONEY", "0");
			record.setColumn("V_PAY_NUM", "0");
			statefulDAO.pandora().UPDATE(Constant.TableName.T_ACCOUNT_COLLECTION).SET(record).excute();
			statefulDAO.sql("update t_bond_today set V_WX_RECEIVABLES = 0,V_ALI_RECEIVABLES=0,V_COUNT_RECEIVABLES = 0,V_LOCK_MONEY= 0,V_YS_BOND= V_SURPLUS_BOND").update();
			statefulDAO.sql("update t_collection_tab set V_WX_COLLECTION = 0,V_ALI_COLLECTION = 0,V_TOTAL_COLLECTION = 0,V_LAST_COLLECTION = V_CASH_COLLECTION").update();
			statefulDAO.pandora().DELETE_FROM(Constant.TableName.T_ORDER_OPT).excute();
			statefulDAO.pandora().DELETE_FROM(Constant.TableName.T_SEND_TAB).excute();
			statefulDAO.sql("delete from t_order_history where  date_add(V_CREATE_TIME, interval 5 DAY) < now()");
			statefulDAO.sql("insert into t_order_history select * from t_order_tab").insert();
			statefulDAO.sql("delete from t_order_tab").delete();
			statefulDAO.sql("delete from t_recieve_tab where date_add(V_RECIEVE_TIME, interval 1 DAY) < now()").delete();
			statefulDAO.sql("delete from t_withdraw_tab where  date_add(V_APPLY_TIME, interval 5 DAY) < now()").delete();
			statefulDAO.sql("delete from t_user_topup where  date_add(V_TOPUP_TIME, interval 5 DAY) < now()").delete();
			statefulDAO.sql("delete from t_commission_tab where  date_add(V_COMMISSION_TIME, interval 5 DAY) < now()").delete();
			statefulDAO.commit();
		} catch (Exception e) {
			statefulDAO.rollback();
			logger.error(LogUtils.format("清除数据异常", e.getMessage()), e);
			throw new ServiceException("", "清除数据异常" + e.getMessage());
		} finally {
			statefulDAO.close();
			logger.error(LogUtils.format("清除一天数据结束"));
		}
	}
}

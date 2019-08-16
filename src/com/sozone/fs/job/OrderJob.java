package com.sozone.fs.job;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jacob.com.STA;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.StatefulDAO;
import com.sozone.aeolus.dao.StatefulDAOImpl;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.exception.DAOException;
import com.sozone.aeolus.exception.ServiceException;
import com.sozone.aeolus.timer.AeolusJobExecutionContext;
import com.sozone.aeolus.timer.job.AeolusJob;
import com.sozone.fs.common.Constant;

public class OrderJob implements AeolusJob{

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(OrderJob.class);
	
	@Override
	public void run(AeolusJobExecutionContext arg0) throws ServiceException {
		
		logger.debug(LogUtils.format("订单定时器开启"));
		StatefulDAO statefulDAO = null;
		try {
			statefulDAO = new StatefulDAOImpl();
			Record<String, Object> paramsRecord = new RecordImpl<String, Object>();
			List<Record<String, Object>> list = statefulDAO.statement().selectList("Job.orderList");
			for(Record<String, Object> record :list) {
				StatefulDAO orderDao = null;
				try {
					orderDao = new StatefulDAOImpl();
					paramsRecord.clear();
					paramsRecord.setColumn("V_STATUS","2");
					statefulDAO.pandora().UPDATE(Constant.TableName.T_ORDER_TAB).EQUAL("ID", record.getString("ID")).SET(paramsRecord).excute();
					paramsRecord.clear();
					double money = record.getDouble("V_MONEY");
					String payType = record.getString("V_PAY_TYPE");
					paramsRecord.setColumn("V_MONEY", -money);
					if (StringUtils.equals("01", payType)){
						paramsRecord.setColumn("ALI_MONEY", -money);
						paramsRecord.setColumn("WX_MONEY", "0");
					}else {
						paramsRecord.setColumn("WX_MONEY", -money);
						paramsRecord.setColumn("ALI_MONEY", "0");
					}
					paramsRecord.setColumn("V_ACCOUNT_ID", record.getString("V_BELONG_ACCOUNT"));
					orderDao.statement().update("Order.updateAccount", paramsRecord);
					paramsRecord.remove("V_ACCOUNT_ID");
					paramsRecord.setColumn("SURPLUS_BOND", money);
					paramsRecord.setColumn("V_USER_ID", record.getString("V_BELONG_USER"));
					orderDao.statement().update("Order.updateUser",paramsRecord);
					orderDao.commit();
				} catch (Exception e) {
					orderDao.rollback();
					logger.error(LogUtils.format("修改订单号["+record.getString("V_ORDER_NO")+"]状态失败", e.getMessage()));
				}finally{
					orderDao.close();
				}
			}	
		} catch (Exception e) {
			logger.error(LogUtils.format("订单定时器异常", e.getMessage()));
			throw new ServiceException("","订单定时器异常"+e.getMessage());
		}finally {
			statefulDAO.close();
		}
	}

}

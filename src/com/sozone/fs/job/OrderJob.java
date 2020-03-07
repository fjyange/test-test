package com.sozone.fs.job;

import java.util.List;

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

public class OrderJob implements AeolusJob {

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(OrderJob.class);
	private static boolean IS_EXCUTE = false;

	@Override
	public synchronized void run(AeolusJobExecutionContext arg0) throws ServiceException {
		logger.debug(LogUtils.format("订单定时器开启"));
		if (!IS_EXCUTE) {
			IS_EXCUTE = true;
			StatefulDAO statefulDAO = null;
			try {
				statefulDAO = new StatefulDAOImpl();
				List<Record<String, Object>> list = statefulDAO.statement().selectList("Job.orderList");
				for (Record<String, Object> record : list) {
					StatefulDAO orderDao = null;
					try {
						Record<String, Object> paramsRecord = new RecordImpl<String, Object>();
						orderDao = new StatefulDAOImpl();
						paramsRecord.clear();
						paramsRecord.setColumn("V_STATUS", "2");
						// 修改订单状态
						orderDao.pandora().UPDATE(Constant.TableName.T_ORDER_TAB).EQUAL("ID", record.getString("ID"))
								.SET(paramsRecord).excute();
						paramsRecord.clear();

						double money = record.getDouble("V_MONEY");
						paramsRecord.setColumn("V_LOCK_MONEY", -money);
						paramsRecord.setColumn("V_USER_ID", record.getString("V_BELONG_USER"));
						// 修改锁住金额
						orderDao.statement().update("Order.updateUserLockMoney", paramsRecord);
//						paramsRecord.clear();
//						int times = record.getInteger("V_PAY_NUM");
//						times = times + 1;
//						paramsRecord.setColumn("V_PAY_NUM", times);
//						orderDao.pandora().UPDATE(Constant.TableName.T_ACCOUNT_SHOW)
//								.EQUAL("V_ACCOUNT_ID", record.getString("V_BELONG_ACCOUNT")).SET(paramsRecord).excute();
						orderDao.commit();
					} catch (Exception e) {
						orderDao.rollback();
						logger.error(
								LogUtils.format("修改订单号[" + record.getString("V_ORDER_NO") + "]状态失败", e.getMessage()));
					} finally {
						orderDao.close();
					}
				}
			} catch (Exception e) {
				logger.error(LogUtils.format("订单定时器异常", e.getMessage()));
				throw new ServiceException("", "订单定时器异常" + e.getMessage());
			} finally {
				IS_EXCUTE = false;
				statefulDAO.close();
			}
		}
	}

}

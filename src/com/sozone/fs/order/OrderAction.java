package com.sozone.fs.order;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.Utils;
import com.alibaba.fastjson.JSON;
import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.authorize.utlis.ApacheShiroUtils;
import com.sozone.aeolus.authorize.utlis.LogUtils;
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
import com.sozone.fs.common.util.HttpClientUtils;
import com.sozone.fs.menu.MenuAction;
import com.sozone.fs.third.ThirdAction;

/**
 * 
 * @author yange
 *
 */
@Path(value = "/order", desc = "订单处理")
@Permission(Level.Authenticated)
public class OrderAction {
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
	private static Logger logger = LoggerFactory.getLogger(OrderAction.class);

	@Path(value = "/findPage", desc = "获取订单列表")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData)
			throws FacadeException {
		logger.debug(LogUtils.format("获取订单信息", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		record.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("IS_ADMIN", ApacheShiroUtils.getCurrentUser()
				.getString("IS_ADMIN"));
		Page<Record<String, Object>> page = this.activeRecordDAO.statement()
				.selectPage("Order.orderList", aeolusData.getPageRequest(),
						record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value = "/audit", desc = "订单审核")
	@Service
	public ResultVO<String> audit(AeolusData aeolusData) throws FacadeException {
		ResultVO<String> resultVO = new ResultVO<String>(false);
		Record<String, Object> record = aeolusData.getRecord();
		String id = record.getString("ID");
		String status = record.getString("V_STATUS");
		Record<String, Object> orderRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_ORDER_TAB)
				.EQUAL("ID", id).get();
		Record<String, Object> updateRecord = new RecordImpl<>();
		String payType = orderRecord.getString("V_PAY_TYPE");
		String appID = orderRecord.getString("V_BELONG_APP");
		String userID = orderRecord.getString("V_BELONG_USER");
		String account = orderRecord.getString("V_BELONG_ACCOUNT");
		double money = orderRecord.getDouble("V_MONEY");
		
		if (StringUtils.equals("4", status)
				&& !StringUtils.equals("2", orderRecord.getString("V_STATUS"))) {
			updateRecord.setColumn("V_MONEY", String.valueOf(-money));
			if (StringUtils.equals("01", payType)) {
				updateRecord.setColumn("ALI_MONEY", String.valueOf(-money));
				updateRecord.setColumn("WX_MONEY", "0");
			} else {
				updateRecord.setColumn("ALI_MONEY", "0");
				updateRecord.setColumn("WX_MONEY", String.valueOf(-money));
			}
			updateRecord.remove("V_APP_ID");
			updateRecord.setColumn("V_ACCOUNT_ID", account);
			this.activeRecordDAO.statement().update("Order.updateAccount",
					updateRecord);
			updateRecord.remove("V_ACCOUNT_ID");
			updateRecord.setColumn("V_USER_ID", userID);
			updateRecord.setColumn("SURPLUS_BOND", String.valueOf(money));
			this.activeRecordDAO.statement().update("Order.updateUser",
					updateRecord);
		} else if (StringUtils.equals("3", status)) {
			updateRecord.setColumn("V_APP_ID", appID);
			updateRecord.setColumn("V_MONEY", String.valueOf(money));
			if (StringUtils.equals("01", payType)) {
				updateRecord.setColumn("ALI_MONEY", String.valueOf(money));
				updateRecord.setColumn("WX_MONEY", "0");
			} else {
				updateRecord.setColumn("ALI_MONEY", "0");
				updateRecord.setColumn("WX_MONEY", String.valueOf(money));
			}
			this.activeRecordDAO.statement().update("Order.updateApp",
					updateRecord);
			updateRecord.remove("V_APP_ID");
			updateRecord.setColumn("V_ACCOUNT_ID", account);
			this.activeRecordDAO.statement().update("Order.updateAccount",
					updateRecord);
			updateRecord.remove("V_ACCOUNT_ID");
			updateRecord.setColumn("V_USER_ID", userID);
			updateRecord.setColumn("SURPLUS_BOND", String.valueOf(-money));
			this.activeRecordDAO.statement().update("Order.updateUser",
					updateRecord);
		} else if (StringUtils.equals("1", status)
				&& StringUtils.equals("2", record.getString("V_STATUS"))) {
			resultVO.setSuccess(false);
			resultVO.setResult("订单已过期，请联系客服进行补单");
			return resultVO;
		} else if (StringUtils.equals("1", status)) {
			Date date = DateUtils.parseDate(
					orderRecord.getString("V_CREATE_TIME"),
					"yyyy-MM-dd HH:mm:ss");
			long outtime = date.getTime() + 3 * 24 * 60 * 60 * 1000;
			if (outtime < System.currentTimeMillis()) {
				resultVO.setSuccess(false);
				resultVO.setResult("订单已过期，请联系客服进行补单");
				return resultVO;
			}
			orderRecord.getDate("V_CREATE_TIME", "yyyy-MM");
			updateRecord.setColumn("V_APP_ID", appID);
			updateRecord.setColumn("V_MONEY", String.valueOf(money));
			if (StringUtils.equals("01", payType)) {
				updateRecord.setColumn("ALI_MONEY", String.valueOf(money));
				updateRecord.setColumn("WX_MONEY", "0");
			} else {
				updateRecord.setColumn("ALI_MONEY", "0");
				updateRecord.setColumn("WX_MONEY", String.valueOf(money));
			}
			this.activeRecordDAO.statement().update("Order.updateApp",
					updateRecord);
			
		}
		if (StringUtils.equals("3", status) || StringUtils.equals("1", status)) {
			Record<String, Object> appRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_APP_TAB).EQUAL("ID", appID).get();
			if (!CollectionUtils.isEmpty(appRecord)) {
				Record<String, Object> sendRecord = new RecordImpl<>();
				sendRecord.setColumn("orderno", orderRecord.getString("V_ORDER_NO"));
				sendRecord.setColumn("money", orderRecord.getString("V_MONEY"));
				sendRecord.setColumn("status", "1");
				String signStr = ThirdAction.getSign(sendRecord, appRecord.getString("V_SECRET"));
				sendRecord.setColumn("sign", signStr);
				try {
					if(StringUtils.isNotBlank(orderRecord.getString("V_NOTIFY_URL"))) {
						String result =HttpClientUtils.sendJsonPostRequest(orderRecord.getString("V_NOTIFY_URL"), JSON.toJSONString(sendRecord), "utf-8");
						System.out.println(result);
					}
				} catch (Exception e) {
					logger.error(LogUtils.format("发送数据失败", e.getMessage()));
				}
			}
		}
		Record<String, Object> params = new RecordImpl<String, Object>();
		params.setColumn("V_STATUS", record.getString("V_STATUS"));
		params.setColumn("V_UPDATE_USER", ApacheShiroUtils.getCurrentUserID());
		params.setColumn("V_UPDATE_TIME", DateUtils.getDateTime());
		this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_ORDER_TAB)
				.EQUAL("ID", id).SET(params).excute();
		resultVO.setSuccess(true);
		resultVO.setResult("操作成功");
		return resultVO;
	}

}

package com.sozone.fs.order;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.authorize.utlis.ApacheShiroUtils;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.authorize.utlis.Random;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.StatefulDAO;
import com.sozone.aeolus.dao.StatefulDAOImpl;
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
import com.sozone.fs.rsa.RSAEncrypt;
import com.sozone.fs.third.ThirdAction;

@Path(value = "/order", desc = "订单处理")
@Permission(Level.Authenticated)
public class OrderAction
{
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
	public void setActiveRecordDAO(ActiveRecordDAO activeRecordDAO)
	{
		this.activeRecordDAO = activeRecordDAO;
	}

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(OrderAction.class);

	@Path(value = "/findPage", desc = "获取订单列表")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData) throws FacadeException
	{
		logger.debug(LogUtils.format("获取订单信息", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		String searchTime = record.getString("SEARCH_TIME");
		if (StringUtils.isNotBlank(searchTime))
		{
			JSONArray jsonArray = record.getJSONArray("SEARCH_TIME");
			record.setColumn("START_TIME", jsonArray.get(0));
			record.setColumn("END_TIME", jsonArray.get(1));
		}
		record.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("IS_ADMIN", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		Page<Record<String, Object>> page = this.activeRecordDAO.statement().selectPage("Order.orderList",
				aeolusData.getPageRequest(), record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value = "/findHistory", desc = "获取订单列表")
	@Service
	public ResultVO<Page<Record<String, Object>>> findHistory(AeolusData aeolusData) throws FacadeException
	{
		logger.debug(LogUtils.format("获取订单信息", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		record.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("IS_ADMIN", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		String searchTime = record.getString("SEARCH_TIME");
		if (StringUtils.isNotBlank(searchTime))
		{
			JSONArray jsonArray = record.getJSONArray("SEARCH_TIME");
			record.setColumn("START_TIME", jsonArray.get(0));
			record.setColumn("END_TIME", jsonArray.get(1));
		}
		Page<Record<String, Object>> page = this.activeRecordDAO.statement().selectPage("Order.historyList",
				aeolusData.getPageRequest(), record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value = "/errorOrder", desc = "错单重提")
	@Service
	public ResultVO<String> errorOrder(AeolusData aeolusData) throws FacadeException
	{
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		String id = record.getString("ID");
		Double money = record.getDouble("V_PAY_TOTAL");
		Record<String, Object> orderRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_ORDER_TAB).EQUAL("ID", id).get();
		String userID = orderRecord.getString("V_BELONG_USER");
		if (StringUtils.equals("1", orderRecord.getString("V_STATUS"))
				|| StringUtils.equals("3", orderRecord.getString("V_STATUS")))
		{
			resultVO.setResult("订单已被确认，无法重提错单");
			return resultVO;
		}
		Date date = DateUtils.parseDate(orderRecord.getString("V_CREATE_TIME"), "yyyy-MM-dd HH:mm:ss");
		long outtime = date.getTime() + 5 * 58 * 1000;
		if (outtime > System.currentTimeMillis())
		{
			resultVO.setSuccess(false);
			resultVO.setResult("订单未过期，请稍后");
			return resultVO;
		}
		Record<String, Object> bondRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_BOND_TODAY).EQUAL("V_USER_ID", userID).get();
		if (bondRecord.getDouble("V_SURPLUS_BOND") < money)
		{
			resultVO.setResult("保证金额度不足，请充值");
			return resultVO;
		}

		Record<String, Object> userRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_SYS_USER_BASE)
				.EQUAL("USER_ID", ApacheShiroUtils.getCurrentUserID()).get();
		if (CollectionUtils.isEmpty(userRecord))
		{
			resultVO.setResult("当前登录用户不存在");
			return resultVO;
		}

		String validKey = userRecord.getString("VALID_KEY");
		if (StringUtils.isEmpty(validKey))
		{
			resultVO.setResult("无错单权限");
			return resultVO;
		}

		if (!StringUtils.equals(validKey, record.getString("V_KEY")))
		{
			resultVO.setResult("验证秘钥错误");
			return resultVO;
		}

		orderRecord.remove("ID");
		orderRecord.remove("V_MONEY");
		orderRecord.setColumn("ID", Random.generateUUID());
		orderRecord.setColumn("V_MONEY", money);
		orderRecord.setColumn("V_UPDATE_USER", ApacheShiroUtils.getCurrentUserID());
		orderRecord.setColumn("V_UPDATE_TIME", DateUtils.getDateTime());
		orderRecord.setColumn("V_STATUS", "4");
		this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_ORDER_TAB).VALUES(orderRecord).excute();
		resultVO.setSuccess(true);
		resultVO.setResult("提交成功");
		return resultVO;
	}

	@Path(value = "/audit", desc = "订单审核")
	@Service
	public ResultVO<String> audit(AeolusData aeolusData) throws Exception
	{
		ResultVO<String> resultVO = new ResultVO<String>(false);
		Record<String, Object> record = aeolusData.getRecord();
		String id = record.getString("ID");
		String status = record.getString("V_STATUS");
		StatefulDAO statefulDAO = null;
		StatefulDAO orderDao = null;
		Record<String, Object> orderRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_ORDER_TAB).EQUAL("ID", id).get();
		String payType = orderRecord.getString("V_PAY_TYPE");
		String appID = orderRecord.getString("V_BELONG_APP");
		String userID = orderRecord.getString("V_BELONG_USER");
		String account = orderRecord.getString("V_BELONG_ACCOUNT");
		double money = orderRecord.getDouble("V_MONEY");
		try
		{
			statefulDAO = new StatefulDAOImpl();
			orderDao = new StatefulDAOImpl();
			Record<String, Object> updateRecord = new RecordImpl<>();
			if (StringUtils.equals("1", orderRecord.getString("V_STATUS"))
					|| StringUtils.equals("3", orderRecord.getString("V_STATUS")))
			{
				resultVO.setResult("订单已被确认，请刷新页面");
				return resultVO;
			}
			if (StringUtils.equals("4", status) && !StringUtils.equals("2", orderRecord.getString("V_STATUS")))
			{
				updateRecord.setColumn("V_MONEY", String.valueOf(-money));
				if (StringUtils.equals("01", payType))
				{
					updateRecord.setColumn("ALI_MONEY", String.valueOf(-money));
					updateRecord.setColumn("WX_MONEY", "0");
				}
				else
				{
					updateRecord.setColumn("ALI_MONEY", "0");
					updateRecord.setColumn("WX_MONEY", String.valueOf(-money));
				}
				updateRecord.remove("V_APP_ID");
				updateRecord.setColumn("V_ACCOUNT_ID", account);
				statefulDAO.statement().update("Order.updateAccount", updateRecord);
				updateRecord.remove("V_ACCOUNT_ID");
				updateRecord.setColumn("V_USER_ID", userID);
				updateRecord.setColumn("SURPLUS_BOND", String.valueOf(money));
				statefulDAO.statement().update("Order.updateUser", updateRecord);
			}
			else if (StringUtils.equals("3", status))
			{// 补单
				Record<String, Object> bondRecord = this.activeRecordDAO.pandora()
						.SELECT_ALL_FROM(Constant.TableName.T_BOND_TODAY).EQUAL("V_USER_ID", userID).get();
				if (bondRecord.getDouble("V_SURPLUS_BOND") < money)
				{
					resultVO.setResult("保证金额度不足，请充值");
					return resultVO;
				}
				Record<String, Object> params = new RecordImpl<String, Object>();
				params.setColumn("V_STATUS", record.getString("V_STATUS"));
				params.setColumn("V_UPDATE_USER", ApacheShiroUtils.getCurrentUserID());
				params.setColumn("V_UPDATE_TIME", DateUtils.getDateTime());
				orderDao.pandora().UPDATE(Constant.TableName.T_ORDER_TAB).EQUAL("ID", id).SET(params).excute();
				orderDao.commit();
				updateRecord.setColumn("V_APP_ID", appID);
				updateRecord.setColumn("V_MONEY", String.valueOf(money));
				if (StringUtils.equals("01", payType))
				{
					updateRecord.setColumn("ALI_MONEY", String.valueOf(money));
					updateRecord.setColumn("WX_MONEY", "0");
				}
				else
				{
					updateRecord.setColumn("ALI_MONEY", "0");
					updateRecord.setColumn("WX_MONEY", String.valueOf(money));
				}
				statefulDAO.statement().update("Order.updateApp", updateRecord);
				updateRecord.remove("V_APP_ID");
				updateRecord.setColumn("V_ACCOUNT_ID", account);
				statefulDAO.statement().update("Order.updateAccount", updateRecord);
				updateRecord.remove("V_ACCOUNT_ID");
				updateRecord.setColumn("V_USER_ID", userID);
				updateRecord.setColumn("SURPLUS_BOND", String.valueOf(-money));
				updateRecord.setColumn("V_LOCK_MONEY", "0");
				statefulDAO.statement().update("Order.updateUser", updateRecord);
				updateRecord.clear();
				updateRecord.setColumn("V_ACCOUNT_ID", account);
				statefulDAO.statement().update("Order.updateJAccountTimes");
			}
			else if (StringUtils.equals("1", status) && StringUtils.equals("2", record.getString("V_STATUS")))
			{
				resultVO.setSuccess(false);
				resultVO.setResult("订单已过期，请联系客服进行补单");
				return resultVO;
			}
			else if (StringUtils.equals("1", status))
			{// 确认
				Date date = DateUtils.parseDate(orderRecord.getString("V_CREATE_TIME"), "yyyy-MM-dd HH:mm:ss");
				long outtime = date.getTime() + 5 * 58 * 1000;
				if (outtime < System.currentTimeMillis())
				{
					resultVO.setSuccess(false);
					resultVO.setResult("订单已过期，请联系客服进行补单");
					return resultVO;
				}
				Record<String, Object> bondRecord = this.activeRecordDAO.pandora()
						.SELECT_ALL_FROM(Constant.TableName.T_BOND_TODAY).EQUAL("V_USER_ID", userID).get();
				if (bondRecord.getDouble("V_SURPLUS_BOND") < money)
				{
					resultVO.setResult("保证金额度不足，请充值");
					return resultVO;
				}
				Record<String, Object> params = new RecordImpl<String, Object>();
				params.setColumn("V_STATUS", record.getString("V_STATUS"));
				params.setColumn("V_UPDATE_USER", ApacheShiroUtils.getCurrentUserID());
				params.setColumn("V_UPDATE_TIME", DateUtils.getDateTime());
				orderDao.pandora().UPDATE(Constant.TableName.T_ORDER_TAB).EQUAL("ID", id).SET(params).excute();
				orderDao.commit();
				updateRecord.setColumn("V_APP_ID", appID);
				updateRecord.setColumn("V_MONEY", String.valueOf(money));
				if (StringUtils.equals("01", payType))
				{
					updateRecord.setColumn("ALI_MONEY", String.valueOf(money));
					updateRecord.setColumn("WX_MONEY", "0");
				}
				else
				{
					updateRecord.setColumn("ALI_MONEY", "0");
					updateRecord.setColumn("WX_MONEY", String.valueOf(money));
				}
				statefulDAO.statement().update("Order.updateApp", updateRecord);
				updateRecord.remove("V_APP_ID");
				updateRecord.setColumn("V_ACCOUNT_ID", account);
				statefulDAO.statement().update("Order.updateAccount", updateRecord);
				updateRecord.remove("V_ACCOUNT_ID");
				updateRecord.setColumn("V_USER_ID", userID);
				updateRecord.setColumn("V_LOCK_MONEY", String.valueOf(-money));
				updateRecord.setColumn("SURPLUS_BOND", String.valueOf(-money));
				statefulDAO.statement().update("Order.updateUser", updateRecord);

			}

			Record<String, Object> optParams = new RecordImpl<>();
			optParams.setColumn("ID", Random.generateUUID());
			optParams.setColumn("V_ORDER_ID", id);
			optParams.setColumn("V_CREATE_TIME", DateUtils.getDateTime());
			optParams.setColumn("V_CREATE_USER", userID);
			optParams.setColumn("V_REQUEST_IP", getIp(aeolusData.getHttpServletRequest()));
			statefulDAO.pandora().INSERT_INTO(Constant.TableName.T_ORDER_OPT).VALUES(optParams).excute();
			statefulDAO.commit();
		}
		catch (Exception e)
		{
			orderDao.rollback();
			statefulDAO.rollback();
			logger.error(LogUtils.format("审核失败", e.getMessage()), e);
			resultVO.setResult("审核失败");
			return resultVO;
		}
		finally
		{
			statefulDAO.close();
			orderDao.close();
		}
		if (StringUtils.equals("3", status) || StringUtils.equals("1", status) || StringUtils.equals("4", status))
		{
			Record<String, Object> appRecord = this.activeRecordDAO.pandora()
					.SELECT_ALL_FROM(Constant.TableName.T_APP_TAB).EQUAL("ID", appID).get();
			if (!CollectionUtils.isEmpty(appRecord))
			{
				Record<String, String> sendRecord = new RecordImpl<>();
				sendRecord.setColumn("orderno", orderRecord.getString("V_ORDER_NO"));
				sendRecord.setColumn("money", orderRecord.getString("V_MONEY"));
				sendRecord.setColumn("status", "1");
				if (StringUtils.equals("2", orderRecord.getString("V_EBCRYPT_TYPE")))
				{
					String signStr = RSAEncrypt.encryptPub(JSONObject.toJSONString(sendRecord),
							appRecord.getString("RSA_APP"));
					sendRecord.clear();
					sendRecord.setColumn("data", signStr);
				}
				else
				{
					String signStr = ThirdAction.getSign(sendRecord, appRecord.getString("V_SECRET"));
					sendRecord.setColumn("sign", signStr);
				}

				if (StringUtils.isNotBlank(orderRecord.getString("V_NOTIFY_URL")))
				{
					Record<String, Object> sendPar = new RecordImpl<>();
					try
					{
						sendPar.setColumn("ID", Random.generateUUID());
						sendPar.setColumn("V_SEND_URL", orderRecord.getString("V_NOTIFY_URL"));
						sendPar.setColumn("V_SEND_ORDER", id);
						sendPar.setColumn("V_SEND_TIME", System.currentTimeMillis());
						String result = HttpClientUtils.sendJsonPostRequest(orderRecord.getString("V_NOTIFY_URL"),
								JSONObject.toJSONString(sendRecord), "utf-8");
						sendPar.setColumn("V_SEND_MSG", JSONObject.toJSONString(sendRecord));
						if (StringUtils.equals("success", result))
						{
							sendPar.setColumn("V_SEND_STATUS", "success");
						}
						else
						{
							JSONObject jsonObject = JSONObject.parseObject(result);
							if (StringUtils.equals("success", jsonObject.getString("success")))
							{
								sendPar.setColumn("V_SEND_STATUS", "success");
							}
							else
							{
								sendPar.setColumn("V_SEND_STATUS", "fail");
							}
						}
						sendPar.setColumn("V_RETURN_MSG", result);
						sendPar.setColumn("V_RETURN_TIME", System.currentTimeMillis());
					}
					catch (Exception e)
					{
						sendPar.setColumn("V_SEND_STATUS", "fail");
						sendPar.setColumn("V_RETURN_MSG", e.getMessage());
						sendPar.setColumn("V_RETURN_TIME", System.currentTimeMillis());
						logger.error(LogUtils.format("发送数据失败", e.getMessage()), e);
					}
					finally
					{
						this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_SEND_TAB).VALUES(sendPar)
								.excute();
					}
				}
			}
		}
		resultVO.setSuccess(true);
		resultVO.setResult("操作成功");
		return resultVO;
	}

	public String getIp(HttpServletRequest request) throws Exception
	{
		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null)
		{
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip))
			{
				int index = ip.indexOf(",");
				if (index != -1)
				{
					return ip.substring(0, index);
				}
				else
				{
					return ip;
				}
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (ip != null)
		{
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip))
			{
				return ip;
			}
		}
		return request.getRemoteAddr();
	}
}

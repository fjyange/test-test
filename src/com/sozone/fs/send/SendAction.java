package com.sozone.fs.send;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.data.Page;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.FacadeException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.object.ResJson;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.fs.common.Constant;
import com.sozone.fs.common.util.HttpClientUtils;
import com.sozone.fs.third.ThirdAction;

@Path(value = "/send", desc = "发送异常单子修改")
@Permission(Level.Authenticated)
public class SendAction {
	
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
	private static Logger logger = LoggerFactory.getLogger(SendAction.class);
	
	@Path(value="findpage",desc="获取异常单子")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("获取异常单子信息", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Page<Record<String, Object>> page = this.activeRecordDAO.statement().selectPage("Send.getErrorOrder",
				aeolusData.getPageRequest(), record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value="/dealSend",desc="修改异常单子")
	@Service
	public ResJson dealSend(AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("获取异常单子信息", aeolusData));
		ResJson resJson = new ResJson();
		String id = aeolusData.getParam("ID");
		Record<String, Object> sendMsgRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_SEND_TAB).EQUAL("ID", id).get();
		if (StringUtils.equals("SUCCESS", sendMsgRecord.getString("V_SEND_STATUS"))) {
			resJson.setMsg("单子已正常发送");
			return resJson;
		}
		Record<String, Object> orderRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_ORDER_TAB).EQUAL("ID", sendMsgRecord.getString("V_SEND_ORDER")).get();
		if (StringUtils.equals("1", orderRecord.getString("V_STATUS")) || StringUtils.equals("3", orderRecord.getString("V_STATUS"))) {
			Record<String, Object> appRecord = this.activeRecordDAO.pandora()
					.SELECT_ALL_FROM(Constant.TableName.T_APP_TAB).EQUAL("ID", orderRecord.getString("V_BELONG_APP")).get();
			if (!CollectionUtils.isEmpty(appRecord)) {
				Record<String, String> sendRecord = new RecordImpl<>();
				sendRecord.setColumn("orderno", orderRecord.getString("V_ORDER_NO"));
				sendRecord.setColumn("money", orderRecord.getString("V_MONEY"));
				sendRecord.setColumn("status", "1");
				String signStr = ThirdAction.getSign(sendRecord, appRecord.getString("V_SECRET"));
				sendRecord.setColumn("sign", signStr);
				if (StringUtils.isNotBlank(orderRecord.getString("V_NOTIFY_URL"))) {
					Record<String, Object> sendPar = new RecordImpl<>();
					try {
						sendPar.setColumn("V_SEND_URL", orderRecord.getString("V_NOTIFY_URL"));
						sendPar.setColumn("V_SEND_ORDER", sendMsgRecord.getString("V_SEND_ORDER"));
						sendPar.setColumn("V_SEND_TIME", System.currentTimeMillis());
						String result = HttpClientUtils.sendJsonPostRequest(orderRecord.getString("V_NOTIFY_URL"),
								JSONObject.toJSONString(sendRecord), "utf-8");
						sendPar.setColumn("V_SEND_MSG", JSONObject.toJSONString(sendRecord));
						if (StringUtils.equals("success", result)) {
							sendPar.setColumn("V_SEND_STATUS", "success");
							resJson.setSuccess(true);
							resJson.setMsg("重新发送成功");
						}else {
							JSONObject jsonObject = JSONObject.parseObject(result);
							if (StringUtils.equals("success", jsonObject.getString("success"))) {
								sendPar.setColumn("V_SEND_STATUS", "success");
								resJson.setSuccess(true);
								resJson.setMsg("重新发送成功");
							}else {
								sendPar.setColumn("V_SEND_STATUS", "fail");
								resJson.setMsg("重新发送失败");
							}
						}
						sendPar.setColumn("V_RETURN_MSG", result);
						sendPar.setColumn("V_RETURN_TIME", System.currentTimeMillis());
					} catch (Exception e) {
						sendPar.setColumn("V_RETURN_MSG", e.getMessage());
						sendPar.setColumn("V_RETURN_TIME", System.currentTimeMillis());
						logger.error(LogUtils.format("发送数据失败", e.getMessage()), e);
						resJson.setMsg("发送异常");
					} finally {
						this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_SEND_TAB).SET(sendPar).EQUAL("ID", id)
								.excute();
					}
				}
			}
		}else {
			resJson.setMsg("单子未审核");
		}
		return resJson;
	}
	
	@Path(value="/finderrororder",desc="查询多点单子")
	@Service
	public ResultVO<Page<Record<String, Object>>> finderrororder(AeolusData aeolusData) throws FacadeException{
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Page<Record<String, Object>> page = this.activeRecordDAO.statement().selectPage("Send.getErrorOrder",
				aeolusData.getPageRequest(), record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value="/dealErrorData",desc="数据回正")
	@Service
	public ResJson dealErrorData(AeolusData aeolusData) throws FacadeException{
		ResJson resJson = new ResJson(false,"操作失败");
		dealUserErrorData();
		dealAppErrorData();
		dealAccountErrorData();
		resJson.setSuccess(true);
		resJson.setSuccess(true);
		return resJson;
	}
	
	public void dealUserErrorData() throws FacadeException{
		List<Record<String,Object>> list = this.activeRecordDAO.statement().selectList("Send.getUserCount");
		for(Record<String, Object> record:list) {
			String userId = record.getString("V_BELONG_USER");
			double orderMoney = record.getDouble("ORDER_MONEY");
			double countMoney = record.getDouble("V_COUNT_RECEIVABLES");
			double surplusMoney = record.getDouble("V_SURPLUS_BOND");
			double lastMoney = record.getDouble("V_YS_BOND");
			Record<String, Object> params  = new RecordImpl<>();
			params.setColumn("USER_ID", userId);
			Record<String, Object> topupRecord = this.activeRecordDAO.statement().selectOne("Send.getTopupCount", params);
			double topupMoney = topupRecord.getDouble("TOPUP_MONEY");
			
			if ((countMoney +surplusMoney)  == (lastMoney + topupMoney)) {
				double syMoney = orderMoney - countMoney;
				params.clear();
				params.setColumn("syMoney", syMoney);
				params.setColumn("MONEY", -syMoney);
				params.setColumn("USER_ID", userId);
				this.activeRecordDAO.statement().update("Send.updateUserCount",params);
			}
		}
	}
	public void dealAppErrorData() throws FacadeException{
		List<Record<String,Object>> list = this.activeRecordDAO.statement().selectList("Send.getAppCount");
		for(Record<String, Object> record:list) {
			String userId = record.getString("V_BELONG_APP");
			double orderMoney = record.getDouble("ORDER_MONEY");
			double countMoney = record.getDouble("V_TOTAL_COLLECTION");
			Record<String, Object> params  = new RecordImpl<>();
			params.setColumn("USER_ID", userId);
			
			if (countMoney != orderMoney) {
				double syMoney = orderMoney - countMoney;
				params.clear();
				params.setColumn("syMoney", syMoney);
				params.setColumn("USER_ID", userId);
				this.activeRecordDAO.statement().update("Send.updateAppCount",params);
			}
		}
	}
	
	public void dealAccountErrorData() throws FacadeException{
		List<Record<String,Object>> list = this.activeRecordDAO.statement().selectList("Send.getAccountCount");
		for(Record<String, Object> record:list) {
			String userId = record.getString("V_BELONG_ACCOUNT");
			double orderMoney = record.getDouble("ORDER_MONEY");
			double countMoney = record.getDouble("V_TOTAL_MONEY");
			double syMoney = orderMoney - countMoney;
			Record<String, Object> params = new RecordImpl<>();
			params.setColumn("syMoney", syMoney);
			params.setColumn("USER_ID", userId);
			this.activeRecordDAO.statement().update("Send.updateAccountCount",params);
		}
	}
}

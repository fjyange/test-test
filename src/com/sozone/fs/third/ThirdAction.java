package com.sozone.fs.third;

import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.PathParam;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.authorize.utlis.Random;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.FacadeException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.object.ResJson;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.aeolus.utils.DateUtils;
import com.sozone.fs.common.Constant;
import com.sozone.fs.common.util.HttpClientUtils;
import com.sozone.fs.common.util.UtilSign;

@Path(value = "/third", desc = "第三方接入")
@Permission(Level.Guest)
public class ThirdAction {

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
	private static Logger logger = LoggerFactory.getLogger(ThirdAction.class);

	@Path(value = "/confirmorder", desc = "订单回调接口")
	@Service
	public ResultVO<String> confirmorder(AeolusData aeolusData) throws FacadeException {
		ResultVO<String> resultVO = new ResultVO<String>();
		Record<String, Object> record = aeolusData.getRecord();
		String appId = record.getString("appid");
		if (StringUtils.isEmpty(appId)) {
			resultVO.setResult("平台id为空");
			return resultVO;
		}
		String orderno = record.getString("orderno");
		if (StringUtils.isEmpty(orderno)) {
			resultVO.setResult("订单号为空");
			return resultVO;
		}
		Record<String, Object> appRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_APP_TAB)
				.EQUAL("V_APPID", appId).get();
		if (CollectionUtils.isEmpty(appRecord)) {
			resultVO.setResult("此平台在系统中不存在，请联系客服");
		}
		Record<String, Object> sendRecord = new RecordImpl<String, Object>();
		sendRecord.setColumn("orderno", orderno);
		sendRecord.setColumn("appid", appId);
		sendRecord.setColumn("status", record.getString("status"));
		String sign = getSign(sendRecord, appRecord.getString("V_SECRET"));
		String sendSign = record.getString("sign");
		if (!StringUtils.equals(sign, sendSign)) {
			resultVO.setResult("签名失败");
			return resultVO;
		}
		Record<String, Object> params = new RecordImpl<String, Object>();
		params.setColumn("V_APP_ID", appId);
		params.setColumn("V_ORDER_NO", orderno);
		Record<String, Object> orderRecord = this.activeRecordDAO.statement().selectOne("Third.getOrderInfo", params);
		if (CollectionUtils.isEmpty(orderRecord)) {
			resultVO.setResult("订单已确认");
			resultVO.setSuccess(true);
		} else {
			resultVO.setResult("订单未确认");
		}
		return resultVO;
	}

	@Path(value = "/sendorder", desc = "接收订单信息")
	@Service
	public synchronized ResJson sendorder(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("接收订单信息", aeolusData));
		ResJson resJson = new ResJson(false,"下单失败");
		Record<String, Object> dictRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_DICT_TAB).EQUAL("V_DICT_TYPE", "SYS_SWICH").get();
		if (StringUtils.equals("off", dictRecord.getString("V_DICT_VALUE"))) {
			resJson.setMsg("系统已停止接单");
			resJson.setMapData("result", "系统已停止接单");
			return resJson;
		}
		Record<String, Object> logRecord = new RecordImpl<>();
		logRecord.setColumn("ID", Random.generateUUID());
		logRecord.setColumn("V_RECIEVE_CONTENT", aeolusData.getRecord().toString());
		logRecord.setColumn("V_RECIEVE_TIME", DateUtils.getDateTime());
		Record<String, Object> record = aeolusData.getRecord();
		String appId = record.getString("appid");
		if (StringUtils.isEmpty(appId)) {
			logRecord.setColumn("V_RECIEVE_STATUS", "2");
			logRecord.setColumn("V_RECIEVE_MSG", "平台id为空");
			writeLog(logRecord);
			resJson.setMsg("平台id为空");
			resJson.setMapData("result", "平台id为空");
			return resJson;
		}
		String money = record.getString("money");
		if (StringUtils.isEmpty(money)) {
			logRecord.setColumn("V_RECIEVE_STATUS", "2");
			logRecord.setColumn("V_RECIEVE_MSG", "金额为空");
			writeLog(logRecord);
			resJson.setMsg("金额为空");
			resJson.setMapData("result", "金额为空");
			return resJson;
		}
		if (!isDoubleOrFloat(money)) {
			logRecord.setColumn("V_RECIEVE_STATUS", "2");
			logRecord.setColumn("V_RECIEVE_MSG", "金额不是数字");
			writeLog(logRecord);
			resJson.setMsg("金额不是数字");
			resJson.setMapData("result", "金额不是数字");
			return resJson;
		}
		String orderno = record.getString("orderno");
		if (StringUtils.isEmpty(orderno)) {
			logRecord.setColumn("V_RECIEVE_STATUS", "2");
			logRecord.setColumn("V_RECIEVE_MSG", "订单号为空");
			writeLog(logRecord);
			resJson.setMsg("订单号为空");
			resJson.setMapData("result", "订单号为空");
			return resJson;
		}
		String payType = record.getString("paytype");
		if (StringUtils.isEmpty(payType)) {
			logRecord.setColumn("V_RECIEVE_STATUS", "2");
			logRecord.setColumn("V_RECIEVE_MSG", "付款方式为空");
			writeLog(logRecord);
			resJson.setMsg("付款方式为空");
			resJson.setMapData("result", "付款方式为空");
			return resJson;
		}
		Record<String, Object> appRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_APP_TAB)
				.EQUAL("V_APPID", appId).get();
		if (CollectionUtils.isEmpty(appRecord)) {
			logRecord.setColumn("V_RECIEVE_STATUS", "2");
			logRecord.setColumn("V_RECIEVE_MSG", "此平台在系统中不存在，请联系客服");
			writeLog(logRecord);
			resJson.setMsg("此平台在系统中不存在，请联系客服");
			resJson.setMapData("result", "此平台在系统中不存在，请联系客服");
			return resJson;
		}
		if (StringUtils.equals("N", appRecord.getString("V_IS_MATCH"))) {
			logRecord.setColumn("V_RECIEVE_STATUS", "2");
			logRecord.setColumn("V_RECIEVE_MSG", "此平台已被禁止传入数据");
			writeLog(logRecord);
			resJson.setMsg("此平台已被禁止传入数据");
			resJson.setMapData("result", "此平台已被禁止传入数据");
			return resJson;
		}
		Record<String, Object> checkOrder = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_ORDER_TAB).EQUAL("V_ORDER_NO", orderno).EQUAL("V_MONEY", money)
				.EQUAL("V_BELONG_APP", appRecord.getString("ID")).get();
		if(!CollectionUtils.isEmpty(checkOrder)) {
			Record<String, Object> accountRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_PAY_ACCOUNT).EQUAL("ID", checkOrder.getString("V_BELONG_ACCOUNT")).get();
			Record<String, Object> fileRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_FILE_TAB).EQUAL("ID", accountRecord.getString("V_FILE_ID")).get();
			String url = Constant.WEB_URL + fileRecord.getString("V_NAME");
			resJson.setMapData("result", url);
			resJson.setMapData("view", Constant.VIEW_URL + "/showPayApp.jsp?id=" + checkOrder.getString("ID"));
			resJson.setMapData("pcView", Constant.VIEW_URL + "/showPayPC.jsp?id=" + checkOrder.getString("ID"));
			resJson.setSuccess(true);
			resJson.setMsg("下单成功");
			return resJson;
		}
		Record<String, Object> sendRecord = new RecordImpl<>();
		sendRecord.setColumn("appid", appId);
		sendRecord.setColumn("money", money);
		sendRecord.setColumn("orderno", orderno);
		sendRecord.setColumn("paytype", payType);
		if (StringUtils.isNotBlank(record.getString("notifyurl"))) {
			sendRecord.setColumn("notifyurl", record.getString("notifyurl"));
		}
		String sign = getSign(sendRecord, appRecord.getString("V_SECRET"));
		String sendSign = record.getString("sign");
		if (!StringUtils.equals(sign, sendSign)) {
			logRecord.setColumn("V_RECIEVE_STATUS", "2");
			logRecord.setColumn("V_RECIEVE_MSG", "签名失败");
			writeLog(logRecord);
			resJson.setMsg("签名失败");
			resJson.setMapData("result", "签名失败");
			return resJson;
		}
		Record<String, Object> params = new RecordImpl<>();
		params.setColumn("V_MONEY", money);
		params.setColumn("V_PAY_TYPE", payType);
		Record<String, Object> fileRecord = this.activeRecordDAO.statement().selectOne("Third.getpayimage", params);
		if (CollectionUtils.isEmpty(fileRecord)) {
			logRecord.setColumn("V_RECIEVE_STATUS", "2");
			logRecord.setColumn("V_RECIEVE_MSG", "无可收款商户");
			writeLog(logRecord);
			resJson.setMsg("无可收款商户");
			resJson.setMapData("result", "无可收款商户");
			return resJson;
		}
		String url = Constant.WEB_URL + fileRecord.getString("V_NAME");
		resJson.setMapData("result", url);
		
		Record<String, Object> orderRecord = new RecordImpl<>();
		String id = Random.generateUUID();
		resJson.setMapData("view", Constant.VIEW_URL + "/showPayApp.jsp?id=" + id);
		resJson.setMapData("pcView", Constant.VIEW_URL + "/showPayPC.jsp?id=" + id);
		orderRecord.setColumn("ID", id);
		orderRecord.setColumn("V_ORDER_NO", orderno);
		orderRecord.setColumn("V_BELONG_APP", appRecord.getString("ID"));
		orderRecord.setColumn("V_NOTIFY_URL", record.getString("notifyurl"));
		orderRecord.setColumn("V_BELONG_ACCOUNT", fileRecord.getString("PAY_ID"));
		orderRecord.setColumn("V_BELONG_USER", fileRecord.getString("V_USER_ID"));
		orderRecord.setColumn("V_MONEY", money);
		orderRecord.setColumn("V_PAY_TYPE", payType);
		orderRecord.setColumn("V_STATUS", "0");
		orderRecord.setColumn("V_CREATE_TIME", DateUtils.getDateTime());
		// 订单添加
		this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_ORDER_TAB).VALUES(orderRecord).excute();
		Record<String, Object> updateRecord = new RecordImpl<>();
		updateRecord.setColumn("V_PAY_TIME", DateUtils.getDateTime());
		// 修改用户支付时间
		this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_USER_SHOW)
				.EQUAL("V_USER_ID", fileRecord.getString("V_USER_ID")).SET(updateRecord).excute();
		updateRecord.clear();
		double monayD = Double.parseDouble(money);
		updateRecord.setColumn("V_LOCK_MONEY", monayD);
		updateRecord.setColumn("V_USER_ID", fileRecord.getString("V_USER_ID"));
		// 添加锁住金额
		this.activeRecordDAO.statement().update("Order.updateUserLockMoney", updateRecord);
		updateRecord.clear();
		updateRecord.setColumn("V_ACCOUNT_ID", fileRecord.getString("PAY_ID"));
		updateRecord.setColumn("V_PAY_TIME", DateUtils.getDateTime());
		updateRecord.setColumn("V_PAY_NUM", "-1");
		// 修改账户支付次数
		this.activeRecordDAO.statement().update("Order.updateAccountTimes", updateRecord);
		resJson.setSuccess(true);
		resJson.setMsg("下单成功");
		logRecord.setColumn("V_RECIEVE_STATUS", "1");
		logRecord.setColumn("V_RECIEVE_MSG", "成功");
		writeLog(logRecord);
		return resJson;
	}

	public static String getSign(Record<String, ?> record, String key) {
		String paramStr = UtilSign.splicingStr(record);
		String signStr = paramStr + "&key=" + key;
		String sign = DigestUtils.md5Hex(signStr).toUpperCase();
		return sign;
	}

	public void writeLog(Record<String, Object> record) throws FacadeException {
		this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_RECIEVE_TAB).VALUES(record).excute();
	}

	@Path(value="/getOrderMsg/{id}",desc="获取订单信息")
	@Service
	public ResJson getOrderMsg(@PathParam("id")String id) throws FacadeException{
		ResJson resJson = new ResJson(false,"获取订单信息失败");
		Record<String, Object> params = new RecordImpl<>();
		params.setColumn("ID", id);
		Record<String, Object> orderRecord = this.activeRecordDAO.statement().getOne("Order.getOrderMsg", params);
		orderRecord.setColumn("IMG_URL", Constant.WEB_URL + orderRecord.getString("V_NAME"));
		orderRecord.setColumn("ALI_URL", Constant.ALI_URL + orderRecord.getString("V_URL_SCHEME"));
		orderRecord.setColumn("DOWN_URL", Constant.VIEW_URL +"/authorize/attach/downFile?ID="+ orderRecord.getString("ID"));
		resJson.setSuccess(true);
		resJson.setMap(orderRecord);
		resJson.setMsg("订单获取成功");
		return resJson;
	}
	
	/*
	 * 是否为浮点数？double或float类型。
	 * 
	 * @param str 传入的字符串。
	 * 
	 * @return 是浮点数返回true,否则返回false。
	 */
	public static boolean isDoubleOrFloat(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}

	// private String newSign(Record<String, Object> record) {
	//
	// }
	public static void main(String[] args) throws Exception {
		// for(int i = 0;i< 20;i++) {
		Record<String, Object> record = new RecordImpl<>();
		record.setColumn("appid", "4f5442209f644ed3b44a78074c566f97");
		 record.setColumn("money", "5");
		record.setColumn("orderno", "201909142035013425921532");
		 record.setColumn("paytype", "01");
//		 record.setColumn("notifyurl", "http:\\/\\/47.75.253.95:10002\\/callback\\/test\\/testCallBack");
//		record.setColumn("orderno", "test12312");
//		record.setColumn("money", "123");
//		record.setColumn("status", "1");
		String sign = getSign(record, "G6ZeCZ3rdYsQInD");
		record.setColumn("sign", sign);
//		 System.out.println(HttpClientUtils.sendJsonPostRequest(
//		 "http://www.tdwj.xyz/Pay_YfuScan_notifyurl.html",
//		 JSONObject.toJSONString(record), "utf-8"));
		System.out.println(HttpClientUtils.sendJsonPostRequest(
				"http://www.shurenpay.com/authorize/third/sendorder", JSONObject.toJSONString(record), "utf-8"));
		// }
		// Record<String, Object> record = new RecordImpl<>();
		// String url = "https://qr-test2.chinaums.com/netpay-route-server/api/";
		// record.setColumn("msgSrc", "WWW.TEST.COM");
		// record.setColumn("msgType", "bills.getQRCode");
		// record.setColumn("requestTimestamp", DateUtils.getDateTime());
		// record.setColumn("mid", "898340149000005");
		// record.setColumn("tid", "88880001");
		// System.out.println(HttpClientUtils.sendJsonPostRequest(url,
		// JSONObject.toJSONString(record), "utf-8"));

	}
}

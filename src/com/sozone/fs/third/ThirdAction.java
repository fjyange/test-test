package com.sozone.fs.third;

import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.authorize.utlis.Random;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.DAOException;
import com.sozone.aeolus.exception.FacadeException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.aeolus.utils.DateUtils;
import com.sozone.fs.common.Constant;
import com.sozone.fs.common.util.HttpClientUtils;
import com.sozone.fs.common.util.UtilSign;

/**
 * 
 * @author yange
 *
 */
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
	public ResultVO<String> confirmorder(AeolusData aeolusData)
			throws FacadeException {
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
		Record<String, Object> appRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_APP_TAB)
				.EQUAL("V_APPID", appId).get();
		if (CollectionUtils.isEmpty(appRecord)) {
			resultVO.setResult("此平台在系统中不存在，请联系客服");
		}
		Record<String, Object> sendRecord = new RecordImpl<String, Object>();
		sendRecord.setColumn("orderno", orderno);
		sendRecord.setColumn("appid", appId);
		String sign = getSign(sendRecord, appRecord.getString("V_SECRET"));
		String sendSign = record.getString("sign");
		if (!StringUtils.equals(sign, sendSign)) {
			resultVO.setResult("签名失败");
			return resultVO;
		}
		Record<String, Object> params = new RecordImpl<String, Object>();
		params.setColumn("V_APP_ID", appId);
		params.setColumn("V_ORDER_NO", orderno);
		Record<String, Object> orderRecord = this.activeRecordDAO.statement()
				.selectOne("Third.getOrderInfo", params);
		if (CollectionUtils.isEmpty(orderRecord)) {
			resultVO.setResult("订单已确认");
		} else {
			resultVO.setResult("订单未确认");
		}
		return resultVO;
	}

	@Path(value = "/sendorder", desc = "接收订单信息")
	@Service
	public synchronized ResultVO<String> sendorder(AeolusData aeolusData)
			throws FacadeException {
		logger.error(LogUtils.format("接收订单信息", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>(false);
		Record<String, Object> dictRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_DICT_TAB).EQUAL("V_DICT_TYPE", "SYS_SWICH").get();
		if (StringUtils.equals("off", dictRecord.getString("V_DICT_VALUE"))) {
			resultVO.setResult("系统已停止接单");
			return resultVO;
		}
		
		Record<String, Object> record = aeolusData.getRecord();
		String appId = record.getString("appid");
		if (StringUtils.isEmpty(appId)) {
			resultVO.setResult("平台id为空");
			return resultVO;
		}
		String money = record.getString("money");
		if (StringUtils.isEmpty(money)) {
			resultVO.setResult("金额为空");
			return resultVO;
		}
		if (!isDoubleOrFloat(money)) {
			resultVO.setResult("金额不是数字");
			return resultVO;
		}
		String orderno = record.getString("orderno");
		if (StringUtils.isEmpty(orderno)) {
			resultVO.setResult("订单号为空");
			return resultVO;
		}
		String payType = record.getString("paytype");
		if (StringUtils.isEmpty(payType)) {
			resultVO.setResult("付款方式为空");
			return resultVO;
		}
		Record<String, Object> appRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_APP_TAB)
				.EQUAL("V_APPID", appId).get();
		if (CollectionUtils.isEmpty(appRecord)) {
			resultVO.setResult("此平台在系统中不存在，请联系客服");
			return resultVO;
		}
		if (StringUtils.equals("N", appRecord.getString("V_IS_MATCH"))) {
			resultVO.setResult("此平台已被禁止传入数据");
			return resultVO;
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
			resultVO.setResult("签名失败");
			return resultVO;
		}
		Record<String, Object> params = new RecordImpl<>();
		params.setColumn("V_MONEY", money);
		params.setColumn("V_PAY_TYPE", payType);
		Record<String, Object> fileRecord = this.activeRecordDAO.statement()
				.selectOne("Third.getpayimage", params);
		if (CollectionUtils.isEmpty(fileRecord)) {
			resultVO.setResult("无可收款商户");
			return resultVO;
		}
		try {
			String url = "http://193.112.135.244/authorize/attach/getFile?ID="
					+ fileRecord.getString("FILE_ID");
			resultVO.setResult(url);
		} catch (Exception e) {
			logger.error(LogUtils.format("读取付款图片异常", e.getMessage()), e);
			resultVO.setResult("读取付款图片异常");
			return resultVO;
		}

		Record<String, Object> orderRecord = new RecordImpl<>();
		orderRecord.setColumn("ID", Random.generateUUID());
		orderRecord.setColumn("V_ORDER_NO", orderno);
		orderRecord.setColumn("V_BELONG_APP", appRecord.getString("ID"));
		orderRecord.setColumn("V_NOTIFY_URL", record.getString("notifyurl"));
		orderRecord.setColumn("V_BELONG_ACCOUNT",
				fileRecord.getString("PAY_ID"));
		orderRecord.setColumn("V_BELONG_USER",
				fileRecord.getString("V_USER_ID"));
		orderRecord.setColumn("V_MONEY", money);
		orderRecord.setColumn("V_PAY_TYPE", payType);
		orderRecord.setColumn("V_STATUS", "0");
		orderRecord.setColumn("V_CREATE_TIME", DateUtils.getDateTime());
		this.activeRecordDAO.pandora()
				.INSERT_INTO(Constant.TableName.T_ORDER_TAB)
				.VALUES(orderRecord).excute();
		Record<String, Object> updateRecord = new RecordImpl<>();
		updateRecord.setColumn("V_PAY_TIME", DateUtils.getDateTime());
		this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_USER_SHOW)
				.EQUAL("ID", fileRecord.getString("ID")).SET(updateRecord)
				.excute();
		updateRecord.clear();
		double monayD = Double.parseDouble(money);
		updateRecord.setColumn("V_SURPLUS_BOND",
				fileRecord.getDouble("V_SURPLUS_BOND") - monayD);
		updateRecord.setColumn("V_COUNT_RECEIVABLES",
				fileRecord.getDouble("V_COUNT_RECEIVABLES") + monayD);
		if (StringUtils.equals("01", payType)) {
			updateRecord.setColumn("V_ALI_RECEIVABLES",
					fileRecord.getDouble("V_ALI_RECEIVABLES") + monayD);
		} else {
			updateRecord.setColumn("V_WX_RECEIVABLES",
					fileRecord.getDouble("V_WX_RECEIVABLES") + monayD);
		}
		this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_BOND_TODAY)
				.EQUAL("ID", fileRecord.getString("BOND_ID")).SET(updateRecord)
				.excute();
		updateRecord.clear();
		updateRecord.setColumn("V_PAY_TIME", DateUtils.getDateTime());
		int times = fileRecord.getInteger("V_PAY_NUM");
		times = times - 1;
		if (times == 0) {
			updateRecord.setColumn("V_IS_SHOW", "N");
		}
		updateRecord.setColumn("V_PAY_NUM", times);
		this.activeRecordDAO.pandora()
				.UPDATE(Constant.TableName.T_ACCOUNT_SHOW)
				.EQUAL("ID", fileRecord.getString("ACCOUNT_SHOW_ID"))
				.SET(updateRecord).excute();
		updateRecord.clear();
		Record<String, Object> accountRecord = this.activeRecordDAO.pandora()
				.SELECT_ALL_FROM(Constant.TableName.T_ACCOUNT_COLLECTION)
				.EQUAL("V_ACCOUNT_ID", fileRecord.getString("V_ACCOUNT_ID"))
				.get();

		updateRecord.setColumn("V_TOTAL_MONEY",
				accountRecord.getDouble("V_TOTAL_MONEY") + monayD);
		if (StringUtils.equals("01", payType)) {
			updateRecord.setColumn("V_ALI_MONEY",
					accountRecord.getDouble("V_ALI_MONEY") + monayD);
		} else {
			updateRecord.setColumn("V_WX_MONEY",
					accountRecord.getDouble("V_WX_MONEY") + monayD);
		}
		this.activeRecordDAO.pandora()
				.UPDATE(Constant.TableName.T_ACCOUNT_COLLECTION)
				.EQUAL("ID", accountRecord.getString("ID")).SET(updateRecord)
				.excute();
		updateRecord.clear();
		resultVO.setSuccess(true);
		return resultVO;
	}

	public static String getSign(Record<String, Object> record, String key) {
		String paramStr = UtilSign.splicingStr(record);
		String signStr = paramStr + "&key=" + key;
		String sign = DigestUtils.md5Hex(signStr).toUpperCase();
		return sign;
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

	public static void main(String[] args) throws Exception {
		Record<String, Object> record = new RecordImpl<>();
		record.setColumn("appid", "0a82b4fb7e2446e2a52619d2bc0fd424");
		record.setColumn("money", "1000");
		record.setColumn("orderno", "20190812151847559799");
		record.setColumn("paytype", "01");
//		record.setColumn("notifyurl", "https://www.baidu.com");
		String sign = getSign(record, "oRELu0wCXTvPovH");
		record.setColumn("sign", sign);
		System.out.println(HttpClientUtils.sendJsonPostRequest(
				"http://193.112.135.244/authorize/third/sendorder",
				JSONObject.toJSONString(record), "utf-8"));

	}
}

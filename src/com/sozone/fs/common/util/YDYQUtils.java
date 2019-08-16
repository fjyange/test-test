/**
 * 包名：com.sozone.ydyq.api.test
 * 文件名：TestYDYQAPI.java<br/>
 * 创建时间：2018年11月6日 上午10:57:00<br/>
 * 创建者：don<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package com.sozone.fs.common.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.authorize.utlis.SystemParamUtils;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.exception.ServiceException;
import com.sozone.fs.common.Constant;
import com.sozone.fs.common.Constant.SysParamKey;
import com.sozone.fs.common.Constant.faceMethod;

/**
 * TODO 一句话描述类的主要作用<br/>
 * <p>
 * TODO 该类的详细描述<br/>
 * </p>
 * Time：2018年11月6日 上午10:57:00<br/>
 * 
 * @author don
 * @version 1.0.0
 * @since 1.0.0
 */
public class YDYQUtils
{
	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(YDYQUtils.class);

	/**
	 * 获取用户token<br/>
	 * <p>
	 * 获取用户token
	 * </p>
	 * 
	 * @param appID
	 *            appID
	 * @param password
	 *            密码
	 * @return token
	 * @throws Exception
	 *             异常
	 */
	public static String getUserToken(String appID, String password) throws Exception
	{

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("APPID", appID);
		map.put("PASSWORD", password);
		String ydyqRootUrl = SystemParamUtils
				.getString(SysParamKey.RA_YDYQ_URL_KEY);// 云盾云签地址
		String ydyqLoginMethod = faceMethod.RA_YDYQ_YD_LOGIN;//云盾云签登录方法
		String result = HttpClientUtils.doPost(ydyqRootUrl + ydyqLoginMethod,
				map);
		JSONObject json = JSON.parseObject(result);
		String token = json.getJSONObject("result").getString("token");
		return token;
	}

	/**
	 * @return
	 * @throws ServiceException
	 */
	public static Map<String, String> getHeadMapOfToken(String appid,
			String apppwd) throws Exception
	{
		String token = "";
		try
		{
			token = getUserToken(appid, apppwd);
		}
		catch (Exception e)
		{
			logger.error(LogUtils.format("获取用tonken失败"), e);
			throw new ServiceException("", "获取用tonken失败，" + e.getMessage());
		}

		HashMap<String, String> head = new HashMap<String, String>();
		head.put("Authorization", "JWT " + token);

		return head;
	}

	public static void main(String[] args)
	{
		String url = "http://test-www.ydunyqian.com/authorize/req/sendBusData";
		Record<String,String> params = new RecordImpl<String,String>();
		try
		{
			getHeadMapOfToken( "c5186275559341989817c83a1752edaa", "a976c2a2");
			params.put("orderid", "1");
			JSONObject json = new JSONObject();
			json.put("aa", "1");
			params.put("data", json.toJSONString());
			params.put("file", Base64.encodeBase64String(FileUtils.readFileToByteArray(new File("C:/Users/don/Desktop/document.pdf"))));
			String result = HttpClientUtils.doPost(url, params, getHeadMapOfToken("e2a0ad7736b94379893186fb53bab7b7", "a976c2a2"), Constant.DEFAULT_CHARSET);
			System.out.println(result);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

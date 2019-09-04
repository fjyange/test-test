package com.sozone.fs.app;

import org.apache.commons.codec.digest.DigestUtils;
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

@Path(value = "app", desc = "平台管理")
@Permission(Level.Authenticated)
public class AppAction {

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
	private static Logger logger = LoggerFactory.getLogger(AppAction.class);

	@Path(value = "/findPage", desc = "获取平台列表")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("获取平台列表", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Pageable pageable = aeolusData.getPageRequest();
		record.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		record.setColumn("IS_ADMIN", ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		Page<Record<String, Object>> page = this.activeRecordDAO.statement().selectPage("App.list", pageable, record);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}

	@Path(value = "/save", desc = "添加平台用户")
	@Service
	public ResultVO<String> save(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("添加平台用户", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Record<String, Object> userRecord = new RecordImpl<>();
		String id = record.getString("ID");
		record.setColumn("V_UPDATE_TIME", DateUtils.getDateTime());
		record.setColumn("V_UPDATE_USER", ApacheShiroUtils.getCurrentUserID());
		String userId = record.getString("V_USER_ID");
		userRecord.setColumn("USER_ID", userId);
		userRecord.setColumn("USER_ACCOUNT", record.getString("V_USER_ACCOUNT"));
		userRecord.setColumn("USER_NAME", record.getString("V_APP_NAME"));
		userRecord.setColumn("USER_PHONE", record.getString("V_USER_PHONE"));
		userRecord.setColumn("ROLE_ID", record.getString("V_ROLE_ID"));
		if (StringUtils.isEmpty(userId)) {
			userId = Random.generateUUID();
			userRecord.setColumn("PASSWORD", DigestUtils.md5Hex(DigestUtils.md5Hex("111111")));
			userRecord.setColumn("USER_ID", userId);
			userRecord.setColumn("IS_ADMIN", "0");
			userRecord.setColumn("IS_LOCK", "0");
			userRecord.setColumn("USER_TYPE", "2");
			userRecord.setColumn("CREATE_USER", ApacheShiroUtils.getCurrentUserID());
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_BASE).save(userRecord);
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_ROLE).save(userRecord);
		} else {
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_BASE).modify(userRecord);
			Record<String, Object> params = new RecordImpl<>();
			params.setColumn("USER_ID", userId);
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_ROLE).remove(params);
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_USER_ROLE).save(userRecord);
		}
		record.setColumn("V_USER_ID", userId);
		if (StringUtils.isEmpty(id)) {
			id = Random.generateUUID();
			record.setColumn("ID", id);
			record.setColumn("V_APPID", Random.generateUUID());
			record.setColumn("V_SECRET", getCode());
			record.setColumn("V_CREATE_USER", ApacheShiroUtils.getCurrentUserID());
			this.activeRecordDAO.auto().table(Constant.TableName.T_APP_TAB).save(record);
			Record<String, Object> params = new RecordImpl<>();
			params.setColumn("ID", Random.generateUUID());
			params.setColumn("V_APP_ID", id);
			params.setColumn("V_WX_COLLECTION", "0");
			params.setColumn("V_ALI_COLLECTION", "0");
			params.setColumn("V_TOTAL_COLLECTION", "0");
			params.setColumn("V_CASH_COLLECTION", "0");
			this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_COLLECTION_TAB).VALUES(params).excute();
		} else {
			this.activeRecordDAO.auto().table(Constant.TableName.T_APP_TAB).modify(record);
		}
		resultVO.setSuccess(true);
		resultVO.setResult("平台添加成功");
		return resultVO;
	}

	@Path(value = "delete", desc = "删除平台")
	@Service
	public ResultVO<String> delete(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("删除平台", aeolusData));
		ResultVO<String> resultVO = new ResultVO<String>();
		Record<String, Object> record = aeolusData.getRecord();
		Record<String, Object> userRecord = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_APP_TAB)
				.EQUAL("ID", record.getString("ID")).get();
		if (CollectionUtils.isEmpty(userRecord)) {
			resultVO.setSuccess(false);
			resultVO.setResult("平台不存在");
			return resultVO;
		}
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_APP_TAB).EQUAL("ID", record.getString("ID"))
				.excute();
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_COLLECTION_TAB)
				.EQUAL("V_APP_ID", record.getString("ID")).excute();
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_SYS_USER_BASE)
				.EQUAL("USER_ID", record.getString("V_USER_ID")).excute();
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_SYS_USER_ROLE)
				.EQUAL("USER_ID", record.getString("V_USER_ID")).excute();
		resultVO.setSuccess(true);
		resultVO.setResult("删除平台成功");
		return resultVO;
	}

	@Path(value = "/appConf", desc = "平台配置")
	@Service
	public ResultVO<String> appConf(AeolusData aeolusData) throws FacadeException {
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		this.activeRecordDAO.auto().table(Constant.TableName.T_APP_TAB).setCondition("and", "ID=#{ID}").modify(record);
		resultVO.setSuccess(true);
		resultVO.setResult("平台配置修改成功");
		return resultVO;
	}

	public static String getCode() {
		String string = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";// 保存数字0-9 和 大小写字母
		char[] ch = new char[15]; // 声明一个字符数组对象ch 保存 验证码
		for (int i = 0; i < 15; i++) {
			java.util.Random random = new java.util.Random();// 创建一个新的随机数生成器
			int index = random.nextInt(string.length());// 返回[0,string.length)范围的int值 作用：保存下标
			ch[i] = string.charAt(index);// charAt() : 返回指定索引处的 char 值 ==》保存到字符数组对象ch里面
		}
		// 将char数组类型转换为String类型保存到result
		// String result = new String(ch);//方法一：直接使用构造方法 String(char[] value) ：分配一个新的
		// String，使其表示字符数组参数中当前包含的字符序列。
		String result = String.valueOf(ch);// 方法二： String方法 valueOf(char c) ：返回 char 参数的字符串表示形式。
		return result;
	}
}

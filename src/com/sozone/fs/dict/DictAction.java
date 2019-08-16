/**
 * 
 */
package com.sozone.fs.dict;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.Odd;
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
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.FacadeException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.utils.DateUtils;
import com.sozone.fs.common.Constant;

/**
 * @author Administrator
 *
 */
@Path(value="/dictIndex",desc="字典管理")
@Permission(Level.Guest)
public class DictAction {

	
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
	private static Logger logger = LoggerFactory.getLogger(DictAction.class);
	
	@Path(value = "/findPage", desc = "获取字典列表")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("获取字典列表", aeolusData));
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		Pageable pageable = aeolusData.getPageRequest();
		Page<Record<String, Object>> page = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_DICT_TAB).ORDER_BY("V_DICT_TYPE").page(pageable);
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}
	
	@Path(value = "/save", desc = "添加字典")
	@Service
	public ResultVO<String> save(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("添加字典", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getTableRecord(Constant.TableName.T_DICT_TAB);
		String id = record.getString("ID");
		if(StringUtils.isEmpty(id)){
			record.setColumn("ID", Random.generateUUID());
			record.setColumn("V_DISABLED", "Y");
			record.setColumn("V_CREATE_USER", ApacheShiroUtils.getCurrentUserID());
			record.setColumn("V_CREATE_TIME", DateUtils.getDateTime());
			this.activeRecordDAO.pandora().INSERT_INTO(Constant.TableName.T_DICT_TAB).VALUES(record).excute();
		}else{
			record.remove("V_DISABLED", "Y");
			record.remove("V_CREATE_USER", ApacheShiroUtils.getCurrentUserID());
			record.remove("V_CREATE_TIME", DateUtils.getDateTime());
			this.activeRecordDAO.pandora().UPDATE(Constant.TableName.T_DICT_TAB).EQUAL("ID", id).SET(record).excute();
		}
		
		resultVO.setSuccess(true);
		resultVO.setResult("添加字典成功");
		return resultVO;
	}
	
	@Path(value = "delete", desc = "删除字典")
	@Service
	public ResultVO<String> delete(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("删除字典", aeolusData));
		ResultVO<String> resultVO = new ResultVO<String>();
		Record<String, Object> record = aeolusData.getRecord();
		 this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_DICT_TAB).EQUAL("ID", record.getString("ID")).excute();
		resultVO.setSuccess(true);
		resultVO.setResult("删除字典成功");
		return resultVO;
	}
	
	@Path(value="/getDict",desc="获取字典类型列表")
	@Service
	public ResultVO<List<Record<String, Object>>> getDict(AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("添加字典", aeolusData));
		ResultVO<List<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String,Object> record = aeolusData.getRecord();
		List<Record<String, Object>> list = this.activeRecordDAO.pandora().SELECT_ALL_FROM(Constant.TableName.T_DICT_TAB).EQUAL("V_DICT_TYPE", record.getString("V_DICT_TYPE")).list();
		resultVO.setSuccess(true);
		resultVO.setResult(list);
		return resultVO;
	}
	
}

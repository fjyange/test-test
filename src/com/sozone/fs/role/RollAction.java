package com.sozone.fs.role;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.sql.visitor.functions.Nil;
import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
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
import com.sozone.fs.common.Constant;
import com.sozone.fs.user.UserAction;

import antlr.RecognitionException;

@Path(value="/role",desc="用户信息处理接口")
@Permission(Level.Authenticated)
public class RollAction {
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
	private static Logger logger = LoggerFactory.getLogger(RollAction.class);
	
	@Path(value="/findPage",desc="获取角色列表")
	@Service
	public ResultVO<Page<Record<String, Object>>> findPage(AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("获取角色列表", aeolusData));
		Record<String, Object> record = aeolusData.getRecord();
		Pageable pageable = aeolusData.getPageRequest();
		Page<Record<String, Object>> page = this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_ROLE_INFO).page(pageable, record);
		ResultVO<Page<Record<String, Object>>> resultVO = new ResultVO<>();
		resultVO.setSuccess(true);
		resultVO.setResult(page);
		return resultVO;
	}
	
	@Path(value="/save",desc="保存角色")
	@Service
	public ResultVO<String> save (AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("保存角色", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		if(StringUtils.isBlank(record.getString("ROLE_ID"))) {
			record.setColumn("ROLE_ID", Random.generateUUID());
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_ROLE_INFO).save(record);	
		}else {
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_ROLE_INFO).modify(record);
		}
		resultVO.setSuccess(true);
		resultVO.setResult("保存成功");
		return resultVO;
	}
	
	@Path(value="/findRoleMenus",desc="获取角色菜单")
	@Service
	public ResultVO<List<Record<String, Object>>> findRoleMenus(AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("获取角色菜单", aeolusData));
		ResultVO<List<Record<String, Object>>> resultVO = new ResultVO<>();
		Record<String, Object> record  = aeolusData.getRecord();
		if ("4eed68c341e049fa8c6e8341b8af2915".equals(record.getString("ROLE_ID"))) {
			record.setColumn("TYPE", "admin");
		}
		List<Record<String, Object>> list = this.activeRecordDAO.statement().selectList("Role.roleMenuList", record);
		resultVO.setSuccess(true);
		resultVO.setResult(list);
		return resultVO;
	}
	
	@Path(value="/delete",desc="删除角色")
	@Service
	public ResultVO<String> delete(AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("删除角色", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_ROLE_INFO).remove(record);
		this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_ROLE_INFO).setCondition("AND", "ROLE_ID = #{ROLE_ID}");
		resultVO.setSuccess(true);
		resultVO.setResult("角色删除成功");
		return resultVO;
	}
	
	@Path(value="/saveRoleMenus",desc="保存角色菜单")
	@Service
	public ResultVO<String> saveRoleMenus(AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("保存角色菜单", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Record<String, Object> params = new RecordImpl<String, Object>().setColumn("ROLE_ID", record.getString("ROLE_ID"));
		String ids = record.getString("MENU_ID");
		if (StringUtils.isEmpty(ids)) {
			resultVO.setSuccess(false);
			resultVO.setResult("菜单id为空");
			return resultVO;
		}
		String[] idArr = ids.split(",");
		this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_ROLE_FUNC).setCondition("and", "ROLE_ID=#{ROLE_ID}").remove(params);
		for(String id : idArr) {
			params.setColumn("FUNC_ID", id);
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_ROLE_FUNC).save(params);
		}
		resultVO.setSuccess(true);
		resultVO.setResult("角色菜单保存成功");
		return resultVO;
	}
	
	@Path(value="/findAll",desc="查询所有角色")
	@Service
	public ResultVO<List<Record<String, Object>>> findAll(AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("查询所有角色", aeolusData));
		ResultVO<List<Record<String, Object>>> resultVO = new ResultVO<>();
		List<Record<String, Object>> list = this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_ROLE_INFO).list(null);
		resultVO.setSuccess(true);
		resultVO.setResult(list);
		return resultVO;
	}
}

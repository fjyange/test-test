/**
 * 
 */
package com.sozone.fs.menu;

import java.util.ArrayList;
import java.util.List;

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
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.FacadeException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.utils.DateUtils;
import com.sozone.fs.common.Constant;


@Path(value = "/menu", desc = "")
@Permission(Level.Authenticated)
public class MenuAction {

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
	private static Logger logger = LoggerFactory.getLogger(MenuAction.class);

	@Path(value="/delete",desc="删除菜单")
	@Service
	public ResultVO<String> delete(AeolusData aeolusData) throws FacadeException{
		logger.debug(LogUtils.format("删除菜单", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_SYS_MENU).EQUAL("ID", record.getString("id")).excute();
		this.activeRecordDAO.pandora().DELETE_FROM(Constant.TableName.T_SYS_MENU).EQUAL("PARENT_ID", record.getString("id")).excute();
		resultVO.setResult("菜单删除成功");
		resultVO.setSuccess(true);
		return resultVO;
	}
	
	@Path(value = "/findNavTree", desc = "获取菜单")
	@Service
	public ResultVO<List<Record<String, Object>>> findNavTree(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("获取菜单", aeolusData));
		ResultVO<List<Record<String, Object>>> resultVO = new ResultVO<>();
		List<Record<String, Object>> menuList = new ArrayList<Record<String, Object>>();
		Record<String, Object> params = new RecordImpl<String, Object>();
		params.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		List<Record<String, Object>> sysMenus =  null;
		if (StringUtils.equals("77fa0140861d49c98ca2a628e1f4e4d8", ApacheShiroUtils.getCurrentUserID())) {
			sysMenus = this.activeRecordDAO.statement().selectList("Menu.findMenu", params);
		}else {
			sysMenus = this.activeRecordDAO.statement().selectList("Menu.findAll", params);
		}
		for (Record<String, Object> record : sysMenus) {
			if (StringUtils.isBlank(record.getString("parent_id"))
					|| StringUtils.equals("0", record.getString("parent_id"))) {
				record.setColumn("level", "0");
				if (!exists(menuList, record)) {
					menuList.add(record);
				}
			}
		}
		menuList.sort((o1, o2) -> o1.getString("order_num").compareTo(o2.getString("order_num")));
		findChildren(menuList, sysMenus, 1);
		resultVO.setSuccess(true);
		resultVO.setResult(menuList);
		return resultVO;
	}

	@Path(value = "/findMenuTree", desc = "获取权限菜单")
	@Service
	public ResultVO<List<Record<String, Object>>> findMenuTree(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("获取权限菜单", aeolusData));
		ResultVO<List<Record<String, Object>>> resultVO = new ResultVO<>();
		List<Record<String, Object>> menuList = new ArrayList<Record<String, Object>>();
		Record<String, Object> params = new RecordImpl<String, Object>();
		params.setColumn("USER_ID", ApacheShiroUtils.getCurrentUserID());
		params.setColumn("USER_TYPE",ApacheShiroUtils.getCurrentUser().getString("IS_ADMIN"));
		List<Record<String, Object>> list =  null;
		if (StringUtils.equals("77fa0140861d49c98ca2a628e1f4e4d8", ApacheShiroUtils.getCurrentUserID())) {
			list = this.activeRecordDAO.statement().selectList("Menu.findMenu", params);
		}else {
			list = this.activeRecordDAO.statement().selectList("Menu.findAll", params);
		}
		for (Record<String, Object> record : list) {
			if (StringUtils.isBlank(record.getString("parent_id"))
					|| StringUtils.equals("0", record.getString("parent_id"))) {
				record.setColumn("level", "0");
				if (!exists(menuList, record)) {
					menuList.add(record);
				}
			}
		}
		menuList.sort((o1, o2) -> o1.getString("order_num").compareTo(o2.getString("order_num")));
		findChildren(menuList, list, 0);
		resultVO.setSuccess(true);
		resultVO.setResult(menuList);
		return resultVO;
	}

	@Path(value = "/save", desc = "保存菜单")
	@Service
	public ResultVO<String> save(AeolusData aeolusData) throws FacadeException {
		logger.debug(LogUtils.format("保存菜单", aeolusData));
		ResultVO<String> resultVO = new ResultVO<>();
		Record<String, Object> record = aeolusData.getRecord();
		Record<String, Object> params = new RecordImpl<>();
		params.setColumn("ID", record.getString("id")).setColumn("NAME", record.getString("name"))
				.setColumn("PARENT_ID", record.getString("parent_id")).setColumn("URL", record.getString("url"))
				.setColumn("PERMS", record.getString("perms")).setColumn("TYPE", record.getString("type"))
				.setColumn("ICON", record.getString("icon")).setColumn("ORDER_NUM", record.getString("order_num"));
		if (StringUtils.isBlank(record.getString("id"))) {
			params.setColumn("ID", Random.generateUUID());
			params.setColumn("CREATE_BY", ApacheShiroUtils.getCurrentUserID());
			params.setColumn("CREATE_TIME", DateUtils.getDateTime());
			params.setColumn("LAST_UPDATE_BY", ApacheShiroUtils.getCurrentUserID());
			params.setColumn("LAST_UPDATE_TIME", DateUtils.getDateTime());
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_MENU).save(params);
		} else {
			params.setColumn("LAST_UPDATE_BY", ApacheShiroUtils.getCurrentUserID());
			params.setColumn("LAST_UPDATE_TIME", DateUtils.getDateTime());
			this.activeRecordDAO.auto().table(Constant.TableName.T_SYS_MENU).modify(params);
		}
		resultVO.setSuccess(true);
		resultVO.setResult("菜单保存成功");
		return resultVO;
	}

	private void findChildren(List<Record<String, Object>> sysMenus, List<Record<String, Object>> menus, int menuType) {
		for (Record<String, Object> sysMenu : sysMenus) {
			List<Record<String, Object>> children = new ArrayList<>();
			for (Record<String, Object> menu : menus) {
				if (menuType == 1 && StringUtils.equals("2", menu.getString("type"))) {
					// 如果是获取类型不需要按钮，且菜单类型是按钮的，直接过滤掉
					continue;
				}
				if (!StringUtils.isBlank(sysMenu.getString("id"))
						&& StringUtils.equals(sysMenu.getString("id"), menu.getString("parent_id"))) {
					menu.setColumn("parent_name", sysMenu.getString("name"));
					menu.setColumn("level", sysMenu.getInteger("level") + 1);
					if (!exists(children, menu)) {
						children.add(menu);
					}
				}
			}
			sysMenu.setColumn("children", children);
			children.sort((o1, o2) -> o1.getString("order_num").compareTo(o2.getString("order_num")));
			findChildren(children, menus, menuType);
		}
	}

	private boolean exists(List<Record<String, Object>> list, Record<String, Object> record) {
		boolean exist = false;
		for (Record<String, Object> menuRecord : list) {
			if (StringUtils.equals(record.getString("id"), menuRecord.getString("id"))) {
				exist = true;
			}
		}
		return exist;
	}
}

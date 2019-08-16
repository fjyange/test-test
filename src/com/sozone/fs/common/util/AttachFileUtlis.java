package com.sozone.fs.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.sozone.aeolus.attach.common.AttachUtlis;
import com.sozone.aeolus.authorize.common.Constant;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.ActiveRecordDAOImpl;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.exception.FacadeException;

/**
 * 
 * 附件删除工具类<br/>
 * <p>
 * 附件删除工具类<br/>
 * </p>
 * Time：2018年1月26日 下午2:06:14<br/>
 * 
 * @author zhangguanxing
 * @version 1.0.0
 * @since 1.0.0
 */
public class AttachFileUtlis
{
	/**
	 * 
	 * 删除附件组ID文件信息<br/>
	 * <p>
	 * 包含组信息，附件信息，文件系统文件
	 * </p>
	 * 
	 * @param refid
	 *            附件组ID
	 * @throws FacadeException
	 *             自定义Facade异常
	 */
	public static void deleteByRefID(String refid) throws FacadeException
	{
		if (StringUtils.isEmpty(refid))
		{
			return;
		}
		ActiveRecordDAO activeRecordDAO = ActiveRecordDAOImpl.getInstance();
		Record<String, Object> param = new RecordImpl<String, Object>();
		param.setColumn("REFERENCE_ID", refid);
		// 获取附件组信息
		List<Record<String, Object>> list = activeRecordDAO.auto()
				.table(Constant.TableName.T_ATTACH_FILE_REFERENCE).list(param);

		for (Record<String, Object> record : list)
		{
			String attachId = record.getString("ATTACH_ID");
			// 获取文件信息
			Record<String, Object> attachInfoRecord = activeRecordDAO.auto()
					.table(Constant.TableName.T_ATTACH_FILE_INFO).get(attachId);
			// 获取文件类型
			String attachTypeId = attachInfoRecord.getString("ATTACH_TYPE_ID");
			// 获取文件类型信息
			Record<String, Object> attachTypeRecord = activeRecordDAO.auto()
					.table(Constant.TableName.T_ATTACH_TYPE_INFO)
					.get(attachTypeId);
			// 文件的保存路径
			String filePath = attachTypeRecord.getString("V_SAVEPATH")
					+ File.separator
					+ attachInfoRecord.getString("V_REAL_PATH");
			// 删除文件的保存路径信息
			AttachUtlis.deleteFile(filePath);
			// 删除文件信息
			activeRecordDAO.auto().table(Constant.TableName.T_ATTACH_FILE_INFO)
					.remove(attachId);

		}
		// 删除附件组信息
		activeRecordDAO.auto()
				.table(Constant.TableName.T_ATTACH_FILE_REFERENCE)
				.remove(param);

	}

	/**
	 * 
	 * 删除ID文件信息<br/>
	 * <p>
	 * 附件信息，文件系统文件
	 * </p>
	 * 
	 * @param fileId
	 *            附件ID
	 * @throws FacadeException
	 *             自定义Facade异常
	 */
	public static void deleteByFileId(String fileId) throws FacadeException
	{
		if (StringUtils.isEmpty(fileId))
		{
			return;
		}
		ActiveRecordDAO activeRecordDAO = ActiveRecordDAOImpl.getInstance();
		// 获取文件信息
		Record<String, Object> attachInfoRecord = activeRecordDAO.auto()
				.table(Constant.TableName.T_ATTACH_FILE_INFO).get(fileId);
		// 获取文件类型
		String attachTypeId = attachInfoRecord.getString("ATTACH_TYPE_ID");
		Record<String, Object> attachTypeRecord = activeRecordDAO.auto()
				.table(Constant.TableName.T_ATTACH_TYPE_INFO).get(attachTypeId);
		// 文件保存路径
		String filePath = attachTypeRecord.getString("V_SAVEPATH")
				+ File.separator + attachInfoRecord.getString("V_REAL_PATH");
		// 删除文件保存路径信息
		AttachUtlis.deleteFile(filePath);
		// 删除文件信息
		activeRecordDAO.auto().table(Constant.TableName.T_ATTACH_FILE_INFO)
				.remove(fileId);

	}

	/**
	 * 
	 * 获取组ID文件信息<br/>
	 * <p>
	 * 获取组ID文件信息
	 * </p>
	 * 
	 * @param refid
	 *            附件组ID
	 * @return List<Record<String,Object>>
	 * @throws FacadeException
	 *             自定义Facade异常
	 */
	public static List<Record<String, Object>> getByRefID(String refid)
			throws FacadeException
	{
		List<Record<String, Object>> list = new ArrayList<Record<String, Object>>();
		if (StringUtils.isEmpty(refid))
		{
			return list;
		}
		ActiveRecordDAO activeRecordDAO = ActiveRecordDAOImpl.getInstance();
		Record<String, Object> param = new RecordImpl<String, Object>();
		param.setColumn("REFERENCE_ID", refid);
		// 获取附件组信息
		list = activeRecordDAO.auto()
				.table(Constant.TableName.T_ATTACH_FILE_REFERENCE).list(param);
		return list;
	}
}

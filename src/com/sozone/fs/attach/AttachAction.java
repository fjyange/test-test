/**
 * 
 */
package com.sozone.fs.attach;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sozone.aeolus.annotation.Handler;
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
import com.sozone.aeolus.exception.DAOException;
import com.sozone.aeolus.ext.rs.ResultVO;
import com.sozone.aeolus.upload.handler.MultipartFormDataHandler;
import com.sozone.aeolus.util.CollectionUtils;
import com.sozone.aeolus.util.MultiValueMap;
import com.sozone.aeolus.util.StringUtils;
import com.sozone.fs.common.Constant;

/**
 * @author yange
 *
 */
@Path(value = "/attach", desc = "文件上传处理接口")
@Permission(Level.Guest)
public class AttachAction {

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
	private static Logger logger = LoggerFactory.getLogger(AttachAction.class);

	/**
	 * 文件上传<br/>
	 * <p>
	 * 文件上传
	 * </p>
	 * 
	 * @param data
	 * @throws Exception
	 * @author:严格
	 * @date：2019年7月19日 下午10:49:45
	 */
	@Path(value = "fileUpload", desc = "文件上传")
	// 访问方式
	@Service
	@Handler(MultipartFormDataHandler.class)
	@Permission(Level.Guest)
	public void fileUpload(AeolusData data) throws Exception {
		logger.debug(LogUtils.format("fileupload文件上传", data));
		ResultVO<Record<String, Object>> resultVO = new ResultVO<Record<String, Object>>(
				false);
		// 获取文件参数值
		MultiValueMap<String, Object> paramsMap = data.getParamsMap();
		Set<String> keySet = paramsMap.keySet();
		Record<String, Object> params = new RecordImpl<String, Object>();
		// StringBuffer errorString = new StringBuffer();
		for (String key : keySet) {
			Object obj = data.getParam(key);
			if (null != obj && obj instanceof String) {
				params.put(key, new String(
						((String) obj).getBytes("ISO8859-1"), "UTF-8"));
			}
		}
		// 获取文件信息
		DiskFileItem fileItem = data.getParam("file");
		// 处理文件名称
		String fileSuffix = "."
				+ StringUtils.lowerCase(FilenameUtils.getExtension(fileItem
						.getName()));
		String fileName = System.currentTimeMillis() + fileSuffix;
		if (fileName.indexOf("\\") != -1) {
			fileName = fileName.substring(fileName.lastIndexOf("\\") + 1,
					fileName.length());
		}
		// 写入文件
		String path = "FILE";
		File newFile = new File(path, fileName);
		if (!newFile.getParentFile().exists()) {
			newFile.getParentFile().mkdirs();
		}
		try {
			fileItem.write(newFile);
		} catch (Exception e) {
			// throw new ValidateException("", "文件上传失败:" + e.getMessage(), e);
			logger.error(LogUtils.format("文件上传失败"));
			resultVO.setErrorDesc("文件上传失败");
			renderHtmlStr(data.getHttpServletResponse(), resultVO.toString());
			return;
		}
		// 判断文件是否有值
		if (StringUtils.isNotEmpty(fileName)) {
			resultVO = saveFile(path, fileName, data, resultVO);
		}
		renderHtmlStr(data.getHttpServletResponse(), resultVO.toString());
	}

	/**
	 * 
	 * 保存附件表信息<br/>
	 * <p>
	 * 保存附件表信息
	 * </p>
	 * 
	 * @param fileSize
	 *            文件大小
	 * @param path
	 *            文件路径
	 * @param fileName
	 *            文件名称
	 * @param data
	 * @return
	 * @throws DAOException
	 */
	private ResultVO<Record<String, Object>> saveFile(String path,
			String fileName, AeolusData data,
			ResultVO<Record<String, Object>> resultVO) throws DAOException {
		// 附件信息保存
		Record<String, Object> record = new RecordImpl<String, Object>();
		String fileID = Random.generateUUID();
		record.setColumn("ID", fileID);
		record.setColumn("V_NAME", fileName);
		record.setColumn("V_PATH", path);
		record.setColumn("V_USER_ID", ApacheShiroUtils.getCurrentUserID());
		this.activeRecordDAO.pandora()
				.INSERT_INTO(Constant.TableName.T_FILE_TAB).VALUES(record)
				.excute();
		resultVO.setSuccess(true);
		Record<String, Object> resultRecord = new RecordImpl<String, Object>();
		resultRecord.setColumn("ID", fileID);
		resultRecord.setColumn("V_NAME", fileName);
		resultRecord.setColumn("V_LETTER_NO",
				FilenameUtils.getBaseName(fileName));
		resultVO.setResult(resultRecord);
		resultVO.setSuccess(true);
		return resultVO;
	}

	@Path(value = "/getFile", desc = "获取图片")
	@Service
	@Permission(Level.Guest)
	public void getFile(AeolusData aeolusData) throws IOException {
		logger.debug(LogUtils.format("获取图片", aeolusData));
		Record<String, Object> record = aeolusData.getRecord();
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		OutputStream out = null;
		FileInputStream inputFile = null;
		try {
			Record<String, Object> fileRecord = this.activeRecordDAO.pandora()
					.SELECT_ALL_FROM(Constant.TableName.T_FILE_TAB)
					.EQUAL("ID", record.getString("ID")).get();
			if (CollectionUtils.isEmpty(fileRecord)) {
				throw new Exception("图片丢失，请联系客服");
			}
			File file = new File(fileRecord.getString("V_PATH"),
					fileRecord.getString("V_NAME"));
			inputFile = new FileInputStream(file);
			int i = inputFile.available();
			// byte数组用于存放图片字节数据
			byte[] buff = new byte[i];
			inputFile.read(buff);
			// 设置发送到客户端的响应内容类型
			response.setContentType("image/*");
			out = response.getOutputStream();
			out.write(buff);

		} catch (Exception e) {
			logger.error(LogUtils.format("读取图片异常", e.getMessage()), e);
			e.printStackTrace();
		} finally {
			// 记得关闭输入流
			inputFile.close();
			// 关闭响应输出流
			out.close();
		}

	}

	/**
	 * 渲染HTML输出
	 * 
	 * @param response
	 * @param str
	 */
	protected void renderHtmlStr(HttpServletResponse response, String str) {
		response.setContentType("text/html;charset=UTF-8");
		try {
			response.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

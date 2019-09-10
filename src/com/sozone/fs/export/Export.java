
package com.sozone.fs.export;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.PathParam;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.dao.data.RecordImpl;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.FacadeException;
import com.sozone.aeolus.utils.DateUtils;
import com.sozone.fs.common.util.WebToolUtils;


@Path(value = "/export", desc = "文件导出")
@Permission(Level.Guest)
public class Export {

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(Export.class);
	/**
	 * 持久化接口
	 */
	protected ActiveRecordDAO activeRecordDAO = null;

	/**
	 * activeRecordDAO属性的set方法
	 * 
	 * @param activeRecordDAO
	 *            the activeRecordDAO to set
	 */
	public void setActiveRecordDAO(ActiveRecordDAO activeRecordDAO) {
		this.activeRecordDAO = activeRecordDAO;
	}

	@Path(value = "/tenderLefferExport", desc = "招标人导出保函信息")
	@Service
	public void tenderLefferExport(AeolusData aeolusData) throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		Record<String, Object> params = aeolusData.getRecord();
		String ids= params.getString("lefferIds");
		String[] idAttr = ids.split(",");
		String idArr = "";
		for (String id : idAttr) {
			idArr += ",'" + id + "'";
		}
		params.setColumn("IDS", idArr.substring(1));
		List<Record<String, Object>> list = this.activeRecordDAO.statement().selectList("Project.projectInfoList",
				params);
		List<ProjectLefferBean> projectList = new ArrayList<ProjectLefferBean>();
		int i = 1;
		for (Record<String, Object> projectRecord : list)
		{
			ProjectLefferBean projectLefferBean = new ProjectLefferBean(i + "",
					projectRecord.getString("V_NO"),
					projectRecord.getString("BIDDER_NAME"),
					projectRecord.getString("FINANCIAL_NAME"),
					WebToolUtils.timeToString(projectRecord
							.getString("N_UPDATE_TIME")));
			projectList.add(projectLefferBean);
			i++;
		}
		OutputStream out = null;
		if (projectList.size() > 0) {
			String[] headers = { "序号", "电子保函单号", "投标人名字", "开具保函的金融机构名称", "开具时间"};
			ExportExcel<ProjectLefferBean> ex = new ExportExcel<ProjectLefferBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = (DateUtils.getDate()).replace(" ", "-") + "project.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, projectList, out);
				int c;
				while ((c = is.read()) != -1) {
					out.write(c);
				}
				is.close();
				out.flush();
				out.close();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				out.close();
			}
		}
	}

	
	@Path(value = "/financialExportLeffer/{ids}", desc = "金融机构导出选中保函")
	@Service
	public void financialExportLeffer(@PathParam("ids") String ids, AeolusData aeolusData) throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		String[] idAttr = ids.split(",");
		String idArr = "";
		for (String id : idAttr) {
			idArr += ",'" + id + "'";
		}
		Record<String, Object> params = new RecordImpl<String, Object>();
		params.setColumn("ID", idArr.substring(1));
		List<LefferBean> list = selectLeffer(params);
		OutputStream out = null;
		if (list.size() > 0) {
			String[] headers = { "序号", "保单号", "保函申请时间", "招标项目名称", "标段包名称", "保函类型", "投保人名称", "协议签约状态", "电子保函状态" };
			ExportExcel<LefferBean> ex = new ExportExcel<LefferBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = (DateUtils.getDate()).replace(" ", "-") + "_bh.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, list, out);
				int c;
				while ((c = is.read()) != -1) {
					out.write(c);
				}
				is.close();
				out.flush();
				out.close();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				out.close();
			}
		}
	}

	private List<LefferBean> selectLeffer(Record<String, Object> params) throws FacadeException {
		List<Record<String, Object>> list = this.activeRecordDAO.statement().selectList("Leffer.financialExportLeffer",
				params);
		List<LefferBean> lefferList = new ArrayList<LefferBean>();
		if (list.size() > 0) {
			int i = 1;
			for (Record<String, Object> df : list) {
				LefferBean lb = new LefferBean();
				lb.setRowNum(i + "");
				lb.setVno(df.getString("V_NO") == null ? "" : df.getString("V_NO"));
				String tempTime=df.getString("N_CREATE_TIME") == null ? "" : df.getString("N_CREATE_TIME");
				lb.setCreateTime(WebToolUtils.timeToString(tempTime));
				lb.setTenderName(
						df.getString("V_PROJECT_TENDER_NAME") == null ? "" : df.getString("V_PROJECT_TENDER_NAME"));
				lb.setSectionName(
						df.getString("V_PROJECT_SECTION_NAME") == null ? "" : df.getString("V_PROJECT_SECTION_NAME"));
				lb.setLefferStatus(
						df.getString("N_LEFFER_STATUS_TXT") == null ? "" : df.getString("N_LEFFER_STATUS_TXT"));
				lb.setBidderName(df.getString("BIDDER_NAME") == null ? "" : df.getString("BIDDER_NAME"));
				lb.setSignStatus(df.getString("N_SIGN_STATUS_TXT") == null ? "" : df.getString("N_SIGN_STATUS_TXT"));
				lb.setAuditStatus(df.getString("N_AUDIT_STATUS_TXT") == null ? "" : df.getString("N_AUDIT_STATUS_TXT"));
				lefferList.add(lb);
				i++;
			}
		}
		return lefferList;
	}

}

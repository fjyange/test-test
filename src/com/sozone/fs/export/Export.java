
package com.sozone.fs.export;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.VoiceStatus;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.authorize.utlis.ApacheShiroUtils;
import com.sozone.aeolus.dao.ActiveRecordDAO;
import com.sozone.aeolus.dao.data.Page;
import com.sozone.aeolus.dao.data.Record;
import com.sozone.aeolus.data.AeolusData;

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

	@Path(value = "/todayCommisionExportCount", desc = "导出今日平台提现")
	@Service
	public void todayCommisionExportCount(AeolusData aeolusData) throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		Record<String, Object> params = aeolusData.getRecord();
		List<Record<String, Object>> list = this.activeRecordDAO.statement().selectList("Commission.commissionCount",
				params);
		List<CommissionCountBean> commissionCountBeans = new ArrayList<CommissionCountBean>();
		for (Record<String, Object> commissionRecord : list) {
			CommissionCountBean commissionCountBean = new CommissionCountBean(commissionRecord.getString("V_MONEY"),
					commissionRecord.getString("V_REALITY"), commissionRecord.getString("RATE_MONEY"),
					commissionRecord.getString("V_FORMALITIES"), commissionRecord.getString("V_APP_NAME"));
			commissionCountBeans.add(commissionCountBean);
		}
		OutputStream out = null;
		if (commissionCountBeans.size() > 0) {
			String[] headers = { "平台名称", "总提现金额", "实际到账金额", "扣除费用", "手续费" };
			ExportExcel<CommissionCountBean> ex = new ExportExcel<CommissionCountBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = System.currentTimeMillis() + "commissionCount.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, commissionCountBeans, out);
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

	@Path(value = "/todayCommisionExportDetail", desc = "导出今日平台明细")
	@Service
	public void todayCommisionExportDetail(AeolusData aeolusData) throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		Record<String, Object> params = aeolusData.getRecord();
		List<Record<String, Object>> list = this.activeRecordDAO.statement().selectList("Commission.commissionDetail",
				params);
		List<CommissionDetailBean> commissionDetailBeans = new ArrayList<CommissionDetailBean>();
		for (Record<String, Object> commissionRecord : list) {
			CommissionDetailBean commissionDetailBean = new CommissionDetailBean(commissionRecord.getString("V_MONEY"),
					commissionRecord.getString("V_REALITY"), commissionRecord.getString("V_FORMALITIES"),
					commissionRecord.getString("V_APP_NAME"));
			commissionDetailBeans.add(commissionDetailBean);
		}
		OutputStream out = null;
		if (commissionDetailBeans.size() > 0) {
			String[] headers = { "平台名称", "总提现金额", "实际到账金额", "手续费" };
			ExportExcel<CommissionDetailBean> ex = new ExportExcel<CommissionDetailBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = System.currentTimeMillis() + "commissionDetail.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, commissionDetailBeans, out);
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

	@Path(value = "/todayTopupExportDetail", desc = "导出今日充值明细")
	@Service
	public void todayTopupExportDetail(AeolusData aeolusData) throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		Record<String, Object> params = aeolusData.getRecord();
		List<Record<String, Object>> list = this.activeRecordDAO.statement().selectList("Charge.chargeDetail", params);
		List<TopupBean> topupBeans = new ArrayList<TopupBean>();
		for (Record<String, Object> topupRecord : list) {
			TopupBean topupBean = new TopupBean(topupRecord.getString("USER_NAME"), topupRecord.getString("V_MONEY"));
			topupBeans.add(topupBean);
		}
		OutputStream out = null;
		if (topupBeans.size() > 0) {
			String[] headers = { "用户名", "充值金额" };
			ExportExcel<TopupBean> ex = new ExportExcel<TopupBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = System.currentTimeMillis() + "topupdetail.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, topupBeans, out);
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

	@Path(value = "/todayTopupExportCount", desc = "导出今日充值总额")
	@Service
	public void todayTopupExportCount(AeolusData aeolusData) throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		Record<String, Object> params = aeolusData.getRecord();
		List<Record<String, Object>> list = this.activeRecordDAO.statement().selectList("Charge.chargeCount", params);
		List<TopupBean> topupBeans = new ArrayList<TopupBean>();
		for (Record<String, Object> topupRecord : list) {
			TopupBean topupBean = new TopupBean(topupRecord.getString("USER_NAME"), topupRecord.getString("V_MONEY"));
			topupBeans.add(topupBean);
		}
		OutputStream out = null;
		if (topupBeans.size() > 0) {
			String[] headers = { "用户名", "充值金额" };
			ExportExcel<TopupBean> ex = new ExportExcel<TopupBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = System.currentTimeMillis() + "topupcount.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, topupBeans, out);
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

	@Path(value = "/todayAPPOrderExportCount", desc = "导出平台收款总额")
	@Service
	public void todayAPPOrderExportCount(AeolusData aeolusData) throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		Record<String, Object> params = aeolusData.getRecord();
		List<Record<String, Object>> list = this.activeRecordDAO.statement().selectList("Order.appOrder", params);
		List<AppOrderBean> appOrderBeans = new ArrayList<AppOrderBean>();
		for (Record<String, Object> appOrderRecord : list) {
			AppOrderBean topupBean = new AppOrderBean(appOrderRecord.getString("V_APP_NAME"),
					appOrderRecord.getString("V_MONEY"), appOrderRecord.getString("COMMISSION_MONEY"),
					appOrderRecord.getString("FORMALITIES_MONEY"), appOrderRecord.getString("REALITY_MONEY"),
					appOrderRecord.getString("V_CASH_COLLECTION"));
			appOrderBeans.add(topupBean);
		}
		OutputStream out = null;
		if (appOrderBeans.size() > 0) {
			String[] headers = { "平台名", "收款金额","今日提成金额","手续费","实际到账金额","可提现金额" };
			ExportExcel<AppOrderBean> ex = new ExportExcel<AppOrderBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = System.currentTimeMillis() + "apporder.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, appOrderBeans, out);
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

	@Path(value = "/todayUserOrderExportCount", desc = "导出用户收款总额")
	@Service
	public void todayUserOrderExportCount(AeolusData aeolusData) throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		Record<String, Object> params = aeolusData.getRecord();
		List<Record<String, Object>> list = this.activeRecordDAO.statement().selectList("Order.userOrder", params);
		List<UserOrderBean> userOrderBeans = new ArrayList<UserOrderBean>();
		for (Record<String, Object> userOrderRecord : list) {
			UserOrderBean userOrderBean = new UserOrderBean(userOrderRecord.getString("USER_NAME"),
					userOrderRecord.getString("V_MONEY"), userOrderRecord.getString("TOPUP_MONEY"),
					userOrderRecord.getString("WITHDRAW_MONEY"), userOrderRecord.getString("V_SURPLUS_BOND"));
			userOrderBeans.add(userOrderBean);
		}
		OutputStream out = null;
		if (userOrderBeans.size() > 0) {
			String[] headers = { "用户名", "收款金额", "充值金额", "提现金额", "剩余保证金" };
			ExportExcel<UserOrderBean> ex = new ExportExcel<UserOrderBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = System.currentTimeMillis() + "userorder.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, userOrderBeans, out);
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

	@Path(value="/exportOrder",desc="导出订单")
	@Service
	public void exportOrder(AeolusData aeolusData)  throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		Record<String, Object> record = aeolusData.getRecord();
		String searchTime = record.getString("SEARCH_TIME");
		if (StringUtils.isNotBlank(searchTime)) {
			String[] times = searchTime.split(",");
			record.setColumn("START_TIME", times[0]);
			record.setColumn("END_TIME", times[1]);
		}
		List<Record<String, Object>> list =  this.activeRecordDAO.statement().selectList("Order.orderList",record);
		List<OrderBean> orderBeans = new ArrayList<OrderBean>();
		for (Record<String, Object> orderRecord : list) {
			String payType = "";
			if (StringUtils.equals("01",  orderRecord.getString("V_PAY_TYPE"))) {
				payType = "支付宝";
			}else {
				payType = "微信";
			}
			String status = "";
			if (StringUtils.equals("0", orderRecord.getString("V_STATUS"))) {
				status = "未确认";
			}else if (StringUtils.equals("1", orderRecord.getString("V_STATUS"))) {
				status = "已确认";
			}else if (StringUtils.equals("2", orderRecord.getString("V_STATUS"))) {
				status = "超时";
			}else if (StringUtils.equals("3", orderRecord.getString("V_STATUS"))) {
				status = "补单";
			} 
			OrderBean orderBean = new OrderBean(orderRecord.getString("V_ORDER_NO"),
					orderRecord.getString("V_MONEY"), payType,
					orderRecord.getString("V_APP_NAME"), orderRecord.getString("USER_NAME"), orderRecord.getString("V_PAY_NAME")
					, orderRecord.getString("V_CREATE_TIME"),status);
			orderBeans.add(orderBean);
		}
		OutputStream out = null;
		if (orderBeans.size() > 0) {
			String[] headers = { "订单号", "金额", "支付方式", "所属平台", "所属用户","支付账户","订单时间","订单状态" };
			ExportExcel<OrderBean> ex = new ExportExcel<OrderBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = System.currentTimeMillis() + "order.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, orderBeans, out);
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
	
	@Path(value="/exportOrderHistory",desc="导出历史订单")
	@Service
	public void exportOrderHistory(AeolusData aeolusData)  throws Exception {
		HttpServletResponse response = aeolusData.getHttpServletResponse();
		Record<String, Object> record = aeolusData.getRecord();
		String searchTime = record.getString("SEARCH_TIME");
		if (StringUtils.isNotBlank(searchTime)) {
			String[] times = searchTime.split(",");
			record.setColumn("START_TIME", times[0]);
			record.setColumn("END_TIME", times[1]);
		}
		List<Record<String, Object>> list =  this.activeRecordDAO.statement().selectList("Order.historyList",record);
		List<OrderBean> orderBeans = new ArrayList<OrderBean>();
		for (Record<String, Object> orderRecord : list) {
			String payType = "";
			if (StringUtils.equals("01",  orderRecord.getString("V_PAY_TYPE"))) {
				payType = "支付宝";
			}else {
				payType = "微信";
			}
			String status = "";
			if (StringUtils.equals("0", orderRecord.getString("V_STATUS"))) {
				status = "未确认";
			}else if (StringUtils.equals("1", orderRecord.getString("V_STATUS"))) {
				status = "已确认";
			}else if (StringUtils.equals("2", orderRecord.getString("V_STATUS"))) {
				status = "超时";
			}else if (StringUtils.equals("3", orderRecord.getString("V_STATUS"))) {
				status = "补单";
			} 
			OrderBean orderBean = new OrderBean(orderRecord.getString("V_ORDER_NO"),
					orderRecord.getString("V_MONEY"), payType,
					orderRecord.getString("V_APP_NAME"), orderRecord.getString("USER_NAME"), orderRecord.getString("V_PAY_NAME")
					, orderRecord.getString("V_CREATE_TIME"),status);
			orderBeans.add(orderBean);
		}
		OutputStream out = null;
		if (orderBeans.size() > 0) {
			String[] headers = { "订单号", "金额", "支付方式", "所属平台", "所属用户","支付账户","订单时间","订单状态" };
			ExportExcel<OrderBean> ex = new ExportExcel<OrderBean>();
			try {
				response.reset();
				// 设置response的Header
				String fileName = System.currentTimeMillis() + "orderHistory.xls";
				response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
				out = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				InputStream is = ex.exportExcel(headers, orderBeans, out);
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
	
}

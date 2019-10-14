package com.sozone.fs.export;

public class OrderBean {

	private String orderNo;
	private String vmoney;
	private String payType;
	private String appName;
	private String userName;
	private String accountName;
	private String orderTime;
	private String status;
	
	
	public OrderBean(String orderNo, String vmoney, String payType, String appName, String userName, String accountName,
			String orderTime, String status) {
		super();
		this.orderNo = orderNo;
		this.vmoney = vmoney;
		this.payType = payType;
		this.appName = appName;
		this.userName = userName;
		this.accountName = accountName;
		this.orderTime = orderTime;
		this.status = status;
	}
	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}
	/**
	 * @param orderNo the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * @return the vmoney
	 */
	public String getVmoney() {
		return vmoney;
	}
	/**
	 * @param vmoney the vmoney to set
	 */
	public void setVmoney(String vmoney) {
		this.vmoney = vmoney;
	}
	/**
	 * @return the payType
	 */
	public String getPayType() {
		return payType;
	}
	/**
	 * @param payType the payType to set
	 */
	public void setPayType(String payType) {
		this.payType = payType;
	}
	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}
	/**
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}
	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * @return the orderTime
	 */
	public String getOrderTime() {
		return orderTime;
	}
	/**
	 * @param orderTime the orderTime to set
	 */
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}

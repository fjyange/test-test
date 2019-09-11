package com.sozone.fs.export;

import org.apache.commons.lang.StringUtils;

public class UserOrderBean {
	private String userName;
	private String vmoney;
	private String topupMoney;
	private String withdrawMoney;
	private String vsurplusBond;
	
	public UserOrderBean() {
		super();
	}

	public UserOrderBean(String userName, String vmoney, String topupMoney, String withdrawMoney, String vsurplusBond) {
		super();
		this.userName = StringUtils.isNotEmpty(userName)?userName:"";
		this.vmoney = StringUtils.isNotEmpty(vmoney)?vmoney:"0";
		this.topupMoney = StringUtils.isNotEmpty(topupMoney)?topupMoney:"0";
		this.withdrawMoney = StringUtils.isNotEmpty(withdrawMoney)?withdrawMoney:"0";
		this.vsurplusBond = StringUtils.isNotEmpty(vsurplusBond)?vsurplusBond:"0";
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
	 * @return the topupMoney
	 */
	public String getTopupMoney() {
		return topupMoney;
	}

	/**
	 * @param topupMoney the topupMoney to set
	 */
	public void setTopupMoney(String topupMoney) {
		this.topupMoney = topupMoney;
	}

	/**
	 * @return the withdrawMoney
	 */
	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	/**
	 * @param withdrawMoney the withdrawMoney to set
	 */
	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	/**
	 * @return the vsurplusBond
	 */
	public String getVsurplusBond() {
		return vsurplusBond;
	}

	/**
	 * @param vsurplusBond the vsurplusBond to set
	 */
	public void setVsurplusBond(String vsurplusBond) {
		this.vsurplusBond = vsurplusBond;
	}
	
}

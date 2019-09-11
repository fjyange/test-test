package com.sozone.fs.export;

public class TopupBean {

	private String userName;
	private String vmoney;
	
	public TopupBean() {
		super();
	}
	public TopupBean(String userName, String vmoney) {
		super();
		this.userName = userName;
		this.vmoney = vmoney;
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
	
}

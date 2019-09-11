package com.sozone.fs.export;

import org.apache.commons.lang.StringUtils;

public class CommissionCountBean {
	private String vappName;
	private String vmoney;
	private String vreality;
	private String rateMoney;
	private String vformalities;

	
	
	public CommissionCountBean() {
		super();
	}
	public CommissionCountBean(String vMoney, String vReality, String rateMoney, String vFormalities,
			String vAppName) {
		super();
		this.vappName = StringUtils.isNotBlank(vAppName)?vAppName:"";
		this.vmoney = StringUtils.isNotBlank(vMoney)?vMoney:"";
		this.vreality = StringUtils.isNotBlank(vReality)? vReality:"";
		this.rateMoney = StringUtils.isNotBlank(rateMoney)?rateMoney:"";
		this.vformalities = StringUtils.isNotBlank(vFormalities)?vFormalities:"";
	}
	/**
	 * @return the vappName
	 */
	public String getVappName() {
		return vappName;
	}
	/**
	 * @param vappName the vappName to set
	 */
	public void setVappName(String vappName) {
		this.vappName = vappName;
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
	 * @return the vreality
	 */
	public String getVreality() {
		return vreality;
	}
	/**
	 * @param vreality the vreality to set
	 */
	public void setVreality(String vreality) {
		this.vreality = vreality;
	}
	/**
	 * @return the rateMoney
	 */
	public String getRateMoney() {
		return rateMoney;
	}
	/**
	 * @param rateMoney the rateMoney to set
	 */
	public void setRateMoney(String rateMoney) {
		this.rateMoney = rateMoney;
	}
	/**
	 * @return the vformalities
	 */
	public String getVformalities() {
		return vformalities;
	}
	/**
	 * @param vformalities the vformalities to set
	 */
	public void setVformalities(String vformalities) {
		this.vformalities = vformalities;
	}
	
}

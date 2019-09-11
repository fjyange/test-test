package com.sozone.fs.export;

import org.apache.commons.lang.StringUtils;

public class AppOrderBean {

	private String vappName;
	private String vmoney;
	private String vcashCollection;
	private String commissionMoney;
	private String formalitiesMoney;
	private String realityMoney;
	public AppOrderBean() {
		super();
	}
	public AppOrderBean(String vappName, String vmoney, String vcashCollection, String commissionMoney,
			String formalitiesMoney, String realityMoney) {
		super();
		this.vappName = StringUtils.isNotBlank(vappName)?vappName:"";
		this.vmoney = StringUtils.isNotBlank(vmoney)?vmoney:"0";
		this.vcashCollection = StringUtils.isNotBlank(vcashCollection)?vcashCollection:"0";
		this.commissionMoney = StringUtils.isNotBlank(commissionMoney)?commissionMoney:"0";
		this.formalitiesMoney = StringUtils.isNotBlank(formalitiesMoney)?formalitiesMoney:"0";
		this.realityMoney = StringUtils.isNotBlank(realityMoney)?vmoney:"0";
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
	 * @return the vcashCollection
	 */
	public String getVcashCollection() {
		return vcashCollection;
	}
	/**
	 * @param vcashCollection the vcashCollection to set
	 */
	public void setVcashCollection(String vcashCollection) {
		this.vcashCollection = vcashCollection;
	}
	/**
	 * @return the commissionMoney
	 */
	public String getCommissionMoney() {
		return commissionMoney;
	}
	/**
	 * @param commissionMoney the commissionMoney to set
	 */
	public void setCommissionMoney(String commissionMoney) {
		this.commissionMoney = commissionMoney;
	}
	/**
	 * @return the formalitiesMoney
	 */
	public String getFormalitiesMoney() {
		return formalitiesMoney;
	}
	/**
	 * @param formalitiesMoney the formalitiesMoney to set
	 */
	public void setFormalitiesMoney(String formalitiesMoney) {
		this.formalitiesMoney = formalitiesMoney;
	}
	/**
	 * @return the realityMoney
	 */
	public String getRealityMoney() {
		return realityMoney;
	}
	/**
	 * @param realityMoney the realityMoney to set
	 */
	public void setRealityMoney(String realityMoney) {
		this.realityMoney = realityMoney;
	}
	
}

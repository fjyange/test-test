/**
 * @Package: com.sozone.fs.export 
 * @author: Administrator   
 * @date: 2019年2月26日 下午10:36:47
 */
package com.sozone.fs.export;

import com.sozone.aeolus.util.StringUtils;

/**
 * @Description: 招标人导出保函类
 * @author 严格
 * @date: 2019年2月26日 下午10:36:47 
 */
public class ProjectLefferBean {
	
	
	/**
     * 序号
     */
    private String rowNum;
    
    private String vno;
    
    private String bidderName;
    
    private String financialName;
    
    private String nupdateName;

    

	public ProjectLefferBean() {
		super();
	}

	public ProjectLefferBean(String rowNum, String vno, String bidderName, String financialName, String nupdateName) {
		super();
		this.rowNum = rowNum;
		this.vno = StringUtils.isNotEmpty(vno)?vno:"";
		this.bidderName = StringUtils.isNotEmpty(bidderName)?bidderName:"";
		this.financialName = StringUtils.isNotEmpty(financialName)?financialName:"";
		this.nupdateName = StringUtils.isNotEmpty(nupdateName)?nupdateName:"";
	}

	public String getRowNum() {
		return rowNum;
	}

	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}

	public String getVno() {
		return vno;
	}

	public void setVno(String vno) {
		this.vno = vno;
	}

	public String getBidderName() {
		return bidderName;
	}

	public void setBidderName(String bidderName) {
		this.bidderName = bidderName;
	}

	public String getFinancialName() {
		return financialName;
	}

	public void setFinancialName(String financialName) {
		this.financialName = financialName;
	}

	public String getNupdateName() {
		return nupdateName;
	}

	public void setNupdateName(String nupdateName) {
		this.nupdateName = nupdateName;
	}

}

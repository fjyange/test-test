/**
 * @Package: com.sozone.fs.export 
 * @author: Administrator   
 * @date: 2019年1月9日 下午4:42:44
 */
package com.sozone.fs.export;

/**
 * @Description: 保函导出类
 * @author 严格
 * @date: 2019年1月9日 下午4:42:44 
 */
public class LefferBean {

	/**
     * 序号
     */
    private String rowNum;
    
    private String vno;
    
    private String createTime;
    
    private String tenderName;
    
    private String sectionName;
    
    private String lefferStatus;
    
    private String bidderName;
    
    private String signStatus;
    
    private String auditStatus;

    
    
	public LefferBean() {
		super();
	}


	public LefferBean(String rowNum, String vno, String createTime, String tenderName, String sectionName,
			String lefferStatus, String bidderName, String signStatus, String auditStatus) {
		super();
		this.rowNum = rowNum;
		this.vno = vno;
		this.createTime = createTime;
		this.tenderName = tenderName;
		this.sectionName = sectionName;
		this.lefferStatus = lefferStatus;
		this.bidderName = bidderName;
		this.signStatus = signStatus;
		this.auditStatus = auditStatus;
	}


	public String getRowNum() {
		return rowNum;
	}

	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}


	public String getCreateTime() {
		return createTime;
	}

	public String getVno() {
		return vno;
	}


	public void setVno(String vno) {
		this.vno = vno;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getTenderName() {
		return tenderName;
	}

	public void setTenderName(String tenderName) {
		this.tenderName = tenderName;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getLefferStatus() {
		return lefferStatus;
	}

	public void setLefferStatus(String lefferStatus) {
		this.lefferStatus = lefferStatus;
	}

	public String getBidderName() {
		return bidderName;
	}

	public void setBidderName(String bidderName) {
		this.bidderName = bidderName;
	}

	public String getSignStatus() {
		return signStatus;
	}

	public void setSignStatus(String signStatus) {
		this.signStatus = signStatus;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
    
	
}

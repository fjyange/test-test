/**创建所需表**/
DROP TABLE IF EXISTS  T_USER_EXT_INFO;
DROP TABLE IF EXISTS  T_CA_USER_INFO;
DROP TABLE IF EXISTS  T_CERT_INFO;

CREATE TABLE T_USER_EXT_INFO(
   ID                   VARCHAR(32) NOT NULL COMMENT 'ID',
   V_EMAIL              VARCHAR(320)         COMMENT 'Email',
   V_PHONE              VARCHAR(20)          COMMENT '联系电话',
   N_ALLOW_AP_AUTH      TINYINT		NOT NULL COMMENT '是否允许账号密码登录,1是,0不是',
   V_CERT_ID            VARCHAR(32)          COMMENT '关联的证书',
   V_JSON_OBJ           LONGTEXT             COMMENT '其他信息',
   V_CREATE_USER        VARCHAR(32)          COMMENT '创建人',
   N_CREATE_TIME        BIGINT               COMMENT '创建时间',
   V_UPDATE_USER        VARCHAR(32)          COMMENT '修改人',
   N_UPDATE_TIME        BIGINT               COMMENT '修改时间',
   PRIMARY KEY (ID)
);
ALTER TABLE T_USER_EXT_INFO COMMENT '用户扩展信息';

/*==============================================================*/
/* TABLE: T_CA_USER_INFO                                        */
/*==============================================================*/
CREATE TABLE T_CA_USER_INFO
(
   ID                   VARCHAR(32) NOT NULL COMMENT 'ID',
   V_SYS_ID             VARCHAR(100) COMMENT '开放式授权返回的ID',
   V_NAME               VARCHAR(300) COMMENT '用户名称',
   V_USER_TYPE          VARCHAR(6) COMMENT '用户类型1企业2个人',
   V_SOCIALCREDIT_NO    VARCHAR(50) COMMENT '统一社会信用编码',
   V_REG_NO             VARCHAR(50) COMMENT '工商注册号',
   V_LEGAL_NO           VARCHAR(50) COMMENT '单位法人证书事证号',
   V_COMPANY_CODE       VARCHAR(50) COMMENT '组织机构代码',
   V_TAX_NO             VARCHAR(50) COMMENT '税务登记号',
   V_RENT_NO            VARCHAR(50) COMMENT '地税电脑编码',
   V_SOCIAL_NO          VARCHAR(50) COMMENT '社保代码号',
   V_OTHER_NO           VARCHAR(50) COMMENT '其他证件号码',
   V_LEGAL_NAME         VARCHAR(300) COMMENT '法定代表姓名',
   V_ID_NO              VARCHAR(50) COMMENT '企业的是经办身份证,个人为个人的身份证',
   V_UNI_TTEL           VARCHAR(50) COMMENT '单位电话',
   V_REG_ADDRESS        VARCHAR(300) COMMENT '登记住所',
   N_CREATE_TIME        BIGINT COMMENT '插入时间',
   N_UPDATE_TIME        BIGINT COMMENT '修改时间',
   PRIMARY KEY (ID)
);

ALTER TABLE T_CA_USER_INFO COMMENT 'CA用户信息表';

/*==============================================================*/
/* TABLE: T_CERT_INFO                                           */
/*==============================================================*/
CREATE TABLE T_CERT_INFO
(
   ID                   VARCHAR(32) NOT NULL COMMENT '关联用户基本信息表(T_SYS_USER_BASE)的USER_ID字段',
   V_CA_ID              VARCHAR(100) COMMENT '开放式授权返回的主键',
   V_SERIAL             VARCHAR(50) COMMENT '证书序列号',
   N_TYPE               INT COMMENT '证书类型,1软证,0介质证',
   V_START_TIME         VARCHAR(50) COMMENT '有效开始时间',
   V_END_TIME           VARCHAR(50) COMMENT '有效结束时间',
   V_CERT_BASE64        MEDIUMTEXT COMMENT '证书BASE64字符串',
   V_LOGIN_NAME         VARCHAR(200) COMMENT '登录名',
   V_KEY_NAME           VARCHAR(200) COMMENT 'KEY名',
   V_CA_USER_ID         VARCHAR(32) COMMENT '关联的CA用户ID(即T_CA_USER_INFO表的ID)',
   N_CREATE_TIME        BIGINT COMMENT '插入时间',
   N_UPDATE_TIME        BIGINT COMMENT '修改时间',
   PRIMARY KEY (ID)
);

ALTER TABLE T_CERT_INFO COMMENT 'CA证书信息表';

/*==============================================================*/
/* VIEW: V_CERT_USER_INFO                                       */
/*==============================================================*/
CREATE OR REPLACE VIEW V_CERT_USER_INFO
    AS
    SELECT CERT.ID AS V_CERT_ID,
           CERT.V_CA_ID,
           CERT.V_SERIAL,
           CERT.N_TYPE,
           CERT.V_START_TIME,
           CERT.V_END_TIME,
           CERT.V_CERT_BASE64,
           CERT.V_LOGIN_NAME,
           CERT.V_KEY_NAME,
           CERT.V_CA_USER_ID,
           CERT.N_CREATE_TIME AS N_CERT_CT,
           CERT.N_UPDATE_TIME AS N_CERT_UT,
           CA.V_SYS_ID,
           CA.V_NAME,
           CA.V_USER_TYPE,
           CA.V_SOCIALCREDIT_NO,
           CA.V_REG_NO,
           CA.V_LEGAL_NO,
           CA.V_COMPANY_CODE,
           CA.V_TAX_NO,
           CA.V_RENT_NO,
           CA.V_SOCIAL_NO,
           CA.V_OTHER_NO,
           CA.V_LEGAL_NAME,
           CA.V_ID_NO,
           CA.V_UNI_TTEL,
           CA.V_REG_ADDRESS,
           CA.N_CREATE_TIME AS N_CA_CT,
           CA.N_UPDATE_TIME AS N_CA_UT
      FROM T_CERT_INFO CERT
      LEFT JOIN T_CA_USER_INFO CA ON (CERT.V_CA_USER_ID = CA.ID);


/*==============================================================*/
/* VIEW: V_USER_INFO                                            */
/*==============================================================*/
CREATE OR REPLACE VIEW V_USER_INFO
    AS
    SELECT USER_ID,
           USER_ACCOUNT,
           USER_NAME,
           PASSWORD,
           IS_ADMIN,
           IS_LOCK,
           V_EMAIL,
           V_PHONE,
           N_ALLOW_AP_AUTH,
           V_CERT_ID,
           V_JSON_OBJ,
           V_CREATE_USER,
           N_CREATE_TIME,
           V_UPDATE_USER,
           N_UPDATE_TIME
      FROM T_SYS_USER_BASE UB
      LEFT JOIN T_USER_EXT_INFO UE ON (UB.USER_ID = UE.ID);
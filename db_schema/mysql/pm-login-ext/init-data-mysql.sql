/**初始化资源**/
INSERT INTO T_SYS_RESOURCE_INFO (RESOURCE_ID, RESOURCE_NAME, RESOURCE_TYPE, RESOURCE_CODE, RESOURCE_URI, HTTP_METHOD, IS_AVAILABLE, RESOURCE_MEMO) VALUES ('9527d5ec4e634bb992f6d961c3c825d7', '运维登录页面', 0, NULL, '/view/pmlogin/**', 'SERVICE', 1, '必须任何用户都可以访问');


--DELETE FROM T_SYS_PARAM WHERE param_key LIKE '%aeolus.oauth2.%';
INSERT INTO T_SYS_PARAM (PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_RULE, PARAM_TIP, PARAM_DEMO)
VALUES ('456aeb7123454df989460ed21234c6f5', 'aeolus.oauth2.as.auth.url', 'http://ra-cloud.okap.com/service/oauth2/auth?client_id=${aeolus.oauth2.client.id}&response_type=code&redirect_uri=${aeolus.oauth2.client.redirect.uri}&scope=all&state=${user_type}&auth_page_type=0', '.+', '开放式授权服务器端登录地址,不允许为空!', '开放式授权服务器端登录地址');
INSERT INTO T_SYS_PARAM (PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_RULE, PARAM_TIP, PARAM_DEMO)
VALUES ('963aeb7123454df989460ed21234c6f5', 'aeolus.oauth2.as.token.url', 'http://ra-cloud.okap.com/service/oauth2/token', '.+', '开放式授权服务器端ACCESS TOKEN换取路径,不允许为空!', '开放式授权服务器端ACCESS TOKEN换取路径');
INSERT INTO T_SYS_PARAM (PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_RULE, PARAM_TIP, PARAM_DEMO)
VALUES ('8931234540e64df989460ed21234c6f5', 'aeolus.oauth2.client.redirect.uri', 'http://ebid-online-kb.okap.com/authorize/o2c/gtk', '.+', '回调地址,不允许为空', '开放式授权客户端回调地址 ');
INSERT INTO T_SYS_PARAM (PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_RULE, PARAM_TIP, PARAM_DEMO)
VALUES ('123aeb7b47894df989460ed21234c6f5', 'aeolus.oauth2.client.id', '6f0f54ae791445bdb3a3ede0722a1f0d', '\\w+', '客户端ID不允许为空', '开放式授权客户端ID');
INSERT INTO T_SYS_PARAM (PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_RULE, PARAM_TIP, PARAM_DEMO)
VALUES ('9631eb7b40e64df989460ed21234c123', 'aeolus.oauth2.client.secret', '361fad663ef8aced1129c0d22ee16739f6a09c5d1f6dbdc2b1fea1c6a7f87760550a565c7a71848ef8aeb5f53a74dba4', '\\w+', '客户端密钥不允许为空', '开放式授权客户端密钥');
INSERT INTO T_SYS_PARAM (PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_RULE, PARAM_TIP, PARAM_DEMO)
VALUES ('qwerty7412e64df989460ed21234c111', 'aeolus.oauth2.as.current.user.info.url', 'http://ra-cloud.okap.com/service/o2api/cert/userinfo', '.+', '开放式授权服务器端获取用户基本信息请求路径不允许为空!', '开放式授权服务器端获取用户基本信息请求路径');
INSERT INTO T_SYS_PARAM (PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_RULE, PARAM_TIP, PARAM_DEMO)
VALUES ('yiuidd7412e64df989460ed212348989', 'aeolus.oauth2.as.current.cert.info.url', 'http://ra-cloud.okap.com/service/o2api/cert/current', '.+', '开放式授权服务器端获取证书基本信息请求路径不允许为空!', '开放式授权服务器端获取证书基本信息请求路径');
INSERT INTO T_SYS_PARAM (PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_RULE, PARAM_TIP, PARAM_DEMO)
VALUES ('234c6f574254fdftwjcdded21234121d', 'aeolus.oauth2.login.success.url', 'authorize/view/manage/index.html', '.+', '开放式授权登录成功后要跳转到的主页,不允许为空!', '开放式授权登录成功后要跳转到的主页路径');


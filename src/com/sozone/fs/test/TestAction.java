package com.sozone.fs.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sozone.aeolus.annotation.Path;
import com.sozone.aeolus.annotation.Service;
import com.sozone.aeolus.authorize.annotation.Level;
import com.sozone.aeolus.authorize.annotation.Permission;
import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.aeolus.data.AeolusData;
import com.sozone.aeolus.exception.FacadeException;

@Path(value = "/test", desc = "第三方接入")
@Permission(Level.Guest)
public class TestAction {


	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(TestAction.class);
	@Path(value="/test")
	@Service
	public String test(AeolusData aeolusData) throws FacadeException{
		logger.error(LogUtils.format("请求参数", aeolusData));
		return "success";
	}

}

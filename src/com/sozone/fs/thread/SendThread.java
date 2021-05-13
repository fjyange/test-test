package com.sozone.fs.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sozone.aeolus.authorize.utlis.LogUtils;
import com.sozone.fs.order.OrderAction;

public class SendThread implements Runnable
{
	private static Logger logger = LoggerFactory.getLogger(SendThread.class);
	String id = "";
	String appid = "";

	/**
	 * @param id
	 */
	public SendThread(String id, String appid)
	{
		super();
		this.id = id;
		this.appid = appid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			Thread.sleep(2000);
			OrderAction orderAction = new OrderAction();
			orderAction.sendOrder(this.id, this.appid);
		}
		catch (Exception e)
		{
			logger.error(LogUtils.format("发送线程失败", e.getMessage()),e);
		}
	}

}

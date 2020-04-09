package com.sozone.aeolus.servlet.listener;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sozone.aeolus.config.AeolusConfig;
import com.sozone.aeolus.config.AeolusConfigBuilder;
import com.sozone.aeolus.context.AeolusContextImpl;
import com.sozone.aeolus.ext.dao.MybatisEnvironmentEntry;
import com.sozone.aeolus.plugin.AeolusPlugIn;
import com.sozone.aeolus.util.Assert;
import com.sozone.aeolus.util.LogFormatUtils;
import com.sozone.aeolus.util.ResourceUtils;
import com.sozone.aeolus.web.util.WebUtils;

public class AeolusConfigListener implements ServletContextListener
{
	private static Logger logger = LoggerFactory.getLogger(AeolusConfigListener.class);
	private static String rootPathTempKey = "aeolus.root.path."
			+ UUID.randomUUID().toString().toLowerCase().replaceAll("-", ".");

	public static String getRootPathKey()
	{
		return rootPathTempKey;
	}

	protected void setWebAppRootSystemProperty(ServletContext servletContext)
	{
		String root = servletContext.getRealPath("/");
		if (root == null)
		{
			throw new IllegalStateException("无法获取到项目根路径");
		}
		System.setProperty(rootPathTempKey, root);
	}

	public void contextDestroyed(ServletContextEvent event)
	{
		System.getProperties().remove(rootPathTempKey);
		logger.debug(LogFormatUtils.formatFrameMessage(
				"**********************************开始销毁插件**********************************", new Object[0]));
		Collection<AeolusPlugIn> plugins = AeolusContextImpl.getDefaultAeolusContext().getPlugIns().values();
		if ((plugins != null) && (!plugins.isEmpty()))
		{
			for (AeolusPlugIn plugin : plugins)
			{
				plugin.destroy();
			}
		}
		logger.debug(LogFormatUtils.formatFrameMessage(
				"**********************************插件销毁结束**********************************", new Object[0]));
	}

	public void contextInitialized(ServletContextEvent event)
	{
		long start = System.nanoTime();
		ServletContext servletContext = event.getServletContext();
		try
		{
			setWebAppRootSystemProperty(servletContext);

			logger.debug(LogFormatUtils.formatFrameMessage("开始准备解析配置文件!", new Object[0]));

			servletContext.setAttribute("serverPort", servletContext.getInitParameter("serverPort"));

			String location = servletContext.getInitParameter("aeolusConfigLocation");
			Assert.notNull(location, "请配置正确的aeolusConfigLocation参数,正确指定配置文件的路径!");
			if (org.apache.commons.lang.StringUtils.startsWithAny(location, new String[]
			{ "/WEB-INF/", "WEB-INF/" }))
			{
				location = WebUtils.getRealPath(servletContext, location);
			}
			logger.info(LogFormatUtils.formatFrameMessage("从[" + location + "]路径中加载配置文件!", new Object[0]));
			URL url = ResourceUtils.getURL(location);
			AeolusConfigBuilder builder = new AeolusConfigBuilder(url);

			AeolusConfig aeolusConfig = builder.buildAeolusConfig();

			AeolusContextImpl.init(aeolusConfig);

			String xml = builder.buildMybatisConfig();
			if (com.sozone.aeolus.util.StringUtils.isEmpty(xml))
			{
				logger.error(
						LogFormatUtils.formatFrameMessage("没有指定Mybatis配置信息,框架将无法使用Mybatis进行持久化化操作!", new Object[0]));
			} else
			{
				logger.info(LogFormatUtils.formatFrameMessage("开始初始化Mybatis工作环境", new Object[0]));
				logger.debug(LogFormatUtils.formatFrameMessage("MyBatis配置文件内容", new Object[]
				{ xml }));

				MybatisEnvironmentEntry.startMybatis(xml, aeolusConfig);
			}
			long end = System.nanoTime();
			logger.info(LogFormatUtils.formatFrameMessage("**********************************框架初始化结束,共耗时("
					+ (end - start) + ")ns**********************************", new Object[0]));

			initPlugins(servletContext, aeolusConfig);
		} catch (Throwable e)
		{
			logger.error(LogFormatUtils.formatFrameMessage("初始化框架上下文环境发生异常!", new Object[0]), e);
			throw new RuntimeException(e);
		}
	}

	private void initPlugins(ServletContext servletContext, AeolusConfig aeolusConfig) throws Exception
	{
		if (aeolusConfig.getPlugins().isEmpty())
		{
			return;
		}
		logger.info(LogFormatUtils.formatFrameMessage(
				"**********************************开始初始化插件**********************************", new Object[0]));
		AeolusContextImpl aci = (AeolusContextImpl) AeolusContextImpl.getDefaultAeolusContext();
		Class<? extends AeolusPlugIn> plugin = null;
		AeolusPlugIn pinstance = null;

		Iterator localIterator = aeolusConfig.getPlugins().entrySet().iterator();
		while (localIterator.hasNext())
		{
			Map.Entry<String, Class<? extends AeolusPlugIn>> entry = (Map.Entry) localIterator.next();

			plugin = (Class) entry.getValue();
			pinstance = (AeolusPlugIn) plugin.newInstance();
			aci.addPlugIn((String) entry.getKey(), pinstance);
			pinstance.init(servletContext, aci);
		}
		logger.info(LogFormatUtils.formatFrameMessage(
				"**********************************插件初始化结束**********************************", new Object[0]));
	}

	private void exit()
	{
		long exitTime = 86400000L;
		Timer timer = new Timer();

		timer.schedule(new TimerTask()
		{
			public void run()
			{
				System.exit(1);
			}
		}, exitTime);
	}
}

/**
 * 包名：com.sozone.bhrzgz.common.utils
 * 文件名：UtilsSign.java<br>
 * 创建时间：2018年9月10日 下午5:27:03<br>
 * 创建者：zhenglin<br>
 * 修改者：暂无<br>
 * 修改简述：暂无<br>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br>
 */
package com.sozone.fs.common.util;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

/**
 * 接口数据签名工具类
 * Time：2018年9月10日 下午5:27:03<br>
 * @author zhenglin
 * @version 1.0.0
 * @since JDK1.6
 */
public class UtilSign
{
	/**
	 * 按照key=value的格式，并按照参数名ASCII字典序排序
	 * 
	 * @param map
	 *            参数
	 * @return 拼接后字符串 key=value&key=value
	 */
	public static String splicingStr(Map<String, ?> map)
	{
		TreeMap<String, Object> treeMap = new TreeMap<String, Object>(map);
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : treeMap.entrySet())
		{
			String value=(String)entry.getValue();
			if (StringUtils.isNotEmpty(value) && !StringUtils.equals("SIGN", entry.getKey()))
			{
				sb.append(entry.getKey()).append("=").append(entry.getValue())
						.append("&");
			}
		}
		if (sb.length() > 0)
		{
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}
}

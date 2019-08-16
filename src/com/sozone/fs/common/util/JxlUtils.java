/**
 * 包名：com.sozone.fs.common.util
 * 文件名：JxlUtils.java<br/>
 * 创建时间：2019-1-22 下午7:58:20<br/>
 * 创建者：huangbh<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package com.sozone.fs.common.util;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * TODO 一句话描述类的主要作用<br/>
 * <p>
 * TODO 该类的详细描述<br/>
 * </p>
 * Time：2019-1-22 下午7:58:20<br/>
 * @author huangbh
 * @version 1.0.0
 * @since 1.0.0
 */
public class JxlUtils
{

	/**
	 * 
	 * xls文件转Str<br/>
	 * <p>
	 * xls文件转Str
	 * </p>
	 * 
	 * @param xlsFile
	 * @return
	 */
	public static String xlsToStr(File xlsFile)
	{
		StringBuilder resbBuilder = new StringBuilder();
		try
		{
			Workbook workbook = Workbook.getWorkbook(xlsFile);
			Sheet sheet = workbook.getSheet(0);
			// File file = new File("d:/1.txt");
			// FileWriter fw = new FileWriter(file);
			// BufferedWriter bw = new BufferedWriter(fw);
			// j为行数，getCell("列号","行号")

			int j = sheet.getRows();
			int y = sheet.getColumns();
			for (int i = 0; i < j; i++)
			{
				for (int x = 0; x < y; x++)
				{

					Cell c = sheet.getCell(x, i);
					String s = c.getContents();

					resbBuilder.append(s);
					resbBuilder.append(" ");
					// bw.write(s);
					// bw.write(" ");
					// bw.flush();
				}
				// bw.newLine();
				// bw.flush();
			}
		}
		catch (BiffException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return resbBuilder.toString();
	}
}

package com.sozone.fs.common.util;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class JxlUtils
{

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

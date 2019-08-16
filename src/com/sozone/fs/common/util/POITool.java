/**
 * 包名：com.jfaker.framework.utils
 * 文件名：POITool.java<br/>
 * 创建时间：2017年1月5日 下午10:46:52<br/>
 * 创建者：don<br/>
 * 修改者：暂无<br/>
 * 修改简述：暂无<br/>
 * 修改详述：
 * <p>
 * 暂无<br/>
 * </p>
 * 修改时间：暂无<br/>
 */
package com.sozone.fs.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;

/**
 * doc转html<br/>
 * <p>
 * doc转html<br/>
 * </p>
 * Time：2017年1月6日 上午09:46:52<br/>
 * 
 * @author don
 * @version 1.0.0
 * @since 1.0.0
 */
public class POITool
{
	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory.getLogger(POITool.class);

	/**
	 * doc文件转换为html文件<br/>
	 * <p>
	 * doc文件转换为html文件
	 * </p>
	 * 
	 * @param istream
	 *            输入流
	 * @return 字符串
	 * @throws Exception
	 *             异常
	 */
	public static String docToHtml(InputStream istream) throws Exception
	{
		ByteArrayOutputStream ostream = null;
		try
		{
			ostream = new ByteArrayOutputStream();
			HWPFDocument wordDocument = new HWPFDocument(istream);
			WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
					DocumentBuilderFactory.newInstance().newDocumentBuilder()
							.newDocument());
			wordToHtmlConverter.processDocument(wordDocument);
			Document htmlDocument = wordToHtmlConverter.getDocument();
			DOMSource domSource = new DOMSource(htmlDocument);
			StreamResult streamResult = new StreamResult(ostream);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING,
					System.getProperty("file.encoding"));
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.METHOD, "html");
			serializer.transform(domSource, streamResult);
			return ostream.toString();
		}
		catch (Exception e)
		{
			logger.error("doc文件转换为html文件", e);
			throw e;
		}
		finally
		{
			IOUtils.closeQuietly(istream);
			IOUtils.closeQuietly(ostream);
		}
	}

	/**
	 * docx文件转换为html文件<br/>
	 * <p>
	 * docx文件转换为html文件
	 * </p>
	 * 
	 * @param istream
	 *            输入流
	 * @return 字符串
	 * @throws Exception
	 *             异常
	 */
	public static String docxToHtml(InputStream istream) throws Exception
	{
		OutputStreamWriter outputStreamWriter = null;
		ByteArrayOutputStream ostream = null;
		try
		{
			ostream = new ByteArrayOutputStream();
			XWPFDocument document = new XWPFDocument(istream);
			XHTMLOptions options = XHTMLOptions.create();
			outputStreamWriter = new OutputStreamWriter(ostream,
					System.getProperty("file.encoding"));
			XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter
					.getInstance();
			xhtmlConverter.convert(document, outputStreamWriter, options);
			return ostream.toString();
		}
		catch (Exception e)
		{
			logger.error("docx文件转换为html文件", e);
			throw e;
		}
		finally
		{
			IOUtils.closeQuietly(istream);
			IOUtils.closeQuietly(ostream);
		}
	}

	/**
	 * docx文件转换为txt文件<br/>
	 * <p>
	 * docx文件转换为txt文件
	 * </p>
	 * 
	 * @param filepath
	 *            文件路径
	 * @return 字符串
	 * @throws Exception
	 *             异常
	 */
	public static String docxToTxt(String filepath) throws Exception
	{
		POIXMLTextExtractor ex = null;
		try
		{
			ex = new XWPFWordExtractor(new XWPFDocument(
					POIXMLDocument.openPackage(filepath)));
			return ex.getText();
		}
		catch (Exception e)
		{
			logger.error("docx文件转换为txt文件", e);
			throw e;
		}
		finally
		{
			if (ex != null)
			{
				// ex.close();
			}
		}
	}

	public static String docToStr(String path)
	{
		String buffer = "";
		try
		{
			if (path.endsWith(".doc"))
			{
				InputStream is = new FileInputStream(new File(path));
				WordExtractor ex = new WordExtractor(is);
				buffer = ex.getText();
				// ex.close();
			}
			else if (path.endsWith("docx"))
			{
				OPCPackage opcPackage = POIXMLDocument.openPackage(path);
				POIXMLTextExtractor extractor = new XWPFWordExtractor(
						opcPackage);
				buffer = extractor.getText();
				// extractor.close();
			}
			else
			{
				System.out.println("此文件不是word文件！");
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return buffer;
	}

	public static String xlsxToStr(File xlsxFile) throws IOException
	{
		StringBuilder resbuilder = new StringBuilder();
		try
		{
			InputStream is = new FileInputStream(xlsxFile); // 读取指定目录下的文件
			XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is); // 创建一个工作薄
			for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++)
			{ // 循环取Sheet
				XSSFSheet shset = hssfWorkbook.getSheetAt(numSheet);
				if (shset == null)
				{
					continue;
				}
				for (int rowNum = 0; rowNum <= shset.getLastRowNum(); rowNum++)
				{ // 循环一个工作薄里面的所有行
					XSSFRow hssfRow = shset.getRow(rowNum);
					if (hssfRow == null)
					{
						continue;
					}
					int hssFCell = hssfRow.getPhysicalNumberOfCells(); // 取一个行里面的所有列
					for (int cellNum = 0; cellNum < hssFCell; cellNum++)
					{ // 循环取列
						XSSFCell hsc = hssfRow.getCell(cellNum);
						if (hsc == null)
						{
							continue;
						}
						resbuilder.append(getValue(hsc) + " ");
						// System.out.print(getValue(hsc) + " "); // 取值
					}
					// System.out.println("\n");
					resbuilder.append("\n");
				}
				// hssfWorkbook.close();
				is.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return resbuilder.toString();
	}

	@SuppressWarnings("static-access")
	private static String getValue(XSSFCell hssfCell)
	{
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN)
		{
			// 返回布尔类型的值
			return String.valueOf(hssfCell.getBooleanCellValue());
		}
		else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC)
		{

			hssfCell.setCellType(hssfCell.CELL_TYPE_STRING);
			// 返回数值类型的值
			return hssfCell.getStringCellValue();
		}
		else
		{
			// 返回字符串类型的值
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}
	
	/**
	 * 
	 * 替换doc文件中的变量<br/>
	 * <p>
	 * TODO 函数的详细描述
	 * </p>
	 * @param sourcePath
	 * @param targetPath
	 * @param map
	 */
	public static void docReplaceWithPOI(String sourcePath, String targetPath,
			Map<String, String> map)
	{
		HWPFDocument doc = null;
		try
		{
			InputStream inp = new FileInputStream(sourcePath);
			POIFSFileSystem fs = new POIFSFileSystem(inp);
			doc = new HWPFDocument(fs);

			Range range = doc.getRange();
			for (Map.Entry<String, String> entry : map.entrySet())
			{
				range.replaceText(entry.getKey(), entry.getValue());
			}
			inp.close();
			OutputStream os = new FileOutputStream(targetPath);
			doc.write(os);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean replaceStringInExcel(String sourceFile,String targetFile,
			Map<String, String> item)
	{
		boolean isSuccess = true;
		Workbook wb = null;
		FileOutputStream fileOut=null;
		try
		{
			if (getFileType(sourceFile).equals("xls"))
			{
				System.out.print("xls");
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(
						sourceFile));
				wb = new HSSFWorkbook(fs);
				processHSSFWorkbook(wb, item);
			}
			else if (getFileType(sourceFile).equals("xlsx"))
			{
				//System.out.print("xlsx ");
				FileInputStream input = new FileInputStream(new File(sourceFile));
				wb = new XSSFWorkbook(OPCPackage.open(input));
				processXSSFWorkbook(wb, item);
			}
			// 输出文件
			fileOut = new FileOutputStream(targetFile);
			wb.write(fileOut);
		}
		catch (Exception e)
		{
			isSuccess = false;
			e.printStackTrace();
		}
		finally
		{
			IOUtils.closeQuietly(fileOut);
			// wb.close();
			//System.out.println("replace success");
		}
		return isSuccess;
	}

	private static String getFileType(String fileName)
	{
		String[] strArray = fileName.split("\\.");
		int suffixIndex = strArray.length - 1;
		return strArray[suffixIndex];
	}

	/**** 处理Excel2003 ****/
	private static void processHSSFWorkbook(Workbook wb, Map<String, String> item)
	{
		HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);
		Iterator<?> rows = sheet.rowIterator();
		while (rows.hasNext())
		{
			HSSFRow row = (HSSFRow) rows.next();

			if (row != null)
			{
				int num = row.getLastCellNum();
				for (int i = 0; i < num; i++)
				{
					HSSFCell cell = row.getCell(i);
					if (cell != null)
					{
						cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					}
					if (cell == null || cell.getStringCellValue() == null)
					{
						continue;
					}
					String value = cell.getStringCellValue();
					if (!"".equals(value))
					{
						Set<String> keySet = item.keySet();
						Iterator<String> it = keySet.iterator();
						while (it.hasNext())
						{
							String text = it.next();
							if (value.equals(text))
							{
								cell.setCellValue((String) item.get(text));

								break;
							}

						}
					}
					else
					{
						cell.setCellValue("");
					}

				}
			}
		}
	}

	/**** 处理Excel2007+ ****/
	private static void processXSSFWorkbook(Workbook wb,
			Map<String, String> item)
	{
		XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(0);
		Iterator<?> rows = sheet.rowIterator();
		while (rows.hasNext())
		{
			XSSFRow row = (XSSFRow) rows.next();

			if (row != null)
			{
				int num = row.getLastCellNum();
				for (int i = 0; i < num; i++)
				{
					XSSFCell cell = row.getCell(i);
					if (cell != null)
					{
						cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					}
					if (cell == null || cell.getStringCellValue() == null)
					{
						continue;
					}
					String value = cell.getStringCellValue();
					if (!"".equals(value))
					{
						Set<String> keySet = item.keySet();
						Iterator<String> it = keySet.iterator();
						while (it.hasNext())
						{
							String text = it.next();
							if (value.equals(text))
							{
								cell.setCellValue((String) item.get(text));

								break;
							}

						}
					}
					else
					{
						cell.setCellValue("");
					}

				}
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException,
			Exception
	{
		String sourcePath = "D:/Java/workspace/jrpt-dt/WebContent/doc/投标保证险投保单-长安.xlsx";
		String targetPath = "D:\\test.xlsx";
		String outFilePath = "D:/Java/workspace/jrpt-dt/WebContent/doc/投标保证险投保单-长安.pdf";
		Map<String, String> map = new HashMap<String, String>();
		map.put("#{投保人名称}#", "张三");
		map.put("#{组长机构代码}#", "20");
		replaceStringInExcel(sourcePath, targetPath, map);
		JacobUtils.excel2Pdf(targetPath, outFilePath);
		
		String sourcePath2 = "D:/Java/workspace/jrpt-dt/WebContent/doc/建设工程施工合同履约保证保险投保单-紫金.doc";
		String targetPath2 = "D:\\test.doc";
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("#{投保人名称}#", "张三");
		map2.put("#{投保人证件号码}#", "20");
		//docReplaceWithPOI(sourcePath2, targetPath2, map2);
		
		

		//String content = docToStr("D:/Java/workspace/jrpt-dt/WebContent/doc/建设工程施工合同履约保证保险投保单-紫金.doc");
		//System.out.println("content====" + content);

		// String path =
		// "D:/Java/workspace/jrpt-dt/WebContent/doc/投标保证险投保单-长安.xlsx";
		// inputExcel(new File(path));
		// // TODO Auto-generated method stub String filepath =
		// // "D:/Java/workspace/jrpt-dt/WebContent/doc\\投标保证险投保单-阳光.xls";
		// String f =
		// "D:\\Java\\workspace\\jrpt-dt\\WebContent\\doc\\建设工程施工合同履约保证保险投保单-紫金.doc";
		// String html = POITool.docxToHtml(new FileInputStream(new File(f)));
		// System.out.println(html);
	}

}

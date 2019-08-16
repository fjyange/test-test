/**
 * 
 */
package com.sozone.fs.common.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.sozone.aeolus.exception.ServiceException;

/**
 * @TODO
 * @author yange 2018年10月9日 下午7:52:03
 */
public class MSWordPoi4 {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static void main(String[] args) throws IOException, ServiceException {
		// TODO Auto-generated method stub
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("TENDER_NAME", "湖南大学");
		String filePath = "E:\\bank.docx";
		FileInputStream is = new FileInputStream(filePath);
		XWPFDocument doc = new XWPFDocument(is);
		// 替换段落里面的变量
		replaceInPara(doc, params);
		// 替换表格里面的变量
		replaceInTable(doc, params);
		FileInputStream is2 = new FileInputStream(filePath);
		setStyle(new XWPFDocument(is2), doc, params);
		OutputStream os = new FileOutputStream("C:\\Users\\Administrator.PC-20180906PRTJ\\Desktop\\q.docx");
		doc.write(os);
		// 排队转换pdf
		close(os);
		close(is);
		close(is2);
	}

	public static void createFile(String sourceFile, String targetFile, Map<String, Object> paramsMap)
			throws IOException {
		FileInputStream is = new FileInputStream(sourceFile);
		XWPFDocument doc = new XWPFDocument(is);
		// 替换段落里面的变量
		replaceInPara(doc, paramsMap);
		// 替换表格里面的变量
		replaceInTable(doc, paramsMap);
		FileInputStream is2 = new FileInputStream(sourceFile);
		setStyle(new XWPFDocument(is2), doc, paramsMap);
		OutputStream os = new FileOutputStream(targetFile);
		doc.write(os);
		close(os);
		close(is);
		close(is2);
	}

	public static void setStyle(XWPFDocument doc, XWPFDocument tempdoc, Map<String, Object> params) {
		Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
		Iterator<XWPFParagraph> iterator2 = tempdoc.getParagraphsIterator();
		XWPFParagraph para;
		XWPFParagraph para2;
		while (iterator.hasNext()) {
			para = iterator.next();
			para2 = iterator2.next();
			styleInPara(para, para2, params);
		}
	}

	private static void styleInPara(XWPFParagraph para, XWPFParagraph para2, Map<String, Object> params) {
		List<XWPFRun> runs;
		List<XWPFRun> runs2;
		Matcher matcher;
		if (matcher(para.getParagraphText()).find()) {
			runs = para.getRuns();
			runs2 = para2.getRuns();
			for (int i = 0; i < runs.size(); i++) {
				XWPFRun run = runs.get(i);
				XWPFRun run2 = runs2.get(i);
				String runText = run.toString();
				matcher = matcher(runText);
				if (matcher.find()) {
					// 按模板文件格式设置格式
					run2.getCTR().setRPr(run.getCTR().getRPr());
				}
			}
		}
	}

	/**
	 * 替换段落里面的变量
	 * 
	 * @param doc
	 *            要替换的文档
	 * @param params
	 *            参数
	 */
	public static void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
		Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
		XWPFParagraph para;
		while (iterator.hasNext()) {
			para = iterator.next();
			replaceInPara(para, params);
		}
	}

	/**
	 * 替换段落里面的变量
	 * 
	 * @param para
	 *            要替换的段落
	 * @param params
	 *            参数
	 */
	public static void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
		List<XWPFRun> runs;
		Matcher matcher;
		if (matcher(para.getParagraphText()).find()) {
			runs = para.getRuns();
			for (int i = 0; i < runs.size(); i++) {
				XWPFRun run = runs.get(i);
				String runText = run.toString();
				matcher = matcher(runText);
				if (matcher.find()) {
					while ((matcher = matcher(runText)).find()) {
						runText = matcher.replaceFirst(java.util.regex.Matcher.quoteReplacement(String.valueOf(params.get(matcher.group(1)))));
					}
					// 直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
					// 所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
					para.removeRun(i);
					para.insertNewRun(i).setText(runText);
				}
			}
		}
	}

	// private static void replaceInPara(XWPFParagraph para, Map<String, Object>
	// params) {
	// String paraText = para.getParagraphText();
	// Matcher m = matcher(paraText);
	// if (m.find()) {
	// paraText = m.replaceAll(params.get(m.group(1)));
	// clearParagraph(para);
	// para.createRun().setText(paraText);
	// }
	// }

	/**
	 * 替换表格里面的变量
	 * 
	 * @param doc
	 *            要替换的文档
	 * @param params
	 *            参数
	 */
	public static void replaceInTable(XWPFDocument doc, Map<String, Object> params) {
		Iterator<XWPFTable> iterator = doc.getTablesIterator();
		XWPFTable table;
		List<XWPFTableRow> rows;
		List<XWPFTableCell> cells;
		List<XWPFParagraph> paras;
		Matcher matcher;
		while (iterator.hasNext()) {
			table = iterator.next();
			rows = table.getRows();
			for (XWPFTableRow row : rows) {
				cells = row.getTableCells();
				List<XWPFTableCell> xcells = row.getTableCells();
				for (XWPFTableCell cell : xcells) {
					String runText = cell.getText();
					matcher = matcher(runText);
					if (matcher.find()) {
						while ((matcher = matcher(runText)).find()) {
							runText = matcher.replaceFirst(String.valueOf(params.get(matcher.group(1))));
						}
						// 直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
						// 所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
						// cell.getParagraphs().remove(cell);
						List<XWPFParagraph> list = cell.getParagraphs();

						for (int i = 0; i < list.size(); i++) {
							while (list.get(i).getRuns().size() > 0) {
								list.get(i).removeRun(0);
							}
						}

						cell.setText(runText);
					}
				}
			}
		}
	}

	/**
	 * 正则匹配字符串
	 * 
	 * @param str
	 * @return
	 */
	private static Matcher matcher(String str) {
		Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		return matcher;
	}

	/**
	 * 关闭输入流
	 * 
	 * @param is
	 */
	private static void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭输出流
	 * 
	 * @param os
	 */
	private static void close(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

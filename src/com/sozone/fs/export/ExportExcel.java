package com.sozone.fs.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;


public class ExportExcel<T> {
	
	public InputStream exportExcel(Collection<T> dataset, OutputStream out) {
		return exportExcel("EXCEL文档", null, dataset, out, "yyyy-MM-dd");
	}

	public InputStream exportExcel(String[] headers, Collection<T> dataset,
			OutputStream out) {
		return exportExcel("EXCEL文档", headers, dataset, out, "yyyy-MM-dd");
	}

	public InputStream exportExcel(String[] headers, Collection<T> dataset,
			OutputStream out, String pattern) {
		return exportExcel("EXCEL文档", headers, dataset, out, pattern);
	}

	/**
	 * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
	 * 
	 * @param title
	 *            表格标题名
	 * @param headers
	 *            表格属性列名数组
	 * @param dataset
	 *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
	 *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	 * @param out
	 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @param pattern
	 *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
	 */
	@SuppressWarnings("unchecked")
	public InputStream exportExcel(String title, String[] headers,
			Collection<T> dataset, OutputStream out, String pattern) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 30);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		style.setFont(font);
		// 生成并设置另一个样式
		HSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font2 = workbook.createFont();
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);

		// 声明一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置,详见文档
		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
				0, 0, 0, (short) 4, 2, (short) 6, 5));
		// 设置注释内容
		comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
		comment.setAuthor("huzh");

		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}

		// 遍历集合数据，产生数据行
		Iterator<T> it = dataset.iterator();
		int index = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = (T) it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			Field[] fields = t.getClass().getDeclaredFields();
			for (short i = 0; i < fields.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(style2);
				Field field = fields[i];
				String fieldName = field.getName();
				String getMethodName = "get"
						+ fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
				try {
					Class tCls = t.getClass();
					Method getMethod = tCls.getMethod(getMethodName,
							new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});
					// 判断值的类型后进行强制类型转换
					String textValue = null;

					if (value instanceof Boolean) {
						boolean bValue = (Boolean) value;
						textValue = "男";
						if (!bValue) {
							textValue = "女";
						}
					} else if (value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat(pattern);
						textValue = sdf.format(date);
					} else if (value instanceof byte[]) {
						// 有图片时，设置行高为60px;
						row.setHeightInPoints(60);
						// 设置图片所在列宽度为80px,注意这里单位的一个换算
						sheet.setColumnWidth(i, (short) (35.7 * 80));
						// sheet.autoSizeColumn(i);
						//byte[] bsValue = (byte[]) value;
						//HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
						//		1023, 255, (short) 6, index, (short) 6, index);
						//anchor.setAnchorType(2);
						//patriarch.createPicture(anchor, workbook.addPicture(
						//		bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
					} else {
						// 其它数据类型都当作字符串简单处理
						textValue = String.valueOf(value);
					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null) {
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
						} else {
							HSSFRichTextString richString = new HSSFRichTextString(
									textValue);
							HSSFFont font3 = workbook.createFont();
							font3.setColor(HSSFColor.BLUE.index);
							richString.applyFont(font3);
							cell.setCellValue(richString);
						}
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					// 清理资源
				}
			}

		}
		try {
			//先将数据写入内存
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			workbook.write(baos);  
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());  
			return bais;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  根据文件路径取到文件，并从文件中读取数据修改数据库
	 * @param filePath 文件路径
	 * @return
	 * @throws IOException 
	 */
	/*@SuppressWarnings("unused")
	public List<FinanceBean> readXls(String filePath) throws IOException, InvalidFormatException {
        Boolean flag = null;
        Workbook wb = null;
        try {  
        	InputStream is = new FileInputStream(filePath);
    		flag = flag(filePath);
        	 if(flag){//2003  
                 wb = new HSSFWorkbook(is);  
             }else{//2007  
                 wb = new XSSFWorkbook(is);  
             } 
          } catch (FileNotFoundException e) {  
            e.printStackTrace();  
          } catch (IOException e) {  
            e.printStackTrace();  
          }
        FinanceBean ca = null;
        List<FinanceBean> list = new ArrayList<FinanceBean>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < wb.getNumberOfSheets(); numSheet++) {
        	Sheet sheet = wb.getSheetAt(numSheet);
            if (wb == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                ca = new FinanceBean();
                // 循环列Cell
                // 0:ID, 1:CA类型 ,2:证书名称, 3:证书名称编号 ,4:证书生效时间,5:证书作废时间,6:序列号,7:更新时间
                Cell ID = row.getCell(0);
                if (ID == null) {
                    continue;
                }
                ca.setID(ID.getRichStringCellValue().getString());
                Cell keyName = row.getCell(2);
                if (keyName == null) {
                    continue;
                }
                ca.setKeyName(keyName.getRichStringCellValue().getString());
                Cell searal = row.getCell(6);
                if (searal == null) {
                    continue;
                }
                ca.setSearal(searal.getRichStringCellValue().getString());
                list.add(ca);
            }
        }
        return list;
    }*/
	
	/**
	 * 判断Excel版本是2003或是2007
	 * @param path
	 * @return
	 * @throws Exception
	 */
	static private Boolean flag(String path){
		String prefix;
		File file=new File(path);
		String fileName=file.getName();
		prefix = fileName.substring(fileName.lastIndexOf(".")+1);
		System.out.println(prefix);
	    if("xls".equals(prefix)){
	    	return true;
	    }
	    return false;
	}
	
	//public

	public static void main(String[] args) {
		// 测试证书
//		String[] headers = { "编号", "CA类型", "证书名称","证书名称编号", "证书生效时间","证书作废时间","序列号","更新时间"};
//		List<CaInfoBean> dataset = new ArrayList<CaInfoBean>();
//		dataset.add(new CaInfoBean("1","个人","福建CA","","2012-3-07","2012-3-08","0001","2014-10-20"));
//		dataset.add(new CaInfoBean("2","企业","首众软件","4352","2012-1-23","2012-3-09","0002","2014-10-10"));
//		dataset.add(new CaInfoBean("3","企业","首众CA","4566","2011-4-12","2012-3-07","0003","2014-10-30"));
//		ExportExcel<CaInfoBean> ex = new ExportExcel<CaInfoBean>();
//		try {
//	
//
//			OutputStream out = new FileOutputStream("d://ceshi.xls");
//			ex.exportExcel(headers, dataset, out);
//			out.close();
//			JOptionPane.showMessageDialog(null, "导出成功!");
//			System.out.println("excel导出成功！");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	
	
}

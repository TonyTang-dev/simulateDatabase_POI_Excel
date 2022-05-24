package com.dbLab.dao;

import java.io.*;

import com.dbLab.utils.DBUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


//进行数据库操作的函数
public class sysDao {
	/*创建数据库--新建一个.xls表*/
	public boolean createDatabase(String baseName){
		File file=new File(baseName+".xls");
		if(!file.exists()){
			System.out.println("正在创建数据库");
		}
//		InputStream is=null;
		HSSFWorkbook wb = null;
		try {
			if (!file.exists()) {
//				is = new FileInputStream(file);
				wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet("系统表");
				HSSFRow currow = sheet.createRow(0);
				HSSFCell cell=null;
				/*首行存储行数和字段数*/
				cell=currow.createCell(0);
				cell.setCellValue("数据库名");     //设置cell浮点类型的值
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell=currow.createCell(1);
				cell.setCellValue(baseName);   //设置cell字符类型的值
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				FileOutputStream fileOut = new FileOutputStream(baseName+".xls");//创建文件流
				wb.write(fileOut);//把Workbook对象输出到路径path中
				fileOut.close();
			} else {
				return false;
			}
//			is.close();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	/*选择数据库*/
	public boolean selectDatabase(String baseName){
		globalCmd.curDatabase = baseName;
		return true;
	}

	/*在数据库创建表*/
	public boolean createTable(String tableName, String value[]){
		HSSFWorkbook conn = null;
		try {
			conn = new DBUtils().getWrokbook(globalCmd.curDatabase);
		}catch(Exception e){
			System.out.println("打开数据库出错");
			return false;
		}
		HSSFSheet sheet=conn.createSheet(tableName);

		HSSFRow currow = sheet.createRow(0);
		HSSFCell cell=null;
		/*首行存储行数和字段数*/
		cell=currow.createCell(0);
		cell.setCellValue("当前表行数");     //设置cell浮点类型的值
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell=currow.createCell(1);
		cell.setCellValue("2");   //设置cell字符类型的值
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell=currow.createCell(2);
		cell.setCellValue("当前表字段数");     //设置cell浮点类型的值
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell=currow.createCell(3);
		cell.setCellValue(String.valueOf(value.length));   //设置cell字符类型的值
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);

//		写入字段
		currow = sheet.createRow(1);
		for(int i=0;i<value.length;i++){
			cell=currow.createCell(i);
			cell.setCellValue(value[i]);   //设置cell字符类型的值
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		try {
			FileOutputStream fileOut = new FileOutputStream(globalCmd.curDatabase + ".xls");

			conn.write(fileOut);
			fileOut.close();
		}catch(Exception e){
			System.out.println("修改数据库出错");
		}
		return true;
	}

	//判断端口是否已经被占用
	public boolean isPort(int port) throws IOException
	{

		boolean bok=false;

		HSSFWorkbook conn=new DBUtils().getWrokbook("String baseName");

		try {
			for (int i=0;i<3;i++) {
				HSSFSheet sheet=conn.getSheetAt(i);
				int num=sheet.getLastRowNum();
				if(sheet.getRow(0)==null) {//第一行是空就代表没有数据了，更不能用getCell了
//					return false;
					continue;
				}
				for (int j=0;j<=num;j++) {
					HSSFRow row = sheet.getRow(j);
					if(String.valueOf(port).equals(String.valueOf(row.getCell(4).getStringCellValue()))) {
						return true;
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();

		}
		return bok;
	}

	//登录函数,返回account对象
//	public Account login(Account acc) throws IOException
//	{
//
//		HSSFWorkbook conn=new DBConn().getWrokbook();
//
//		try {
//			boolean flag1=false;
//			int index=-1;
//
//			HSSFSheet sheet=conn.getSheetAt(0);
//			int num=sheet.getLastRowNum();
//			if(sheet.getRow(0)==null) {
//				return null;
//			}
//			for (int j=0;j<=num;j++) {
//				HSSFRow row = sheet.getRow(j);
//			}
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//
//		}
//		return acc;
//	}


	//删除好友
//	public boolean delFriend(Account acc,int friendcode) throws IOException
//	{
//
//		HSSFWorkbook conn=new DBConn().getWrokbook();
//		HSSFSheet sheet=conn.getSheetAt(1);
//		FileOutputStream fileOut=null;
//
//		int num=sheet.getLastRowNum();
//		if(sheet.getRow(num)==null) {
//			return false;
//		}
//		try {
//			int index=-1;
//			boolean flag6=false;
//
//			for(int i=0;i<num;i++) {
//				HSSFRow row = sheet.getRow(i);
//				if(String.valueOf(friendcode).equals(String.valueOf(row.getCell(0).getStringCellValue()))) {
//					index=i;
//					flag6=true;
//					break;
//				}
//			}
//			if(flag6) {
//				//逐步上移
//				for(int i=index;i<num;i++) {
//					HSSFRow row = sheet.getRow(i+1);
//					String qqcode=(String.valueOf(row.getCell(0).getStringCellValue()));
//					String NickName=(String.valueOf(row.getCell(1).getStringCellValue()));
//					String pwd=(String.valueOf(row.getCell(2).getStringCellValue()));
//					String IpAddr=(String.valueOf(row.getCell(3).getStringCellValue()));
//					String Port=(String.valueOf(row.getCell(4).getStringCellValue()));
//					String Age=(String.valueOf(row.getCell(5).getStringCellValue()));
//					String Sex=(String.valueOf(row.getCell(6).getStringCellValue()));
//					String Nation=(String.valueOf(row.getCell(7).getStringCellValue()));
//					String Star=(String.valueOf(row.getCell(8).getStringCellValue()));
//					String Face=(String.valueOf(row.getCell(9).getStringCellValue()));
//					String Remark=(String.valueOf(row.getCell(10).getStringCellValue()));
//					String Selfsign=(String.valueOf(row.getCell(11).getStringCellValue()));
//					String Status=(String.valueOf(row.getCell(12).getStringCellValue()));
//
//					HSSFRow currow = sheet.createRow((short)i);
//					//在row里建立新cell（单元格），参数为列号（第一列）                  //设置cell的整数类型的值
//					currow.createCell(0).setCellValue(String.valueOf(qqcode));     //设置cell浮点类型的值
//					currow.createCell(1).setCellValue(String.valueOf(NickName));   //设置cell字符类型的值
//					currow.createCell(2).setCellValue(String.valueOf(pwd));    //设置cell布尔类型的值
//					currow.createCell(3).setCellValue(String.valueOf(IpAddr));     //设置cell浮点类型的值
//					currow.createCell(4).setCellValue(String.valueOf(Port));   //设置cell字符类型的值
//					currow.createCell(5).setCellValue(String.valueOf(Age));    //设置cell布尔类型的值
//					currow.createCell(6).setCellValue(String.valueOf(Sex));     //设置cell浮点类型的值
//					currow.createCell(7).setCellValue(String.valueOf(Nation));   //设置cell字符类型的值
//					currow.createCell(8).setCellValue(String.valueOf(Star));    //设置cell布尔类型的值
//					currow.createCell(9).setCellValue(String.valueOf(Face));     //设置cell浮点类型的值
//					currow.createCell(10).setCellValue(String.valueOf(Remark));   //设置cell字符类型的值
//					currow.createCell(11).setCellValue(String.valueOf(Selfsign));    //设置cell布尔类型的值
//					currow.createCell(12).setCellValue(String.valueOf(Status));    //设置cell布尔类型的值
//					fileOut = new FileOutputStream("data.xls");
//
////				        conn.write(fileOut);
////				        fileOut.close();
//
//				}
////					sheet.createRow((short)num);//最后一行置空
//				sheet.removeRow(sheet.getRow(num));
//				conn.write(fileOut);
//				fileOut.close();
//			}
//			JOptionPane.showMessageDialog(null, "已将好友从数据库删除", "提示", JOptionPane.PLAIN_MESSAGE);
//
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		return true;
//
//	}
}
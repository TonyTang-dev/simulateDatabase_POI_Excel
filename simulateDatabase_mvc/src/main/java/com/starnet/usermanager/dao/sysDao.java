package com.dbLab.dao;

import java.io.*;

import com.dbLab.utils.DBUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


//进行数据库操作的函数
public class sysDao {
//	获取excel的对象类
	private DBUtils dbUtils;

	/**
	 * @description: java poi simulate database 设置获取excel的对象实例
	 * @author: CQU dbLab group Tang&Guo
	 * @date: 2022/5/24 20:43
	 * @param: [dbUtils]
	 * @return: void
	 **/
	public void setDbUtils(DBUtils dbUtils){
		this.dbUtils=dbUtils;
	}
	/*创建数据库--新建一个.xls表*/

	/**
	 * @author CQU db group
	 * @param baseName 创建的数据库名
	 * @return	successful or failed
	 */
	public boolean createDatabase(String baseName){
		File file=new File(baseName+".xls");
		if(!file.exists()){
			System.out.println("正在创建数据库···");
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

	/**
	 * @author CQU dbgroup
	 * @param tableName 创建的表明
	 * @param value 创建表的字段名
	 * @return successful or failed
	 */
	public boolean createTable(String tableName, String value[]){
		HSSFWorkbook conn = null;
		try {
			conn = dbUtils.getWrokbook(globalCmd.curDatabase);
			if(conn == null){
				System.out.println("获取数据表失败");
				return false;
			}
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
	/*insert*/
	/**
	 * @description: java poi simulate database
	 * @author: CQU dbLab group Tang&Guo
	 * @date: 2022/5/24 20:38
	 * @param: [tableName, values]	数据库表名和插入的数据列表
	 * @return: boolean successful or failed
	 **/
	public boolean insertTable(String tableName, String values[]){
		HSSFWorkbook conn = null;
		try {
			conn = dbUtils.getWrokbook(globalCmd.curDatabase);
			if(conn == null){
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		HSSFSheet sheet = conn.getSheet(tableName);
		int rowIndex = sheet.getLastRowNum()+1;
//		创建行
		HSSFRow row = sheet.createRow(rowIndex);
		HSSFCell cell = null;
		cell=sheet.getRow(0).getCell(1);
		cell.setCellValue(rowIndex);   //设置cell字符类型的值
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		try {
			for (int i = 0; i < values.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(values[i]);     //设置cell浮点类型的值
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
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	/*delete*/
	/**
	 * @description: java poi simulate database
	 * @author: CQU dbLab group Tang&Guo 
	 * @date: 2022/5/24 20:49
	 * @param: [tableName, cmd, params]
	 * @return: boolean
	 **/
	public boolean deleteTable(String tableName, int cmd, String params[]){
		HSSFWorkbook conn = null;
		try {
			conn = dbUtils.getWrokbook(globalCmd.curDatabase);
			if(conn == null){
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		HSSFSheet sheet = conn.getSheet(tableName);
		int rowIndex = sheet.getLastRowNum()+1;
		HSSFCell cell = null;
		cell=sheet.getRow(0).getCell(1);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//		删除整个表
		if(cmd == 1){
			for(int i=2;i<rowIndex;i++){
				sheet.shiftRows(3,sheet.getLastRowNum()+1,-1);
				sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
			}
			try {
				cell.setCellValue("2");   //设置cell字符类型的值
				FileOutputStream fileOut = new FileOutputStream(globalCmd.curDatabase + ".xls");

				conn.write(fileOut);
				fileOut.close();
			}catch(Exception e){
				System.out.println("修改数据库出错");
			}
			return true;
		}
		else if(cmd == 2){
			int j=0;
			for(j=0;j<Integer.parseInt(sheet.getRow(0).getCell(3).getStringCellValue());j++){
				if(params[0].equals(sheet.getRow(1).getCell(j).getStringCellValue())){
					break;
				}
			}
			for(int i=2;i<rowIndex;i++){
				if(params[1].equals(sheet.getRow(i).getCell(j).getStringCellValue())){
//					相等说明找到了
					sheet.shiftRows(i+1,sheet.getLastRowNum()+1,-1);
					sheet.removeRow(sheet.getRow(sheet.getLastRowNum()));
					try {
						cell.setCellValue(rowIndex-1);   //设置cell字符类型的值
						FileOutputStream fileOut = new FileOutputStream(globalCmd.curDatabase + ".xls");

						conn.write(fileOut);
						fileOut.close();
					}catch(Exception e){
						System.out.println("修改数据库出错");
					}
					return true;
				}
			}
		}
		return false;
	}
	
	/*select*/
	/**
	 * @description: java poi simulate database
	 * @author: CQU dbLab group Tang&Guo 
	 * @date: 2022/5/24 20:50
	 * @param: [tableName, cmd, params]
	 * @return: boolean
	 **/
	public selectEntity selectTable(String tableName, int cmd, String selectParams[], String params[]){
		HSSFWorkbook conn = null;
		try {
			conn = dbUtils.getWrokbook(globalCmd.curDatabase);
			if(conn == null){
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		HSSFSheet sheet = conn.getSheet(tableName);
		int rowIndex = sheet.getLastRowNum()+1;
		int column = Integer.parseInt(sheet.getRow(0).getCell(3).getStringCellValue());
		selectEntity rs = new selectEntity();
		rs.setSelectRS("");
		if(cmd == 1){
			for(int i=1;i<rowIndex;i++){
				for(int j=0;j<column;j++){
					rs.setSelectRS(rs.getSelectRS()+" "+sheet.getRow(i).getCell(j).getStringCellValue());
				}
				if(i==1){
					rs.setSelectRS(rs.getSelectRS()+"\n- - - - - - - - -\n");
				}
				else {
					rs.setSelectRS(rs.getSelectRS() + "\n");
				}
			}
			return rs;
		}
		else if(cmd == 2){
			int index = 0;
			int flag = 1;
			for(int j=0;j<column;j++){
				rs.setSelectRS(rs.getSelectRS()+" "+sheet.getRow(1).getCell(j).getStringCellValue());
				if(params[0].equals(sheet.getRow(1).getCell(j).getStringCellValue())){
					flag = 0;
				}
				else if(flag == 1){
					index++;
				}
			}
			rs.setSelectRS(rs.getSelectRS()+"\n- - - - - - - - -\n");
			for(int i=2;i<rowIndex;i++){
				if(params[1].equals(sheet.getRow(i).getCell(index).getStringCellValue())){
//					相等说明找到了
					for(int k=0;k<column;k++){
						rs.setSelectRS(rs.getSelectRS()+" "+sheet.getRow(i).getCell(k).getStringCellValue());
					}
					return rs;
				}
			}
		}
		return null;
	}
	/**
	 * @description: java poi simulate database
	 * @author: CQU dbLab group Tang&Guo 
	 * @date: 2022/5/25 15:16
	 * @param: [tableName, cmd, whereParams, params]
	 * @return: boolean
	 **/
	public boolean updateTable(String tableName, int cmd, String whereParams[], String params[]){
		HSSFWorkbook conn = null;
		try {
			conn = dbUtils.getWrokbook(globalCmd.curDatabase);
			if(conn == null){
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		HSSFSheet sheet = conn.getSheet(tableName);
		int rowIndex = sheet.getLastRowNum()+1;
		int column = Integer.parseInt(sheet.getRow(0).getCell(3).getStringCellValue());
		HSSFRow row = null;
		HSSFCell cell = null;
		int columnIndex=0;
		for(columnIndex=0;columnIndex<column;columnIndex++){
			if(params[0].equals(sheet.getRow(1).getCell(columnIndex).getStringCellValue())){
				break;
			}
		}

		if(cmd == 1){
			for(int i=2;i<rowIndex;i++){
				row = sheet.getRow(i);
				cell = row.getCell(columnIndex);
				cell.setCellValue(params[1]);     //设置cell浮点类型的值
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
		else if(cmd == 2){
			int whereIndex=0;
			for(whereIndex=0;whereIndex<column;whereIndex++){
				if(whereParams[0].equals(sheet.getRow(1).getCell(whereIndex).getStringCellValue())){
					break;
				}
			}
			for(int i=2;i<rowIndex;i++){
				if(whereParams[1].equals(sheet.getRow(i).getCell(whereIndex).getStringCellValue())){
//					相等说明找到了
					row = sheet.getRow(i);
					cell = row.getCell(columnIndex);
					cell.setCellValue(params[1]);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					try {
						FileOutputStream fileOut = new FileOutputStream(globalCmd.curDatabase + ".xls");

						conn.write(fileOut);
						fileOut.close();
					}catch(Exception e){
						System.out.println("修改数据库出错");
					}
					return true;
				}
			}
		}
		return false;
	}
}

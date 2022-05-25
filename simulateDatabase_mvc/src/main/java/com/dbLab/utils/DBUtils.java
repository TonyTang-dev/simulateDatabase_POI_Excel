package com.dbLab.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;


//这类仅仅负责调入驱动程序 并且连接数据库 其他功能都在库同名javaBean里写
public class DBUtils {
	//使用文件存储，不用数据库了

//	public WritableWorkbook DBConn() throws IOException {
//		File file = new File("data.xls");
//		if(!file.exists()) {
//			file.createNewFile();
//		}
//		WritableWorkbook workbook = Workbook.createWorkbook(file);
//
//		return workbook;
//	}

	public HSSFWorkbook getWrokbook(String baseName) {
		File file=new File(baseName+".xls");
		if(!file.exists()){
			System.out.println("数据库不存在");
			return null;
		}
		try {
			FileInputStream fs = new FileInputStream(baseName + ".xls");

			HSSFWorkbook wb = new HSSFWorkbook(fs);
			FileOutputStream fileOut = new FileOutputStream(baseName + ".xls");//创建文件流
			wb.write(fileOut);//把Workbook对象输出到路径path中
			fileOut.close();
			return wb;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

}

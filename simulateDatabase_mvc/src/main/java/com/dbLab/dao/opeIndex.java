package com.dbLab.dao;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class opeIndex {
    //查 有索引
    /**
     * @description: java poi simulate database 而分查找--有索引
     * @author: CQU dbLab group Tang&Guo
     * @date: 2022/5/25 22:28
     * @param: [name, id]
     * @return: java.util.List<java.lang.String>
     **/
    public static selectEntity select(String name, String id) {
        List<String> res = null;
        FileInputStream fis = null;
        HSSFWorkbook wb = null;
        HSSFSheet sheet = null;
        HSSFRow row = null;
        HSSFCell cell = null;

        try {
//            res = new ArrayList<>();
            fis = new FileInputStream(globalCmd.curDatabase + ".xls");
            wb = new HSSFWorkbook(fis);
            sheet = wb.getSheet(name);

            int sheetIndex = wb.getSheetIndex(sheet);
            sheet = wb.getSheetAt(0);
            int right = Integer.parseInt(sheet.getRow(2).getCell(sheetIndex - 1).getStringCellValue()) + 2;
            int left = 3;
            int mid = left + (right - left) / 2;
            while (left < right) {
                mid = left + (right - left) / 2;
                String stringCellValue = sheet.getRow(mid).getCell(sheetIndex - 1).getStringCellValue();
                if (id.compareTo(stringCellValue.split("-")[0]) == 0)
                    break;
                if (id.compareTo(stringCellValue.split("-")[0]) < 0)
                    left = mid + 1;
                else
                    right = mid;
            }
            String s = sheet.getRow(mid).getCell(sheetIndex - 1).getStringCellValue().split("-")[1];
            int line = Integer.parseInt(s) - 1;
            sheet = wb.getSheet(name);
            row = sheet.getRow(line);


            selectEntity rs = new selectEntity();
            rs.setSelectRS("");


            for (int i = 0; i < row.getLastCellNum(); i++) {
//                res.add(row.getCell(i).getStringCellValue());
                rs.setSelectRS(rs.getSelectRS()+" "+row.getCell(i).getStringCellValue());
//                if(i==0){
//                    rs.setSelectRS(rs.getSelectRS()+"\n- - - - - - - - -\n");
//                }
//                else {
//                    rs.setSelectRS(rs.getSelectRS() + "\n");
//                }
            }
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //插入索引头
    /**
     * @description: java poi simulate database 当创建表，将表信息插入数据库系统表
     * @author: CQU dbLab group Tang&Guo
     * @date: 2022/5/25 22:28
     * @param: [wb, sheetIndex, head]
     * @return: void
     **/
    public static void insertIndexHead(HSSFWorkbook wb, int sheetIndex, String head) {
        try {
            HSSFSheet sheet = wb.getSheetAt(0);	//获取系统sheet
            //记录表的个数
            if (sheet.getRow(0) == null) {
                HSSFCell cell = sheet.createRow(0).createCell(0);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("1");
            } else {
                HSSFRow row = sheet.getRow(0);
                if (row.getCell(0) == null) {
                    HSSFCell cell = row.createCell(0);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("1");
                } else {
                    HSSFCell cell = sheet.getRow(0).getCell(0);
                    String numericCellValue = cell.getStringCellValue();
                    cell.setCellValue(String.valueOf(Integer.parseInt(numericCellValue) + 1));
                }
            }
            //初始化表的名称
            if (sheet.getRow(1) == null) {
                sheet.createRow(1).createCell(sheetIndex - 1).setCellValue(head);
            } else {
                HSSFRow row = sheet.getRow(1);
                if (row.getCell(sheetIndex - 1) == null) {
                    row.createCell(sheetIndex - 1).setCellValue(head);
                } else {
                    row.getCell(sheetIndex - 1).setCellValue(head);
                }
            }
            //初始化表的索引长度
            if (sheet.getRow(2) == null) {
                HSSFCell cell = sheet.createRow(2).createCell(sheetIndex - 1);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("0");
            } else {
                HSSFRow row = sheet.getRow(2);
                if (row.getCell(sheetIndex - 1) == null) {
                    HSSFCell cell = row.createCell(sheetIndex - 1);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("0");
                } else {
                    HSSFCell cell = row.getCell(sheetIndex - 1);
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description: java poi simulate database 插入数据项，调用函数并记录ID-RowNum
     * @author: CQU dbLab group Tang&Guo
     * @date: 2022/5/25 22:29
     * @param: [conn, sheetIndex, indexing]
     * @return: boolean
     **/
    public static boolean insertIndex(HSSFWorkbook conn, int sheetIndex, String indexing) {
        sheetIndex -= 1;
        try {
            HSSFSheet sheet = conn.getSheetAt(0);	//获取系统sheet
            int len = Integer.parseInt(sheet.getRow(2).getCell(sheetIndex).getStringCellValue());
            sheet.getRow(2).getCell(sheetIndex).setCellValue((String.valueOf(len + 1)));
            System.out.println(len);
            int i = len;


            /*int lastRowNum = sheet.getLastRowNum(); //没有数据返回0，有一行数据返回0，有2行数据返回1，有n行数据返回n-1
            int i = 0;
            while (i < lastRowNum && sheet.getRow(i).getCell(sheetIndex) != null) {
                i++;
            }
            //只有一列索引时，i一定会等于lastRowNum；
            //当不止有一列索引时，新增的列的实际行数一定会小于lastRowNum，且等于i-1
            if (sheet.getRow(i).getCell(sheetIndex) == null)
                i--;
            lastRowNum = i;*/

            //放在最后
            if (i == 0 || sheet.getRow(i + 2).getCell(sheetIndex).getStringCellValue().compareTo(indexing) < 0) {
                if (sheet.getRow(i + 3) == null) {
                    sheet.createRow(i + 3).createCell(sheetIndex).setCellValue(indexing);
                } else {
                    HSSFRow row = sheet.getRow(i + 3);
                    if (row.getCell(sheetIndex) == null) {
                        row.createCell(sheetIndex).setCellValue(indexing);
                    } else {
                        row.getCell(sheetIndex).setCellValue(indexing);
                    }
                }
            } else {    //插入
                for (i = 1; i <= len; i++) {
                    HSSFCell cell = sheet.getRow(i + 2).getCell(sheetIndex);
                    //indexing 字典序在 单元格内容的字典序之前
                    if (indexing.compareTo(cell.getStringCellValue()) < 0) {
                        break;
                    }
                }
                for (int j = len; j >= i; j--) {
                    HSSFCell cell1 = sheet.getRow(j + 2).getCell(sheetIndex, Row.CREATE_NULL_AS_BLANK);
                    HSSFCell cell2 = null;
                    if (sheet.getRow(j + 3) != null) {
                        cell2 = sheet.getRow(j + 3).createCell(sheetIndex);
                    } else {
                        cell2 = sheet.createRow(j + 3).createCell(sheetIndex);
                    }
                    cell2.setCellValue(cell1.getStringCellValue());
                }
                if (sheet.getRow(i + 2) == null) {
                    HSSFRow row = sheet.createRow(i + 2);
                    row.createCell(sheetIndex).setCellValue(indexing);
                } else {
                    HSSFRow row = sheet.getRow(i + 2);
                    if (row.getCell(sheetIndex) == null) {
                        HSSFCell cell = row.createCell(sheetIndex);
                        cell.setCellValue(indexing);
                    } else {
                        row.getCell(sheetIndex).setCellValue(indexing);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}

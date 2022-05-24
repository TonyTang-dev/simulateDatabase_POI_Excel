package com.dbLab.service;

import java.util.List;

import com.dbLab.dao.*;

public class SysUserServiceImpl implements ISysUserService {
	
	public sysDao sysDao;
	
	public void setSysUserDao(sysDao sysUserDao) {
		this.sysDao = sysUserDao;
	}

	@Override
	public boolean createDatabase(String baseName){
		return sysDao.createDatabase(baseName);
	}
	@Override
	public boolean selectDatabase(String baseName){
		return sysDao.selectDatabase(baseName);
	}
	@Override
	public boolean createTable(String tableName, String value[]){
		return sysDao.createTable(tableName, value);
	}
	@Override
	public boolean operateDatabase(String operation){
		String operationStr[] = operation.split("\\s+");

		String mainOpe = operationStr[0];

		switch (mainOpe){
			/*create操作，包括创建数据库和创建数据库表*/
			case "create":
				switch(operationStr[1]){
					case "database":
						return createDatabase(globalCmd.databasePath+operationStr[2]);
					case "table":
						return createTable(operationStr[2], operationStr[3].substring(1, operationStr[3].length()-1).split(","));
				}
				break;
			/*选择数据库，在对数据库进行操作时，应当先选择数据库，否则计算机不知道你要操作哪个数据库*/
			case "use":
				return selectDatabase(globalCmd.databasePath+operationStr[1]);
			/*insert增加数据*/
			case "insert":
				return sysDao.insertTable(operationStr[2], operationStr[3].substring(1, operationStr[3].length()-1).split(","));
			/*delete删除数据*/
			case "delete":
				if(operationStr.length == 3) {
					return sysDao.deleteTable(operationStr[2], 1, null);
				}
				else{
					return sysDao.deleteTable(operationStr[2], 2, operationStr[4].split("="));
				}
			/*select查询数据*/
			case "select":
				if(operationStr.length == 4) {
					return sysDao.selectTable(operationStr[3], 1, operationStr[1].split(","), null);
				}
				else{
					return sysDao.selectTable(operationStr[3], 2, operationStr[1].split(","), operationStr[5].split("="));
				}
		}

		return true;
	}
}

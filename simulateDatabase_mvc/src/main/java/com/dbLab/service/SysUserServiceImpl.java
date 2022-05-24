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
			case "create":
				switch(operationStr[1]){
					case "database":
						return createDatabase(globalCmd.databasePath+operationStr[2]);
					case "table":
						return createTable(operationStr[2], operationStr[3].substring(1, operationStr[3].length()-1).split(","));
				}
				break;
			case "use":
				return selectDatabase(globalCmd.databasePath+operationStr[1]);
		}

		return true;
	}
}

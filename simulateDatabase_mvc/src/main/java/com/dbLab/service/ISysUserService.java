package com.dbLab.service;

import java.util.List;

import com.dbLab.dao.*;

public interface ISysUserService {
	public boolean createDatabase(String baseName);
	public boolean selectDatabase(String baseName);
	public boolean createTable(String tableName, String value[]);
	public boolean operateDatabase(String operation);
}

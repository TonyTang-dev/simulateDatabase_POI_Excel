package com.dbLab.dao;

import java.io.File;

public class globalCmd {
    public static String curDatabase="";

    public static String databasePath = "C:\\simulateDB\\";

    public void initSQL(){
        File file = new File(databasePath);
        if(!file.exists()){
            try {
                file.mkdir();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

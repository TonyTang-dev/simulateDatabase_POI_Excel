package com.dbLab.controller;

import com.dbLab.service.ISysUserService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class operateDatabaseController implements Controller {

    public ISysUserService sysUserService;

    public void setSysUserService(ISysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }
    @Override
    public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
        Long past = System.currentTimeMillis();
        arg1.setHeader("Content-type", "text/json;charset=UTF-8");


        System.out.println(arg0.getParameter("operation"));
        if(sysUserService.operateDatabase(arg0.getParameter("operation"))) {
            arg1.getOutputStream().write("1".getBytes());
        }
        else{
            arg1.getOutputStream().write("0".getBytes());
        }

        Long now = System.currentTimeMillis();
        System.out.println(((now-past))+"ms");
        return null;
    }
}

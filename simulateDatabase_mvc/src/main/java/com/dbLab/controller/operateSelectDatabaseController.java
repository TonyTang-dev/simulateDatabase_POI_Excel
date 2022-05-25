package com.dbLab.controller;

import com.alibaba.fastjson.JSONArray;
import com.dbLab.service.ISysUserService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class operateSelectDatabaseController implements Controller {
    public ISysUserService sysUserService;

    public void setSysUserService(ISysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }
    @Override
    public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
        arg1.setHeader("Content-type", "text/json;charset=UTF-8");


        System.out.println(arg0.getParameter("operation"));
        JSONArray jsonArray = new JSONArray();
        List rsList = new ArrayList();
        rsList.add(sysUserService.operateSelectDatabase(arg0.getParameter("operation")));
        jsonArray.addAll(rsList);

        arg1.getOutputStream().write(jsonArray.toJSONString().getBytes());

        return null;
    }
}

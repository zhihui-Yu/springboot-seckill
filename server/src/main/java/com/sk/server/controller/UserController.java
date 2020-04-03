package com.sk.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author yzh
 * @Date 2020/4/3 19:08
 * @Version 1.0
 */
@Controller
public class UserController {

    private Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private Environment environment;

    /**
     * 跳到登录页
     * @return
     */
    @RequestMapping(value = {"to/login","/unauth"})
    public String login(){
     return "login";
    }

    /**
     * 退出登入
     * @return
     */
    @RequestMapping(value = "/logout")
    public String logout(){
        SecurityUtils.getSubject().logout();
        return "login";
    }

    /**
     * 登录认证
     * @param userName
     * @param password
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(@RequestParam String userName, @RequestParam String password, ModelMap modelMap) {
        String errorMsg = "";
        try {
            if(!SecurityUtils.getSubject().isAuthenticated()){
                String pwd = new Md5Hash(password,environment.getProperty("shiro.encrypt.password.salt")).toString();
                UsernamePasswordToken token = new UsernamePasswordToken(userName,pwd);
                SecurityUtils.getSubject().login(token);
            }
        } catch (UnknownAccountException e) {
            errorMsg=e.getMessage();
            modelMap.addAttribute("userName",userName);
        }catch (DisabledAccountException e){
            errorMsg=e.getMessage();
            modelMap.addAttribute("userName",userName);
        }catch (IncorrectCredentialsException e) {
            errorMsg=e.getMessage();
            modelMap.addAttribute("userName",userName);
        }catch (Exception e  ){
            errorMsg="用户登录异常，请联系管理员!";
            e.printStackTrace();
        }
        if(StringUtils.isBlank(errorMsg)){
            return "redirect:/index";
        }
        return "login";
    }

}

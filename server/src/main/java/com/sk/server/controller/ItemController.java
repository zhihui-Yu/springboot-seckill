package com.sk.server.controller;

import com.sk.model.entity.ItemKill;
import com.sk.server.service.ItemService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

/**
 * @Author yzh
 * @Date 2020/3/30 8:43
 * @Version 1.0
 */
@Controller
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private static final String prefix = "item";

    @Resource
    private ItemService itemServiceImpl;

    /**
     * 返回请求页面
     * @param page
     * @return
     */
    @RequestMapping("{page}")
    public String page(@PathVariable String page){
        return page;
    }

    /**
     * 获取秒杀商品列表
     */
    @RequestMapping(value = {"/","/index",prefix+"/list",prefix+"/index.html"},method = RequestMethod.GET)
    public String list(ModelMap modelMap) {
        try{
            //获取代秒杀商品列表
            List<ItemKill> list = itemServiceImpl.getKillItems();
            modelMap.put("list",list);
            log.info("获取秒杀商品列表-数据：{}",list);
        }catch (Exception e){
            log.error("获取秒杀商品列表-发生异常");
            return "redirect:error";
        }
        return "list";
    }

    @RequestMapping(value = prefix+"/detail/{id}",method = RequestMethod.GET)
    public String detail(@PathVariable Integer id, ModelMap modelMap){
        if(id == null || id < 0){
            return "redirect:base";
        }

        try {
            modelMap.put("detail",itemServiceImpl.getKillDetail(id));
        }catch (Exception e ){
            log.error("获取待秒杀商品详情--发生异常：id=",id,e.getStackTrace());
            return "error";
        }
        return "info";
    }
}

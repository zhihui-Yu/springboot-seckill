package com.sk.server.controller;

import com.sk.api.enums.StatusCode;
import com.sk.api.response.BaseResponse;
import com.sk.model.dto.KillSuccessUserInfo;
import com.sk.model.mapper.ItemKillSuccessMapper;
import com.sk.server.dto.KillDto;
import com.sk.server.service.KillService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 *
 * 秒杀服务 controller
 *
 * @Author yzh
 * @Date 2020/3/30 17:46
 * @Version 1.0
 */
@Controller
public class KillController {

    private static final Logger LOG = LoggerFactory.getLogger(KillController.class);

    private static final String PREFIX = "kill";

    @Resource
    private KillService killServiceImpl;

    private ItemKillSuccessMapper itemKillSuccessMapper;

    /**
     * 秒杀商品核心业务
     *
     * @param dto
     * @param result
     * @param session
     * @return
     */
    @RequestMapping(value = PREFIX+"/execute",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse excute(@RequestBody @Validated KillDto dto, BindingResult result, HttpSession session){
        if(result.hasErrors() || dto.getKillId() <=0) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        Object uid = session.getAttribute("uid");
        if(uid==null || uid.equals("")){
            return new BaseResponse(StatusCode.UserNotLogin);
        }

        BaseResponse response = new BaseResponse(StatusCode.Success);

        try {
            //TODO: 1. 没有分布式锁的情况下  实现秒杀业务  当QPS高时 可能有问题
            //Boolean res = killServiceImpl.killItem(dto.getKillId(), dto.getUserId());

            //TODO: 2. 使用 redis 分布式锁 实现秒杀业务
            Boolean res = killServiceImpl.killItemV2(dto.getKillId(), (Integer)uid);

            //TODO: 3. 使用 redisson 分布式锁 实现秒杀业务
            //Boolean res = killServiceImpl.killItemV3(dto.getKillId(), dto.getUserId());

            //TODO: 4. 使用 zookeeper 分布式锁 实现秒杀业务
            //Boolean res = killServiceImpl.killItemV4(dto.getKillId(), dto.getUserId());
            //不成功
            if(!res) {
                return new BaseResponse(StatusCode.Fail.getCode(),"商品已抢购完");
            }
        }catch (Exception e ) {
            LOG.info("秒杀业务出现异常");
            response = new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     * 查看订单详情
     * @return
     */
    @RequestMapping(value = PREFIX+"/record/detail/{orderNo}",method = RequestMethod.GET)
    public String killRecordDetail(@PathVariable String orderNo, ModelMap modelMap,HttpSession session){
        if(session.getAttribute("uid")==null){
            return "redirect:/unauth";
        }
        if (StringUtils.isBlank(orderNo)){
            return "error";
        }
        System.out.println(orderNo);
        KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNo);
        if (info==null){
            return "error";
        }
        modelMap.put("info",info);
        return "killRecord";
    }

    /**
     * 抢购后 跳转的页面
     * @param url
     * @return
     */
    @RequestMapping(value = PREFIX+"/execute/{url}",method = RequestMethod.GET)
    public String executeSuccess(@PathVariable String url){
        if(url.equals("success")){  return "executeSuccess";   }
        return "executeFail";
    }
}

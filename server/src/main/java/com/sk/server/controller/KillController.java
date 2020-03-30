package com.sk.server.controller;

import com.sk.api.enums.StatusCode;
import com.sk.api.response.BaseResponse;
import com.sk.server.dto.KillDto;
import com.sk.server.service.KillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.provider.certpath.PKIXTimestampParameters;

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
    public BaseResponse excute(@RequestBody @Validated KillDto dto, BindingResult result){
        if(result.hasErrors() || dto.getKillId() <=0) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);

        try {
            Boolean res = killServiceImpl.killItem(dto.getKillId(), dto.getUserId());
            //不成功
            if(!res) {
                return new BaseResponse(StatusCode.Fail.getCode(),"商品已抢购完");
            }
        }catch (Exception e ) {
            response = new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}

package com.sk.server.service;

import com.sk.model.dto.KillSuccessUserInfo;
import com.sk.model.entity.ItemKillSuccess;
import com.sk.model.mapper.ItemKillSuccessMapper;
import com.sk.server.dto.MailDto;
import org.omg.IOP.ComponentIdHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RabbitMQ接收消息服务
 * @Author yzh
 * @Date 2020/4/1 20:25
 * @Version 1.0
 */
@Service
public class RabbitReceiverService {
    public static final Logger log= LoggerFactory.getLogger(RabbitReceiverService.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private Environment env;

    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;

    /**
     * 秒杀异步邮件通知-接收消息
     * @param info
     */
    @RabbitListener(queues = {"${mq.kill.item.success.email.queue}"},containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(KillSuccessUserInfo info) {
        try {
            log.info("秒杀异步邮件通知-接收消息:{}",info);

            //TODO:真正的发送邮件....
            /** 简单邮件发送 **/
            //MailDto dto=new MailDto(env.getProperty("mail.kill.item.success.subject"),"这是测试内容",new String[]{info.getEmail()});
            //mailService.sendSimpleEmail(dto);

            /** 花哨邮件发送 **/
            System.out.println(env.getProperty("mail.kill.item.success.content"));
            final String content = String.format(env.getProperty("mail.kill.item.success.content"),info.getItemName(),info.getCode());
            MailDto dto = new MailDto(env.getProperty("mail.kill.item.success.subject"),content,
                    new String[] {info.getEmail()});
            mailService.sendHTMLMail(dto);
        }catch (Exception e) {
            log.error("秒杀异步邮件通知-接收消息-发生异常：",e.fillInStackTrace());
        }
    }

    @RabbitListener(queues = {"${mq.kill.item.success.kill.dead.real.queue}"},containerFactory = "singleListenerContainer")
    public void consumeExpireOrder(KillSuccessUserInfo info) {
        try {
            log.info("用户秒杀成功后超时未支付-监听者-接收消息:{}",info);

            if(info!=null) {
                ItemKillSuccess entity = itemKillSuccessMapper.selectByCode(info.getCode());
                if(entity != null && entity.getStatus().intValue() == 0) {
                    itemKillSuccessMapper.expireOrder(-1,info.getCode());
                }
            }
        }catch (Exception e) {
            log.error("用户秒杀成功后超时未支付-监听者-发生异常：",e.fillInStackTrace());
        }
    }
}

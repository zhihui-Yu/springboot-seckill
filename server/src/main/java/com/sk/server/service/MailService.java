package com.sk.server.service;

import com.sk.server.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * 邮件服务
 * @Author yzh
 * @Date 2020/4/1 20:27
 * @Version 1.0
 */
@Service
@EnableAsync
public class MailService {
    private static final Logger log= LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    /**
     * 发送一个简单的邮件 -- 异步
     * @param dto
     */
    @Async
    public void sendSimpleEmail(final MailDto dto) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            //谁发
            message.setFrom(env.getProperty("mail.send.from"));
            //发给谁
            message.setTo(dto.getTos());
            //主题
            message.setSubject(dto.getSubject());
            //内容
            message.setText(dto.getContent());
            mailSender.send(message);
            log.info("发送简单文本文件-发送成功!");
        }catch (Exception e) {
            log.error("发送简单文本文件-发生异常： ",e.fillInStackTrace());
        }
    }

    /**
     * 发送花哨邮件
     * @param dto
     */
    @Async
    public void sendHTMLMail(final MailDto dto) {
        try{
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper msgHelp = new MimeMessageHelper(msg, true, "UTF-8");
            msgHelp.setFrom(env.getProperty("mail.send.from"));
            msgHelp.setTo(dto.getTos());
            msgHelp.setSubject(dto.getSubject());
            msgHelp.setText(dto.getContent(),true);

            mailSender.send(msg);
            log.info("发送花哨邮件-发送成功!");
        }catch (Exception e ) {
            log.error("发送花哨邮件-发生异常： ",e.fillInStackTrace());
        }
    }
}

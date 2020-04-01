package com.sk.server.dto;/**
 * Created by Administrator on 2019/6/22.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2019/6/22 10:11
 **/

public class MailDto implements Serializable{
    //邮件主题
    private String subject;
    //邮件内容
    private String content;
    //接收人
    private String[] tos;

    public MailDto() {
    }

    @Override
    public String toString() {
        return "MailDto{" +
                "subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", tos=" + Arrays.toString(tos) +
                '}';
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getTos() {
        return tos;
    }

    public void setTos(String[] tos) {
        this.tos = tos;
    }

    public MailDto(String subject, String[] tos) {
        this.subject = subject;
        this.tos = tos;
    }

    public MailDto(String[] tos) {
        this.tos = tos;
    }

    public MailDto(String subject, String content, String[] tos) {
        this.subject = subject;
        this.content = content;
        this.tos = tos;
    }
}
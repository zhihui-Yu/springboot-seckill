package com.sk.server.service;

/**
 * @Author yzh
 * @Date 2020/3/30 18:24
 * @Version 1.0
 */
public interface KillService {

    Boolean killItem(Integer killId,Integer userId) throws Exception;

    Boolean killItemV2(Integer killId, Integer userId) throws Exception;

    Boolean killItemV3(Integer killId, Integer userId) throws Exception;

    Boolean killItemV4(Integer killId, Integer userId) throws Exception;

}

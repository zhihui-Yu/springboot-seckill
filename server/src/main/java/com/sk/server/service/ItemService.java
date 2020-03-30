package com.sk.server.service;

import com.sk.model.entity.ItemKill;

import java.util.List;

/**
 * @Author yzh
 * @Date 2020/3/30 8:53
 * @Version 1.0
 */
public interface ItemService {

    /**
     * 获取待秒杀商品列表
     * @return
     * @throws Exception
     */
    List<ItemKill> getKillItems() throws Exception;

    /**
     * 获取秒杀详情
     * @param id
     * @return
     * @throws Exception
     */
    ItemKill getKillDetail(Integer id) throws Exception;
}

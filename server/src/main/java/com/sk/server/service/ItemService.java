package com.sk.server.service;

import com.sk.model.entity.ItemKill;

import java.util.List;

/**
 * @Author yzh
 * @Date 2020/3/30 8:53
 * @Version 1.0
 */
public interface ItemService {

    List<ItemKill> getKillItems() throws Exception;

}

package com.sk.server.service.impl;

import com.sk.model.entity.ItemKill;
import com.sk.model.mapper.ItemKillMapper;
import com.sk.server.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author yzh
 * @Date 2020/3/30 8:53
 * @Version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    @Resource
    private ItemKillMapper itemKillMapper;

    @Override
    public List<ItemKill> getKillItems() throws Exception {
        return itemKillMapper.selectAll();
    }

    @Override
    public ItemKill getKillDetail(Integer id) throws Exception {
        ItemKill itemKill = itemKillMapper.selectById(id);
        if(itemKill == null ){
            throw new Exception("获取秒杀商品详情--待秒杀商品记录不存在");
        }
        return itemKill;
    }


}

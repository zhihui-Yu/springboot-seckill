package com.sk.model.mapper;

import com.sk.model.dto.KillSuccessUserInfo;
import com.sk.model.entity.ItemKillSuccess;
import com.sk.model.entity.ItemKillSuccessExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemKillSuccessMapper {

    int expireOrder(Integer status, String code);

    KillSuccessUserInfo selectByCode(String orderId);

    int countByExample(ItemKillSuccessExample example);

    int deleteByExample(ItemKillSuccessExample example);

    int deleteByPrimaryKey(String code);

    int insert(ItemKillSuccess record);

    int insertSelective(ItemKillSuccess record);

    List<ItemKillSuccess> selectByExample(ItemKillSuccessExample example);

    ItemKillSuccess selectByPrimaryKey(String code);

    int updateByExampleSelective(@Param("record") ItemKillSuccess record, @Param("example") ItemKillSuccessExample example);

    int updateByExample(@Param("record") ItemKillSuccess record, @Param("example") ItemKillSuccessExample example);

    int updateByPrimaryKeySelective(ItemKillSuccess record);

    int updateByPrimaryKey(ItemKillSuccess record);
}
package com.sk.server.service.impl;

import com.sk.model.entity.ItemKill;
import com.sk.model.entity.ItemKillExample;
import com.sk.model.entity.ItemKillSuccess;
import com.sk.model.entity.ItemKillSuccessExample;
import com.sk.model.mapper.ItemKillMapper;
import com.sk.model.mapper.ItemKillSuccessMapper;
import com.sk.server.service.KillService;
import com.sk.server.service.RabbitSenderService;
import com.sk.server.utils.RandomUtil;
import com.sk.server.utils.SnowFlake;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.text.html.parser.Entity;
import java.util.Date;
import java.util.Random;

/**
 * @Author yzh
 * @Date 2020/3/30 18:24
 * @Version 1.0
 */
@Service
public class KillServiceImpl implements KillService {

    private SnowFlake snowFlake = new SnowFlake(2,3);
    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Resource
    private ItemKillMapper itemKillMapper;

    @Resource
    private RabbitSenderService rabbitSenderService;

    /**
     * 商品秒杀处理
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {

        boolean result = false;

        ItemKillSuccessExample example = new ItemKillSuccessExample();
        example.createCriteria().andUserIdEqualTo(userId+"").andKillIdEqualTo(killId);
        //判断用户是否已经抢过
        if(itemKillSuccessMapper.countByExample(example) <= 0) {
            //查看待秒杀商品详情
            ItemKill itemKill = itemKillMapper.selectById(killId);

            //判断是否可以被秒杀
            if(itemKill != null && 1 == itemKill.getCanKill()){
                //库存减一
                int res = itemKillMapper.updateItemKillCount(killId);
                //扣除成功？是生成订单并通知用户
                if(res>0){
                    commonRecordKillsuccessInfo(itemKill,userId);
                    return true;
                }
            }
        }
        return result;
    }

    private void commonRecordKillsuccessInfo(ItemKill kill,Integer userid) throws Exception{
        //记录抢购成功后生成的秒杀订单记录
        ItemKillSuccess item = new ItemKillSuccess();
        //item.setCode(RandomUtil.generaterOrderCode());
        String code = snowFlake.nextId()+"";
        item.setCode(code);
        item.setKillId(kill.getId());
        item.setItemId(kill.getItemId());
        item.setUserId(userid+"");
        item.setStatus((byte)0);
        item.setCreateTime(new Date());
        int res = itemKillSuccessMapper.insertSelective(item);

        if(res > 0) {
            //进行异步发送邮件通知
            rabbitSenderService.sendKillSuccesEmailMsg(code);
        }
    }

    @Override
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {
        return null;
    }

    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        return null;
    }

    @Override
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        return null;
    }

    @Override
    public Boolean killItemV5(Integer killId, Integer userId) throws Exception {
        return null;
    }
}

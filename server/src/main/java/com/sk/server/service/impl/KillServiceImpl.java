package com.sk.server.service.impl;

import com.sk.model.entity.ItemKill;
import com.sk.model.entity.ItemKillSuccess;
import com.sk.model.entity.ItemKillSuccessExample;
import com.sk.model.mapper.ItemKillMapper;
import com.sk.model.mapper.ItemKillSuccessMapper;
import com.sk.server.service.KillService;
import com.sk.server.service.RabbitSenderService;
import com.sk.server.utils.SnowFlake;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author yzh
 * @Date 2020/3/30 18:24
 * @Version 1.0
 */
@Service
public class KillServiceImpl implements KillService {

    private SnowFlake snowFlake = new SnowFlake(2, 3);
    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Resource
    private ItemKillMapper itemKillMapper;

    @Resource
    private RabbitSenderService rabbitSenderService;

    /**
     * 商品秒杀处理 -- 只做数据库层面处理
     *
     * @param killId 秒杀商品id
     * @param userId 用户id
     * @return 是否成功
     * @throws Exception 处理异常
     */
    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {

        boolean result = false;

        ItemKillSuccessExample example = new ItemKillSuccessExample();
        example.createCriteria().andUserIdEqualTo(userId + "").andKillIdEqualTo(killId);
        //判断用户是否已经抢过
        if (itemKillSuccessMapper.countByExample(example) <= 0) {
            result = seckill(killId, userId);
        }
        return result;
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 使用redis的分布式锁
     * 确定 在获取锁后 还没设置失效时间 redis突然挂了，那么后面的获取锁操作都将不成功
     *
     * @param killId 商品id
     * @param userId 用户id
     * @return 是否秒杀成功
     * @throws Exception 处理异常
     */
    @Override
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {
        boolean result = false;

        ItemKillSuccessExample example = new ItemKillSuccessExample();
        example.createCriteria().andUserIdEqualTo(userId + "").andKillIdEqualTo(killId);

        //TODO: 判断用户是否已经抢过
        if (itemKillSuccessMapper.countByExample(example) <= 0) {

            //TODO: 获取分布式锁 -- 设置key-value
            ValueOperations<String, String> oper = stringRedisTemplate.opsForValue();
            //为了保证线程安全 所以用StringBuffer
            final String key = new StringBuffer().append(killId).append(userId).append("-lock").toString();
            final String value = "lock";
            //相当于 setnx key value
            boolean get = oper.setIfAbsent(key, value);

            //TODO: 如果获取锁了
            if (get) {
                //TODO: 设置失效时间
                stringRedisTemplate.expire(key, 30, TimeUnit.SECONDS);
                try {
                    //TODO: 秒杀核心业务
                    result = seckill(killId, userId);
                } catch (Exception e) {
                    throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
                } finally {
                    if (value.equals(oper.get(key).toString())) {
                        stringRedisTemplate.delete(key);
                    }
                }
            }
        }
        return result;
    }


    @Resource
    private RedissonClient redissonClient;

    /**
     * Redisson 实现的分布式锁
     *
     * @param killId 商品id
     * @param userId 用户id
     * @return 是否秒杀成功
     * @throws Exception 处理异常
     */
    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception  {
        boolean result = false;

        ItemKillSuccessExample example = new ItemKillSuccessExample();
        example.createCriteria().andUserIdEqualTo(userId + "").andKillIdEqualTo(killId);

        //TODO：判断用户是否已经抢过
        if (itemKillSuccessMapper.countByExample(example) <= 0) {

            //TODO：获取分布式锁
            final String key = new StringBuffer().append(killId).append(userId).append("-lock").toString();
            RLock lock = redissonClient.getLock(key);

            try {
                //TODO: 设置失效时间 如果没有获取到 10秒后重新尝试
                Boolean get = lock.tryLock(30, 10, TimeUnit.SECONDS);
                //获取锁了
                if (get) {
                    result = seckill(killId, userId);
                }
            } catch (Exception e) {
                throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
            } finally {
                lock.unlock();
            }
        }
        return result;
    }

    @Resource
    private CuratorFramework curatorFramework;

    private static final String pathPrefix="/kill/zkLock/";

    /**
     * zookeeper 实现的分布式锁
     *
     * @param killId 商品id
     * @param userId 用户id
     * @return 是否秒杀成功
     * @throws Exception 处理异常
     */
    @Override
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        boolean result = false;

        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, pathPrefix + killId + userId + "-lock");
        //TODO：创建一个临时节点 有则Error 无则成功
        try {
            if(mutex.acquire(10L,TimeUnit.SECONDS)) {
                //TODO：判断用户是否已经抢过
                ItemKillSuccessExample example = new ItemKillSuccessExample();
                example.createCriteria().andUserIdEqualTo(userId + "").andKillIdEqualTo(killId);
                if (itemKillSuccessMapper.countByExample(example) <= 0) {
                    result = seckill(killId, userId);
                }
            }
        } catch (Exception e) {
            throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
        } finally {
            //TODO:释放节点
            if ( mutex != null ){
                mutex.release();
            }
        }

        return result;
    }

    /**
     * 秒杀核心业务
     *
     * @param killId 秒杀商品id
     * @param userId 用户id
     * @return 是否秒杀成功
     * @throws Exception 处理异常
     */
    private boolean seckill(Integer killId, Integer userId) throws Exception {
        //TODO：查看待秒杀商品详情
        ItemKill itemKill = itemKillMapper.selectById(killId);

        //TODO：判断是否可以被秒杀
        if (itemKill != null && 1 == itemKill.getCanKill() && itemKill.getTotal() > 0) {
            //TODO：库存减一
            int res = itemKillMapper.updateItemKillCount(killId);
            //TODO： 扣除成功？是生成订单并通知用户
            if (res > 0) {
                commonRecordKillsuccessInfo(itemKill, userId);
                return true;
            }
        }
        return false;
    }

    /**
     * 发送邮件服务
     *
     * @param kill   秒杀商品信息
     * @param userid 用户id
     * @throws Exception 异常
     */
    private void commonRecordKillsuccessInfo(ItemKill kill, Integer userid) throws Exception {
        //TODO：记录抢购成功后生成的秒杀订单记录
        ItemKillSuccess item = new ItemKillSuccess();
        String code = snowFlake.nextId() + "";
        item.setCode(code);
        item.setKillId(kill.getId());
        item.setItemId(kill.getItemId());
        item.setUserId(userid + "");
        item.setStatus((byte) 0);
        item.setCreateTime(new Date());
        int res = itemKillSuccessMapper.insertSelective(item);

        if (res > 0) {
            try {
                //TODO：进行异步发送邮件通知
                rabbitSenderService.sendKillSuccesEmailMsg(code);

                //TODO: 将订单信息入死信队列 用于失效过期订单
                rabbitSenderService.sendKillSuccessOrderExpireMsg(code);
            } catch (Exception e) {
                throw new Exception("邮件队列或者订单队列出现异常");
            }
        }
    }
}

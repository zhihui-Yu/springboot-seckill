# springboot-seckill
说明：本项目是基于SpringBoot实现的Java商城秒杀系统，只实现主要的秒杀和登入。<br>
实现：jdk1.8 + tomcat8 + redis-3.0 + zookeeper-3.4.6<br>
1. 基于redis实现分布式锁<br>
  1.1 redis API实现<br>
  1.2 redisson API实现<br>
2. 基于zookeeper实现的分布式锁
3. 使用shiro进行认证管理

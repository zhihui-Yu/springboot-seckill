package com.sk.server.service;

import com.sk.model.entity.User;
import com.sk.model.entity.UserExample;
import com.sk.model.mapper.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * Shiro 认证 授权
 * @Author yzh
 * @Date 2020/4/2 21:48
 * @Version 1.0
 */
public class CustomRealm extends AuthorizingRealm {

    private static final Logger log = LoggerFactory.getLogger(CustomRealm.class);

    private static final Long sessionKeyTimeOut = 3600_000L;

    @Resource
    private UserMapper userMapper;

    /**
     * 认证
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 授权
     * @param authenticationToken 含有用户信息的token
     * @return
     * @throws AuthenticationException 异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken;
        String userName = token.getUsername();
        String password = String.valueOf(token.getPassword());

        log.info("当前登录的用户名={} 密码={} ",userName,password);

        UserExample example = new UserExample();
        example.createCriteria().andUserNameEqualTo(userName);
        List<User> users = userMapper.selectByExample(example);
        if ( users == null && users.size() < 1  ) {
            throw  new UnknownAccountException("用户名不存在");
        }
        if(!Objects.equals(1,users.get(0).getIsActive().intValue())){
            throw new DisabledAccountException("当前用户已被禁用");
        }
        if(!users.get(0).getPassword().equals(password)){
            throw new IncorrectCredentialsException("用户名密码不匹配");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(users.get(0).getUserName(),password,getName());
        setSession("uid",users.get(0).getId());
        return info;
    }

    /**
     * 将key与对应的value塞入shiro的session中-最终交给HttpSession进行管理(如果是分布式session配置，那么就是交给redis管理)
     * @param key 键
     * @param value 值
     */
    private void setSession(String key, Object value) {
        Session session = SecurityUtils.getSubject().getSession();
        if(session != null) {
            session.setAttribute(key,value);
            session.setTimeout(sessionKeyTimeOut);
        }
    }
}

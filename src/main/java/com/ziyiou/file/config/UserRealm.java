package com.ziyiou.file.config;

import com.ziyiou.file.mapper.UserMapper;
import com.ziyiou.file.pojo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;

public class UserRealm extends AuthorizingRealm {
    @Autowired
    UserMapper userMapper;

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // 获取当前用户
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User)subject.getPrincipal();

        // 设置当前用户角色
        info.setRoles(new HashSet<>(Arrays.asList(currentUser.getRole().split(","))));

        //todo 设置当前用户权限

        return info;
    }

    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        User user = userMapper.selectByUsername(token.getUsername());
        if (user == null) {
            return null;
        }
        return new SimpleAuthenticationInfo(user, user.getPassword(), this.getName());
    }
}
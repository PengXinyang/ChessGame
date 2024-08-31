package com.pengxinyang.chessgame.service.tools;

import com.pengxinyang.chessgame.entity.User;
import com.pengxinyang.chessgame.mapper.UserMapper;
import com.pengxinyang.chessgame.service.impl.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CurrentUser {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    /**
     * 获取当前登录用户的uid，也是JWT认证的一环
     * @return 当前登录用户的uid
     */
    public Integer getUserId() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User suser = loginUser.getUser();   // 这里的user是登录时存的security:user，因为是静态数据，可能会跟实际的有区别，所以只能用作获取uid用
        return suser.getUid();
    }

    /**
     * 获取当前用户
     * @return User
     */
    public User getUser() {
        Integer uid = getUserId();
        return userMapper.selectById(uid);
    }

    public Integer getUserUid(){
        AtomicReference<UsernamePasswordAuthenticationToken> authenticationToken = new AtomicReference<>((UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
        AtomicReference<User> sUser = new AtomicReference<>(new User());
        CompletableFuture<?> futureUid = CompletableFuture.runAsync(()->{
            authenticationToken.set((UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
            UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.get().getPrincipal();
            sUser.set(loginUser.getUser());   // 这里的user是登录时存的security:user，因为是静态数据，可能会跟实际的有区别，所以只能用作获取uid用
        },taskExecutor);
        CompletableFuture<?> futureUser = CompletableFuture.runAsync(()->{
            sUser.set((User) authenticationToken.get().getPrincipal());
        },taskExecutor);
        CompletableFuture.allOf(futureUid, futureUser).join();
        return sUser.get().getUid();
    }
}

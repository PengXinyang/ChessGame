package com.pengxinyang.chessgame.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengxinyang.chessgame.entity.User;
import com.pengxinyang.chessgame.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        queryWrapper.ne("state", 2);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }

        return new UserDetailsImpl(user);
    }
}

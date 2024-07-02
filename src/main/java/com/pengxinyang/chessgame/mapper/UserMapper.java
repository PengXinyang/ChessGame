package com.pengxinyang.chessgame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengxinyang.chessgame.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}

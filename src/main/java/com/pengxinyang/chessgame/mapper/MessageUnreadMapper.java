package com.pengxinyang.chessgame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengxinyang.chessgame.entity.MessageUnread;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageUnreadMapper extends BaseMapper<MessageUnread> {
    int updateByIdWithVersion(MessageUnread messageUnread);
}

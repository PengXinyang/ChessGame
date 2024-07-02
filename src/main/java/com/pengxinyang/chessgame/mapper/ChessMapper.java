package com.pengxinyang.chessgame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengxinyang.chessgame.entity.Chess;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChessMapper extends BaseMapper<Chess> {
}

package com.pengxinyang.chessgame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengxinyang.chessgame.entity.ChessStats;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChessStatsMapper extends BaseMapper<ChessStats> {
}

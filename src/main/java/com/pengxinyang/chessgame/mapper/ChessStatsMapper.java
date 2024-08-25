package com.pengxinyang.chessgame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengxinyang.chessgame.entity.ChessStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ChessStatsMapper extends BaseMapper<ChessStats> {
    @Update("truncate table chess_stats")
    void deleteChessStats();
}

package com.pengxinyang.chessgame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengxinyang.chessgame.entity.ChessStats;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ChessStatsMapper extends BaseMapper<ChessStats> {
    @Update("truncate table chess_stats")
    void deleteChessStats();
    @Insert("""
            insert into chess_stats (cid, x, y, color, ate) values (1, 0, 0, 1, 0),
                                                                 (2, 1, 0, 1, 0),
                                                                 (3, 2, 0, 1, 0),
                                                                 (4, 3, 0, 1, 0),
                                                                 (5, 4, 0, 1, 0),
                                                                 (6, 5, 0, 1, 0),
                                                                 (7, 6, 0, 1, 0),
                                                                 (8, 7, 0, 1, 0),
                                                                 (9, 8, 0, 1, 0),
                                                                 (10, 1, 2, 1, 0),
                                                                 (11, 7, 2, 1, 0),
                                                                 (12, 0, 3, 1, 0),
                                                                 (13, 2, 3, 1, 0),
                                                                 (14, 4, 3, 1, 0),
                                                                 (15, 6, 3, 1, 0),
                                                                 (16, 8, 3, 1, 0),
                                                                 (17, 0, 9, 0, 0),
                                                                 (18, 1, 9, 0, 0),
                                                                 (19, 2, 9, 0, 0),
                                                                 (20, 3, 9, 0, 0),
                                                                 (21, 4, 9, 0, 0),
                                                                 (22, 5, 9, 0, 0),
                                                                 (23, 6, 9, 0, 0),
                                                                 (24, 7, 9, 0, 0),
                                                                 (25, 8, 9, 0, 0),
                                                                 (26, 1, 7, 0, 0),
                                                                 (27, 7, 7, 0, 0),
                                                                 (28, 0, 6, 0, 0),
                                                                 (29, 2, 6, 0, 0),
                                                                 (30, 4, 6, 0, 0),
                                                                 (31, 6, 6, 0, 0),
                                                                 (32, 8, 6, 0, 0);""")
    void insertAll();
}

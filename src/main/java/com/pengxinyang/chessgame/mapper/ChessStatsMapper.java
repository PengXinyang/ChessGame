package com.pengxinyang.chessgame.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengxinyang.chessgame.entity.ChessStats;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface ChessStatsMapper extends BaseMapper<ChessStats> {
    @Delete("delete from chess_stats where room_id = #{room_id}")
    void deleteChessStats(@Param("room_id") int roomId);
    @Insert("""
            insert into chess_stats (cid, x, y, color, ate, room_id) values (1, 0, 0, 1, 0, #{room_id}),
                                                                 (2, 1, 0, 1, 0, #{room_id}),
                                                                 (3, 2, 0, 1, 0, #{room_id}),
                                                                 (4, 3, 0, 1, 0, #{room_id}),
                                                                 (5, 4, 0, 1, 0, #{room_id}),
                                                                 (6, 5, 0, 1, 0, #{room_id}),
                                                                 (7, 6, 0, 1, 0, #{room_id}),
                                                                 (8, 7, 0, 1, 0, #{room_id}),
                                                                 (9, 8, 0, 1, 0, #{room_id}),
                                                                 (10, 1, 2, 1, 0, #{room_id}),
                                                                 (11, 7, 2, 1, 0, #{room_id}),
                                                                 (12, 0, 3, 1, 0, #{room_id}),
                                                                 (13, 2, 3, 1, 0, #{room_id}),
                                                                 (14, 4, 3, 1, 0, #{room_id}),
                                                                 (15, 6, 3, 1, 0, #{room_id}),
                                                                 (16, 8, 3, 1, 0, #{room_id}),
                                                                 (17, 0, 9, 0, 0, #{room_id}),
                                                                 (18, 1, 9, 0, 0, #{room_id}),
                                                                 (19, 2, 9, 0, 0, #{room_id}),
                                                                 (20, 3, 9, 0, 0, #{room_id}),
                                                                 (21, 4, 9, 0, 0, #{room_id}),
                                                                 (22, 5, 9, 0, 0, #{room_id}),
                                                                 (23, 6, 9, 0, 0, #{room_id}),
                                                                 (24, 7, 9, 0, 0, #{room_id}),
                                                                 (25, 8, 9, 0, 0, #{room_id}),
                                                                 (26, 1, 7, 0, 0, #{room_id}),
                                                                 (27, 7, 7, 0, 0, #{room_id}),
                                                                 (28, 0, 6, 0, 0, #{room_id}),
                                                                 (29, 2, 6, 0, 0, #{room_id}),
                                                                 (30, 4, 6, 0, 0, #{room_id}),
                                                                 (31, 6, 6, 0, 0, #{room_id}),
                                                                 (32, 8, 6, 0, 0, #{room_id});
""")
    void insertAll(@Param("room_id") int roomId);
}

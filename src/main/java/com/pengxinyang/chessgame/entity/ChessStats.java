package com.pengxinyang.chessgame.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChessStats {
    @TableId
    private int cid;//棋子id
    private int x;//坐标x
    private int y;//坐标y
}

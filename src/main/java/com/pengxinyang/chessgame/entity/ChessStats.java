package com.pengxinyang.chessgame.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChessStats {
    private int cid;//棋子id
    private int x;//坐标x
    private int y;//坐标y
}

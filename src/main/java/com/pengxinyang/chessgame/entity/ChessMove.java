package com.pengxinyang.chessgame.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//类似于chatDetailed
public class ChessMove {
    private Integer cid; // 棋子ID
    private Integer roomId;//在哪个房间下棋
    private Integer fromX; // 原来的X坐标
    private Integer fromY; // 原来的Y坐标
    private Integer toX; // 目标X坐标
    private Integer toY; // 目标Y坐标
    private Integer postId;//是谁在下棋
    private Integer acceptId;//是谁在接棋
    private Integer isJiangJun;//是否将军
    private Integer eat;//代表是否吃子，1吃0不吃
}

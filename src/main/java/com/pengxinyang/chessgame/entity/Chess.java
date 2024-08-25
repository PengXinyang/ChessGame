package com.pengxinyang.chessgame.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chess {
    @TableId
    private Integer cid;//棋子id
    private Integer uid;//棋子属于哪个人
    private String chessName;//哪种棋子
    private Integer color;//红方黑方，1 是 红方
}

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
    private Integer cid;//棋子id
    private Integer x;//坐标x
    private Integer y;//坐标y
    private Integer color;//颜色
    private Integer ate;//标记是否被吃掉。1 是已经被吃掉
}

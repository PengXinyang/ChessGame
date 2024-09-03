package com.pengxinyang.chessgame.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @TableId(type = IdType.AUTO)
    private Integer roomId;
    private String roomName;
    private Integer uidRed;//红方uid
    private Integer uidBlack;//黑方uid
}

package com.pengxinyang.chessgame.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommandType {
    /**
     * 建立连接
     */
    CONNETION(100),

    /**
     * 聊天功能 发送
     */
    CHAT_SEND(101),

    /**
     * 聊天功能 撤回
     */
    CHAT_WITHDRAW(102),

    /**
     * 通知功能，发送
     */

    NOTICE(188),
    /**
     * 传输象棋数据
     */
    Chess(190),
    /**
     * 传输加入房间
     */
    Room(195),

    ERROR(-1),
    ;

    private final Integer code;

    public static CommandType match(Integer code) {
        for (CommandType value: CommandType.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ERROR;
    }
}

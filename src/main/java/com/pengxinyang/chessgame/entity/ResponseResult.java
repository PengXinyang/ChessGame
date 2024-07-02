package com.pengxinyang.chessgame.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 自定义响应对象
public class ResponseResult {
    private int code = 200;//200表示正常
    private String message = "正常";
    private Object data;
}
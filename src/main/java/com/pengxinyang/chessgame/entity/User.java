package com.pengxinyang.chessgame.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private int uid;//用户id
    private String account;//账户
    private String password;//密码
    private String name;//用户名
    private String description;//个性签名
    private int experience;//经验
    private int threshold;//经验阈值
    private int level;//等级
    private int state;//用户状态,0表示正常，1表示已删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Beijing")
    private Date createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Beijing")
    private Date deleteDate;
}

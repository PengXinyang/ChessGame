package com.pengxinyang.chessgame.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private Integer uid;//用户id
    private String account;//账户
    private String password;//密码
    private String name;//用户名
    private String avatar;//头像链接
    private String background;//背景链接
    private String description;//个性签名
    private Integer experience;//经验
    private Integer threshold;//经验阈值
    private Integer level;//等级
    private Integer state;//用户状态,0表示正常，1表示已删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Beijing")
    private Date createDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Beijing")
    private Date deleteDate;
    private Integer loginState;//登录状态，0表示未登录，1表示已登录
}

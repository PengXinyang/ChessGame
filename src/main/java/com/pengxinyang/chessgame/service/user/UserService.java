package com.pengxinyang.chessgame.service.user;

import com.pengxinyang.chessgame.entity.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    /**
     * 用户注册
     * @param account 账户
     * @param password 密码
     * @param confirmPassword 确认密码
     */
    ResponseResult register(String account, String password, String confirmPassword);

    /**
     * 用户注销
     * @param uid 用户id
     */
    ResponseResult unsubscribe(int uid);

    /**
     * 用户登录
     * @param account 账户
     * @param password 密码
     */
    ResponseResult login(String account, String password);

    /**
     * 用户登出
     */
    ResponseResult logout(int uid);

    /**
     * 重置密码
     * @param account 账户
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param confirmNewPassword 确认密码
     */
    ResponseResult updatePassword(String account, String oldPassword, String newPassword, String confirmNewPassword);

    /**
     * 获取用户信息
     * @param uid 用户id
     */
    ResponseResult getUserInfo(int uid);
    /**
     * 更新用户信息
     * @param name 用户名称
     * @param description 个人介绍
     */
    ResponseResult updateUserInfo(int uid, String name, String description);

    ResponseResult updateUserAvatar(Integer uid, MultipartFile file) throws IOException;

    /**
     * 升级
     * @param uid 用户id
     * @param experience 用户修改的经验值
     * @param isAdd 是增加经验还是减少经验
     */
    ResponseResult updateUserExperience(int uid, int experience, boolean isAdd);
}

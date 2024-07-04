package com.pengxinyang.chessgame.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.entity.User;
import com.pengxinyang.chessgame.mapper.UserMapper;
import com.pengxinyang.chessgame.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param account  账户
     * @param password 密码
     * @param confirmPassword 确认密码
     */
    @Override
    public ResponseResult register(String account, String password, String confirmPassword) {
        ResponseResult result = new ResponseResult();
        User user = new User();
        if(account == null || account.isEmpty() || password == null || password.isEmpty()){
            result.setCode(403);
            result.setMessage("账号和密码不能为空");
            return result;
        }
        if(account.length()>20||password.length()>20){
            result.setCode(403);
            result.setMessage("账号和密码长度请不要超过20");
            return result;
        }
        if(!password.equals(confirmPassword)){
            result.setCode(403);
            result.setMessage("输入的密码和确认密码不一致，请重新输入");
            return result;
        }
        account = account.trim();//删除空白符
        password = password.trim();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User queryUser = userMapper.selectOne(queryWrapper);
        if(queryUser != null){
            if(queryUser.getState() == 0){
                result.setCode(403);
                result.setMessage("账户已存在，请重新更换账户");
                return result;
            }
            else{
                UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("account", account);
                updateWrapper.set("password", password);
                updateWrapper.set("state", 0);
                updateWrapper.set("create_date",new Date());
                updateWrapper.set("delete_date",null);
                userMapper.update(updateWrapper);
                result.setCode(200);
                result.setMessage("用户注册成功");
            }
        }
        user.setAccount(account);
        user.setPassword(password);
        user.setCreateDate(new Date());
        userMapper.insert(user);
        result.setMessage("用户注册成功");
        return result;
    }

    /**
     * 用户注销
     *
     * @param uid 用户id
     */
    @Override
    public ResponseResult unsubscribe(int uid) {
        ResponseResult result = new ResponseResult();
        User user = userMapper.selectById(uid);
        if(user == null){
            result.setCode(403);
            result.setMessage("用户不存在");
            return result;
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("state", 1);
        updateWrapper.set("delete_date",new Date());
        userMapper.update(updateWrapper);
        result.setMessage("已删除账户");
        return result;
    }

    /**
     * 用户登录
     *
     * @param account  账户
     * @param password 密码
     */
    @Override
    public ResponseResult login(String account, String password) {
        ResponseResult result = new ResponseResult();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("account", account);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            result.setCode(403);
            result.setMessage("该用户不存在，请先注册");
            return result;
        }
        if (!user.getPassword().equals(password)) {
            result.setCode(403);
            result.setMessage("密码错误，请重新输入");
            return result;
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account);
        updateWrapper.set("login_state", 1);
        userMapper.update(updateWrapper);
        user.setLoginState(1);
        result.setData(user);
        result.setMessage("登录成功");
        return result;
    }

    /**
     * 用户登出
     */
    @Override
    public ResponseResult logout(int uid) {
        ResponseResult result = new ResponseResult();
        User user = userMapper.selectById(uid);
        if(user == null){
            result.setCode(403);
            result.setMessage("用户不存在");
            return result;
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("login_state", 0);
        userMapper.update(updateWrapper);
        user.setLoginState(0);
        result.setData(user);
        result.setMessage("登出成功");
        return result;
    }

    /**
     * 重置密码
     *
     * @param account     账户
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    public ResponseResult updatePassword(String account, String oldPassword, String newPassword, String confirmNewPassword) {
        ResponseResult result = new ResponseResult();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            result.setCode(403);
            result.setMessage("用户不存在");
            return result;
        }
        if(!user.getPassword().equals(oldPassword)){
            result.setCode(403);
            result.setMessage("输入原密码有误，请重新输入");
            return result;
        }
        if(!newPassword.equals(confirmNewPassword)){
            result.setCode(403);
            result.setMessage("两次输入新密码不一致，请重新输入");
            return result;
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", account);
        updateWrapper.set("password", newPassword);
        userMapper.update(updateWrapper);
        result.setMessage("修改密码成功");
        return result;
    }

    /**
     * 获取用户信息
     *
     * @param uid 用户id
     */
    @Override
    public ResponseResult getUserInfo(int uid) {
        ResponseResult result = new ResponseResult();
        User user = userMapper.selectById(uid);
        if(user == null){
            result.setCode(403);
            result.setMessage("用户不存在");
            return result;
        }
        Map<String,Object>map = new HashMap<>();
        map.put("uid",uid);
        map.put("account",user.getAccount());
        map.put("name",user.getName());
        map.put("description",user.getDescription());
        map.put("experience",user.getExperience());
        map.put("threshold",user.getThreshold());
        map.put("level",user.getLevel());
        map.put("state",user.getState());
        map.put("createDate",user.getCreateDate());
        map.put("deleteDate",user.getDeleteDate());
        result.setData(map);
        result.setMessage("已获取用户信息");
        return result;
    }

    /**
     * 更新用户信息
     *
     * @param name        用户名称
     * @param description 个人介绍
     */
    @Override
    public ResponseResult updateUserInfo(int uid, String name, String description) {
        ResponseResult result = new ResponseResult();
        User user = userMapper.selectById(uid);
        if(user == null || user.getState() == 1){
            result.setCode(403);
            result.setMessage("用户不存在或已删除");
            return result;
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("name", name);
        updateWrapper.set("description", description);
        userMapper.update(updateWrapper);
        result.setMessage("更新成功！");
        return result;
    }

    /**
     * 升级
     *
     * @param uid        用户id
     * @param experience 用户修改的经验值
     * @param isAdd      是增加经验还是减少经验
     */
    @Override
    public ResponseResult updateUserExperience(int uid, int experience, boolean isAdd) {
        ResponseResult result = new ResponseResult();
        User user = userMapper.selectById(uid);
        if(user == null || user.getState() == 1){
            result.setCode(403);
            result.setMessage("用户不存在或已删除");
            return result;
        }
        int exp = experience;
        if(isAdd) exp+=experience;
        else exp-=experience;
        int level = user.getLevel();
        int threshold = level*100;//经验阈值等于等级乘以100
        if(exp>=0){
            while(exp>=threshold){//每一级经验阈值不同，所以只能循环处理
                exp-=threshold;
                level++;
                threshold = level*100;
            }
        }
        else{
            while(exp<0){
                level--;
                threshold = level*100;
                exp+=threshold;
            }
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("experience", exp);
        updateWrapper.set("level", level);
        updateWrapper.set("threshold", threshold);
        userMapper.update(updateWrapper);
        result.setMessage("更新经验值和等级成功");
        return result;
    }
}

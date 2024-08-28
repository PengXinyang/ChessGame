package com.pengxinyang.chessgame.service.impl.user;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.entity.User;
import com.pengxinyang.chessgame.im.IMServer;
import com.pengxinyang.chessgame.mapper.UserMapper;
//import com.pengxinyang.chessgame.service.tools.CurrentUser;
import com.pengxinyang.chessgame.service.user.UserService;
import com.pengxinyang.chessgame.utils.JsonWebTokenTool;
import com.pengxinyang.chessgame.utils.OssTool;
import com.pengxinyang.chessgame.utils.RedisTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import io.netty.channel.Channel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Value("${oss.bucketUrl}")
    private String OSS_BUCKET_URL;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTool redisTool;

    @Autowired
    private JsonWebTokenTool jsonWebTokenTool;

//    @Autowired
//    private CurrentUser currentUser;

    @Qualifier("taskExecutor")
    @Autowired
    private Executor taskExecutor;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OssTool ossTool;

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
        user.setName(account);
        try{
            userMapper.insert(user);
        }catch(Exception e){
            e.printStackTrace();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("account", account);
        map.put("password", password);
        map.put("name",account);
        map.put("state", 1);
        map.put("create_date",new Date());
        map.put("delete_date",null);
        result.setMessage("用户注册成功");
        result.setData(map);
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
        //将uid封装成一个jwttoken，同时token也会被缓存到redis中
        String token = jsonWebTokenTool.createToken(user.getUid().toString(), "user");
        user.setLoginState(1);
        try {
            // 把完整的用户信息存入redis，时间跟token一样，注意单位
            // 这里缓存的user信息建议只供读取uid用，其中的状态等非静态数据可能不准，所以 redis另外存值
            String jsonString = JSON.toJSONString(user);
            redisTemplate.opsForValue().set(
                    "security:user:" + user.getUid(),
                    jsonString,
                    60L * 60 * 24 * 2,
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.error("存储redis数据失败");
            throw e;
        }
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("token", token);
        userMap.put("user", user);
        userMap.put("user_name",user.getName());
        result.setData(userMap);
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
        Integer LoginUserId = uid;
        // 清除redis中该用户的登录认证数据
        //1注释Redis
        redisTool.deleteValue("token:user:" + LoginUserId);
        redisTool.deleteValue("security:user:" + LoginUserId);
        redisTool.deleteSetMember("login_member", LoginUserId);   // 从在线用户集合中移除
        // 清除全部在聊天窗口的状态,删除指定前缀的所有key
        // 获取以指定前缀开头的所有键
        Set<String> userKeys = redisTemplate.keys("message:" + LoginUserId + ":" + "*");
        // 删除匹配的键
        if (userKeys != null && !userKeys.isEmpty()) {
            redisTemplate.delete(userKeys);
        }

        // 断开全部该用户的channel 并从 userChannel 移除该用户
        Set<Channel> userChannels = IMServer.userChannel.get(LoginUserId);
        if(userChannels == null){ return result;}
        else{
            for (Channel channel : userChannels) {
                try {
                    channel.close().sync(); // 等待通道关闭完成
                } catch (InterruptedException e) {
                    // 处理异常，如果有必要的话
                    e.printStackTrace();
                }
            }
            IMServer.userChannel.remove(LoginUserId);
        }
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
        map.put("avatar",user.getAvatar());
        map.put("background",user.getBackground());
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
     * 更新用户头像
     * @param uid
     * @param file
     * @return
     * @throws IOException
     */
    @Override
    public ResponseResult updateUserAvatar(Integer uid, MultipartFile file) throws IOException {
        ResponseResult responseResult = new ResponseResult();
        // 保存封面到OSS，返回URL
        String headPortrait_url = ossTool.uploadImage(file, "headPortrait");
        // 查旧的头像地址
        User user = userMapper.selectById(uid);
        // 先更新数据库
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid).set("head_portrait", headPortrait_url);
        userMapper.update(null, updateWrapper);
        CompletableFuture.runAsync(() -> {
            //1注释Redis
            redisTool.deleteValue("user:" + uid);  // 删除redis缓存
            // 如果就头像不是初始头像就去删除OSS的源文件
            if (user.getAvatar().startsWith(OSS_BUCKET_URL)) {
                String filename = user.getAvatar().substring(OSS_BUCKET_URL.length());
                ossTool.deleteFiles(filename);
            }
        }, taskExecutor);
        responseResult.setData(headPortrait_url);
        return responseResult;
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

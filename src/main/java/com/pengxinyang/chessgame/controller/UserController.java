package com.pengxinyang.chessgame.controller;

import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    public static boolean isValidPassword(String password) {
        // 正则表达式: ^ 表示开始, [a-zA-Z0-9]{6,20} 表示6到20位的字母或数字, $ 表示结束
        String regex = "^[a-zA-Z0-9]{6,20}$";

        // 使用 Pattern 和 Matcher 类进行正则表达式匹配
        return password.matches(regex);
    }
    /**
     * 用户注册
     * @param map 账户map
     */
    @PostMapping("/user/register")
    public ResponseResult register(@RequestBody Map<String, Object> map){
        ResponseResult result = new ResponseResult();
        String account = (String) map.get("account");
        String password = (String) map.get("password");
        String confirmPassword = (String) map.get("confirmPassword");
        if(!isValidPassword(password) || !isValidPassword(confirmPassword)){
            result.setMessage("密码格式不正确，请重新输入");
            result.setCode(500);
            return result;
        }
        try{
            return userService.register(account, password, confirmPassword);
        }
        catch(Exception e){
            result.setMessage("出现未知异常");
            result.setCode(500);
            return result;
        }
    }
    /**
     * 用户登录
     * @param map 用户map
     */
    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody Map<String, String> map){
        ResponseResult result = new ResponseResult();
        String account = map.get("account");
        String password = map.get("password");
        if(!isValidPassword(password)){
            result.setCode(500);
            result.setMessage("密码格式不正确");
            return result;
        }
        try{
            return userService.login(account, password);
        }
        catch(Exception e){
            result.setMessage("出现未知异常");
            result.setCode(500);
            return result;
        }
    }
    /**
     * 用户登出
     *
     */
    @PostMapping("/user/logout")
    public ResponseResult logout(@RequestParam("uid") int uid){
        ResponseResult result = new ResponseResult();
        try{
            return userService.logout(uid);
        }
        catch(Exception e){
            result.setMessage("出现未知异常");
            result.setCode(500);
            return result;
        }
    }
    /**
     * 重置密码
     */
    @PostMapping("/user/update/password")
    public ResponseResult updatePassword(@RequestBody Map<String, String> map){
        ResponseResult result = new ResponseResult();
        String account = map.get("account");
        String oldPassword = map.get("oldPassword");
        String newPassword = map.get("newPassword");
        String confirmPassword = map.get("confirmPassword");
        if(!isValidPassword(oldPassword) || !isValidPassword(newPassword)){
            result.setCode(500);
            result.setMessage("密码格式有误");
            return result;
        }
        try{
            return userService.updatePassword(account, oldPassword, newPassword, confirmPassword);
        }
        catch(Exception e){
            result.setMessage("出现未知错误");
            result.setCode(500);
            return result;
        }
    }
    /**
     * 获取用户信息
     */
    @GetMapping("/user/get/information")
    public ResponseResult getUserInformation(@RequestParam("uid") int uid){
        try{
            return userService.getUserInfo(uid);
        }
        catch(Exception e){
            return new ResponseResult(500,"出现未知错误",null);
        }
    }
    /**
     * 更新用户信息
     */
    @PostMapping("/user/update/information")
    public ResponseResult updateUserInformation(@RequestParam("uid") int uid,
                                                @RequestParam("name") String name,
                                                @RequestParam("description") String description){
        try{
            return userService.updateUserInfo(uid,name,description);
        }
        catch(Exception e){
            return new ResponseResult(500,"出现未知错误",null);
        }
    }
    /**
     * 升级
     */
    @PostMapping("/user/update/level")
    public ResponseResult updateUserLevel(@RequestParam("uid") int uid,
                                          @RequestParam("experience") int experience,
                                          @RequestParam("isAdd") int isAdd){
        boolean IsAdd = isAdd != 0;
        try{
            return userService.updateUserExperience(uid,experience,IsAdd);
        }
        catch(Exception e){
            return new ResponseResult(500,"出现未知错误",null);
        }
    }
}

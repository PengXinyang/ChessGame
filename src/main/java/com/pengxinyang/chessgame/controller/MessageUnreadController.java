package com.pengxinyang.chessgame.controller;

import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.service.message.MessageUnreadService;
import com.pengxinyang.chessgame.service.tools.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageUnreadController {

    @Autowired
    private MessageUnreadService messageUnreadService;

    @Autowired
    private CurrentUser currentUser;

    /**
     * 获取当前用户全部消息未读数
     * @return
     */
    @GetMapping("/msg-unread/all")
    public ResponseResult getMsgUnread() {
        //System.out.println("已进入getMsgUnread");
        Integer uid = currentUser.getUserId();
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(messageUnreadService.getUnreadByUid(uid));
        return responseResult;
    }

    /**
     * 清除某一列的未读消息提示
     * @param column    msg_unread表列名 "reply"/"at"/"love"/"system"/"whisper"/"dynamic"
     */
    @PostMapping("/msg-unread/clear")
    public void clearUnread(@RequestParam("column") String column) {
        Integer uid = currentUser.getUserId();
        messageUnreadService.clearOneUnread(uid, column);
    }
}

package com.pengxinyang.chessgame.service.impl.message;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pengxinyang.chessgame.entity.ChatDetailed;
import com.pengxinyang.chessgame.mapper.ChatDetailedMapper;
import com.pengxinyang.chessgame.service.message.ChatDetailedService;
import com.pengxinyang.chessgame.utils.RedisTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ChatDetailedServiceImpl implements ChatDetailedService {
    @Autowired
    private ChatDetailedMapper chatDetailedMapper;
    @Autowired
    private RedisTool redisTool;

    /**
     * 获取当前聊天的10条消息
     *
     * @param post_id   发消息者UID（对方）
     * @param accept_id 收消息者UID（自己）
     * @param offset    偏移量 从哪条开始数（已经查过了几条）
     * @return 消息列表以及是否还有更多 { messageList: List, moreMessage: boolean }
     */
    @Override
    public Map<String, Object> getMessage(Integer post_id, Integer accept_id, Long offset) {
        Map<String, Object> map = new HashMap<>();
        //1注释Redis
        String key = "chat_detailed_zset:" + post_id + ":" + accept_id;
        if (offset + 10 < redisTool.getZSetNumber(key)) {
            map.put("more", true);
        } else {
            map.put("more", false);
        }
        Set<Object> set = redisTool.reverseRange(key, offset, offset + 9);
        // 没有数据则返回空列表
        if (set == null || set.isEmpty()) {
            map.put("list", Collections.emptyList());
            return map;
        }
        QueryWrapper<ChatDetailed> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", set);
        if(offset >= Integer.MAX_VALUE){
            //不要超过int最大值
            map.put("messageList", Collections.emptyList());
            map.put("moreMessage", true);
            return map;
        }
        List<ChatDetailed> chatDetailedList = chatDetailedMapper.selectAllChatDetailed(post_id,accept_id);
        if(chatDetailedList == null || chatDetailedList.isEmpty()){
            map.put("messageList", Collections.emptyList());
            map.put("moreMessage", false);
            return map;
        }
        if(offset+10 < chatDetailedList.size()){
            map.put("moreMessage", true);
            List<ChatDetailed> littleList = IntStream.range(offset.intValue(),10+offset.intValue()).mapToObj(chatDetailedList::get).collect(Collectors.toList());
            map.put("messageList", littleList);
        }
        else {
            map.put("moreMessage", false);
            map.put("list", chatDetailedList);
        }
        return map;
    }

    /**
     * 删除单条消息记录
     * @param id 消息记录的id
     * @param uid   当前登录用户的UID（自己）
     * @return  成功/失败
     */
    @Override
    public boolean deleteChatDetail(Integer id, Integer uid) {
        try {
            ChatDetailed chatDetailed = chatDetailedMapper.selectById(id);
            // 查询 查不到数据
            if (chatDetailed == null) return false;
            UpdateWrapper<ChatDetailed> updateWrapper = new UpdateWrapper<>();
            if (chatDetailed.getPostId().equals(uid)) {
                // 如果删除的消息是自己发送的
                updateWrapper.eq("id", id).setSql("post_del = 1");
                chatDetailedMapper.update(null, updateWrapper);
                //1注释Redis
                String key = "chat_detailed_zset:" + chatDetailed.getAcceptId() + ":" + uid;
                redisTool.deleteZSetMember(key, id);
                return true;
            } else if (chatDetailed.getAcceptId().equals(uid)) {
                // 如果自己是接收方
                updateWrapper.eq("id", id).setSql("accept_del = 1");
                chatDetailedMapper.update(null, updateWrapper);
                //1注释Redis
                String key = "chat_detailed_zset:" + chatDetailed.getPostId() + ":" + uid;
                redisTool.deleteZSetMember(key, id);
                return true;
            } else return false;
        } catch (Exception e) {
            log.error("删除消息记录时出错了{}", e.getMessage());
            return false;
        }
    }
}

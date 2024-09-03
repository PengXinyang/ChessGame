package com.pengxinyang.chessgame.im.handler;

import com.alibaba.fastjson2.JSONObject;
import com.pengxinyang.chessgame.entity.ChessMove;
import com.pengxinyang.chessgame.entity.IMResponse;
import com.pengxinyang.chessgame.im.IMServer;
import com.pengxinyang.chessgame.service.room.RoomService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class ChessHandler {

    private static RoomService roomService;
    private static Executor taskExecutor;

    @Autowired
    private void setDependencies(RoomService roomService, @Qualifier("taskExecutor") Executor taskExecutor) {
        ChessHandler.roomService = roomService;
        ChessHandler.taskExecutor = taskExecutor;
    }

    // 处理玩家移动棋子的请求
    static void ChessHandle(ChannelHandlerContext ctx, TextWebSocketFrame tx) {
        try {
            ChessMove move = JSONObject.parseObject(tx.text(), ChessMove.class);
            // 从channel中获取当前用户id 封装写库
            Integer user_id = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            // 更新游戏状态
            //理论上，棋子移动先通过url先进行更新，更新完毕后再获取棋子状态，然后同步给玩家
            move.setPostId(user_id);
            Map<String,Object>map = new HashMap<>();
            map.put("postId",user_id);
            map.put("acceptId",move.getAcceptId());
            map.put("cid",move.getCid());
            map.put("fromX",move.getFromX());
            map.put("fromY",move.getFromY());
            map.put("toX",move.getToX());
            map.put("toY",move.getToY());
            map.put("roomId",move.getRoomId());
            map.put("isJiangJun",move.getIsJiangJun());
            map.put("eat",move.getEat());
            // 同步给房间内所有玩家
            Set<Channel> players = IMServer.userChannel.get(user_id);
            for (Channel player : players) {
                player.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(map)));
            }
            // 发给对方的全部channel
            Set<Channel> to = IMServer.userChannel.get(move.getAcceptId());
            System.out.println("to is " + to + "AnotherId" + move.getAcceptId());
            if (to != null) {
                for (Channel channel : to) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(map)));
                }
            }

        } catch (Exception e) {
            log.error("处理玩家移动棋子时出错：" + e);
            ctx.channel().writeAndFlush(IMResponse.error("处理棋子移动时出错 Σ(ﾟдﾟ;)"));
        }
    }

}
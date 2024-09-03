package com.pengxinyang.chessgame.im.handler;

import com.alibaba.fastjson2.JSONObject;
import com.pengxinyang.chessgame.entity.IMResponse;
import com.pengxinyang.chessgame.entity.Room;
import com.pengxinyang.chessgame.im.IMServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class RoomHandler extends WebSocketHandler{
    public static void RoomHandle(ChannelHandlerContext ctx, TextWebSocketFrame tx){
        try{
            Room room = JSONObject.parseObject(tx.text(), Room.class);
            sendRoomMessage(room);
        }catch (Exception e) {
            log.error("处理玩家加入房间时出错：" + e);
            ctx.channel().writeAndFlush(IMResponse.error("处理加入房间时出错 Σ(ﾟдﾟ;)"));
        }
    }

    public static void sendRoomMessage(Room room) {
        Map<String,Room> map = new HashMap<>();
        map.put("房间已准备好",room);
        // 同步给房间内所有玩家
        Set<Channel> players = IMServer.userChannel.get(room.getUidRed());
        System.out.println("roomHandler: "+IMServer.userChannel);
        for (Channel player : players) {
            player.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(map)));
        }
        // 发给对方的全部channel
        Set<Channel> to = IMServer.userChannel.get(room.getUidBlack());
        if (to != null) {
            for (Channel channel : to) {
                channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(map)));
            }
        }
    }
}

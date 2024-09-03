package com.pengxinyang.chessgame.service.room;

import com.pengxinyang.chessgame.entity.ChessStats;
import com.pengxinyang.chessgame.entity.ResponseResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoomService {
    /**
     * 根据房间id获取当前房间 棋子的状态
     * @param roomId 房间id
     * @return 数组ChessStats
     */
    List<ChessStats> getChessStatsByRoomId(int roomId);

    /**
     * 创建房间
     * @return 响应对象
     */
    ResponseResult createRoom(String roomName);
    /**
     * 根据名称获取roomId
     */
    ResponseResult getRoomByName(String roomName);

    Integer getRoomIdByName(String roomName);

    /**
     * 根据roomId加入游戏
     * @param roomId 房间id
     * @param uid 用户id
     * @return 响应结果
     */
    ResponseResult joinRoom(int roomId, int uid);
}

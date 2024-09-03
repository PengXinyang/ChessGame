package com.pengxinyang.chessgame.service.impl.room;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pengxinyang.chessgame.entity.ChessStats;
import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.entity.Room;
import com.pengxinyang.chessgame.entity.User;
import com.pengxinyang.chessgame.im.handler.RoomHandler;
import com.pengxinyang.chessgame.mapper.ChessStatsMapper;
import com.pengxinyang.chessgame.mapper.RoomMapper;
import com.pengxinyang.chessgame.mapper.UserMapper;
import com.pengxinyang.chessgame.service.room.RoomService;
import com.pengxinyang.chessgame.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {
    @Autowired
    private ChessStatsMapper chessStatsMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    /**
     * 根据房间id获取当前房间 棋子的状态
     *
     * @param roomId 房间id
     * @return 数组ChessStats
     */
    @Override
    public List<ChessStats> getChessStatsByRoomId(int roomId) {
        QueryWrapper<ChessStats> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId);
        return chessStatsMapper.selectList(queryWrapper);
    }

    /**
     * 创建房间
     *
     * @return 响应对象
     */
    @Override
    public ResponseResult createRoom(String roomName) {
        ResponseResult result = new ResponseResult();
        Room room = new Room();
        Integer roomId = getRoomIdByName(roomName);
        if (roomId != null) {
            result.setCode(500);
            result.setMessage("创建房间失败,房间名字已经存在");
            return result;
        }
        room.setRoomName(roomName);
        try{
            roomMapper.insert(room);
        }catch(Exception e){
            result.setCode(500);
            result.setMessage("创建房间失败 "+e.getMessage());
        }
        return result;
    }

    @Override
    public Integer getRoomIdByName(String roomName) {
        QueryWrapper<Room> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_name", roomName);
        Room room = roomMapper.selectOne(queryWrapper);
        if(room==null) return null;
        return room.getRoomId();
    }

    /**
     * 根据名称模糊匹配相关房间和用户
     *
     * @param roomName 名字
     */
    @Override
    public ResponseResult getRoomByName(String roomName) {
        ResponseResult result = new ResponseResult();
        List<Room> rooms = roomMapper.findByNameContaining(roomName);
        if(rooms == null || rooms.isEmpty()) {
            result.setCode(500);
            result.setMessage("没有找到合适的房间");
            return result;
        }
        List<Integer> roomIds = new ArrayList<>();
        List<String> roomNames = new ArrayList<>();
        List<Integer> redUids = new ArrayList<>();
        List<String> redNames = new ArrayList<>();
        List<Integer> blackUids = new ArrayList<>();
        List<String> blackNames = new ArrayList<>();
        for(Room room : rooms) {
            roomIds.add(room.getRoomId());
            roomNames.add(room.getRoomName());
            if(room.getUidRed() == null){
                //红方没有加入游戏,-1代表没有人
                redUids.add(-1);
                redNames.add("无");
            }
            else{
                try{
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("uid",room.getUidRed());
                    User redUser = userMapper.selectOne(queryWrapper);
                    redUids.add(redUser.getUid());
                    redNames.add(redUser.getName());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(room.getUidBlack() == null){
                blackUids.add(-1);
                blackNames.add("无");
            }
            else{
                try{
                    QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("uid",room.getUidBlack());
                    User blackUser = userMapper.selectOne(queryWrapper1);
                    blackUids.add(blackUser.getUid());
                    blackNames.add(blackUser.getName());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("room_ids",roomIds);
        map.put("room_id",getRoomIdByName(roomName));
        map.put("room_name",roomNames);
        map.put("red_uid",redUids);
        map.put("red_name",redNames);
        map.put("black_uid",blackUids);
        map.put("black_name",blackNames);
        result.setData(map);
        return result;
    }

    /**
     * 根据roomId加入游戏
     *
     * @param roomId 房间id
     * @param uid    用户id
     * @return 响应结果
     */
    @Override
    public ResponseResult joinRoom(int roomId, int uid) {
        ResponseResult responseResult = new ResponseResult();
        Room room = roomMapper.selectById(roomId);
        if(room == null){
            responseResult.setCode(500);
            responseResult.setMessage("没有这个房间");
            return responseResult;
        }
        if(room.getUidRed()==null){
            room.setUidRed(uid);
            UpdateWrapper<Room> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("room_id", roomId);
            updateWrapper.set("uid_red", uid);
            roomMapper.update(null, updateWrapper);
            responseResult.setData(room);
        }else if(room.getUidBlack()==null){
            room.setUidBlack(uid);
            UpdateWrapper<Room> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("room_id", roomId);
            updateWrapper.set("uid_black", uid);
            roomMapper.update(null, updateWrapper);
            responseResult.setData(room);
            RoomHandler.sendRoomMessage(room);
        }else{
            responseResult.setCode(500);
            responseResult.setMessage("人员已满");
        }
        return responseResult;
    }
}

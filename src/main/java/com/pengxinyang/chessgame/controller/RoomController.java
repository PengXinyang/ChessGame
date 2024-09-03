package com.pengxinyang.chessgame.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.entity.Room;
import com.pengxinyang.chessgame.mapper.RoomMapper;
import com.pengxinyang.chessgame.service.room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomMapper roomMapper;

    @PostMapping("/create/room")
    public ResponseResult createRoom(@RequestParam("room_name") String room_name) {
        return roomService.createRoom(room_name);
    }
    @GetMapping("/get/room_id/by_name")
    public ResponseResult getRoomByName(@RequestParam("name") String name) {
        return roomService.getRoomByName(name);
    }
    @PostMapping("/join/room")
    public ResponseResult joinRoom(@RequestParam("room_name") String room_name,
                                   @RequestParam("uid") Integer uid) {
        System.out.println("进入加入房间代码");
        Integer room_id = roomService.getRoomIdByName(room_name);
        return roomService.joinRoom(room_id, uid);
    }
    @PostMapping("/exit/room")
    public ResponseResult exitRoom(@RequestParam("room_name") String room_name,
                                   @RequestParam("uid") Integer uid) {
        ResponseResult result = new ResponseResult();
        try{
            Integer room_id = roomService.getRoomIdByName(room_name);
            Room room = roomMapper.selectById(room_id);
            if(room==null) {
                result.setCode(500);
                result.setMessage("没有可以退出房间的选项");
                return result;
            }
            if(!Objects.equals(room.getUidRed(), uid) && !Objects.equals(room.getUidBlack(), uid)){
                result.setCode(500);
                result.setMessage("没有可以退出房间的选项");
                return result;
            }
            if(Objects.equals(room.getUidRed(), uid)){
                UpdateWrapper<Room> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("room_name", room_name);
                updateWrapper.set("uid_red",null);
                roomMapper.update(null, updateWrapper);
            }
            else if(Objects.equals(room.getUidBlack(), uid)){
                UpdateWrapper<Room> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("room_name", room_name);
                updateWrapper.set("uid_black",null);
                roomMapper.update(null, updateWrapper);
            }
            result.setCode(200);
            result.setMessage("成功退出房间");
            return result;
        }catch(Exception e){
            result.setCode(500);
            result.setMessage(e.getMessage());
            e.printStackTrace();
            return result;
        }
    }
}

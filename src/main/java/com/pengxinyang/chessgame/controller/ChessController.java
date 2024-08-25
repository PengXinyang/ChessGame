package com.pengxinyang.chessgame.controller;

import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.mapper.ChessMapper;
import com.pengxinyang.chessgame.mapper.ChessStatsMapper;
import com.pengxinyang.chessgame.service.chess.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChessController {
    @Autowired
    private ChessService chessService;
    @Autowired
    private ChessMapper chessMapper;
    @Autowired
    private ChessStatsMapper chessStatsMapper;

    /**
     * 游戏开始
     * @param uid_red 红方uid
     * @param uid_black 黑方uid
     * @return 响应对象
     */
    @GetMapping("/game/start")
    public ResponseResult startChessGame(@RequestParam("uid_red") int uid_red,
                                         @RequestParam("uid_black") int uid_black){
        ResponseResult result = new ResponseResult();
        chessService.initGame();
    }

    /**
     * 开始一场单机游戏，不用注册用户和登录
     * @return 响应对象
     */
    @GetMapping("/game/one_person")
    public ResponseResult startOnePersonGame(){
        ResponseResult result = new ResponseResult();
        chessService.initGame();
        return result;
    }

    /**
     * 开始用户对弈模式，不需要登录，也不需要调用人机接口
     */
    @GetMapping("/game/together")
    public ResponseResult startTogether(){
        ResponseResult result = new ResponseResult();
        chessService.initGame();
        return result;
    }
}

package com.pengxinyang.chessgame.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengxinyang.chessgame.entity.ChessStats;
import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.mapper.ChessMapper;
import com.pengxinyang.chessgame.mapper.ChessStatsMapper;
import com.pengxinyang.chessgame.service.chess.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
public class ChessController {
    @Autowired
    private ChessService chessService;
    @Autowired
    private ChessMapper chessMapper;
    @Autowired
    private ChessStatsMapper chessStatsMapper;
    @Qualifier("taskExecutor")
    @Autowired
    private Executor taskExecutor;

    /**
     * 游戏开始，进行初始化
     * @return 响应对象
     */
    @GetMapping("/game/start_chess_game")
    public ResponseResult startChessGame(@RequestParam("room_id") Integer roomId){
        ResponseResult result = new ResponseResult();
        try{
            chessService.initGame(roomId);
        }catch (Exception e){
            result.setCode(500);
            e.printStackTrace();
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 移动棋子
     */
    @PostMapping("/move/chess")
    public ResponseResult moveChess(@RequestParam("cid") Integer cid,
                                    @RequestParam("x") Integer x,
                                    @RequestParam("y") Integer y,
                                    @RequestParam("room_id") Integer roomId){
        ResponseResult result = chessService.ChessMove(cid,x,y, roomId);
        System.out.println(",ove+ "+ result);
        Map<String, List<Integer>> map = chessService.ChessNext(cid, roomId);
        List<Integer> Xlist = map.get("棋子可能的横坐标位置");
        List<Integer> Ylist = map.get("棋子可能的纵坐标位置");
        QueryWrapper<ChessStats> chessStatsQueryWrapper = new QueryWrapper<>();
        chessStatsQueryWrapper.eq("cid", cid).eq("room_id",roomId);
        ChessStats chessStats = chessStatsMapper.selectOne(chessStatsQueryWrapper);
        int color = chessStats.getColor();
        CompletableFuture<?> future1 = CompletableFuture.runAsync(()->{
            int generalCid = 5;
            Map<String,Integer> generalMap = chessService.getXYByCid(generalCid, roomId);
            int gx = generalMap.get("x");
            int gy = generalMap.get("y");
            //查黑方所有棋子，有没有可能下一步走到老将的位置
            for(int cid0=17;cid0<=32;++cid0){
                Map<String, List<Integer>> map0 = chessService.ChessNext(cid0, roomId);
                List<Integer> Xlist0 = map0.get("棋子可能的横坐标位置");
                List<Integer> Ylist0 = map0.get("棋子可能的纵坐标位置");
                for(int i=0;i<Xlist0.size();i++){
                    if(gx == Xlist0.get(i) && gy == Ylist0.get(i)){
                        //可能走到的点有老将
                        result.setMessage("将军");
                    }
                }
            }
        },taskExecutor);
        CompletableFuture<?>future2 = CompletableFuture.runAsync(()->{
            int generalCid0 = 21;
            Map<String,Integer> generalMap0 = chessService.getXYByCid(generalCid0, roomId);
            int gx0 = generalMap0.get("x");
            int gy0 = generalMap0.get("y");
            //查红方所有棋子，有没有可能下一步走到老将的位置
            for(int cid0=1;cid0<=16;++cid0){
                Map<String, List<Integer>> map0 = chessService.ChessNext(cid0, roomId);
                List<Integer> Xlist0 = map0.get("棋子可能的横坐标位置");
                List<Integer> Ylist0 = map0.get("棋子可能的纵坐标位置");
                for(int i=0;i<Xlist0.size();i++){
                    if(gx0 == Xlist0.get(i) && gy0 == Ylist0.get(i)){
                        //可能走到的点有老将
                        result.setMessage("将军");
                    }
                }
            }
        },taskExecutor);
        CompletableFuture.allOf(future1,future2).join();
        return result;
    }

    /**
     * 根据棋子id获取位置
     * @param cid 棋子id
     * @return 响应对象
     */
    @GetMapping("/chess/position")
    public ResponseResult getChessPosition(@RequestParam("cid") Integer cid,
                                           @RequestParam("room_id") Integer roomId){
        ResponseResult result = new ResponseResult();
        Map<String,Integer> map = chessService.getXYByCid(cid, roomId);
        if(map == null){
            result.setCode(404);
            result.setMessage("没有找到棋子");
        }
        else result.setData(map);
        return result;
    }

    @GetMapping("/chess/XYposition")
    public ResponseResult getChessXYPosition(@RequestParam("x") Integer x,
                                             @RequestParam("y") Integer y,
                                             @RequestParam("room_id") Integer roomId){
        ResponseResult result = new ResponseResult();
        ChessStats chessStats = chessService.getStatsByXY(x,y, roomId);
        if(chessStats == null){
            result.setMessage("没有棋子");
        }
        else{
            Map<String,Object> map = new HashMap<>();
            map.put("cid", chessStats.getCid());
            map.put("x", x);
            map.put("y", y);
            map.put("color", chessStats.getColor());
            map.put("ate", chessStats.getAte());
            result.setMessage("有棋子");
            result.setData(map);
        }
        return result;
    }

    /**
     * 判断棋子是否死亡
     */
    @GetMapping("/get/die")
    public ResponseResult getDie(@RequestParam("cid") Integer cid,
                                 @RequestParam("room_id") Integer roomId){
        ResponseResult result = new ResponseResult();
        QueryWrapper<ChessStats> chessStatsQueryWrapper = new QueryWrapper<>();
        chessStatsQueryWrapper.eq("cid", cid);
        ChessStats chessStats = chessStatsMapper.selectOne(chessStatsQueryWrapper);
        if(chessStats == null){
            result.setCode(404);
            result.setMessage("没有找到这枚棋子");
        }
        else result.setData(chessStats.getAte());
        return result;
    }
}

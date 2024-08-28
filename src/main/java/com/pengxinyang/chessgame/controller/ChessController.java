package com.pengxinyang.chessgame.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengxinyang.chessgame.entity.ChessStats;
import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.mapper.ChessMapper;
import com.pengxinyang.chessgame.mapper.ChessStatsMapper;
import com.pengxinyang.chessgame.service.chess.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChessController {
    @Autowired
    private ChessService chessService;
    @Autowired
    private ChessMapper chessMapper;
    @Autowired
    private ChessStatsMapper chessStatsMapper;

    /**
     * 游戏开始，进行初始化
     * @return 响应对象
     */
    @GetMapping("/game/start_chess_game")
    public ResponseResult startChessGame(){
        ResponseResult result = new ResponseResult();
        try{
            chessService.initGame();
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
                                    @RequestParam("y") Integer y){
        /*System.out.println("进入moveChess环节");
        System.out.println("cid:"+cid);
        System.out.println("x:"+x);
        System.out.println("y:"+y);*/
        ResponseResult result = chessService.ChessMove(cid,x,y);
        System.out.println(",ove+ "+ result);
        Map<String, List<Integer>> map = chessService.ChessNext(cid);
        List<Integer> Xlist = map.get("棋子可能的横坐标位置");
        List<Integer> Ylist = map.get("棋子可能的纵坐标位置");
        /*System.out.println("Xlist:"+Xlist);
        System.out.println("Ylist:"+Ylist);*/
        QueryWrapper<ChessStats> chessStatsQueryWrapper = new QueryWrapper<>();
        chessStatsQueryWrapper.eq("cid", cid);
        ChessStats chessStats = chessStatsMapper.selectOne(chessStatsQueryWrapper);
        int color = chessStats.getColor();
        int generalCid = 5;
        if(color == 1){
            //自己是红方，判断能不能对老将 将军
            generalCid = 21;
        }
        Map<String,Integer> generalMap = chessService.getXYByCid(generalCid);
        int gx = generalMap.get("x");
        int gy = generalMap.get("y");
        for(int i=0;i<Xlist.size();i++){
            if(gx == Xlist.get(i) && gy == Ylist.get(i)){
                //可能走到的点有老将
                result.setMessage("将军");
            }
        }
        return result;
    }

    /**
     * 根据棋子id获取位置
     * @param cid 棋子id
     * @return 响应对象
     */
    @GetMapping("/chess/position")
    public ResponseResult getChessPosition(@RequestParam("cid") Integer cid){
        ResponseResult result = new ResponseResult();
        Map<String,Integer> map = chessService.getXYByCid(cid);
        if(map == null){
            result.setCode(404);
            result.setMessage("没有找到棋子");
        }
        else result.setData(map);
        return result;
    }

    @GetMapping("/chess/XYposition")
    public ResponseResult getChessXYPosition(@RequestParam("x") Integer x,
                                             @RequestParam("y") Integer y){
        ResponseResult result = new ResponseResult();
        ChessStats chessStats = chessService.getStatsByXY(x,y);
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
    public ResponseResult getDie(@RequestParam("cid") Integer cid){
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

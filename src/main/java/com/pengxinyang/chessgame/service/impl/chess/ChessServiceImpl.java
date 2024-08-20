package com.pengxinyang.chessgame.service.impl.chess;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengxinyang.chessgame.entity.Chess;
import com.pengxinyang.chessgame.entity.ChessStats;
import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.mapper.ChessMapper;
import com.pengxinyang.chessgame.mapper.ChessStatsMapper;
import com.pengxinyang.chessgame.service.chess.ChessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ChessServiceImpl implements ChessService {
    @Autowired
    private ChessMapper chessMapper;
    @Autowired
    private ChessStatsMapper chessStatsMapper;
    /**
     * 这个接口用于统一棋子的移动方法
     *
     * @param cid 棋子id
     * @return 响应结果，包含下一步能走哪些位置
     */
    @Override
    public Map<String, List<Integer>> ChessNext(int cid) {
        ResponseResult result = new ResponseResult();
        QueryWrapper<Chess> chessQueryWrapper = new QueryWrapper<>();
        chessQueryWrapper.eq("cid", cid);
        Chess chess = chessMapper.selectOne(chessQueryWrapper);
        if (chess == null) {
            result.setCode(404);
            result.setMessage("未找到棋子");
            return null;
        }
        QueryWrapper<ChessStats> chessStatsQueryWrapper = new QueryWrapper<>();
        chessStatsQueryWrapper.eq("cid", cid);
        ChessStats chessStats = chessStatsMapper.selectOne(chessStatsQueryWrapper);
        if (chessStats == null) {
            result.setCode(404);
            result.setMessage("未找到棋子状态");
            return null;
        }
        if(chess.getChessName().equals("将")||chess.getChessName().equals("帅")){
            //老将只能周围走一步
            int x=chessStats.getX();
            int y=chessStats.getY();
        }
    }

    /**
     * 反映棋子移动的函数
     *
     * @param cid 棋子的id
     * @param x
     * @param y
     * @return 响应结果，标记这个棋子走到哪里了
     */
    @Override
    public ResponseResult ChessMove(int cid, int x, int y) {
        ResponseResult result = new ResponseResult();
        Map<String,List<Integer>> nextChess = ChessNext(cid);
        if(nextChess==null){
            result.setCode(404);
            result.setMessage("未找到棋子");
            return result;
        }
        List<Integer> XList = nextChess.get("棋子可能的横坐标位置");
        List<Integer> YList = nextChess.get("棋子可能的纵坐标位置");
        Map<String,Integer> map = new HashMap<>();
        for(int i=0;i<XList.size();i++){
            if(XList.get(i)==x){
                if(YList.get(i)==y){
                    result.setCode(200);
                    result.setMessage("可以移动");
                    map.put("x",x);
                    map.put("y",y);
                    result.setData(map);
                    return result;
                }
            }
        }
        result.setCode(404);
        result.setMessage("没有可以移动的选项");
        result.setData(map);
        return result;
    }

    /**
     * 根据坐标点查找这里有没有棋子,用于判断能否其它棋子能否移动
     *
     * @param x 横坐标
     * @param y 纵坐标
     * @return 棋子状态
     */
    @Override
    public ChessStats getStatsByXY(int x, int y) {
        QueryWrapper<ChessStats> chessStatsQueryWrapper = new QueryWrapper<>();
        chessStatsQueryWrapper.eq("cid", x).eq("y", y);
        return chessStatsMapper.selectOne(chessStatsQueryWrapper);
    }
}

package com.pengxinyang.chessgame.service.chess;

import com.pengxinyang.chessgame.entity.ChessStats;
import com.pengxinyang.chessgame.entity.ResponseResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ChessService {
    /**
     * 这个接口用于统一棋子的移动方法
     *
     * @param cid 棋子id
     * @return MapList，包含下一步能走哪些位置
     */
    Map<String,List<Integer>> ChessNext(int cid);

    /**
     * 反映棋子移动的函数
     *
     * @param cid 棋子的id
     * @param x 移动到x点
     * @param y 移动到y点
     * @return 响应结果，标记这个棋子走到哪里了
     */
    ResponseResult ChessMove(int cid,int x,int y);

    /**
     * 根据坐标点查找这里有没有棋子,用于判断能否其它棋子能否移动
     * @param x 横坐标
     * @param y 纵坐标
     * @return 棋子状态
     */
    ChessStats getStatsByXY(int x,int y);
}

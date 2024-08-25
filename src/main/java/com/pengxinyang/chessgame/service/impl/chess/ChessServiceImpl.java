package com.pengxinyang.chessgame.service.impl.chess;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.pengxinyang.chessgame.entity.Chess;
import com.pengxinyang.chessgame.entity.ChessStats;
import com.pengxinyang.chessgame.entity.ResponseResult;
import com.pengxinyang.chessgame.mapper.ChessMapper;
import com.pengxinyang.chessgame.mapper.ChessStatsMapper;
import com.pengxinyang.chessgame.service.chess.ChessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Map<String, List<Integer>> map = new HashMap<>();
        int x = chessStats.getX();
        int y = chessStats.getY();
        String name = chess.getChessName();
        int color = chess.getColor();
        //处理老将的走法
        map = switch (name) {
            case "将", "帅" -> generalJudge(x, y, color);
            case "士", "仕" -> guardJudge(x, y, color);
            case "相", "象" -> ministerJudge(x, y, color);
            case "马", "馬" -> horseJudge(x, y, color);
            case "车", "車" -> carJudge(x,y,color);
            case "兵", "卒" -> soldierJudge(x,y,color);
            case "炮", "砲" -> cannonJudge(x, y, color);
            default -> map;
        };
        return map;
    }

    /**
     * 反映棋子移动的函数
     *
     * @param cid 棋子的id
     * @param x 移动到x
     * @param y 移动到y
     * @return 响应结果，标记这个棋子走到哪里了
     */
    @Override
    public ResponseResult ChessMove(int cid, int x, int y) {
        ResponseResult result = new ResponseResult();
        Map<String,List<Integer>> nextChess = ChessNext(cid);
        QueryWrapper<Chess> chessQueryWrapper = new QueryWrapper<>();
        chessQueryWrapper.eq("cid", cid);
        Chess chess = chessMapper.selectOne(chessQueryWrapper);
        if(nextChess==null || nextChess.isEmpty()){
            result.setCode(404);
            result.setMessage("未找到棋子");
            return result;
        }
        Map<String,Integer> map = new HashMap<>();
        List<Integer> XList = nextChess.get("棋子可能的横坐标位置");
        List<Integer> YList = nextChess.get("棋子可能的纵坐标位置");
        for(int i=0;i<XList.size();i++){
            if(XList.get(i)==x){
                if(YList.get(i)==y){
                    result.setCode(200);
                    result.setMessage("可以移动");
                    map.put("x",x);
                    map.put("y",y);
                    result.setData(map);
                    ChessStats oldChessStats = getStatsByXY(x,y);
                    if(oldChessStats!=null && !Objects.equals(oldChessStats.getColor(), chess.getColor())){
                        //也就是说，X,Y这个地方有棋子，并且颜色不同，可以吃掉
                        eatChess(oldChessStats);
                    }
                    UpdateWrapper<ChessStats> chessStatsUpdateWrapper = new UpdateWrapper<>();
                    chessStatsUpdateWrapper.eq("cid", cid);
                    chessStatsUpdateWrapper.set("x",x).set("y",y);
                    chessStatsMapper.update(null,chessStatsUpdateWrapper);
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

    /**
     * 吃掉这个棋子
     * @param chessStats 棋子状态
     */
    @Override
    public void eatChess(ChessStats chessStats){
        UpdateWrapper<ChessStats> chessStatsUpdateWrapper = new UpdateWrapper<>();
        chessStatsUpdateWrapper.eq("cid", chessStats.getCid()).set("ate",1);
        chessStatsMapper.update(null,chessStatsUpdateWrapper);
    }

    /**
     * 判断老将能否走到点x，y
     * @param x 横坐标
     * @param y 纵坐标
     * @return 是非
     */
    private boolean generalXY(int x, int y, int color) {
        ChessStats XYStats = getStatsByXY(x, y);
        if(XYStats != null && XYStats.getColor() == color){
            //如果颜色相同就不能走，如果颜色不同就可以吃掉
            return false;
        }
        if(x<3||x>5){return false;}
        return y >= 0 && (y <= 3 || y >= 7) && y <= 9;
    }

    /**
     * 处理老将的判断和移动
     *
     * @param x     老将当前的横坐标
     * @param y     老将当前的纵坐标
     * @param color 红方黑方
     * @return 可能的移动位置map
     */
    private Map<String,List<Integer>> generalJudge(int x,int y,int color){
        Map<String,List<Integer>> map = new HashMap<>();
        List<Integer> Xlist = new ArrayList<>();
        List<Integer> Ylist = new ArrayList<>();
        //老将只能周围走一步
        //x在3-5之间
        if(x<3||x>5) {
            System.out.println("棋子本身有问题，请检查原因");
            return null;
        }
        if(color == 1){
            if(y<0||y>3){
                System.out.println("棋子本身有问题，请检查原因");
                return null;
            }
        }
        else{
            if(y<7||y>9){
                System.out.println("棋子本身有问题，请检查原因");
                return null;
            }
        }
        for(int i=x-1;i<x+2;i++){
            for(int j=y-1;j<y+2;j++){
                if(generalXY(i,j,color)){
                    Xlist.add(i);
                    Ylist.add(j);
                }
            }
        }
        map.put("棋子可能的横坐标位置",Xlist);
        map.put("棋子可能的纵坐标位置",Ylist);
        return map;
    }

    /**
     * 判断士能否走到X，Y
     * 由于士走的点很少，所以之直接判断
     * @param x 横坐标
     * @param y 纵坐标
     * @return 是否
     */
    private boolean guardXY(int x,int y, int color){
        boolean xy = false;
        ChessStats XYStats = getStatsByXY(x, y);
        if(XYStats != null && XYStats.getColor() == color){
            return false;
        }
        if((x==3||x==5) && (y==0 || y==3 || y==7 || y==9)) xy = true;
        else if(x==4 && (y==1 || y == 8)) xy = true;
        return xy;
    }

    /**
     * 返回士可以走的点位
     *
     * @param x     当前横坐标
     * @param y     当前纵坐标
     * @param color 红方黑方
     * @return map
     */
    private Map<String,List<Integer>> guardJudge(int x,int y, int color){
        Map<String,List<Integer>> map = new HashMap<>();
        List<Integer> Xlist = new ArrayList<>();
        List<Integer> Ylist = new ArrayList<>();
        if(color == 1){
            if(x<3||x>5 || y<0 || y>3){
                System.out.println("棋子本身有问题，请检查原因");
                return null;
            }
        }
        else{
            if(x<3||x>5 || y<7 || y>9){
                System.out.println("棋子本身有问题，请检查原因");
                return null;
            }
        }
        //士是斜着走，所以判断四个点即可
        for(int i=x-1;i<x+2;i+=2){
            for (int j=y-1;j<y+2;j+=2){
                if(guardXY(i,j,color)){
                    Xlist.add(i);
                    Ylist.add(j);
                }
            }
        }
        map.put("棋子可能的横坐标位置",Xlist);
        map.put("棋子可能的纵坐标位置",Ylist);
        return map;
    }

    /**
     * 相的走法
     * @param x 横坐标
     * @param y 纵坐标
     * @return 是否
     */
    private boolean ministerXY(int x,int y, int color){
        ChessStats XYStats = getStatsByXY(x, y);
        if(XYStats != null && XYStats.getColor() == color){
            return false;
        }
        if(color == 1 && y>4){
            return false;
        }
        if(color!=1 && y<5) return false;
        return x >= 0 && y >= 0 && x <= 8 && y <= 9;
    }

    /**
     * 相可以走的点位
     * @param x 当前横坐标
     * @param y 当前纵坐标
     * @param color 颜色
     * @return map
     */
    private Map<String,List<Integer>> ministerJudge(int x,int y,int color){
        Map<String,List<Integer>> map = new HashMap<>();
        List<Integer> Xlist = new ArrayList<>();
        List<Integer> Ylist = new ArrayList<>();
        for(int i=x-2;i<x+3;i+=4){
            for(int j=y-2;j<y+3;j+=4){
                //判断是否会憋相眼
                if(ministerXY(x,y,color)){
                    int ox = (i+x)/2,oy = (j+y)/2;
                    QueryWrapper<ChessStats> chessStatsQueryWrapper = new QueryWrapper<>();
                    chessStatsQueryWrapper.eq("x",ox).eq("y",oy);
                    ChessStats chessStats = chessStatsMapper.selectOne(chessStatsQueryWrapper);
                    if(chessStats == null){
                        //不会憋相眼
                        Xlist.add(i);
                        Ylist.add(j);
                    }
                }
            }
        }
        map.put("棋子可能的横坐标位置",Xlist);
        map.put("棋子可能的纵坐标位置",Ylist);
        return map;
    }

    /**
     * 判断马能不能到XY，蹩马腿在下面判断
     * @param x 走到的横坐标
     * @param y 走到的纵坐标
     * @return 是否能到达XY
     */
    private boolean horseXY(int x,int y,int color){
        ChessStats XYStats = getStatsByXY(x, y);
        if(XYStats != null && XYStats.getColor() == color){
            return false;
        }
        return x >= 0 && x <= 8 && y >= 0 && y <= 9;
    }

    /**
     * 判断马走的点位。马走日
     * @param x 当前马在的横坐标
     * @param y 纵坐标
     * @param color 红黑方，但是没什么用
     * @return 可能走的点位map
     */
    private Map<String,List<Integer>> horseJudge(int x,int y,int color){
        Map<String,List<Integer>> map = new HashMap<>();
        List<Integer> Xlist = new ArrayList<>();
        List<Integer> Ylist = new ArrayList<>();
        //马走日，注意蹩马腿的情况
        for(int i=x-2;i<x+3;i++){
            //i是x-2，则j是y+-1
            //同样的，i是x-1，则j是y+-2
            int k = Math.abs(x-i);
            if(k==0) continue;
            if(k==1){
                for(int j=y-2;j<y+3;j+=4){
                    if(horseXY(i,j,color)){
                        //判断是否蹩马腿
                        if(getStatsByXY(x,(y+j)/2)==null){
                            Xlist.add(i);
                            Ylist.add(j);
                        }
                    }
                }
            }
            else if(k==2){
                for(int j=y-1;j<y+2;j+=2){
                    if(horseXY(i,j,color)){
                        if(getStatsByXY((x+i)/2,y)==null){
                            Xlist.add(i);
                            Ylist.add(j);
                        }
                    }
                }
            }
        }
        map.put("棋子可能的横坐标位置",Xlist);
        map.put("棋子可能的纵坐标位置",Ylist);
        return map;
    }

    /**
     * 判断车能不能到X,Y
     * @param x 车要走到的横坐标
     * @param y 纵坐标
     * @param color 眼色
     * @return boolean
     */
    private boolean carXY(int x,int y,int color){
        ChessStats XYStats = getStatsByXY(x, y);
        if(XYStats != null && XYStats.getColor() == color){
            return false;
        }
        return x >= 0 && x <= 8 && y >= 0 && y <= 9;
    }

    private Map<String,List<Integer>> carJudge(int x,int y,int color){
        Map<String,List<Integer>> map = new HashMap<>();
        List<Integer> Xlist = new ArrayList<>();
        List<Integer> Ylist = new ArrayList<>();
        //以X,Y为中心，直线扩散，直到有一个棋子为止
        for(int i=y+1;i<10;++i){
            //向上延伸
            if(carXY(x,y,color)){
                Xlist.add(x);
                Ylist.add(i);
            }
            ChessStats XYStats = getStatsByXY(x, i);
            if(XYStats != null){break;}
        }
        for(int i=y-1;i>=0;i--){
            //向下延伸
            if(carXY(x,y,color)){
                Xlist.add(x);
                Ylist.add(i);
            }
            ChessStats XYStats = getStatsByXY(x, i);
            if(XYStats != null){break;}
        }
        for(int i=x+1;i<9;i++){
            //向右延伸
            if(carXY(x,y,color)){
                Xlist.add(i);
                Ylist.add(y);
            }
            ChessStats XYStats = getStatsByXY(i, y);
            if(XYStats != null){break;}
        }
        for(int i=x-1;i>=0;i--){
            //向左延伸
            if(carXY(x,y,color)){
                Xlist.add(i);
                Ylist.add(y);
            }
            ChessStats XYStats = getStatsByXY(i, y);
            if(XYStats != null){break;}
        }
        map.put("棋子可能的横坐标位置",Xlist);
        map.put("棋子可能的纵坐标位置",Ylist);
        return map;
    }

    /**
     * 士兵能不能走到XY
     * @param x 待走到的点X
     * @param y Y
     * @param color 颜色
     * @return boolean
     */
    private boolean soldierXY(int x,int y,int color){
        ChessStats XYStats = getStatsByXY(x, y);
        if(XYStats != null && XYStats.getColor() == color){
            return false;
        }
        return x >= 0 && x <= 8 && y >= 0 && y <= 9;
    }

    /**
     * 判断士兵能走的位置
     * @param x 当前的横坐标
     * @param y 当前的纵坐标
     * @param color 颜色
     * @return map
     */
    private Map<String,List<Integer>> soldierJudge(int x,int y,int color){
        Map<String,List<Integer>> map = new HashMap<>();
        List<Integer> Xlist = new ArrayList<>();
        List<Integer> Ylist = new ArrayList<>();
        if((color == 1 && y<5)){
            //此时的士兵没有过河，只能往前走
            if(soldierXY(x,y+1,color)){
                Xlist.add(x);
                Ylist.add(y+1);
            }
        }
        else if(color == 1){
            //此时红方士兵已经过河
            if(soldierXY(x,y+1,color)){
                Xlist.add(x);
                Ylist.add(y+1);
            }
            if(soldierXY(x-1,y,color)){
                Xlist.add(x-1);
                Ylist.add(y);
            }
            if(soldierXY(x+1,y,color)){
                Xlist.add(x+1);
                Ylist.add(y);
            }
        }
        else if(y>4){
            //此时的士兵没有过河，只能往前走
            if(soldierXY(x,y-1,color)){
                Xlist.add(x);
                Ylist.add(y-1);
            }
        }
        else {
            //此时黑方士兵已经过河
            if(soldierXY(x,y-1,color)){
                Xlist.add(x);
                Ylist.add(y-1);
            }
            if(soldierXY(x-1,y,color)){
                Xlist.add(x-1);
                Ylist.add(y);
            }
            if(soldierXY(x+1,y,color)){
                Xlist.add(x+1);
                Ylist.add(y);
            }
        }
        map.put("棋子可能的横坐标位置",Xlist);
        map.put("棋子可能的纵坐标位置",Ylist);
        return map;
    }

    /**
     * 炮能否走到XY
     * @param x 横坐标
     * @param y 纵坐标
     * @return boolean
     */
    private boolean cannonXY(int x,int y){
        ChessStats XYStats = getStatsByXY(x, y);
        if(XYStats != null){
            //炮因为只能隔着打，所以距离炮最近的棋子，无论是不是敌方棋子都不能吃
            return false;
        }
        return x >= 0 && x <= 8 && y >= 0 && y <= 9;
    }

    private Map<String,List<Integer>> cannonJudge(int x,int y,int color){
        Map<String,List<Integer>> map = new HashMap<>();
        List<Integer> Xlist = new ArrayList<>();
        List<Integer> Ylist = new ArrayList<>();
        //以X,Y为中心，直线扩散，直到有一个棋子为止
        for(int i=y+1;i<10;++i){
            //向上延伸
            if(cannonXY(x,y)){
                Xlist.add(x);
                Ylist.add(i);
            }
            ChessStats XYStats = getStatsByXY(x, i);
            if(XYStats != null){
                //判断后面有没有敌方棋子，如果有的话是可以吃掉的
                for(int j=i+1;j<10;++j){
                    XYStats = getStatsByXY(x, j);
                    if(XYStats!=null && XYStats.getColor()!=color){
                        Xlist.add(x);
                        Ylist.add(j);
                        break;
                    }
                }
                break;
            }
        }
        for(int i=y-1;i>=0;i--){
            //向下延伸
            if(cannonXY(x,y)){
                Xlist.add(x);
                Ylist.add(i);
            }
            ChessStats XYStats = getStatsByXY(x, i);
            if(XYStats != null){
                //判断后面有没有敌方棋子，如果有的话是可以吃掉的
                for(int j=i-1;j>=0;--j){
                    XYStats = getStatsByXY(x, j);
                    if(XYStats!=null && XYStats.getColor()!=color){
                        Xlist.add(x);
                        Ylist.add(j);
                        break;
                    }
                }
                break;
            }
        }
        for(int i=x+1;i<9;i++){
            //向右延伸
            if(cannonXY(x,y)){
                Xlist.add(i);
                Ylist.add(y);
            }
            ChessStats XYStats = getStatsByXY(i, y);
            if(XYStats != null){
                //判断后面有没有敌方棋子，如果有的话是可以吃掉的
                for(int j=i+1;j<9;++j){
                    XYStats = getStatsByXY(j, y);
                    if(XYStats!=null && XYStats.getColor()!=color){
                        Xlist.add(j);
                        Ylist.add(y);
                        break;
                    }
                }
                break;
            }
        }
        for(int i=x-1;i>=0;i--){
            //向左延伸
            if(cannonXY(x,y)){
                Xlist.add(i);
                Ylist.add(y);
            }
            ChessStats XYStats = getStatsByXY(i, y);
            if(XYStats != null){
                //判断后面有没有敌方棋子，如果有的话是可以吃掉的
                for(int j=i-1;j>=0;--j){
                    XYStats = getStatsByXY(j, y);
                    if(XYStats!=null && XYStats.getColor()!=color){
                        Xlist.add(j);
                        Ylist.add(y);
                        break;
                    }
                }
                break;
            }
        }
        map.put("棋子可能的横坐标位置",Xlist);
        map.put("棋子可能的纵坐标位置",Ylist);
        return map;
    }

    /**
     * 初始化游戏棋盘
     */
    @Override
    public void initGame(){
        chessStatsMapper.deleteChessStats();
        UpdateWrapper<ChessStats> chessStatsUpdateWrapper = new UpdateWrapper<>();
        int cid = 0;
        //复原最下面一排棋子
        for(cid = 1;cid<=9;++cid){
            chessStatsUpdateWrapper.eq("cid",cid).set("x",cid-1).set("y",0);
            chessStatsMapper.update(chessStatsUpdateWrapper);
        }
        //复原炮
        cid = 10;
        chessStatsUpdateWrapper.eq("cid",cid).set("x",2).set("y",2);
        chessStatsMapper.update(chessStatsUpdateWrapper);
        cid = 11;
        chessStatsUpdateWrapper.eq("cid",cid).set("x",6).set("y",2);
        chessStatsMapper.update(chessStatsUpdateWrapper);
        //复原兵
        for(cid = 12;cid<=16;++cid){
            chessStatsUpdateWrapper.eq("cid",cid).set("x",(cid-12)*2).set("y",3);
            chessStatsMapper.update(chessStatsUpdateWrapper);
        }
        //下面同理，复原黑方棋子
        for(cid = 17;cid<=25;++cid){
            chessStatsUpdateWrapper.eq("cid",cid).set("x",cid-17).set("y",9);
            chessStatsMapper.update(chessStatsUpdateWrapper);
        }
        cid = 26;
        chessStatsUpdateWrapper.eq("cid",cid).set("x",2).set("y",7);
        chessStatsMapper.update(chessStatsUpdateWrapper);
        cid = 27;
        chessStatsUpdateWrapper.eq("cid",cid).set("x",6).set("y",7);
        chessStatsMapper.update(chessStatsUpdateWrapper);
        for(cid = 28;cid<=32;++cid){
            chessStatsUpdateWrapper.eq("cid",cid).set("x",(cid-28)*2).set("y",6);
            chessStatsMapper.update(chessStatsUpdateWrapper);
        }
    }
}

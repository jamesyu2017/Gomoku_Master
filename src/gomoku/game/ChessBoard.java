/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.game;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author yuhe
 */
public class ChessBoard implements IChessboard{

    private List<Point> pointList;



    public ChessBoard(){
        pointList = new LinkedList<>();
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                pointList.add(new Point(i,j));
            }
        }
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    @Override
    public int getMaxX() {
        return 15;
    }

    @Override
    public int getMaxY() {
        return 15;
    }

    @Override
    public List<Point> getFreePoints() {
        return pointList;
    }
}

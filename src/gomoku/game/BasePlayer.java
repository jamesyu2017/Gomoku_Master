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
public abstract class BasePlayer implements IPlayer {


    //pieces I have placed
    protected List<Point> myPoints = new LinkedList<Point>();
    //the board
    protected IChessboard chessboard;
    //max coordinates of board
    protected int maxX;
    protected int maxY;

    //all blank points
    protected List<Point> allFreePoints;

    @Override
    public final List<Point> getMyPoints() {
        return myPoints;
    }

    @Override
    public void setChessboard(IChessboard chessboard) {
        this.chessboard = chessboard;
        allFreePoints = chessboard.getFreePoints();
        maxX = chessboard.getMaxX();
        maxY = chessboard.getMaxY();
        myPoints.clear();
    }

    private final Point temp = new Point(0, 0);
    //check win or not first
    public final boolean hasWin(){
        if(myPoints.size()<5){
            return false;
        }
        Point point = myPoints.get(myPoints.size()-1);
        int count = 1;
        int x=point.getX(),y=point.getY();
        //check left 
        temp.setX(x).setY(y);
        while (myPoints.contains(temp.setX(temp.getX()-1)) && temp.getX()>=0 && count<5) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        temp.setX(x).setY(y);
        while (myPoints.contains(temp.setX(temp.getX()+1)) && temp.getX()<maxX && count<5) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        //check right
        count = 1;
        temp.setX(x).setY(y);
        while (myPoints.contains(temp.setY(temp.getY()-1)) && temp.getY()>=0) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        temp.setX(x).setY(y);
        while (myPoints.contains(temp.setY(temp.getY()+1)) && temp.getY()<maxY && count<5) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        //check diagonal
        count =1;
        temp.setX(x).setY(y);
        while (myPoints.contains(temp.setX(temp.getX()-1).setY(temp.getY()+1)) && temp.getX()>=0 && temp.getY()<maxY) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        temp.setX(x).setY(y);
        while (myPoints.contains(temp.setX(temp.getX()+1).setY(temp.getY()-1)) && temp.getX()<maxX && temp.getY()>=0 && count<6) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        //check verse diagonal
        count = 1;
        temp.setX(x).setY(y);
        while (myPoints.contains(temp.setX(temp.getX()-1).setY(temp.getY()-1)) && temp.getX()>=0 && temp.getY()>=0) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        temp.setX(x).setY(y);
        while (myPoints.contains(temp.setX(temp.getX()+1).setY(temp.getY()+1)) && temp.getX()<maxX && temp.getY()<maxY && count<5) {
            count ++;
        }
        if(count>=5){
            return true;
        }
        return false;
    }
}

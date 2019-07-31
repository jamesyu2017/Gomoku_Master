/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.game;

import java.util.List;

/**
 *
 * @author yuhe
 */
public interface IChessboard {
    //get max X value
    public int getMaxX();
    //get max Y value
    public int getMaxY();
    //get blank points
    public List<Point> getFreePoints();
}

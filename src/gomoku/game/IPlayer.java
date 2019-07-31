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
public interface IPlayer {
    // interface of player steps 
    public void run(List<Point> enemyPoints, Point point);

    public boolean hasWin();

    public void setChessboard(IChessboard chessboard);

    public List<Point> getMyPoints();
}

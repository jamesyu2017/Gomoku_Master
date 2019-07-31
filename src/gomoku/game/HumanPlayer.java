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
public class HumanPlayer extends BasePlayer {


    @Override
    public void run(List<Point> enemyPoints,Point p) {
        getMyPoints().add(p);
        allFreePoints.remove(p);
    }
}
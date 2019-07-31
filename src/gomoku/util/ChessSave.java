/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.util;

import gomoku.game.BaseComputerAi;
import gomoku.game.ChessBoard;
import gomoku.game.HumanPlayer;
import gomoku.game.IPlayer;
import gomoku.game.Point;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

/**
 *
 * @author yuhe
 */
public class ChessSave implements Externalizable{
    private IPlayer humanPlayer;
    private IPlayer baseComputerAi;
    private boolean black;
    private boolean iswin;
    private ChessBoard chessBoard;

    public ChessSave() {
    }

    public ChessSave(IPlayer humanPlayer, IPlayer baseComputerAi, boolean black, boolean iswin) {
        this.humanPlayer = humanPlayer;
        this.baseComputerAi = baseComputerAi;
        this.black = black;
        this.iswin = iswin;
    }

    public boolean isIswin() {
        return iswin;
    }

    public void setIswin(boolean iswin) {
        this.iswin = iswin;
    }

    public IPlayer getHumanPlayer() {
        return humanPlayer;
    }

    public void setHumanPlayer(IPlayer humanPlayer) {
        this.humanPlayer = humanPlayer;
    }

    public IPlayer getBaseComputerAi() {
        return baseComputerAi;
    }

    public void setBaseComputerAi(IPlayer baseComputerAi) {
        this.baseComputerAi = baseComputerAi;
    }

    public ChessBoard getChessBoard() {
        return chessBoard;
    }

    public void setChessBoard(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }


    public boolean isBlack() {
        return black;
    }

    public void setBlack(boolean black) {
        this.black = black;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(black);
        out.writeObject(humanPlayer.getMyPoints());
        out.writeObject(baseComputerAi.getMyPoints());
        out.writeBoolean(iswin);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        black = in.readBoolean();
        List<Point> human  = (List<Point>) in.readObject();
        List<Point> ai = (List<Point>) in.readObject();
        iswin = in.readBoolean();
        chessBoard = new ChessBoard();
        this.humanPlayer = new HumanPlayer();
        humanPlayer.setChessboard(chessBoard);
        humanPlayer.getMyPoints().addAll(human);
        this.baseComputerAi = new BaseComputerAi();
        baseComputerAi.setChessboard(chessBoard);
        baseComputerAi.getMyPoints().addAll(ai);
    }
}

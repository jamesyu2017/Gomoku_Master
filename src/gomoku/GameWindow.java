/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku;

import gomoku.game.*;
import gomoku.game.Point;
import gomoku.ui.CloseActionListener;
import gomoku.ui.MinActionListener;
import gomoku.ui.MouseDragListener;
import gomoku.ui.MyButton;
import gomoku.ui.MyOptionPane;
import gomoku.util.ChessSave;
import gomoku.util.ResourceUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author yuhe
 */
public class GameWindow extends JFrame {

    private JPanel panel;


    private static final int x0 = 23, y0 = 48;//start point
    private static final int dis = 35;//distance
    private static final int floor = 16;
    private boolean black;//black or white
    private int diff;

    private IChessboard chessBoard;
    private IPlayer aiPlayer;
    private IPlayer human;
    private boolean isWin;  // check whether win or not


    private BufferedImage image;
    private JPopupMenu menu;    // menu on the top right

    //private MP3Player player;


    public GameWindow(boolean black, int diff) {
        this.black = black;
        this.diff = diff;

        //this.player = new MP3Player(ResourceUtil.getStream("chess.mp3"));

        setUndecorated(true);
        setResizable(false);
        setTitle("Gomoku Master");
        setSize(530, 560);

        image = (BufferedImage) ResourceUtil.getImage("chess.jpg");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            }

        };

        JButton btn_min = new MyButton();
        btn_min.setBounds(480, 4, 22, 22);
        btn_min.addActionListener(new MinActionListener(this));
        panel.add(btn_min);

        JButton btn_close = new MyButton();
        btn_close.setBounds(510, 5, 22, 22);
        btn_close.addActionListener(new CloseActionListener(this));
        panel.add(btn_close);

        JButton btn_back = new MyButton();
        btn_back.setBounds(450, 3, 22, 22);
        btn_back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton()==MouseEvent.BUTTON1){
                    menu.show(e.getComponent(),e.getX(),e.getY());
                }
            }
        });
        panel.add(btn_back);

        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {

            private int x15 = x0 + 15 * dis;
            private int y15 = y0 + 15 * dis;

            @Override
            public void mouseReleased(MouseEvent e) {   
                //according to the coordinate to locate pieces
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int x = e.getX(), y = e.getY();
                    if ((x >= x0 - floor) && (x <= x15 + floor) && (y >= y0 - floor) && (y <= y15 + floor)) {
                        int p = -1, q = -1;
                        for (int i = 0; i < 15; i++) {
                            int xi = x0 + dis * i;
                            if (x >= xi - floor && x <= xi + floor) {
                                p = i;
                                break;
                            }
                        }
                        for (int i = 0; i < 15; i++) {
                            int yi = y0 + dis * i;
                            if (y >= yi - floor && y <= yi + floor) {
                                q = i;
                                break;
                            }
                        }
                        if (p >= 0 && q >= 0) {
                            onChessClick(p, q);
                        }
                    }
                }
            }
        });

        menu = new JPopupMenu();
        JMenuItem item1 = new JMenuItem("Restart");
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backPoint();
            }
        });
        menu.add(item1);
        menu.addSeparator();
        JMenuItem item2 = new JMenuItem("Save");
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser() {
                    @Override
                    public void approveSelection() {
                        File savedFile = getSelectedFile();
                        if (savedFile.exists()) {
                            if (MyOptionPane.showConfirmDialog(GameWindow.this, "Confirm", "The file already exits, replace？", "Yes", "No") == JOptionPane.OK_OPTION) {
                                super.approveSelection();
                            }
                        } else {
                            super.approveSelection();
                        }
                    }
                };
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileFilter fileFilter = new FileNameExtensionFilter("File（.chess）", "chess");
                chooser.setFileFilter(fileFilter);
                chooser.setMultiSelectionEnabled(false);
                if (chooser.showSaveDialog(GameWindow.this) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    String fname = chooser.getName(file);
                    if (!fname.contains(".chess")) {
                        file = new File(chooser.getCurrentDirectory(), fname = fname + ".chess");
                    }
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        ChessSave chessSave = new ChessSave(human, aiPlayer, black, isWin);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                        objectOutputStream.writeObject(chessSave);
                        MyOptionPane.showMessageDialog(GameWindow.this, "Successfully save the file to " + fname, "Prompt");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        MyOptionPane.showMessageDialog(GameWindow.this, "Unable to save the file!", "Error");
                    }
                }
            }
        });
        menu.add(item2);
        JMenuItem item3 = new JMenuItem("Load");
        item3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileFilter fileFilter = new FileNameExtensionFilter("File（.chess）", "chess");
                chooser.setFileFilter(fileFilter);
                chooser.setMultiSelectionEnabled(false);
                if (chooser.showOpenDialog(GameWindow.this) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                        ChessSave chessSave = (ChessSave) objectInputStream.readObject();
                        loadChessSave(chessSave);
                        MyOptionPane.showMessageDialog(GameWindow.this, "Successfully save the file to！", "Prompt");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        MyOptionPane.showMessageDialog(GameWindow.this, "Unable to save the file！", "Error");
                    }
                }
            }
        });
        menu.add(item3);
        menu.addSeparator();
        JMenuItem item4 = new JMenuItem("Regret");
        item4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (human.getMyPoints().isEmpty() || aiPlayer.getMyPoints().isEmpty()) {
                    MyOptionPane.showMessageDialog(GameWindow.this,"Can't regret at start!","Prompt");
                    return;
                }
                if (MyOptionPane.showConfirmDialog(GameWindow.this, "Confirm", "Are you sure to regret？", "Yes", "No") == JOptionPane.OK_OPTION) {

                    Point p1 = human.getMyPoints().remove(human.getMyPoints().size() - 1);
                    Point p2 = aiPlayer.getMyPoints().remove(aiPlayer.getMyPoints().size() - 1);
                    chessBoard.getFreePoints().add(p1);
                    chessBoard.getFreePoints().add(p2);
                    repaintPanel();
                    if(isWin){
                        isWin = false;
                    }
                }

            }
        });
        menu.add(item4);

        panel.setLayout(new BorderLayout());
        panel.addMouseMotionListener(new MouseDragListener(panel, this));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        startGame();
        getContentPane().add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


    }

    private static final Image BLACK = ResourceUtil.getImage("black.gif");
    private static final Image WHITE = ResourceUtil.getImage("white.gif");

    private void clear() {
        image = (BufferedImage) ResourceUtil.getImage("chess.jpg");
        startGame();
    }

    public void loadChessSave(ChessSave chessSave) {
        this.human = chessSave.getHumanPlayer();
        this.aiPlayer = chessSave.getBaseComputerAi();
        this.chessBoard = chessSave.getChessBoard();
        this.black = chessSave.isBlack();
        this.isWin = chessSave.isIswin();
        repaintPanel();
    }

    private void repaintPanel() {
        image = (BufferedImage) ResourceUtil.getImage("chess.jpg");
        for (Point point : human.getMyPoints()) {
            chessBoard.getFreePoints().remove(point);
            drawImage(image, point.getX(), point.getY(), black);
        }
        for (Point point : aiPlayer.getMyPoints()) {
            chessBoard.getFreePoints().remove(point);
            drawImage(image, point.getX(), point.getY(), !black);
        }
        panel.repaint();
    }


    private void backPoint() {

        if (MyOptionPane.showConfirmDialog(this, "Restart", "Are you sure to restart？", "Yes", "No") == JOptionPane.OK_OPTION) {
            clear();
        }
    }

    public void startGame() {

        panel.repaint();

        isWin = false;

        chessBoard = new ChessBoard();
        human = new HumanPlayer();
        aiPlayer = new BaseComputerAi();
        human.setChessboard(chessBoard);
        aiPlayer.setChessboard(chessBoard);

        if (!black) {     
            //players are white by default, computer will place pieces in the middle
            drawPoint(7, 7, true);
            Point point = new Point(7, 7);
            aiPlayer.getMyPoints().add(point);
            chessBoard.getFreePoints().remove(point);
        }


    }

    private void onChessClick(int x, int y) {
        //up left is (0,0）down right is（15，15）；when clicked, mouse action performed
        if (isWin) {
            return;
        }
        Point point = new Point(x, y);
        if (chessBoard.getFreePoints().contains(point)) {
            human.run(aiPlayer.getMyPoints(), point);
            drawPoint(x, y, black);
            if (checkWin(true)) {
                return;
            }
            aiPlayer.run(human.getMyPoints(), null);
            Point aiPoint = aiPlayer.getMyPoints().get(aiPlayer.getMyPoints().size() - 1);
            drawPoint(aiPoint.getX(), aiPoint.getY(), !black);
            checkWin(false);
        }

    }


    /**
     * placing points
     *
     * @param x                    0-15，x_coordinate
     * @param y                    0-15，y_coordinate
     * @param black，true:black；false:white
     */
    private void drawPoint(int x, int y, boolean black) {
        drawImage(image, x, y, black);
        panel.repaint();
    }

    private void drawImage(BufferedImage image, int x, int y, boolean black) {
        int px = x0 + dis * x - 20;
        int py = y0 + dis * y - 20;
        Graphics2D g = image.createGraphics();
        if (black) {
            g.drawImage(BLACK, px, py, 40, 40, null);
        } else {
            g.drawImage(WHITE, px, py, 40, 40, null);
        }
        g.dispose();
    }


    private boolean checkWin(boolean player) {
        if (player && human.hasWin()) {
            isWin = true;

            if (MyOptionPane.showConfirmDialog(this, "Victory!", "You win, wanna continue？", "Yes", "No") == JOptionPane.OK_OPTION) {
                clear();
            }
            return true;
        }

        if (!player && aiPlayer.hasWin()) {
            isWin = true;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (MyOptionPane.showConfirmDialog(this, "Defeat", "You lose, wanna challenge again？", "Yes", "No") == JOptionPane.OK_OPTION) {
                clear();
            }
            return true;
        }

        if (chessBoard.getFreePoints().isEmpty()) {
            isWin = true;
            if (MyOptionPane.showConfirmDialog(this, "Really close!", "Tie！Wanna continue？", "Yes", "No") == JOptionPane.OK_OPTION) {
                clear();
            }
            return true;
        }

        return false;
    }


}

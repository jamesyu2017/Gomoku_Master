/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author yuhe
 */
public class MouseDragListener  extends MouseAdapter{

    private JPanel panel;
    private JFrame window;

    public MouseDragListener(JPanel panel, JFrame window) {
        this.panel = panel;
        this.window = window;
    }

    private Point draggingAnchor = null;
    @Override
    public void mouseMoved(MouseEvent e) {
        draggingAnchor = new Point(e.getX() + panel.getX(), e.getY() + panel.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        window.setLocation(e.getLocationOnScreen().x - draggingAnchor.x, e.getLocationOnScreen().y - draggingAnchor.y);
    }
}

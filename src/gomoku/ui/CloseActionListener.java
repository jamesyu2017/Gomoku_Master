/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author yuhe
 */
public class CloseActionListener implements ActionListener{

    private Window window;
    private boolean confirm = true;

    public CloseActionListener(Window window) {
        this.window = window;
    }

    public CloseActionListener(Window window, boolean confirm) {
        this.window = window;
        this.confirm = confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!confirm||MyOptionPane.showConfirmDialog(window,"EXIT","Are you sure to quit？","Yes","No")==JOptionPane.OK_OPTION){
            window.dispose();
        }
    }
}

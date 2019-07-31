/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.ui;

import gomoku.util.ResourceUtil;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author yuhe
 */
public class BackgroundPanel extends JPanel {
    
    private String knight ; // name for image and resources

    private ImageIcon icon;

    public String getKnight() {
        return knight;
    }

    public void setKnight(String knight) {
        this.knight = knight;
    }

    public BackgroundPanel(String name){
        this.knight = name;
        Image image = ResourceUtil.getImage(name);
        if(image!=null){
            this.icon = new ImageIcon(image);
        }

    }

    //rewrite the paint image part
    @Override
    public void paintComponent(Graphics g)
    {
        if(this.icon!=null){
            int x = 0,y = 0;
            // draw the component
            g.drawImage(icon.getImage(),x,y,icon.getIconWidth(),icon.getIconHeight(),this);
        }

    }
}

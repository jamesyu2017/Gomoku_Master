/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author yuhe
 */
public class ResourceUtil {



    public static InputStream getStream(String name){
        return ResourceUtil.class.getResourceAsStream(name);
    }



    public static Image getImage(String name){
        Image image = null;
        try {
            image = ImageIO.read(new File(name));
        }catch (IOException e){
            e.printStackTrace();
        }
            return image;
            
    }

    public static ImageIcon getIcon(String name){
        return new ImageIcon(getImage(name));
    }
}

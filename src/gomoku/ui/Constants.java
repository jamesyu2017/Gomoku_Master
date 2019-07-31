/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.ui;

/**
 *
 * @author yuhe
 */

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

class Constants {
    public static String USER = "user";
    public static String GROUP = "group";
    public static String SUCCESS = "success";
    public static String FAILURE = "failure";
    public static String YES = "yes";
    public static String NO = "no";

    /** space code */
    public static String SPACE = "\u0008";
    /** tab code */
    public static String NEWLINE = "\n";
    /** left slash */
    public static String LEFT_SLASH = "/";

    public static String SEARCH_TXT = "Search：Friend、Group";
    public static String DEFAULT_CATE = "My Friends";
    public static String NONAME_CATE = "Undefined";

    // font set
    public static Font BASIC_FONT = new Font("Microsoft Yahei", Font.PLAIN, 12);
    public static Font BASIC_FONT2 = new Font("Microsoft Yahei", Font.TYPE1_FONT, 12);
    
    public static Font DIALOG_FONT = new Font("Italic", Font.PLAIN, 12);

    public static Border GRAY_BORDER = BorderFactory.createLineBorder(Color.GRAY);
    public static Border ORANGE_BORDER = BorderFactory.createLineBorder(Color.ORANGE);
    public static Border LIGHT_GRAY_BORDER = BorderFactory.createLineBorder(Color.LIGHT_GRAY);

    public static int NO_OPTION = 1;
    public static int YES_OPTION = 0;
}


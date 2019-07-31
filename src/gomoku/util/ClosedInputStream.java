/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.util;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author yuhe
 */
public class ClosedInputStream extends InputStream{


    //public static final ClosedInputStream CLOSED_INPUT_STREAM = new ClosedInputStream();

    @Override
    public int read() throws IOException {
        return -1;
    }
}

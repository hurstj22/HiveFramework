
package com.example.hiveframework;
import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.Tile;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void setPiece() {
    }

    //checking the list in the current turn
    @Test
    public void getPiecesRemain() {
        Tile tile = new Tile(0, 0, Tile.PlayerPiece.B);

        HiveGameState hgs = new HiveGameState();
        assertEquals(6, hgs.getPiecesRemain(Tile.Bug.ANT));
    }

    //set turn
    @Test
    public void setTurn() {
        HiveGameState hgs = new HiveGameState();
        hgs.setWhoseTurn(1);
        int testTurn = 1;
        assertEquals(1, hgs.getWhoseTurn());
    }

    @Test
    public void checkSurround() throws Exception {
        HiveGameState hgs = new HiveGameState();

        hgs.boundsCheck(5, 5);
        boolean bounds = true;
        assertEquals(bounds, hgs.boundsCheck(5, 5));
    }
}

   /** @Test
    public void antValidMove(){
        HiveGameState hgs  = new HiveGameState();
        Tile tile = new tile;
        hgs.antValidMove(tile, 5, 5));
    }
}
**/
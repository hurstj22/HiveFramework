package com.example.hiveframework;

import static org.junit.Assert.*;

import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.Tile;

import org.junit.Test;

import java.util.ArrayList;

/**
 * This class holds all unit tests for testing functionality
 * of methods within our game state.
 *  @author Isaac Reinhard
 *  @author Kelly Ngyuen
 *  @author Ali Sheehan
 *  @author James Hurst
 *  @version November 2021
 */
public class HiveGameStateTest {

    @Test
    public void newGame() {
    }

    @Test
    public void endGame() {
    }

    @Test
    public void undoMove() {
    }

    @Test
    public void validMove() {
    }

    @Test
    public void selectFromHand() {
    }

    @Test
    public void breakHive() {
    }

    @Test
    public void bfs() {
    }

    @Test
    public void isValidBFS() {
    }

    /**
     * This is a test to see if the freedom method functions properly
     * This method should return false if the Tile in question
     * is trapped by other tiles. Written by James Hurst.
     */
    @Test
    public void freedom() {
        HiveGameState gameState = new HiveGameState();
        assertEquals(0, gameState.getComputerPlayersTiles().size()); //list is currently empty

        Tile ant = new Tile(2, 1, Tile.PlayerPiece.B, Tile.Bug.ANT, -1); //create a mundane test bug
        Tile beetle = new Tile(2, 2, Tile.PlayerPiece.B, Tile.Bug.BEETLE, -1); //create a mundane test bug
        Tile grasshopper = new Tile(3, 1, Tile.PlayerPiece.B, Tile.Bug.GRASSHOPPER, -1); //create a mundane test bug
        Tile bee = new Tile(3, 2, Tile.PlayerPiece.B, Tile.Bug.QUEEN_BEE, -1); //create a mundane test bug
        Tile spider = new Tile(3, 3, Tile.PlayerPiece.B, Tile.Bug.SPIDER, -1); //create a mundane test bug
        Tile ant2 = new Tile(4, 1, Tile.PlayerPiece.B, Tile.Bug.ANT, -1); //create a mundane test bug
        Tile grasshopper2 = new Tile(4, 2, Tile.PlayerPiece.B, Tile.Bug.GRASSHOPPER, -1); //create a mundane test bug

        Tile[] testArray1 = new Tile[] {ant, bee, grasshopper, beetle, spider, ant2, grasshopper2}; //all the bugs
        Tile[] testArray2 = new Tile[] {bee, grasshopper, beetle, spider, ant2, grasshopper2}; //testing only surrounded by 5
        Tile[] testArray3 = new Tile[] {bee, grasshopper, spider, ant2, grasshopper2}; //testing only surrounded by 4 and should be able to move

        for(Tile bug: testArray1){
            gameState.addTile(bug); //add all the tiles to the board
        }
        boolean test = gameState.freedom(bee);
        assertEquals(false, test); //the piece shouldn't be able to move

        HiveGameState gameState2 = new HiveGameState();
        for(Tile bug: testArray2){
            gameState2.addTile(bug); //add all the tiles to the board
        }
        test = gameState2.freedom(bee);
        assertEquals(false, test); //the piece shouldn't be able to move

        HiveGameState gameState3 = new HiveGameState();
        for(Tile bug: testArray3){
            gameState3.addTile(bug); //add all the tiles to the board
        }
        test = gameState3.freedom(bee);
        assertEquals(true, test); //the piece should now be able to move since it is only surround on 4 sides next to each other

    }

    @Test
    public void selectTile() {
    }

    @Test
    public void beetleSearch() {
    }

    @Test
    public void queenSearch() {
    }

    @Test
    public void startGrasshopperSearch() {
    }

    @Test
    public void grasshopperSearch() {
    }

    @Test
    public void antMove() {
    }

    @Test
    public void spiderSearch() {
    }

    @Test
    public void nextTo() {
    }

    @Test
    public void testToString() {
    }

    /**
     * Tests that tile is moved correctly in gameBoard
     * Written by Isaac Reinhard
     */
    @Test
    public void makeMove() {
        HiveGameState hiveGameState = new HiveGameState();
        Tile ant = new Tile(2, 3, Tile.PlayerPiece.B, Tile.Bug.ANT, -1); //create a mundane test bug
        hiveGameState.surroundingTiles(ant, 0);
        hiveGameState.makeMove(ant, 3,4);
        assertEquals(3, ant.getIndexX()); //check new xCord
        assertEquals(4, ant.getIndexY()); //check new yCord
        assertEquals(Tile.Bug.ANT, hiveGameState.getGameBoard().get(3).get(4).getType()); //now ant at new position
        assertEquals(Tile.Bug.EMPTY, hiveGameState.getGameBoard().get(2).get(3).getType()); //check empty tile made in old location
    }

    @Test
    public void positionOfTile() {
    }

    /**
     * This is a test to make sure pieces are removed
     * Written by Isaac Reinhard
     */
    @Test
    public void removePiecesRemain() {
        HiveGameState hiveGameState = new HiveGameState();
        hiveGameState.setWhoseTurn(0);
        hiveGameState.removePiecesRemain(Tile.Bug.QUEEN_BEE); //remove Bee
        int numBees = hiveGameState.getPiecesRemain(Tile.Bug.QUEEN_BEE); //get how many bees
        assertEquals(0, numBees); //bees should now be 0 -- you start out with 1 bee
    }

    @Test
    public void getPiecesRemain() {
    }

    @Test
    public void touchingOtherColor() {
    }

    @Test
    public void updatePotentialsForPlacing() {

    }

    /**
     * This is written to test that surrounding tiles works on toggle 0
     * Written by Isaac Reinhard
     */
    @Test
    public void surroundingTiles() {
        HiveGameState hiveGameState = new HiveGameState();
        Tile ant = new Tile(2, 3, Tile.PlayerPiece.B, Tile.Bug.ANT, -1); //create a mundane test bug
        hiveGameState.addTile(ant);
        hiveGameState.surroundingTiles(ant, 0);
        ArrayList<Tile> potentials = hiveGameState.getPotentialMoves();
        int counter = 0; //should be 6 as adding all tiles around placedTile
        for (int i = 0; i < potentials.size(); i++){
            if (potentials.get(i).getIndexX() == 1 && potentials.get(i).getIndexY() == 3){
                counter++;
            }
            if (potentials.get(i).getIndexX() == 1 && potentials.get(i).getIndexY() == 4){
                counter++;
            }
            if (potentials.get(i).getIndexX() == 2 && potentials.get(i).getIndexY() == 2){
                counter++;
            }
            if (potentials.get(i).getIndexX() == 2 && potentials.get(i).getIndexY() == 4){
                counter++;
            }
            if (potentials.get(i).getIndexX() == 3 && potentials.get(i).getIndexY() == 3){
                counter++;
            }
            if (potentials.get(i).getIndexX() == 3 && potentials.get(i).getIndexY() == 4){
                counter++;
            }
        }
        assertEquals(6, counter);
    }

    @Test
    public void addTile() {
    }

    @Test
    public void getTile() {
    }

    @Test
    public void containsTile() {
    }

    @Test
    public void getGameBoard() {
    }

    @Test
    public void getDisplayBoard() {
    }

    @Test
    public void getPotentialMoves() {
    }

    /**
     * This is a test to see if the piecesRemain (which holds all the bugs
     * of the players) decrements properly. Written by James Hurst.
     */
    @Test
    public void testGetPiecesRemain() {
        HiveGameState gameState = new HiveGameState();
        Tile ant = new Tile(-1, -1, Tile.PlayerPiece.W, Tile.Bug.ANT, -1); //create a mundane test bug
        int numAnts = gameState.getPiecesRemain(ant.getType());

        assertEquals(6, numAnts); //should return true
        gameState.removePiecesRemain(ant.getType());
        numAnts = gameState.getPiecesRemain(ant.getType());
        assertEquals(5, numAnts); //true since an ant was decremented
    }

    @Test
    public void setWhoseTurn() {
    }

    @Test
    public void getWhoseTurn() {
    }

    @Test
    public void getBoardSize() {
    }

    @Test
    public void getCurrentIdSelected() {
    }

    @Test
    public void setCurrentIdSelected() {
    }

    @Test
    public void getComputerPlayersTiles() {
    }

    @Test
    public void setComputerPlayersTiles() {
    }

    /**
     * This is a test to see if the tiles add properly to the
     * computer player's pieces. Written by James Hurst.
     */
    @Test
    public void addComputerPlayersTiles() {
        HiveGameState gameState = new HiveGameState();
        assertEquals(0, gameState.getComputerPlayersTiles().size()); //list is currently empty

        Tile ant = new Tile(5, 7, Tile.PlayerPiece.B, Tile.Bug.ANT, -1); //create a mundane test bug
        Tile beetle = new Tile(2, 0, Tile.PlayerPiece.B, Tile.Bug.BEETLE, -1); //create a mundane test bug
        Tile grasshopper = new Tile(9, 2, Tile.PlayerPiece.B, Tile.Bug.GRASSHOPPER, -1); //create a mundane test bug
        Tile bee = new Tile(3, 4, Tile.PlayerPiece.B, Tile.Bug.QUEEN_BEE, -1); //create a mundane test bug
        Tile[] testArray = new Tile[] {ant, bee, grasshopper, beetle};

        for(Tile bug: testArray){
            gameState.addComputerPlayersTiles(bug);
        }
        assertEquals(testArray.length, gameState.getComputerPlayersTiles().size());
    }

    @Test
    public void setPotentialMoves() {
    }

    @Test
    public void boundsCheck() {
    }
}
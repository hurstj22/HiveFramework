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

    //Tests beetlesearch functionality -Ali
    @Test
    public void beetleSearch() {
        HiveGameState gameState = new HiveGameState();
        //Create a board with 5, 2 the Queen to be moved
        for (int i = 2; i < 4; i++) {
            for (int j = 3; j < 6; j++) {
                if (i == 2) {
                    Tile blackTile = new Tile(i, j, Tile.PlayerPiece.B);
                    if (j == 3) {
                        blackTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        blackTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        blackTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(blackTile);
                } else {
                    Tile whiteTile = new Tile(i, j, Tile.PlayerPiece.W);
                    if (j == 3) {
                        whiteTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        whiteTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        whiteTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(whiteTile);
                }
            }
        }

        boolean movePossible = gameState.beetleSearch(gameState.getTile(3, 3));
        Tile.Bug piece = gameState.getTile(3,3).getType();
         assertEquals(Tile.Bug.BEETLE, piece);
        int xCoord = gameState.getPotentialMoves().get(0).getIndexX();
        int yCoord = gameState.getPotentialMoves().get(0).getIndexY();
        int potentialSize = gameState.getPotentialMoves().size();

        assertEquals(4, potentialSize);
    }

    //Tests queensearch for functionality --Ali
    @Test
    public void queenSearch() {
        HiveGameState gameState = new HiveGameState();
        //Create a board with 5, 2 the Queen to be moved
        for (int i = 2; i < 4; i++) {
            for (int j = 3; j < 6; j++) {
                if (i == 2) {
                    Tile blackTile = new Tile(i, j, Tile.PlayerPiece.B);
                    if (j == 3) {
                        blackTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        blackTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        blackTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(blackTile);
                } else {
                    Tile whiteTile = new Tile(i, j, Tile.PlayerPiece.W);
                    if (j == 3) {
                        whiteTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        whiteTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        whiteTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(whiteTile);
                }
            }
        }

        boolean movePossible = gameState.queenSearch(gameState.getTile(3, 5));
        Tile.Bug piece = gameState.getTile(3,5).getType();
//        assertEquals(Tile.Bug.QUEEN_BEE, piece);
        int xCoord = gameState.getPotentialMoves().get(0).getIndexX();
        int yCoord = gameState.getPotentialMoves().get(0).getIndexY();
        int potentialSize = gameState.getPotentialMoves().size();

        assertEquals(2, potentialSize);


    }

    //Tests the funcitonality of GrasshopperSearch ---Ali
    @Test
    public void startGrasshopperSearch() {
        HiveGameState gameState;
        gameState = new HiveGameState();
        //Create a board with 4, 2 the grasshopper to be moved
        for (int i = 2; i < 4; i++) {
            for (int j = 3; j < 6; j++) {
                if (i == 2) {
                    Tile blackTile = new Tile(i, j, Tile.PlayerPiece.B);
                    if (j == 3) {
                        blackTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        blackTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        blackTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(blackTile);
                } else {
                    Tile whiteTile = new Tile(i, j, Tile.PlayerPiece.W);
                    if (j == 3) {
                        whiteTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        whiteTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        whiteTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(whiteTile);
                }
            }
        }
        HiveGameState gameState2 = new HiveGameState(gameState);
        //startgrasshopper search on the tile returned by getTile(2, 4)
        gameState.startGrasshopperSearch(gameState.getTile(2, 4));
        int xCoord = gameState.getPotentialMoves().get(2).getIndexX();
        int yCoord = gameState.getPotentialMoves().get(2).getIndexY();
        int potentialSize = gameState.getPotentialMoves().size();

        //Also test on other grasshopper
        gameState2.startGrasshopperSearch(gameState2.getTile(3, 4));


        Tile.Bug piece = gameState2.getTile(3,4).getType();
        int potentialSize2 = gameState2.getPotentialMoves().size();
        // assertEquals(2, xCoord);
        assertEquals(Tile.Bug.GRASSHOPPER, piece);
        assertEquals(4, potentialSize);
        assertEquals(4, potentialSize2);
        assertEquals(4, xCoord);
        assertEquals(3,yCoord);


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

    //Tests the functionality of nextTo --Ali
    @Test
    public void nextTo() {
        HiveGameState gameState;
        gameState = new HiveGameState();
        //Create a board with 4, 2 the grasshopper to be moved
        for (int i = 2; i < 4; i++) {
            for (int j = 3; j < 6; j++) {
                if (i == 2) {
                    Tile blackTile = new Tile(i, j, Tile.PlayerPiece.B);
                    if (j == 3) {
                        blackTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        blackTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        blackTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(blackTile);
                } else {
                    Tile whiteTile = new Tile(i, j, Tile.PlayerPiece.W);
                    if (j == 3) {
                        whiteTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        whiteTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        whiteTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(whiteTile);
                }
            }
        }

        boolean isNextTo = gameState.nextTo(gameState.getTile(2,5), gameState.getTile(3,6), false);
        assertTrue(isNextTo);
        assertEquals(gameState.getTile(3,5).getType(), Tile.Bug.QUEEN_BEE);
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

    //checking the Bug list in the current turn @Kelly Nguyen
    @Test
    public void getPiecesRemain() {
        Tile tile = new Tile(0, 0, Tile.PlayerPiece.B);

        HiveGameState hgs = new HiveGameState();
        assertEquals(6, hgs.getPiecesRemain(Tile.Bug.ANT));
    }

    //Tests the functionality of touchingOtherColor @Kelly Nguyen
    @Test
    public void touchingOtherColor() {
        HiveGameState gameState;
        gameState = new HiveGameState();

        for (int i = 2; i < 4; i++) {
            for (int j = 3; j < 6; j++) {
                if (i == 2) {
                    Tile blackTile = new Tile(i, j, Tile.PlayerPiece.B);
                    if (j == 3) {
                        blackTile.setType(Tile.Bug.ANT);
                    } else if (j == 4) {
                        blackTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        blackTile.setType(Tile.Bug.SPIDER);
                    }
                    gameState.addTile(blackTile);
                } else {
                    Tile whiteTile = new Tile(i, j, Tile.PlayerPiece.W);
                    if (j == 3) {
                        whiteTile.setType(Tile.Bug.ANT);
                    } else if (j == 4) {
                        whiteTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        whiteTile.setType(Tile.Bug.SPIDER);
                    }
                    gameState.addTile(whiteTile);
                }
            }
        }
        Tile t = gameState.getTile(3,3);
        assertFalse(gameState.touchingOtherColor(2,3, t.getPlayerPiece()));
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


    //Tests to ensure that a tile is preserved if another tile is placed beneath it - Ali
    @Test
    public void addTile() {
        HiveGameState gameState;
        gameState = new HiveGameState();
        //Create a board with 4, 2 the grasshopper to be moved
        for (int i = 2; i < 4; i++) {
            for (int j = 3; j < 6; j++) {
                if (i == 2) {
                    Tile blackTile = new Tile(i, j, Tile.PlayerPiece.B);
                    if (j == 3) {
                        blackTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        blackTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        blackTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(blackTile);
                } else {
                    Tile whiteTile = new Tile(i, j, Tile.PlayerPiece.W);
                    if (j == 3) {
                        whiteTile.setType(Tile.Bug.BEETLE);
                    } else if (j == 4) {
                        whiteTile.setType(Tile.Bug.GRASSHOPPER);
                    } else {
                        whiteTile.setType(Tile.Bug.QUEEN_BEE);
                    }
                    gameState.addTile(whiteTile);
                }
            }
        }
        Tile test = gameState.getTile(2,4);
        gameState.addTile(new Tile( 2, 4, Tile.PlayerPiece.B,Tile.Bug.BEETLE, 1));
        Tile beneath = gameState.getTile(2,4).getOnTopOf();

        assertNotEquals(null, beneath);
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

    //this is test to see if set turn works @Kelly Nguyen
    @Test
    public void setWhoseTurn() {
        HiveGameState hgs = new HiveGameState();
        hgs.setWhoseTurn(1);
        int testTurn = 1;
        assertEquals(1, hgs.getWhoseTurn());
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
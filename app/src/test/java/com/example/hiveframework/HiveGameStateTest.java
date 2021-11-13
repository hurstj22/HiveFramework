package com.example.hiveframework;

import static org.junit.Assert.*;

import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.Tile;

import org.junit.Test;
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

    @Test
    public void freedom() {
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

    @Test
    public void makeMove() {
    }

    @Test
    public void positionOfTile() {
    }

    @Test
    public void removePiecesRemain() {
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

    @Test
    public void surroundingTiles() {
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

    @Test
    public void addComputerPlayersTiles() {
    }

    @Test
    public void setPotentialMoves() {
    }

    @Test
    public void boundsCheck() {
    }
}
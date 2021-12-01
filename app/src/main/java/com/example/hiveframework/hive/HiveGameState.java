package com.example.hiveframework.hive;


import android.util.Log;

import com.example.hiveframework.GameFramework.infoMessage.GameState;
import com.example.hiveframework.GameFramework.utilities.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
/**
 * this is the game state which holds all info/actions related to a state of the Hive game
 *
 * @author Isaac Reinhard
 * @author Kelly Ngyuen
 * @author Ali Sheehan
 * @author James Hurst
 * @version October 2021
 */
public class HiveGameState extends GameState implements Serializable {

    //Tag for logging
    private static final String TAG = "HiveGameState";
    private static final long serialVersionUID = 7552321013488624386L;


    //Holds gamestate from beginning of turn
    private HiveGameState undoTurn = null;

    //directions used in search functions

    public enum Direction {
        UP_LEFT,
        UP_RIGHT,
        LEFT,
        RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT
    }

    //Variables of gameState
    private ArrayList<ArrayList<Tile>> gameBoard;
    private int piecesRemain[][]; //represents how many of each bug a player has
    private int whoseTurn; //whose turn it is currently 0 -> player1, 1 -> player2
    private int countVisited; //number of visited tiles when performing bfs
    private int currentIdSelected; //the current id of the imageButton selected
    private boolean selectFlag; //true if an imageButton is selected

    private static final int tileSize = 300;
    private final int GBSIZE = 7; //size of the gameboard
    private ArrayList<Tile> potentialMoves; //the moves that a selected piece could move to
    private ArrayList<Tile> computerPlayersTiles; //the pieces that the computer currently has down on the board
    Queue<Tile> tileQueue; //the queue used in BFS
    private int numRows = 7; //for the gameBoard row size
    private int numCols = 14; //for the gameBoard col size
    //boolean for placed piece
    private boolean placedPiece;
    private boolean rulesClicked; //rules method

    /**
     * Default constructor.
     */
    public HiveGameState(){
        placedPiece = false;
        //Initialize gameBoard to be 8 rows of empty tiles
        gameBoard = new ArrayList<ArrayList<Tile>>();
        for(int i=0; i < numRows; i++) {
            gameBoard.add(new ArrayList<Tile>(numCols)); //rectangle so make the col's twice the size
        }
        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numCols; j++){ //add twice as many columns as rows to make rect grid
                gameBoard.get(i).add(j, new Tile (i, j, Tile.PlayerPiece.EMPTY));
            }
        }

        //initialize piecesRemain for all players
        //row 0 = PLAYER1
        //row 1 = PLAYER2
        piecesRemain = new int[2][5];
        for(int i = 0; i < piecesRemain.length; i++){
            for(int j = 0; j < piecesRemain[i].length; j++){
                switch(j){
                    case 0: // 1 Queen Bee
                        piecesRemain[i][j] = 1;
                        break;
                    case 1: //4 Spiders
                        piecesRemain[i][j] = 4;
                        break;

                    case 2: //4 Beetles
                        piecesRemain[i][j] = 4;
                        break;

                    case 3: //6 Grasshoppers
                        piecesRemain[i][j] = 6;
                        break;

                    case 4: //6 Soldier Ants
                        piecesRemain[i][j] = 6;
                        break;
                }
            }
        }
        whoseTurn = 0; //initialize the gameboard with Player1 going first
        currentIdSelected = -1;
        potentialMoves = new ArrayList<Tile>();
        computerPlayersTiles = new ArrayList<Tile>();
        selectFlag = false; //default hasn't selected an image
        tileQueue = new LinkedList<Tile>();
        rulesClicked = false;
    }

    /**
     * Deep copy constructor.
     * @param other returns a copy of the current gameState
     */
    public HiveGameState(HiveGameState other){
        this.gameBoard = new ArrayList<ArrayList<Tile>>();
        for(int i=0; i < numRows; i++) {
            this.gameBoard.add(new ArrayList<Tile>(numRows));
        }

        for (int row = 0; row < numRows; row++){
            for (int col = 0; col < numCols; col++){
                Tile copyTile = new Tile(other.gameBoard.get(row).get(col));
                this.gameBoard.get(row).add(col, copyTile);
            }
        }

        this.piecesRemain = new int[2][5];
        for (int i = 0; i < other.getPiecesRemain().length; i++){
            for (int j = 0; j < other.getPiecesRemain()[i].length; j++){
                this.piecesRemain[i][j] = other.getPiecesRemain()[i][j];
            }
        }

        this.potentialMoves = new ArrayList<Tile>();
        for (int i = 0; i < other.getPotentialMoves().size(); i++){
            Tile copyTile = new Tile((other.getPotentialMoves()).get(i));
            this.potentialMoves.add(copyTile);
        }

        this.computerPlayersTiles = new ArrayList<Tile>();
        for (int i = 0; i < other.getComputerPlayersTiles().size(); i++){
            Tile copyTile = new Tile((other.getComputerPlayersTiles()).get(i));
            this.computerPlayersTiles.add(copyTile);
        }
        this.selectFlag = other.getSelectFlag();
        tileQueue = new LinkedList<Tile>();
        this.whoseTurn = other.getWhoseTurn();
        this.currentIdSelected = other.getCurrentIdSelected();
        this.placedPiece = other.getPlacedPiece();
        this.rulesClicked = other.getRulesClicked();
    }

    /**
     * Creates a new gamestate object and
     * is called when the new game button is clicked
     * @return the new blank gameState
     */
    public HiveGameState newGame(){
        return new HiveGameState(); //creates a new blank gameState object
    }

    /**
     * Takes in the previous gamestate
     * and sets the current gameState equal to it
     * using deep copy constructor
     * @return true if the gameState becomes the previous gameState
     */
    public boolean undoMove(HiveGameState previousGameState){
        new HiveGameState(previousGameState);
        return true;
    }

    /**
     * Checks the spot a piece is trying to move to determine if it is
     * a valid move according to the movement rules
     * @param tile the piece that is trying to move
     * @return true if can move there, false if not
     */
    public boolean validMove(Tile tile){
        if(tile.getIndexX() == -1) { //tile coming in from the players hand to be placed
            return selectFromHand(tile);
        }

        if(!breakHive(tile, false)){//as long as the move doesn't break the hive
            //and the type isn't grasshopper or beetle since they
            //don't obey the freedom of movement rule
            if(tile.getType() != Tile.Bug.GRASSHOPPER || tile.getType() != Tile.Bug.BEETLE){
                if(freedom(tile)){//obeys freedom of movement (not surrounded)
                    return true; //pass in the tile to be selected and the corresponding search to run
                }
            }
            else{ //type IS grasshopper or beetle and the move doesn't break the hive
                return true;
            }
        } //move breaks the hive :(
        return false;
    }

    /**
     * Helper method for the validMove method, takes in a tile and determines if it is
     * able to be placed on the board somewhere. If it is, then this method
     * populates the potentialMoves arrayList.
     * ONLY ALLOWS A TILE TO BE PLACED ON A NON EDGE LOCATION
     * Intentional: in hive there are no edges, and it would be unfair to take advantage of edges when placing
     * @param tile the tile that was selected and wants to be placed on the board
     * @return true if the tile is able to be placed, false if it is not permitted to be selected/placed
     */
    public boolean selectFromHand(Tile tile){
        //how many bugs are on the board
        int bugCounter = 0;
        Tile tileInQuestion = new Tile(tile); //default just in case it doesn't get initialized later

        for(int i = 0; i < numRows; i++) {
            for (int j = 0; j < gameBoard.get(i).size(); j++) {
                if(gameBoard.get(i).get(j).getType() != Tile.Bug.EMPTY){
                    if(bugCounter <= 0) {//only update this tile once
                        tileInQuestion = new Tile(gameBoard.get(i).get(j)); //the one tile that's already on the board
                    }
                    bugCounter++;
                }
            }
        }

        //now we check a few things about the board to determine what to highlight as a potential move
        if(bugCounter <= 0){ //there's nothing on the board so you can place it anywhere
            for(int i = 0; i < numRows; i++) {
                for (int j = 0; j < gameBoard.get(i).size(); j++) {
                    if(boundsCheckPlace(i, j)){//if we're not on the edge, only allows placing on non edges
                        potentialMoves.add(gameBoard.get(i).get(j));
                    }
                }
            }
            return true;
        }
        else if(bugCounter == 1){ //if there is exactly one piece placed down
            //then only add the pieces that are surrounding that piece as potential spots
            updatePotentialsForPlacing(tileInQuestion, 0); //this function adds all the pieces to potentialMoves
                                                                // and takes in a mode selector to know which tiles to add
        }
        else if(bugCounter >= 2){ //the first pieces have been put down for each player
            updatePotentialsForPlacing(tile, 1); //only add the surrounding tiles that touch only the same color as the current tile
        }
        if(potentialMoves.size() == 0){ //the tile was on the edge or there was no spot for it
            return false;
        }
        return true; //potentials has been populated, hooray!
    }

    /**
     * Checks the hive (ie gameboard) to see if the entire board is connected and if
     * without the Tile in the spot it currently is the board would STILL be connected
     * @param tile the piece being checked against breaking the hive
     * @param searchFunction, set to true if calling this method to search for a move
     *                        set to false if just checking as part of a regular validMove
     * @return false if move would NOT break the hive, true if move would break hive
     */
    public boolean breakHive(Tile tile, boolean searchFunction){
        int row = -1;
        int col = -1;
        boolean firstFound = false;
        int totalPieces = 41; //default to max minus 1

        if(searchFunction){ //if we're searching for a move function don't remove it
            totalPieces = 42;
        }

        //perform a BFS on the gameBoard
        //copy the old gameBoard into a new temporary board to perform Bfs on
        ArrayList<ArrayList<Tile>> testBoard = new ArrayList<ArrayList<Tile>>();
        for(int i=0; i < numRows; i++) {
            if(testBoard != null){
                testBoard.add(new ArrayList<Tile>(numCols));
            }
        }
        for(int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (tile.getIndexX() != -1) { //if this is a tile that is already on the board then check to take it out
                    if (i == tile.getIndexX() && j == tile.getIndexY() && !searchFunction) { //only remove piece if not looking as part of a move
                        testBoard.get(i).add(j, new Tile(i, j, Tile.PlayerPiece.EMPTY)); //take out the tile in question
                    } else {
                            if(testBoard.get(i) != null){
                                testBoard.get(i).add(j, new Tile(gameBoard.get(i).get(j)));
                         }
                    }
                }
                else{ //just copy the gameBoard over to the testBoard instead
                    testBoard.get(i).add(j, new Tile(gameBoard.get(i).get(j)));
                }
                    //update the row and col of the first position where the row and col aren't empty
                    //for use in the bfs
                if (!firstFound && testBoard.get(i).get(j).getType() != Tile.Bug.EMPTY) {
                    row = i;
                    col = j;
                    firstFound = true;
                }
            }
        }

        if(row + col > -2){ //if the board isn't empty, run bfs
            countVisited = 0; //keeps count of the visited tiles
            int handPieces = 0;
            bfs(row, col, testBoard);
            //counts the amount of pieces remaining in the player's hand
            for(int[] player: piecesRemain){
                for(int piece: player){
                    handPieces += piece;
                }
            }
            for(int i = 0; i < testBoard.size(); i++) {
                for (int j = 0; j < testBoard.size() * 2; j++) {
                    if(!testBoard.get(i).get(j).getVisited() && testBoard.get(i).get(j).getType() != Tile.Bug.EMPTY){
                        Log.i("BFS: ", "Found a tile that wasn't connected");
                        return true; //We didn't visit a tile during bfs
                    }
                }
            }
            return false; //reached this spot and didn't find a piece that wasn't visited. Hive wasn't broken

            //if(countVisited == totalPieces - handPieces) { //there are 22 total pieces, 21 counting the piece taken out
            //    return false; //if the board can be traversed with bfs and all
            //                //tiles on the boardhave been denoted as visited then
             //               //return false the hive has NOT been broken
            //}
        }
        return true; //hive will break, can't move piece
    }

    /**
     * Performs a mix of iterative and recursive breadth first search, found this code on stackOverFlow
     * https://stackoverflow.com/questions/2969033/recursive-breadth-first-travel-function-in-java-or-c
     * @param row the starting x position
     * @param col the starting y position
     * @param board the testBoard to run bfs on
     */
    public int bfs(int row, int col, ArrayList<ArrayList<Tile>> board){
        tileQueue.offer(board.get(row).get(col)); //pass in the starting x and y position as the root
        countVisited++; //update the count to reflect a visited tile

        while(!tileQueue.isEmpty()){
            int x = row;
            int y = col;

            tileQueue.poll().setVisited(true); //visits the tile at front of queue

            if (x % 2 == 0) {
                // For even rows
                //LU: (row--, col), LM: (row, col--), LD: (row++, col),
                //RU: (row--, col++), RM: (row, col++), RD: (row++, col++)

                //Queue all possible valid neighbors
                if (ib(x-1, y) && isValidBFS(board.get(x - 1).get(y))) { //LU
                    //Check tile above left of tile
                    tileQueue.offer(board.get(x - 1).get(y));
                    bfs(x - 1, y, board); //call bfs on neighbor
                }
                if (ib(x-1, y+1) && isValidBFS(board.get(x - 1).get(y + 1))) { //RU
                    //Check tile above right of tile
                    tileQueue.offer(board.get(x - 1).get(y + 1));
                    bfs(x - 1, y+1, board); //call bfs on neighbor
                }
                if(ib(x, y-1) && isValidBFS(board.get(x).get(y-1))){ //LM
                    //Check tile to the left of tile
                    tileQueue.offer(board.get(x).get(y-1));
                    bfs(x, y-1, board); //call bfs on neighbor
                }
                if(ib(x, y+1) && isValidBFS(board.get(x).get(y+1))){ //RM
                    //Check tile to the right of tile
                    tileQueue.offer(board.get(x).get(y+1));
                    bfs(x, y+1, board); //call bfs on neighbor
                }
                if(ib(x+1, y) && isValidBFS(board.get(x+1).get(y))){ //LD
                    //Check tile to the lower left of tile
                    tileQueue.offer(board.get(x+1).get(y));
                    bfs(x + 1, y, board); //call bfs on neighbor
                }
                if(ib(x+1, y+1) && isValidBFS(board.get(x+1).get(y+1))){ //RD
                    //Check tile to the lower right of tile
                    tileQueue.offer(board.get(x+1).get(y + 1));
                    bfs(x + 1, y+1, board); //call bfs on neighbor
                }
            }
            else{
                //for odd rows
                //LU: (row--, col--), LM: (row, col--), LD: (row++, col--),
                //RU: (row--, col), RM: (row, col++), RD: (row++, col)

                //Queue all possible valid neighbors
                if (ib(x-1, y-1) && isValidBFS(board.get(x - 1).get(y - 1))) { //LU
                    //Check tile above left of tile
                    tileQueue.offer(board.get(x - 1).get(y - 1));
                    bfs(x - 1, y-1, board); //call bbfs on neighbor
                }
                if (ib(x-1, y) && isValidBFS(board.get(x - 1).get(y))) { //RU
                    //Check tile above right of tile
                    tileQueue.offer(board.get(x - 1).get(y));
                    bfs(x - 1, y, board); //call bbfs on neighbor
                }
                if(ib(x, y-1) && isValidBFS(board.get(x).get(y-1))){ //LM
                    //Check tile to the left of tile
                    tileQueue.offer(board.get(x).get(y-1));
                    bfs(x, y-1, board); //call bbfs on neighbor
                }
                if(ib(x, y+1) && isValidBFS(board.get(x).get(y+1))){ //RM
                    //Check tile to the right of tile
                    tileQueue.offer(board.get(x).get(y+1));
                    bfs(x, y+1, board); //call bbfs on neighbor
                }
                if(ib(x+1, y-1) && isValidBFS(board.get(x+1).get(y-1))){ //LD
                    //Check tile to the lower left of tile
                    tileQueue.offer(board.get(x+1).get(y-1));
                    bfs(x + 1, y-1, board); //call bbfs on neighbor
                }
                if(ib(x+1, y) && isValidBFS(board.get(x+1).get(y))){ //RD
                    //Check tile to the lower right of tile
                    tileQueue.offer(board.get(x+1).get(y));
                    bfs(x + 1, y, board); //call bbfs on neighbor
                }
            }
        }
        return countVisited;
    }

    public boolean ib(int x, int y){
        if(x < 0 || y < 0 ||
                x >= numRows || y >= numRows * 2){
            return false; //out of bounds
        }
        return true;
    }

    /**
     * Helper method for the BFS,
     * determines if a tile is valid to visit
     * @param tile the tile about to be visited
     * @return true if valid to visit, false otherwise
     */
    public boolean isValidBFS(Tile tile){

        if(tile.getIndexX() < 0 || tile.getIndexY() < 0 ||
                tile.getIndexX() >= numRows || tile.getIndexY() >= numRows * 2){
            return false; //out of bounds
        }

        if(tile.getType() == Tile.Bug.EMPTY){
            return false; //empty spaces aren't part of the graph
        }

        //already visited
        if(tile.getVisited()){
            return false;
        }

        //can be visited, is valid tile
        return true;
    }


    /**
     * checks a tile to see if it is surrounded to the point
     * where it no longer has freedom of movement
     * Different logic due to the offest of hexes
     * @param tile the piece being checked
     * @return true if it is free to move, false if it is trapped
     */
    public boolean freedom(Tile tile){
        int count = 0;
        int sum = 0;
        //boolean values to keep track of which tiles were found to be occupied
        boolean LU = false;
        boolean LM = false;
        boolean LD = false;
        boolean RU = false;
        boolean RM = false;
        boolean RD = false;

        int x = tile.getIndexX();
        int y = tile.getIndexY();

        switch(tile.getIndexY() % 2){
            case 0: //even row
                //LU: (row--, col), LM: (row, col--), LD: (row++, col),
                //RU: (row--, col++), RM: (row, col++), RD: (row++, col++)

                //Check tile above left of tile
                if (boundsCheck(x-1, y) && gameBoard.get(x - 1).get(y).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LU = true;
                    sum += (x - 1) + y;
                }
                //Check tile above right of tile
                if (boundsCheck(x-1, y+1) && gameBoard.get(x - 1).get(y + 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RU = true;
                    sum += (x - 1) + (y + 1);
                }
                // Check tile to the left of tile
                if (boundsCheck(x, y-1) && gameBoard.get(x).get(y - 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LM = true;
                    sum += x + (y - 1);
                }
                // Check tile to the right of tile
                if (boundsCheck(x, y+1) && gameBoard.get(x).get(y + 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RM = true;
                    sum += x + (y + 1);
                }
              //Check tile below left of tile
                if (boundsCheck(x+1, y) && gameBoard.get(x + 1).get(y).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LD = true;
                    sum += (x + 1) + y;
                }
                // Check tile below right of tile
                if (boundsCheck(x+1, y+1) && gameBoard.get(x + 1).get(y + 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RD = true;
                    sum += (x + 1) + (y + 1);
                }

                if(count >= 5) { //piece is surrounded
                    return false;
                }
                else if(count >= 4){
                    //did some fancy math to find
                    // if the sum is odd of all the indices of the tiles connected to
                    //the tile of interest than it isn't blocked
                    if(sum % 2 == 1){
                        return true;
                    }
                    else if((sum % 2 == 0) && !LM){ //this is the exception case to the rule above
                        return true;
                    }
                    else{ //all other cases when the count is 4 or greater the tile is surrounded
                        return false;
                    }
                }
                else{ //count is less than 4 so not surrounded, thus good to go!
                    return true;
                }
                //end of even case

            case 1: //odd row
                //LU: (row--, col--), LM: (row, col--), LD: (row++, col--),
                //RU: (row--, col), RM: (row, col++), RD: (row++, col)

                //Check tile above left of tile
                if (boundsCheck(x-1, y-1) && gameBoard.get(x-1).get(y-1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LU = true;
                    sum += (x - 1) + (y - 1);
                }
                //Check tile above right of tile
                if (boundsCheck(x-1, y) && gameBoard.get(x - 1).get(y).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RU = true;
                    sum += (x - 1) + y;
                }
                // Check tile to the left of tile
                if (boundsCheck(x, y-1) && gameBoard.get(x).get(y - 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LM = true;
                    sum += x + (y - 1);
                }
                // Check tile to the right of tile
                if (boundsCheck(x, y+1) && gameBoard.get(x).get(y + 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RM = true;
                    sum += x + (y + 1);
                }
                //Check tile below left of tile
                if (boundsCheck(x+1, y-1) && gameBoard.get(x + 1).get(y - 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LD = true;
                    sum += (x + 1) + (y - 1);
                }
                // Check tile below right of tile
                if (boundsCheck(x+1, y) && gameBoard.get(x + 1).get(y).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RD = true;
                    sum += (x + 1) + y;
                }

                if(count >= 5) { //piece is surrounded
                    return false;
                }
                else if(count >= 4){
                    //did some fancy math to find
                    // if the sum is odd of all the indices of the tiles connected to
                    //the tile of interest than it isn't blocked
                    if(sum % 2 == 1){
                        return true;
                    }
                    else if((sum % 2 == 0) && !RM){ //this is the exception case to the rule above
                        return true;
                    }
                    else{ //all other cases when the count is 4 or greater the tile is surrounded
                        return false;
                    }
                }
                else{ //count is less than 4 so not surrounded, thus good to go!
                    return true;
                }
                //end of odd case
        }
        return true;
    }

    /**
     * Highlights the potential moves of a selected piece. The class variable potentials is
     * changed to reflect potential moves.
     * @param tile the selected piece
     * @return true if potential moves exist. false if not.
     */
    public boolean selectTile(Tile tile) {
        if(placedPiece){
            return false;
        }
        undoTurn = new HiveGameState(this);
        //we should make sure the person whose turn it is has placed their queen somewhere
        if(validMove(tile)) {
            //if the piece can be moved legally
            switch (tile.getType()){
                case ANT:
                    //utilize the ant function to find potential moves
                    return antValidMove(tile);
                case BEETLE:
                    //utilize the beetleSearch function to find potential moves.
                    return beetleSearch(tile);
                case GRASSHOPPER:
                    //utilize the startGrasshopperSearch function to find potential moves.
                    return startGrasshopperSearch(tile);
                case SPIDER:
                    //utilize the SpiderSearch function to find potential moves.
                    return spiderSearchBetter(tile);
                case QUEEN_BEE:
                    //utilize the queenSearch function to find potential moves.
                    return queenSearch(tile);
                case EMPTY:
                    //
                    return false;

            }

        }
        return false;
    }

    /**
     * Checks for valid moves for the Beetle tile. Modifies ArrayList potentialMoves to reflect
     * valid moves for the beetle tile. Returns false if no moves are found.
     * @param tile the tile the player wishes to move
     * @return returns true if there are valid moves for the beetle piece. False if not.
     */
    public boolean beetleSearch(Tile tile) {
            // Ensure any tile that the beetle touches touches another bug tile
            // before highlighting as potential
            int x = tile.getIndexX();
            int y = tile.getIndexY();

            if (x % 2 == 0) {
                // For even rows

                if (nextTo(tile, gameBoard.get(x - 1).get(y), true)) {
                    //Check tile above left of tile is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x - 1).get(y));
                }
                if (nextTo(tile, gameBoard.get(x - 1).get(y + 1),true )) {
                    //Check tile above right of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x - 1).get(y + 1));
                }
                if (nextTo(tile, gameBoard.get(x).get(y - 1),true )) {
                    //Check tile to the left of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x).get(y - 1));
                }
                if (nextTo(tile, gameBoard.get(x).get(y + 1),true )) {
                    //Check tile to the right of ti is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x).get(y + 1));
                }
                if (nextTo(tile, gameBoard.get(x + 1).get(y),true )) {
                    //Check tile below left of tile is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x + 1).get(y));
                }
                if (nextTo(tile, gameBoard.get(x + 1).get(y + 1),true )) {
                    //Check tile below right of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x + 1).get(y + 1));
                }
            } else {
                // For odd rows

                if (nextTo(tile, gameBoard.get(x - 1).get(y - 1),true )) {
                    //Check tile above left of tile is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x - 1).get(y - 1));
                }
                if (nextTo(tile, gameBoard.get(x - 1).get(y), true)) {
                    //Check tile above right of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x - 1).get(y));
                }
                if (nextTo(tile, gameBoard.get(x).get(y - 1), true)) {
                    //Check tile to the left of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x).get(y - 1));
                }
                if (nextTo(tile, gameBoard.get(x).get(y + 1), true)) {
                    //Check tile to the right of ti is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x).get(y + 1));
                }
                if (nextTo(tile, gameBoard.get(x + 1).get(y - 1), true)) {
                    //Check tile below left of tile is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x + 1).get(y - 1));
                }
                if (nextTo(tile, gameBoard.get(x + 1).get(y), true)) {
                    //Check tile below right of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x + 1).get(y));
                }

            }
            if (!potentialMoves.isEmpty()) {
                return true;
            }
            return false;
    }

    /**
     * Checks for valid moves for the Queen Bee tile. Modifies ArrayList potentialMoves to reflect
     * valid moves for the queen bee tile. Returns false if no moves are found.
     *
     * @param tile the tile the player wishes to move
     * @return true if successful search, false otherwise
     */
    public boolean queenSearch(Tile tile) {
        //Ensure adjacent tiles are empty with things next to them
        int x = tile.getIndexX();
        int y = tile.getIndexY();

        if (x % 2 == 0) {
            // For even rows

            if(gameBoard.get(x-1).get(y).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x-1).get(y), false)){
                //Check tile above left of tile is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x-1).get(y));

            }
            if(gameBoard.get(x-1).get(y+1).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x-1).get(y+1), false)) {
                //Check tile above right of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x-1).get(y+1));
            }
            if(gameBoard.get(x).get(y-1).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x).get(y-1),false )) {
                //Check tile to the left of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x).get(y-1));
            }
            if(gameBoard.get(x).get(y+1).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x).get(y+1), false)) {
                //Check tile to the right of ti is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x).get(y+1));
            }
            if(gameBoard.get(x+1).get(y).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x+1).get(y),false )) {
                //Check tile below left of tile is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x+1).get(y));
            }
            if(gameBoard.get(x+1).get(y+1).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x+1).get(y+1),false )) {
                //Check tile below right of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x+1).get(y+1));
            }
        } else {
            // For odd rows

            if(gameBoard.get(x-1).get(y-1).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x-1).get(y-1),false )){
                //Check tile above left of tile is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x-1).get(y-1));
            }
            if(gameBoard.get(x-1).get(y).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x-1).get(y), false)) {
                //Check tile above right of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x-1).get(y));
            }
            if(gameBoard.get(x).get(y-1).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x).get(y-1), false)) {
                //Check tile to the left of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x).get(y-1));
            }
            if(gameBoard.get(x).get(y+1).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x).get(y+1),false )) {
                //Check tile to the right of ti is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x).get(y+1));
            }
            if(gameBoard.get(x+1).get(y-1).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x+1).get(y-1), false)) {
                //Check tile below left of tile is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x+1).get(y-1));
            }
            if(gameBoard.get(x+1).get(y).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x+1).get(y),false )) {
                //Check tile below right of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x+1).get(y));
            }

        }
        if(!potentialMoves.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks tiles adjacent to the selected tile for bug pieces. Starts recursive grasshopperSearch
     * on valid moves
     * @param tile the tile the player wishes to move.
     * @return true if moves found. False if not
     */
    public boolean startGrasshopperSearch(Tile tile) {
        //set a recursive search in each direction from the starting tile
        int x = tile.getIndexX();
        int y = tile.getIndexY();
        boolean valid = false;

        if ( x % 2 == 0 ) {
            //if the tile is on an even row

            //Check tile to the above left
            if (gameBoard.get(x-1).get(y).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x-1).get(y), Direction.UP_LEFT);
                valid = true;
            }
            //Check the tile to the above right
            if (gameBoard.get(x-1).get(y+1).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x-1).get(y+1), Direction.UP_RIGHT);
                valid = true;
            }

            //Check the tile to the left
            if (gameBoard.get(x).get(y-1).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x).get(y-1), Direction.LEFT);
                valid = true;
            }

            //Check the tile to the right
            if (gameBoard.get(x).get(y+1).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x).get(y+1), Direction.RIGHT);
                valid = true;
            }

            //Check the tile to the below left
            if (gameBoard.get(x+1).get(y).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x+1).get(y), Direction.DOWN_LEFT);
                valid = true;
            }

            if (gameBoard.get(x+1).get(y+1).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x+1).get(y+1), Direction.DOWN_RIGHT);
                valid = true;
            }
        } else {
            //if the tile is on an odd row

            //Check tile to the above left
            if (gameBoard.get(x-1).get(y-1).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x-1).get(y-1), Direction.UP_LEFT);
                valid = true;
            }
            //Check the tile to the above right
            if (gameBoard.get(x-1).get(y).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x-1).get(y), Direction.UP_RIGHT);
                valid = true;
            }

            //Check the tile to the left
            if (gameBoard.get(x).get(y-1).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x).get(y-1), Direction.LEFT);
                valid = true;
            }

            //Check the tile to the right
            if (gameBoard.get(x).get(y+1).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x).get(y+1), Direction.RIGHT);
                valid = true;
            }

            //Check the tile to the below left
            if (gameBoard.get(x+1).get(y-1).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x+1).get(y-1), Direction.DOWN_LEFT);
                valid = true;
            }

            if (gameBoard.get(x+1).get(y).getType() != Tile.Bug.EMPTY) {
                grasshopperSearch(gameBoard.get(x+1).get(y), Direction.DOWN_RIGHT);
                valid = true;
            }
        }

        return valid;
    }

    /**
     * Recursive function to determine where a grasshopper tile will stop moving. Intakes a tile
     * and a direction. Modifies ArrayList potentialMoves to reflect findings.
     * @param tile current tile in path of Grasshopper to be checked
     * @param direction direction of travel for a grasshopper piece.
     * @return Should always return true.
     */
    public boolean grasshopperSearch(Tile tile, Direction direction) {
        int x = tile.getIndexX();
        int y = tile.getIndexY();

        if(x % 2 == 0) {
            //if tile is on an even row
            switch (direction) {
                case UP_LEFT:

                    //If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x-1).get(y), direction);

                case UP_RIGHT:

                    //If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x-1).get(y+1), direction);
                case LEFT:

                    //If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x).get(y-1), direction);
                case RIGHT:

                    //If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x).get(y+1), direction);
                case DOWN_LEFT:

                    // If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x+1).get(y), direction);
                case DOWN_RIGHT:

                    // If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x+1).get(y+1), direction);
            }
        } else {
            //if tile is on an odd row
            switch (direction) {
                case UP_LEFT:

                    // If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x-1).get(y-1), direction);
                case UP_RIGHT:

                    // If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x-1).get(y), direction);
                case LEFT:

                    // If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x).get(y-1), direction);
                case RIGHT:

                    // If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x).get(y+1), direction);
                case DOWN_LEFT:

                    // If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    } // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x+1).get(y-1), direction);
                case DOWN_RIGHT:

                    // If tile is empty, add to potential moves
                    if(tile.getType() == Tile.Bug.EMPTY){
                        potentialMoves.add(tile);
                        return true;

                    }  // Otherwise, continue search in same direction
                    return grasshopperSearch(gameBoard.get(x+1).get(y), direction);
            }
        }
        return false;
    }

    /** ant tile movement: determines if the ant tile is on edge, and
     * @param tile the ant tile coming in
     * @return should always return false.
     */

    private boolean antValidMove(Tile tile) {

        int x = tile.getIndexX();
        int y = tile.getIndexY();
        Tile nextile = gameBoard.get(x).get(y);
        Boolean valid = false;
        gameBoard.get(x).set(y, new Tile(x, y, Tile.PlayerPiece.EMPTY)); //temporarily take out the ant from the gameBoard

        if (!onEdge(tile)){
            return false;
            //checks to make sure the tile is not in the middle of the board
        }
        ArrayList<Tile> checkedTiles = new ArrayList<>();
        checkedTiles.add(tile); //do not check current tile again //error could be local variable
        if ( x % 2 == 0 ) {

            if (nextTo(tile, gameBoard.get(x-1).get(y), false)) {
                antSearch(gameBoard.get(x-1).get(y), checkedTiles);
                //antValidMove(gameBoard.get(x-1).get(y));
                valid = true;
            }

            //Check the tile to the above right
            if (nextTo(tile, gameBoard.get(x-1).get(y+1), false)) {
                antSearch(gameBoard.get(x-1).get(y+1), checkedTiles);
                valid = true;
            }

            //Check the tile to the left
            if (nextTo(tile, gameBoard.get(x).get(y - 1), false))  {
                antSearch(gameBoard.get(x).get(y-1), checkedTiles);
                valid = true;
            }
            //Check the tile to the above right
            if (nextTo(tile, gameBoard.get(x).get(y + 1), false)) {
                antSearch(gameBoard.get(x).get(y+1), checkedTiles);
                valid = true;
            }

            //Check the tile to the below left
            if (nextTo(tile, gameBoard.get(x + 1).get(y - 1), false)) {
                antSearch(gameBoard.get(x+1).get(y-1), checkedTiles);
                valid = true;
            }
            //Check the tile to the above right
            if (nextTo(tile, gameBoard.get(x + 1).get(y), false)) {
                antSearch(gameBoard.get(x+1).get(y), checkedTiles);
                valid = true;
            }
        }
        else {
            //if the tile is on an odd row
            //Check tile to the above left
            if (nextTo(tile, gameBoard.get(x - 1).get(y - 1), false)) {
                antSearch(gameBoard.get(x - 1).get(y - 1), checkedTiles);
                valid = true;
            }
            //Check the tile to the above right
            if (nextTo(tile, gameBoard.get(x - 1).get(y), false)) {
                antSearch(gameBoard.get(x - 1).get(y), checkedTiles);
                valid = true;
            }

            //Check the tile to the left
            if (nextTo(tile, gameBoard.get(x).get(y - 1), false)) {
                antSearch(gameBoard.get(x).get(y - 1), checkedTiles);
                valid = true;
            }

            //Check the tile to the right
            if (nextTo(tile, gameBoard.get(x).get(y + 1), false)) {
                antSearch(gameBoard.get(x).get(y + 1), checkedTiles);
                valid = true;
            }

            //Check the tile to the below left
            if (nextTo(tile, gameBoard.get(x + 1).get(y - 1), false)) {
                antSearch(gameBoard.get(x + 1).get(y - 1), checkedTiles);
                valid = true;
            }

            if (nextTo(tile, gameBoard.get(x + 1).get(y), false)) {
                antSearch(gameBoard.get(x + 1).get(y), checkedTiles);
                valid = true;
            }
        }
        gameBoard.get(x).set(y, tile); //put the ant back in
        for(Tile t : checkedTiles)
            System.out.println("Checked tile " + t.toString());
        return valid;

    }
    private void antSearch(Tile tile, ArrayList<Tile> checkedTiles){

        if(!containsTile(checkedTiles, tile)) { //is not in checked tiles, will add it to check then add to potential moves
            checkedTiles.add(tile);
            potentialMoves.add(tile); //never calls itself not recursive

        }
        else {
            return;
        }


        int x = tile.getIndexX();
        int y = tile.getIndexY();
        if ( x % 2 == 0 ) {
            //if the tile is on an even row
            //check if the surrounding tile is empty and if it is connected to the hive
            //if it is, recursive call and check
            //Check tile to the above left

            if (nextTo(tile, gameBoard.get(x-1).get(y), false)) {
                antSearch(gameBoard.get(x-1).get(y), checkedTiles);
                //antValidMove(gameBoard.get(x-1).get(y));

            }

            //Check the tile to the above right
            if (nextTo(tile, gameBoard.get(x-1).get(y+1), false)) {
                antSearch(gameBoard.get(x-1).get(y+1), checkedTiles);
            }

            //Check the tile to the left
            if (nextTo(tile, gameBoard.get(x).get(y - 1), false))  {
                antSearch(gameBoard.get(x).get(y-1), checkedTiles);
            }
            //Check the tile to the above right
            if (nextTo(tile, gameBoard.get(x).get(y + 1), false)) {
                antSearch(gameBoard.get(x).get(y+1), checkedTiles);

            }

            //Check the tile to the below left
            if (nextTo(tile, gameBoard.get(x + 1).get(y - 1), false)) {
                antSearch(gameBoard.get(x+1).get(y-1), checkedTiles);

            }
            //Check the tile to the above right
            if (nextTo(tile, gameBoard.get(x + 1).get(y), false)) {
                antSearch(gameBoard.get(x+1).get(y), checkedTiles);

            }
        }
        else {
            //if the tile is on an odd row
            //Check tile to the above left
            if (nextTo(tile, gameBoard.get(x - 1).get(y - 1), false)) {
                antSearch(gameBoard.get(x-1).get(y-1), checkedTiles);

            }
            //Check the tile to the above right
            if (nextTo(tile,gameBoard.get(x - 1).get(y),false)) {
                antSearch(gameBoard.get(x-1).get(y), checkedTiles);

            }

            //Check the tile to the left
            if (nextTo(tile, gameBoard.get(x).get(y - 1), false)) {
                antSearch(gameBoard.get(x).get(y-1), checkedTiles);

            }

            //Check the tile to the right
            if (nextTo(tile,gameBoard.get(x).get(y + 1),false)) {
                antSearch(gameBoard.get(x).get(y+1), checkedTiles);

            }

            //Check the tile to the below left
            if (nextTo(tile, gameBoard.get(x + 1).get(y - 1), false)) {
                antSearch(gameBoard.get(x+1).get(y-1), checkedTiles);

            }

            if (nextTo(tile, gameBoard.get(x + 1).get(y), false)) {
                antSearch(gameBoard.get(x+1).get(y), checkedTiles);

            }
        }

    }

    /**
     * Performs a search function on spider tiles, populates potentials arrayList
     * @param spider the spider tile coming in
     * @return true if it was a successful search
     */
    public boolean spiderSearchBetter (Tile spider ) {
        // The tile the spider is on
        int row = spider.getIndexX();
        int col = spider.getIndexY();
        ArrayList<Tile> neighbors = new ArrayList<>(); //create an arraylist to hold the spider's neighbors
        addNeighbors(neighbors, spider); //add all the spider's neighbors
        gameBoard.get(row).set(col, new Tile(row, col, Tile.PlayerPiece.EMPTY)); //temporarily take out the spider from the gameBoard

        for(int i = 0; i < neighbors.size(); i++){ //run through all neighbors
            if(neighbors.get(i).getType() != Tile.Bug.EMPTY ||
                    !onEdge(neighbors.get(i))){ //remove all non-empty neighbors and tiles that aren't on an edge
                neighbors.remove(i);
                i--; //as the arrayList dynamically resizes, have to go back one if we take away from it
            }
        }

        //now call add neighbors on further iterations
        ArrayList<Tile> twoNeighbors = new ArrayList<>();
        for(Tile oneAway : neighbors){
            addNeighbors(twoNeighbors, oneAway); //find the next spot that's two away from the first one away spots
        }
        for(int i = 0; i < twoNeighbors.size(); i++){ //pair down all two away neighbors
            if(twoNeighbors.get(i).getType() != Tile.Bug.EMPTY ||
                    !onEdge(twoNeighbors.get(i)) ||
                    (twoNeighbors.get(i).getIndexX() == spider.getIndexX() &&
                            twoNeighbors.get(i).getIndexY() == spider.getIndexY())){ //remove all non-empty neighbors and tiles that aren't on an edge
                                                                                    //also cannot select the spider's current spot as a potential spot to move
                twoNeighbors.remove(i);
                i--; //as the arrayList dynamically resizes, have to go back one if we take away from it
            }
        }

        //now call add neighbors on 3rd final iteration
        ArrayList<Tile> threeNeighbors = new ArrayList<>();
        for(Tile twoAway : twoNeighbors){
            addNeighbors(threeNeighbors, twoAway); //find the next spot that's three away from the first spot
        }
        for(int i = 0; i < threeNeighbors.size(); i++){ //pair down all three away neighbors
            if(threeNeighbors.get(i).getType() != Tile.Bug.EMPTY ||
                    !onEdge(threeNeighbors.get(i)) || //remove all non-empty neighbors and tiles that aren't on an edge
                    (threeNeighbors.get(i).getIndexX() == spider.getIndexX() && //also cannot select the spider's current spot as a potential spot to move
                            threeNeighbors.get(i).getIndexY() == spider.getIndexY()) ||
                            neighbors.contains(threeNeighbors.get(i))){ //also cannot backtrack, so if the tile is found in the initial neighbor list remove it

                threeNeighbors.remove(i);
                i--; //as the arrayList dynamically resizes, have to go back one if we take away from it
            }
        }

        gameBoard.get(row).set(col, spider); //put the spider back on the board
        potentialMoves.addAll(threeNeighbors); //add all remaining tiles to be potential spots
        if(potentialMoves.isEmpty()) { //if we didn't find any viable options quit
            return false;
        }
        return true;
    }

    /**
     * method that adds all the surrounding tile's neighbors to an arrayList
     * @param neighbors the arrayList being added to
     * @param tile the tile that we are finding the neighbors of
     */
    public void addNeighbors(ArrayList<Tile> neighbors, Tile tile){
        int x = tile.getIndexX();
        int y = tile.getIndexY();

        //add the neighbors of the tile
        if (x % 2 == 0) { //even row
            if(boundsCheck(x - 1, y)) neighbors.add(gameBoard.get(x - 1).get(y)); //tile above left of tile
            if(boundsCheck(x - 1, y + 1)) neighbors.add(gameBoard.get(x - 1).get(y + 1)); //tile above right of tile
            if(boundsCheck(x, y - 1)) neighbors.add(gameBoard.get(x).get(y - 1)); //tile to the left of tile
            if(boundsCheck(x, y + 1)) neighbors.add(gameBoard.get(x).get(y + 1)); //tile to the right of tile
            if(boundsCheck(x + 1, y)) neighbors.add(gameBoard.get(x + 1).get(y)); //tile below left of tile
            if(boundsCheck(x + 1, y + 1)) neighbors.add(gameBoard.get(x + 1).get(y + 1)); //tile below right of tile
        } else { //odd row
            if(boundsCheck(x - 1, y - 1)) neighbors.add(gameBoard.get(x - 1).get(y - 1)); //tile above left of tile
            if(boundsCheck(x - 1, y)) neighbors.add(gameBoard.get(x - 1).get(y)); //tile above right of tile
            if(boundsCheck(x, y - 1)) neighbors.add(gameBoard.get(x).get(y - 1)); //tile to the left of tile
            if(boundsCheck(x, y + 1)) neighbors.add(gameBoard.get(x).get(y + 1)); //tile to the right of tile
            if(boundsCheck(x + 1, y - 1)) neighbors.add(gameBoard.get(x + 1).get(y - 1)); //tile below left of tile
            if(boundsCheck(x + 1, y)) neighbors.add(gameBoard.get(x + 1).get(y)); //tile below right of tile
        }

    }

    /**
     * method to check if a tile is on the edge, ie not floating surrounded by empty tiles
     * @param tile the tile being checked
     * @return true if touching atleast one other non empty tile, false otherwise
     */
    public boolean onEdge(Tile tile){
        int x = tile.getIndexX();
        int y = tile.getIndexY();

        //find if tile touches at least one other non empty tile
        if (x % 2 == 0) { //even row
            if(boundsCheck(x-1, y) && (gameBoard.get(x - 1).get(y)).getType() != Tile.Bug.EMPTY){
                return true;//tile above left of tile
            }
            if(boundsCheck(x-1, y+1) && (gameBoard.get(x - 1).get(y + 1)).getType() != Tile.Bug.EMPTY){
                return true;//tile above right of tile
            }
            if(boundsCheck(x, y-1) && (gameBoard.get(x).get(y - 1)).getType() != Tile.Bug.EMPTY){
                return true; //tile to the left of tile
            }
            if(boundsCheck(x, y+1) && (gameBoard.get(x).get(y + 1)).getType() != Tile.Bug.EMPTY){
                return true;
            } //tile to the right of tile
            if(boundsCheck(x+1, y) && (gameBoard.get(x + 1).get(y)).getType() != Tile.Bug.EMPTY){
                return true;//tile below left of tile
            }
            if(boundsCheck(x+1, y+1) && (gameBoard.get(x + 1).get(y + 1)).getType() != Tile.Bug.EMPTY){
                return true; //tile below right of tile
            }
        } else { //odd row
            if(boundsCheck(x-1, y-1) && (gameBoard.get(x - 1).get(y - 1)).getType() != Tile.Bug.EMPTY){
                return true;//tile above left of tile
            }
            if(boundsCheck(x-1, y) && (gameBoard.get(x - 1).get(y)).getType() != Tile.Bug.EMPTY){
                return true;//tile above right of tile
            }
            if(boundsCheck(x, y-1) && (gameBoard.get(x).get(y - 1)).getType() != Tile.Bug.EMPTY){
                return true; //tile to the left of tile
            }
            if(boundsCheck(x, y+1) && (gameBoard.get(x).get(y + 1)).getType() != Tile.Bug.EMPTY){
                return true;
            } //tile to the right of tile
            if(boundsCheck(x+1, y-1) && (gameBoard.get(x + 1).get(y - 1)).getType() != Tile.Bug.EMPTY){
                return true;//tile below left of tile
            }
            if(boundsCheck(x+1, y) && (gameBoard.get(x + 1).get(y)).getType() != Tile.Bug.EMPTY){
                return true; //tile below right of tile
            }
        }
        return false;
    }

    /**
     * Ensures Tile tile is empty and next to an occupied Tile that is not the selected Tile.
     * Helper function for piece search functions
     *
     * @param selectedTile the tile the player wishes to move
     * @param tile the tile to be checked against
     * @param isBeetle
     * @return
     */
    public boolean nextTo(Tile selectedTile, Tile tile, boolean isBeetle) {
        if (tile.getType() == Tile.Bug.EMPTY) {
            // Assign tile coordinates to integer values for ease of handling
            int x = tile.getIndexX();
            int y = tile.getIndexY();
            if(getTile(x-1, y) == null || getTile(x, y-1) == null
                    || getTile(x + 1, y) == null || getTile(x, y + 1) == null){
                return false; //on the edge
            }

            if (x % 2 == 0) {
                // For even rows

                if((isBeetle || gameBoard.get(x-1).get(y).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x-1).get(y) != selectedTile){
                    //Check tile above left of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x-1).get(y+1).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x-1).get(y+1) != selectedTile) {
                    //Check tile above right of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x).get(y-1).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x).get(y-1) != selectedTile) {
                    //Check tile to the left of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x).get(y+1).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x).get(y+1) != selectedTile) {
                    //Check tile to the right of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x+1).get(y).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x+1).get(y) != selectedTile) {
                    //Check tile below left of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x+1).get(y+1).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x+1).get(y+1) != selectedTile) {
                    //Check tile below right of tile
                    return true;
                }
                } else {
                // For odd rows

                if((isBeetle || gameBoard.get(x-1).get(y-1).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x-1).get(y-1) != selectedTile ){
                    //Check tile above left of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x-1).get(y).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x-1).get(y) != selectedTile ) {
                    //Check tile above right of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x).get(y-1).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x).get(y-1) != selectedTile ) {
                    //Check tile to the left of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x).get(y+1).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x).get(y+1) != selectedTile ) {
                    //Check tile to the right of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x+1).get(y-1).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x+1).get(y-1) != selectedTile) {
                    //Check tile below left of tile
                    return true;
                }
                if((isBeetle || gameBoard.get(x+1).get(y).getType() != Tile.Bug.EMPTY ) &&
                        gameBoard.get(x+1).get(y) != selectedTile) {
                    //Check tile below right of tile
                    return true;
                }

            }
        }
        return false;

    }

    /**
     * Method to print out gameState in readable format.
     * @return the state of the game as a string, each empty tile is ***,
     * and all other pieces are signified by their first letter
     */
    @Override
    public String toString(){
        String currentState = whoseTurn + "'s turn, here is the game board:\n";
        for (ArrayList<Tile> row: gameBoard) {
            for (Tile tile : row) {
                switch (tile.getType()) {
                    case EMPTY:
                        currentState += "***"; //add space for nothing there
                        break;
                    case QUEEN_BEE:
                        currentState += tile.getPlayerPiece().name() + "Q";
                        break;
                    case ANT:
                        currentState += tile.getPlayerPiece().name() + "A";
                        break;
                    case BEETLE:
                        currentState += tile.getPlayerPiece().name() + "B";
                        break;
                    case SPIDER:
                        currentState += tile.getPlayerPiece().name() + "S";
                        break;
                    case GRASSHOPPER:
                        currentState += tile.getPlayerPiece().name() + "G";
                        break;

                }
            }
            currentState += "\n";
        }
        for (int i = 0; i < piecesRemain.length; i++) {
            if(i == 0){
                currentState +=  "\nPLAYER1's pieces in hand:\n";
            }
            else{
                currentState +=  "PLAYER2's pieces in hand:\n";
            }
            for (int j = 0; j < piecesRemain[i].length; j++) {
                switch(j){
                    case 0: // 1 Queen Bee
                        currentState += " Queen:" + piecesRemain[i][j];
                        break;
                    case 1: //4 Spiders
                        currentState += " Spiders:" + piecesRemain[i][j];
                        break;

                    case 2: //4 Beetles
                        currentState += " Beetles:" + piecesRemain[i][j];
                        break;

                    case 3: //6 Grasshoppers
                        currentState += " Grasshoppers:" + piecesRemain[i][j];
                        break;

                    case 4: //6 Soldier Ants
                        currentState += " Ants:" + piecesRemain[i][j];
                        break;
                }
            }
            currentState += "\n";
        }
        currentState += "\n"; //looks better this way, it ain't much, but it's honest work
        return currentState;
    }

    /**
     * Move the tile from one spot to a new destination, (swaps tiles)
     * @param moveTile the old tile moving
     * @param newXIndex the new y coordinates the old tile will go
     * @param newYIndex the new x coordinates the old tile will go
     * @return a boolean, true if it successfully moved, false if the tile failed to move
     */
    public boolean makeMove(Tile moveTile, int newXIndex, int newYIndex) {
        // commented out coordinate things for testing purposes so we can pass in indexes
        //need to get position of newTile based on x and y coordinates
        //int[] newTileCords = positionOfTile(newXCoord, newYCoord);

        //hold old Position
        int[] oldTileCords = new int[2];
        oldTileCords[0] = moveTile.getIndexX();
        oldTileCords[1] = moveTile.getIndexY();
        if(newXIndex >= numRows || newYIndex >= numCols){
            potentialMoves.clear();
            return false; //out of bounds!
        }
        //if potentialMoves holds tile at newPosition then swap
        if(containsTile(potentialMoves, gameBoard.get(newXIndex).get(newYIndex))) {

            //assign newIndexes to move Tile
            moveTile.setIndexX(newXIndex);
            moveTile.setIndexY(newYIndex);

            //not on top of something so make new empty tile
            if (moveTile.getOnTopOf() == null) {
                gameBoard.get(newXIndex).set(newYIndex, moveTile);
                if(oldTileCords[0] != -1) { //if the tile coming in is not from the player's hand perform swap,
                                            //otherwise just update the gameBoard with the new tile.
                    Tile emptyTile = new Tile(oldTileCords[0], oldTileCords[1], Tile.PlayerPiece.EMPTY);
                    gameBoard.get(oldTileCords[0]).set(oldTileCords[1], emptyTile);
                }
                else {
                    removePiecesRemain(moveTile.getType()); //from the player's hand to the board, thus update the player's hand counters
                }
            }

            //on top of something so don't make new empty tile
            else {

                gameBoard.get(newXIndex).set(newYIndex, moveTile);
            }
            potentialMoves.clear(); //reset potentialMoves
            return true;
        }
        potentialMoves.clear(); //reset potentialMoves
       return false;
    }
    /**
     * A helper function to determine the indices given points on the gameboard
     * @param xCord the x coordinate on the board
     * @param yCord the y coordinate on the board
     * @return the index in the array that point on the board belongs to as a pair of indices
     */
    public int[] positionOfTile(int xCord, int yCord){
        //holds row and col value of tile
        int[] positionInGameBoard = new int[2];
        positionInGameBoard[0] = (yCord/tileSize) -1;
        if (positionInGameBoard[0] % 2 == 0){ //even row
            positionInGameBoard[1] = (int) ((xCord/(1.5*tileSize)) - 1);
        }
        else{ //odd row
            positionInGameBoard[1] = xCord/tileSize;
        }
        return positionInGameBoard;
    }

    /**
     * Shows a piece has been removed from the integer representation of the player's hand
     * @param bug the bug piece to decrement in the "hand"
     */
    public void removePiecesRemain(Tile.Bug bug){
        int player = getWhoseTurn();
        switch(bug){
            case QUEEN_BEE: // 1 Queen Bee
                piecesRemain[player][0]--;
                break;
            case SPIDER: //4 Spiders
                piecesRemain[player][1]--;
                break;

            case BEETLE: //4 Beetles
                piecesRemain[player][2]--;
                break;

            case GRASSHOPPER: //6 Grasshoppers
                piecesRemain[player][3]--;
                break;

            case ANT: //6 Soldier Ants
                piecesRemain[player][4]--;
                break;
        }
    }

    /**
     * Returns how many pieces left of bug type in players hand
     * @param bug the bug piece to decrement in the "hand"
     */
    public int getPiecesRemain(Tile.Bug bug){
        int player = getWhoseTurn();
        switch(bug){
            case QUEEN_BEE: // 1 Queen Bee
                return piecesRemain[player][0];
            case SPIDER: //4 Spiders
                return piecesRemain[player][1];
            case BEETLE: //4 Beetles
                return piecesRemain[player][2];
            case GRASSHOPPER: //6 Grasshoppers
                return piecesRemain[player][3];
            case ANT: //6 Soldier Ants
                return piecesRemain[player][4];
        }
        return 0;
    }

    /**
     * Ensures Tile tile is empty and next to an occupied Tile that is not the selected Tile.
     * Helper function for piece search functions
     *
     * @param x - position in gameBoard corresponding to potential space tile could be placed
     * @param y - position in gameBoard corresponding to potential space tile could be placed
     * @return true if not touching other players color, false if touching other players color
     */
    public boolean touchingOtherColor(int x, int y, Tile.PlayerPiece other){
        if (x % 2 == 0) {
            // For even rows
                if (boundsCheck(x-1, y)){
                    if (gameBoard.get(x - 1).get(y).getPlayerPiece() == other) {
                    //Check tile above left of tile
                    return false;
                    }
                }
                if (boundsCheck(x-1 ,y+1)) {
                    if (gameBoard.get(x - 1).get(y + 1).getPlayerPiece() == other) {
                        //Check tile above right of tile
                        return false;
                    }
                }
                if (boundsCheck(x, y-1)) {
                    if (gameBoard.get(x).get(y - 1).getPlayerPiece() == other) {
                        //Check tile to the left of tile
                        return false;
                    }
                }
                if (boundsCheck(x, y+1)) {
                    if (gameBoard.get(x).get(y + 1).getPlayerPiece() == other) {
                        //Check tile to the right of tile
                        return false;
                    }
                }
                if (boundsCheck(x+1, y)) {
                    if (gameBoard.get(x + 1).get(y).getPlayerPiece() == other) {
                        //Check tile below left of tile
                        return false;
                    }
                }
                if (boundsCheck(x+1, y+1)) {
                    if (gameBoard.get(x + 1).get(y + 1).getPlayerPiece() == other) {
                        //Check tile below right of tile
                        return false;
                    }
                }
            }
            else {
                // For odd rows
                if (boundsCheck(x-1, y-1)) {
                    if (gameBoard.get(x - 1).get(y - 1).getPlayerPiece() == other) {
                        //Check tile above left of tile
                        return false;
                    }
                }
                if (boundsCheck(x-1, y)) {
                    if (gameBoard.get(x - 1).get(y).getPlayerPiece() == other) {
                        //Check tile above right of tile
                        return false;
                    }
                }
                if (boundsCheck(x, y-1)) {
                    if (gameBoard.get(x).get(y - 1).getPlayerPiece() == other) {
                        //Check tile to the left of tile
                        return false;
                    }
                }
                if (boundsCheck(x, y+1)) {
                    if (gameBoard.get(x).get(y + 1).getPlayerPiece() == other) {
                        //Check tile to the right of tile
                        return false;
                    }
                }
                if (boundsCheck(x+1, y-1)) {
                    if (gameBoard.get(x + 1).get(y - 1).getPlayerPiece() == other) {
                        //Check tile below left of tile
                        return false;
                    }
                }
                if (boundsCheck(x+1, y)) {
                    if (gameBoard.get(x + 1).get(y).getPlayerPiece() == other) {
                        //Check tile below right of tile
                        return false;
                    }
                }
            }
        return true;
    }

    /**
     * Updates potentialMoves based on tile selected by player to be placed
     * @param inTile the tile being selected
     * for condition 0 it is the only tile on the gameboard
     * for condition 1 it is the tile selected from the players hand
     * @param toggle what mode to be in
     * 0 - only one tile on game board, so potentials all positions surrounding tile
     * 1 - multiple tiles, so potentials empty spaces around tiles with same selected color
     *     that are not touching other players color
     */
    public void updatePotentialsForPlacing(Tile inTile, int toggle){
        if(toggle == 0){
            surroundingTiles(inTile, toggle);
        }
        else{
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numRows * 2; j++) {
                    if (gameBoard.get(i).get(j).getPlayerPiece() == inTile.getPlayerPiece()){
                        //if(boundsCheck(i, j)) {
                            surroundingTiles(gameBoard.get(i).get(j), toggle);
                        //}
                    }
                }
            }
        }
    }

    /**
     * updates potentialMoves based on tile on board
     * used when placing a tile on the board from the hand of a player
     * @param placedTile the tile that is being checked around
     * @param toggle tells whether to put it in look for pieces of the same color
     *               or look for the pieces surrounding the placeTile no matter the color
     */
    public void surroundingTiles(Tile placedTile, int toggle){
        int x = placedTile.getIndexX();
        int y = placedTile.getIndexY();
        Tile.PlayerPiece playerColor = placedTile.getPlayerPiece();
        Tile.PlayerPiece otherColor;
        if(playerColor == Tile.PlayerPiece.B){
            otherColor = Tile.PlayerPiece.W;
        }
        else{
            otherColor = Tile.PlayerPiece.B;
        }
        //we're not allowing players to place things on the edge as there is no edge in Hive
        //Not a bug it's a FeAtURe
        if (boundsCheck(x, y)) {//only check this if we're not on the edge
            switch (toggle) {
                case 0:
                    if (x % 2 == 0) { //even row
                        potentialMoves.add(gameBoard.get(x - 1).get(y)); //tile above left of tile
                        potentialMoves.add(gameBoard.get(x - 1).get(y + 1)); //tile above right of tile
                        potentialMoves.add(gameBoard.get(x).get(y - 1)); //tile to the left of tile
                        potentialMoves.add(gameBoard.get(x).get(y + 1)); //tile to the right of tile
                        potentialMoves.add(gameBoard.get(x + 1).get(y)); //tile below left of tile
                        potentialMoves.add(gameBoard.get(x + 1).get(y + 1)); //tile below right of tile
                    } else {
                        potentialMoves.add(gameBoard.get(x - 1).get(y - 1)); //tile above left of tile
                        potentialMoves.add(gameBoard.get(x - 1).get(y)); //tile above right of tile
                        potentialMoves.add(gameBoard.get(x).get(y - 1)); //tile to the left of tile
                        potentialMoves.add(gameBoard.get(x).get(y + 1)); //tile to the right of tile
                        potentialMoves.add(gameBoard.get(x + 1).get(y - 1)); //tile below left of tile
                        potentialMoves.add(gameBoard.get(x + 1).get(y)); //tile below right of tile
                    }
                    break;
                case 1: //make all the statements separate ifs and boundCheck each statement first
                    if (x % 2 == 0) {
                        // For even rows
                        if(boundsCheck(x - 1, y)) {
                            if (gameBoard.get(x - 1).get(y).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x - 1, y, otherColor)) {
                                potentialMoves.add(gameBoard.get(x - 1).get(y)); //tile above left of tile
                            }
                        }
                        if (boundsCheck(x-1, y+1)) {
                            if (gameBoard.get(x - 1).get(y + 1).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x - 1, y + 1, otherColor)) {
                                potentialMoves.add(gameBoard.get(x - 1).get(y + 1)); //tile above right of tile
                            }
                        }
                        if (boundsCheck(x, y-1)) {
                            if (gameBoard.get(x).get(y - 1).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x, y - 1, otherColor)) {
                                potentialMoves.add(gameBoard.get(x).get(y - 1)); //tile to the left of tile
                            }
                        }
                        if (boundsCheck(x, y+1)) {
                            if (gameBoard.get(x).get(y + 1).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x, y + 1, otherColor)) {
                                potentialMoves.add(gameBoard.get(x).get(y + 1)); //tile to the right of tile
                            }
                        }
                        if (boundsCheck(x+1, y)) {
                            if (gameBoard.get(x + 1).get(y).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x + 1, y, otherColor)) {
                                potentialMoves.add(gameBoard.get(x + 1).get(y)); //tile below left of tile
                            }
                        }
                        if (boundsCheck(x+1, y+1)) {
                            if (gameBoard.get(x + 1).get(y + 1).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x + 1, y + 1, otherColor)) {
                                potentialMoves.add(gameBoard.get(x + 1).get(y + 1)); //tile below right of tile
                            }
                        }
                    }
                    else {
                        // For odd rows
                        if (boundsCheck(x-1, y-1)) {
                            if (gameBoard.get(x - 1).get(y - 1).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x - 1, y - 1, otherColor)) {
                                potentialMoves.add(gameBoard.get(x - 1).get(y - 1)); //tile above left of tile
                            }
                        }
                        if (boundsCheck(x-1, y)) {
                            if (gameBoard.get(x - 1).get(y).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x - 1, y, otherColor)) {
                                potentialMoves.add(gameBoard.get(x - 1).get(y)); //tile above right of tile
                            }
                        }
                        if (boundsCheck(x, y-1)) {
                            if (gameBoard.get(x).get(y - 1).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x, y - 1, otherColor)) {
                                potentialMoves.add(gameBoard.get(x).get(y - 1)); //tile to the left of tile
                            }
                        }
                        if (boundsCheck(x, y+1)) {
                            if (gameBoard.get(x).get(y + 1).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x, y + 1, otherColor)) {
                                potentialMoves.add(gameBoard.get(x).get(y + 1)); //tile to the right of tile
                            }
                        }
                        if (boundsCheck(x+1, y-1)) {
                            if (gameBoard.get(x + 1).get(y - 1).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x + 1, y - 1, otherColor)) {
                                potentialMoves.add(gameBoard.get(x + 1).get(y - 1)); //tile below left of tile
                            }
                        }
                        if (boundsCheck(x+1, y)) {
                            if (gameBoard.get(x + 1).get(y).getType() == Tile.Bug.EMPTY &&
                                    touchingOtherColor(x + 1, y, otherColor)) {
                                potentialMoves.add(gameBoard.get(x + 1).get(y)); //tile below right of tile
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * testing class for playing Oracle, adds a tile to the gameboard,
     * circumvents any move/error checking
     * @param newTile the tile that is being added to the gameBoard
     */
    public void addTile(Tile newTile){
        Tile beneath = gameBoard.get(newTile.getIndexX()).get(newTile.getIndexY());
        if( beneath != null) {
            newTile.setOnTopOf(beneath);
        }
        gameBoard.get(newTile.getIndexX()).set(newTile.getIndexY(), newTile);
    }

    /**
     *  returns null if the get request is out of bounds
     * @param x the row index
     * @param y the col index
     * @return the tile if it is within the limits of the gameboard, otherwise null
     */
    public Tile getTile(int x, int y){
        if(!boundsCheck(x, y)){
            return null;
        }
        return gameBoard.get(x).get(y);
    }

    /**
     * This method looks through a given array list and compares indexes of the tiles within the list
     * with the gamepiece's indexes, if they're the same this tile exists within.
     * @param potentialArr the array that the gamepiece is hoping to be in
     * @param gamePiece the piece that this function is looking for in the potentialArr
     * @return true if the tile was found, false otherwise
     */
    public boolean containsTile(ArrayList<Tile> potentialArr, Tile gamePiece){
        for(int i = 0; i < potentialArr.size(); i++){
            if(potentialArr.get(i).getIndexX() == gamePiece.getIndexX()
                    && potentialArr.get(i).getIndexY() == gamePiece.getIndexY()){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return the gameBoard as a 2D arraylist
     */
    public ArrayList<ArrayList<Tile>> getGameBoard(){
        return gameBoard;
    }

    /**
     * The potentialMoves of a selected tile
     * @return potentialMoves, a 1D arrayList
     */
    public ArrayList<Tile> getPotentialMoves() {
        return potentialMoves;
    }

    /**
     * player 0's pieces are in row 0, player 1's in row 1
     * @return the pieces remaining in the 2 player's hands
     */
    public int[][] getPiecesRemain(){
        return piecesRemain;
    }

    /**
     * Set the current turn
     * @param turn, either 1 or 0
     */
    public void setWhoseTurn(int turn){
       this.whoseTurn = turn;
    }

    /**
     *
     * @return the index of whose turn it is, either 0 or 1
     */
    public int getWhoseTurn(){
        return whoseTurn;
    }
    public HiveGameState getUndoTurn() {
        return getUndoTurn();
    }
    /**
     *
     * @return sets the image popping up
     */

    public void setRulesClicked(boolean flag){
        rulesClicked = flag;
    }

    /**
     *
     * @return gets the image popping up
     */
    public boolean getRulesClicked(){
        return rulesClicked;
    }

    /**
     *
     * @return the current size of the rows of the gameboard
     */
    public int getBoardSize(){
        return numRows;
    }

    /**
     *
     * @return the id of the imageButton selected currently
     */
    public int getCurrentIdSelected(){
        return currentIdSelected;
    }

    /**
     *
     * @param id of the imageButton that should be set
     */
    public void setCurrentIdSelected(int id){
        currentIdSelected = id;
    }

    /**
     *
     * @return the list of tiles that the computer player has down on the board
     */
    public ArrayList<Tile> getComputerPlayersTiles() {
        return computerPlayersTiles;
    }

    /**
     *
     * @param computerPlayersTiles the list of computerTiles to point to
     */
    public void setComputerPlayersTiles(ArrayList<Tile> computerPlayersTiles) {
        this.computerPlayersTiles = computerPlayersTiles;
    }


    /**
     *
     * @param tileToBeAdded the tile to be added to the list of computer tiles currently on the board
     */
    public void addComputerPlayersTiles(Tile tileToBeAdded){
        this.computerPlayersTiles.add(tileToBeAdded);
    }

    /**
     *
     * @param potentialMoves sets the potentialMoves of a piece to the incoming arrayList
     */
    public void setPotentialMoves(ArrayList<Tile> potentialMoves) {
        this.potentialMoves = new ArrayList<Tile>();
        for(int i = 0; i < potentialMoves.size(); i++){
            this.potentialMoves.add(potentialMoves.get(i)); //removes from the incoming array and adds it to the new one held here
        }
    }

    /**
     *
     * @return true if an imageButton is selected, false otherwise
     */
     public boolean getSelectFlag(){
        return this.selectFlag;
     }

    /**
     *
     * @param selectFlag sets to true if an imageButton is selected in the onClick
     */
    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }

    /**
     *
     * @param current the current board to be stored to if the player wants to undo their move
     */
    public void setUndoTurn(HiveGameState current) {
        this.undoTurn = new HiveGameState(current); }

    /**
     *
     * @return the placePiece boolean saying if a piece has been played during a turn
     */
    public boolean getPlacedPiece() {return placedPiece;}

    /**
     *
     * @param bool sets the state's piece for if a place has been played during a turn
     */
    public void setPlacedPiece(boolean bool) {this.placedPiece = bool;}

    /**
     * Basic bounds checking for the gameBoard
     *
     * @param row the row that's being checked
     * @param col the col that's being checked
     * @return true if within the gameboard and NOT on an edge
     */
    public boolean boundsCheck(int row, int col){
        if (row >= 0 && row < (numRows)  && col >= 0 && col < (numCols)) {
            return true;
        }
        return false;
    }

    /**
     * Basic bounds checking for the gameBoard placing a piece
     *
     * @param row the row that's being checked
     * @param col the col that's being checked
     * @return true if within the gameboard and NOT on an edge
     */
    public boolean boundsCheckPlace(int row, int col){
        if (row > 1 && row < (numRows - 2)  && col > 1 && col < (numCols - 2)) {
            return true;
        }
        return false;
    }

    /**
     * Returns an integer representation of who won or -1 if game not won
     * @return 0 if player 1 lost, 1 if player 2 lost, and 2 if they tied
     */
    public int winOrNah(){
        Tile.PlayerPiece player;
        int player1Surr = 0; //black
        int player2Surr = 0; //white
        for (int x = 0; x < numRows; x++){
            for (int y = 0; y < gameBoard.get(x).size(); y++){
                if (gameBoard.get(x).get(y).getType() == Tile.Bug.QUEEN_BEE){
                    player = gameBoard.get(x).get(y).getPlayerPiece();
                    if (x % 2 == 0) { //even row
                        if (boundsCheck(x-1, y)){
                            if (gameBoard.get(x - 1).get(y).getType() != Tile.Bug.EMPTY){ //tile above left of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x-1, y+1)) {
                            if (gameBoard.get(x - 1).get(y + 1).getType() != Tile.Bug.EMPTY) { //tile above right of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x, y-1)) {
                            if (gameBoard.get(x).get(y - 1).getType() != Tile.Bug.EMPTY) { //tile to the left of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x, y+1)) {
                            if (gameBoard.get(x).get(y + 1).getType() != Tile.Bug.EMPTY) { //tile to the right of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x+1, y)) {
                            if (gameBoard.get(x + 1).get(y).getType() != Tile.Bug.EMPTY) { //tile below left of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x+1, y+1)) {
                            if (gameBoard.get(x + 1).get(y + 1).getType() != Tile.Bug.EMPTY) { //tile below right of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                    }
                    else {
                        if (boundsCheck(x-1, y-1)) {
                            if (gameBoard.get(x - 1).get(y - 1).getType() != Tile.Bug.EMPTY) { //tile above left of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x-1, y)) {
                            if (gameBoard.get(x - 1).get(y).getType() != Tile.Bug.EMPTY) { //tile above right of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x, y-1)) {
                            if (gameBoard.get(x).get(y - 1).getType() != Tile.Bug.EMPTY) { //tile to the left of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x, y+1)) {
                            if (gameBoard.get(x).get(y + 1).getType() != Tile.Bug.EMPTY) { //tile to the right of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x+1, y-1)) {
                            if (gameBoard.get(x + 1).get(y - 1).getType() != Tile.Bug.EMPTY) { //tile below left of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                        if (boundsCheck(x+1, y)) {
                            if (gameBoard.get(x + 1).get(y).getType() != Tile.Bug.EMPTY) { //tile below right of tile
                                if (player == Tile.PlayerPiece.B){
                                    player1Surr++;
                                }
                                else{
                                    player2Surr++;
                                }
                            }
                        }
                    }
                }
            }
        }
        int whoWon = -1; // 0 = no one, 1 = player1 (white), 2 = player2 (black), 3 = both
        if (player1Surr == 6){
            whoWon = 0;
        }
        else if (player2Surr == 6){
            whoWon = 1;
        }
        else if (player1Surr == 6 && player2Surr == 6){
            whoWon = 2;
        }
        return whoWon;
    }



    /**
     * Resizes the board by adding a row to the top and bottom
     * and adding a column to the left and right
     */
    public void reSeize(){
        for (int row = 0; row < numRows; row++){
            for (int col = 0; col < gameBoard.get(row).size(); col--){
                if (gameBoard.get(row).get(col).getType() != Tile.Bug.EMPTY){
                    if (row == 0){ //tile in first row
                        gameBoard.add(0, new ArrayList<Tile>(numCols)); //add new row at top
                        for (int i = 0; i < numCols; i++){ //fill it with empty tiles
                            gameBoard.get(0).add(i, new Tile((0), i, Tile.PlayerPiece.EMPTY));
                        }
                        numRows++; //update numRows
                    }
                    if (col == 0){ //tile in first col
                        //add col of empty tiles to left
                        for (int i = 0; i < numRows; i++){
                            gameBoard.get(i).add(0, new Tile(i, 0, Tile.PlayerPiece.EMPTY));
                        }
                        numCols++; //update numCols
                    }
                    if (row == (numRows -1)){
                        gameBoard.add(new ArrayList<Tile>(numCols)); //add new row at bottom
                        for (int i = 0; i < numCols; i++){ //fill it with empty tiles
                            gameBoard.get(numCols-1).add(i, new Tile((numCols-1), i, Tile.PlayerPiece.EMPTY));
                        }
                        numRows++; //update numRows
                    }
                    if (col == (numCols - 1)){
                        //add col of empty tiles to left
                        for (int i = 0; i < numRows; i++){
                            gameBoard.get(i).add(0, new Tile(i, 0, Tile.PlayerPiece.EMPTY));
                        }
                        numCols++; //update numCols
                    }
                }
            }
        }
    }
}

package com.example.hiveframework.hive;


import com.example.hiveframework.GameFramework.infoMessage.GameState;

import java.io.Serializable;
import java.util.ArrayList;
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
    private ArrayList<ArrayList<Tile>> displayBoard;
    private int piecesRemain[][]; //represents how many of each bug a player has
    private int whoseTurn;
    private int countVisited;
    private int currentIdSelected;

    private static final int tileSize = 300;
    private final int GBSIZE = 7; //size of the gameboard
    private ArrayList<Tile> potentialMoves;
    /**
     * Default constructor.
     */
    public HiveGameState(){
        //Initialize gameBoard to be 8 rows of empty tiles
        gameBoard = new ArrayList<ArrayList<Tile>>();
        for(int i=0; i < GBSIZE; i++) {
            gameBoard.add(new ArrayList<Tile>(GBSIZE*2)); //rectangle so make the col's twice the size
        }
        for(int i = 0; i < GBSIZE; i++){
            for(int j = 0; j < GBSIZE*2; j++){ //add twice as many columns as rows to make rect grid
                gameBoard.get(i).add(j, new Tile (i, j, Tile.PlayerPiece.EMPTY));
            }
        }
        //Initialize displayBoard to be mirror gameBoard
        //HELLO! I don't think we need this if we draw everything based on the gameBoard
        displayBoard = new ArrayList<ArrayList<Tile>>();
        for(int i=0; i < GBSIZE; i++) {
            displayBoard.add(new ArrayList<Tile>(GBSIZE));
        }
        for(int i = 0; i < GBSIZE; i++){
            for(int j = 0; j < GBSIZE*2; j++){
                displayBoard.get(i).add(j, gameBoard.get(i).get(j));
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
    }

    /**
     * Deep copy constructor.
     * @param other returns a copy of the current gameState
     */
    public HiveGameState(HiveGameState other){
        this.gameBoard = new ArrayList<ArrayList<Tile>>();
        for(int i=0; i < GBSIZE; i++) {
            this.gameBoard.add(new ArrayList<Tile>(GBSIZE));
        }

        for (int row = 0; row < GBSIZE; row++){
            for (int col = 0; col < GBSIZE*2; col++){
                Tile copyTile = new Tile(other.gameBoard.get(row).get(col));
                this.gameBoard.get(row).add(col, copyTile);
            }
        }

        this.displayBoard = new ArrayList<ArrayList<Tile>>();
        for(int i=0; i < GBSIZE; i++) {
            this.displayBoard.add(new ArrayList<Tile>(GBSIZE));
        }
        for (int row = 0; row < GBSIZE; row++){
            for (int col = 0; col < GBSIZE; col++){
                Tile copyTile = new Tile(other.displayBoard.get(row).get(col));
                this.displayBoard.get(row).add(col, copyTile);
            }
        }
        this.piecesRemain = new int[2][5];
        for (int i = 0; i < other.getPiecesRemain().length; i++){
            for (int j = 0; j < other.getPiecesRemain()[i].length; j++){
                this.piecesRemain[i][j] = other.getPiecesRemain()[i][j];
            }
        }

        this.whoseTurn = other.getWhoseTurn();
        this.currentIdSelected = getCurrentIdSelected();

        this.potentialMoves = new ArrayList<Tile>();
        for (int i = 0; i < other.getPotentialMoves().size(); i++){
            Tile copyTile = new Tile((other.getPotentialMoves()).get(i));
            this.potentialMoves.add(copyTile);
        }
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
     * Exits the game,
     * called when the exit game button is clicked
     * @return true in order to exit the game
     */
    public boolean endGame(){
        return true;
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

        if(!breakHive(tile)){//as long as the move doesn't break the hive
            //and the type isn't grasshopper or beetle since they
            //don't obey the freedom of movement rule
            if(tile.getType() != Tile.Bug.GRASSHOPPER || tile.getType() != Tile.Bug.BEETLE){
                if(freedom(tile)){//obeys freedom of movement (not surrounded)
                    return true;
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
     * @param tile the tile that was selected and wants to be placed on the board
     * @return true if the tile is able to be placed, false if it is not permitted to be selected/placed
     */
    public boolean selectFromHand(Tile tile){
        //how many bugs are on the board
        int bugCounter = 0;
        Tile tileInQuestion = new Tile(tile); //default just in case it doesn't get initialized later

        for(int i = 0; i < gameBoard.size(); i++) {
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
            for(int i = 0; i < gameBoard.size(); i++) {
                for (int j = 0; j < gameBoard.get(i).size(); j++) {
                    potentialMoves.add(gameBoard.get(i).get(j));
                }
            }
            return true;
        }
        else if(bugCounter == 1){ //if there is exactly one piece placed down
            //then only add the pieces that are surrounding that piece as potential spots
            updatePotentialsForPlacing(tileInQuestion, 0); //this function adds all the pieces to potentialMoves
            return true;               // and takes in a mode selector to know which tiles to add
        }
        else if(bugCounter >= 2){ //the first pieces have been put down for each player
            updatePotentialsForPlacing(tile, 1); //only add the surrounding tiles that touch only the same color as the current tile
            return true;
        }

        return false;
    }

    /**
     * Checks the hive (ie gameboard) to see if the entire board is connected and if
     * without the Tile in the spot it currently is the board would STILL be connected
     * @param tile the piece being checked against breaking the hive
     * @return false if move would NOT break the hive, true if move would break hive
     */
    public boolean breakHive(Tile tile){
        int row = -1;
        int col = -1;
        boolean firstFound = false;

        //perform a DFS on the gameBoard
        //copy the old gameBoard into a new temporary board to perform dfs on
        ArrayList<ArrayList<Tile>> testBoard = new ArrayList<ArrayList<Tile>>();
        for(int i=0; i < GBSIZE; i++) {
            testBoard.add(new ArrayList<Tile>(GBSIZE*2));
        }
        for(int i = 0; i < gameBoard.size(); i++) {
            for (int j = 0; j < gameBoard.get(i).size(); j++) {
                if (tile.getIndexX() != -1) { //if this is a tile that is already on the board then check to take it out
                    if (i == tile.getIndexX() && j == tile.getIndexY()) {
                        testBoard.get(i).set(j, new Tile(i, j, Tile.PlayerPiece.EMPTY)); //take out the tile in question
                    } else {
                        testBoard.get(i).set(j, new Tile(gameBoard.get(i).get(j)));
                    }
                }
                else{ //just copy the gameBoard over to the testBoard instead
                    testBoard.get(i).set(j, new Tile(gameBoard.get(i).get(j)));
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
            if(countVisited == 21 - handPieces) { //there are 22 total pieces, 21 counting the piece taken out
                return false; //if the board can be traversed with bfs and all
                            //tiles on the boardhave been denoted as visited then
                            //return false the hive has NOT been broken
            }
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
        Queue<Tile> tileQueue = (Queue<Tile>) new ArrayList<Tile>();
        tileQueue.offer(board.get(row).get(col)); //pass in the starting x and y position as the root

        while(!tileQueue.isEmpty()){
            int x = row;
            int y = col;

            tileQueue.poll().setVisited(true); //visits the tile at front of queue
            countVisited++; //update the count to reflect a visited tile

            if (x % 2 == 0) {
                // For even rows
                //LU: (row--, col), LM: (row, col--), LD: (row++, col),
                //RU: (row--, col++), RM: (row, col++), RD: (row++, col++)

                //Queue all possible valid neighbors
                if (isValidBFS(board.get(x - 1).get(y))) {
                    //Check tile above left of tile
                    tileQueue.offer(board.get(x - 1).get(y));
                    bfs(x - 1, y, board); //call bbfs on neighbor
                }
                if (isValidBFS(board.get(x - 1).get(y + 1))) {
                    //Check tile above right of tile
                    tileQueue.offer(board.get(x - 1).get(y + 1));
                    bfs(x - 1, y+1, board); //call bbfs on neighbor
                }
                if(isValidBFS(board.get(x).get(y-1))){
                    //Check tile to the left of tile
                    tileQueue.offer(board.get(x).get(y-1));
                    bfs(x, y-1, board); //call bbfs on neighbor
                }
                if(isValidBFS(board.get(x).get(y+1))){
                    //Check tile to the right of tile
                    tileQueue.offer(board.get(x).get(y+1));
                    bfs(x, y+1, board); //call bbfs on neighbor
                }
                if(isValidBFS(board.get(x+1).get(y))){
                    //Check tile to the lower left of tile
                    tileQueue.offer(board.get(x+1).get(y));
                    bfs(x + 1, y, board); //call bbfs on neighbor
                }
                if(isValidBFS(board.get(x+1).get(y+1))){
                    //Check tile to the lower right of tile
                    tileQueue.offer(board.get(x+1).get(y + 1));
                    bfs(x + 1, y+1, board); //call bbfs on neighbor
                }
            }
            else{
                //for odd rows
                //LU: (row--, col--), LM: (row, col--), LD: (row++, col--),
                //RU: (row--, col), RM: (row, col++), RD: (row++, col)

                //Queue all possible valid neighbors
                if (isValidBFS(board.get(x - 1).get(y - 1))) {
                    //Check tile above left of tile
                    tileQueue.offer(board.get(x - 1).get(y - 1));
                    bfs(x - 1, y-1, board); //call bbfs on neighbor
                }
                if (isValidBFS(board.get(x - 1).get(y))) {
                    //Check tile above right of tile
                    tileQueue.offer(board.get(x - 1).get(y));
                    bfs(x - 1, y, board); //call bbfs on neighbor
                }
                if(isValidBFS(board.get(x).get(y-1))){
                    //Check tile to the left of tile
                    tileQueue.offer(board.get(x).get(y-1));
                    bfs(x, y-1, board); //call bbfs on neighbor
                }
                if(isValidBFS(board.get(x).get(y+1))){
                    //Check tile to the right of tile
                    tileQueue.offer(board.get(x).get(y+1));
                    bfs(x, y+1, board); //call bbfs on neighbor
                }
                if(isValidBFS(board.get(x+1).get(y-1))){
                    //Check tile to the lower left of tile
                    tileQueue.offer(board.get(x+1).get(y-1));
                    bfs(x + 1, y-1, board); //call bbfs on neighbor
                }
                if(isValidBFS(board.get(x+1).get(y))){
                    //Check tile to the lower right of tile
                    tileQueue.offer(board.get(x+1).get(y));
                    bfs(x + 1, y, board); //call bbfs on neighbor
                }
            }
        }
        return countVisited;
    }

    /**
     * Helper method for the BFS,
     * determines if a tile is valid to visit
     * @param tile the tile about to be visited
     * @return true if valid to visit, false otherwise
     */
    public boolean isValidBFS(Tile tile){

        if(tile.getType() == Tile.Bug.EMPTY){
            return false; //empty spaces aren't part of the graph
        }

        if(tile.getIndexX() < 0 || tile.getIndexY() < 0 ||
           tile.getIndexX() >= gameBoard.size()*2 || tile.getIndexY() >= gameBoard.size()){
            return false; //out of bounds
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
                if (gameBoard.get(x - 1).get(y).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LU = true;
                    sum += (x - 1) + y;
                }
                //Check tile above right of tile
                if (gameBoard.get(x - 1).get(y + 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RU = true;
                    sum += (x - 1) + (y + 1);
                }
                // Check tile to the left of tile
                if (gameBoard.get(x).get(y - 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LM = true;
                    sum += x + (y - 1);
                }
                // Check tile to the right of tile
                if (gameBoard.get(x).get(y + 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RM = true;
                    sum += x + (y + 1);
                }
              //Check tile below left of tile
                if (gameBoard.get(x + 1).get(y).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LD = true;
                    sum += (x + 1) + y;
                }
                // Check tile below right of tile
                if (gameBoard.get(x + 1).get(y + 1).getType() != Tile.Bug.EMPTY) {
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
                if (gameBoard.get(x-1).get(y-1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LU = true;
                    sum += (x - 1) + (y - 1);
                }
                //Check tile above right of tile
                if (gameBoard.get(x - 1).get(y).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RU = true;
                    sum += (x - 1) + y;
                }
                // Check tile to the left of tile
                if (gameBoard.get(x).get(y - 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LM = true;
                    sum += x + (y - 1);
                }
                // Check tile to the right of tile
                if (gameBoard.get(x).get(y + 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    RM = true;
                    sum += x + (y + 1);
                }
                //Check tile below left of tile
                if (gameBoard.get(x + 1).get(y - 1).getType() != Tile.Bug.EMPTY) {
                    count++;
                    LD = true;
                    sum += (x + 1) + (y - 1);
                }
                // Check tile below right of tile
                if (gameBoard.get(x + 1).get(y).getType() != Tile.Bug.EMPTY) {
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
        //we should make sure the person whose turn it is has placed their queen somewhere
        if(validMove(tile)) {
            //if the piece can be moved legally
            switch (tile.getType()){
                case ANT:
                    //utilize the ant function to find potential moves
                    return antMove(tile, tile.getIndexX(), tile.getIndexY());
                case BEETLE:
                    //utilize the beetleSearch function to find potential moves.
                    return beetleSearch(tile);
                case GRASSHOPPER:
                    //utilize the startGrasshopperSearch function to find potential moves.
                    return startGrasshopperSearch(tile);
                case SPIDER:
                    //utilize the SpiderSearch function to find potential moves.
                    return spiderSearch(tile);
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

                if (nextTo(tile, gameBoard.get(x - 1).get(y))) {
                    //Check tile above left of tile is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x - 1).get(y));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x - 1).get(y + 1))) {
                    //Check tile above right of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x - 1).get(y + 1));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x).get(y - 1))) {
                    //Check tile to the left of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x).get(y - 1));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x).get(y + 1))) {
                    //Check tile to the right of ti is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x).get(y + 1));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x + 1).get(y))) {
                    //Check tile below left of tile is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x + 1).get(y));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x + 1).get(y + 1))) {
                    //Check tile below right of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x + 1).get(y + 1));
                    return true;
                }
            } else {
                // For odd rows

                if (nextTo(tile, gameBoard.get(x - 1).get(y - 1))) {
                    //Check tile above left of tile is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x - 1).get(y - 1));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x - 1).get(y))) {
                    //Check tile above right of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x - 1).get(y));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x).get(y - 1))) {
                    //Check tile to the left of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x).get(y - 1));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x).get(y + 1))) {
                    //Check tile to the right of ti is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x).get(y + 1));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x + 1).get(y - 1))) {
                    //Check tile below left of tile is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x + 1).get(y - 1));
                    return true;
                } else if (nextTo(tile, gameBoard.get(x + 1).get(y))) {
                    //Check tile below right of til is nextTo another tile
                    //If true, add tile to ArrayList<Tile> potentials
                    potentialMoves.add(gameBoard.get(x + 1).get(y));
                    return true;
                }

            }
            return false;
    }

    /**
     * Checks for valid moves for the Queen Bee tile. Modifies ArrayList potentialMoves to reflect
     * valid moves for the queen bee tile. Returns false if no moves are found.
     *
     * @param tile the tile the player wishes to move
     * @return
     */
    public boolean queenSearch(Tile tile) {
        //Ensure adjacent tiles are empty with things next to them
        int x = tile.getIndexX();
        int y = tile.getIndexY();

        if (x % 2 == 0) {
            // For even rows

            if(gameBoard.get(x-1).get(y).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x-1).get(y))){
                //Check tile above left of tile is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x-1).get(y));
                return true;
            } else if(gameBoard.get(x-1).get(y+1).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x-1).get(y+1))) {
                //Check tile above right of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x-1).get(y+1));
                return true;
            } else if(gameBoard.get(x).get(y-1).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x).get(y-1))) {
                //Check tile to the left of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x).get(y-1));
                return true;
            } else if(gameBoard.get(x).get(y+1).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x).get(y+1))) {
                //Check tile to the right of ti is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x).get(y+1));
                return true;
            } else if(gameBoard.get(x+1).get(y).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x+1).get(y))) {
                //Check tile below left of tile is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x+1).get(y));
                return true;
            } else if(gameBoard.get(x+1).get(y+1).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x+1).get(y+1))) {
                //Check tile below right of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x+1).get(y+1));
                return true;
            }
        } else {
            // For odd rows

            if(gameBoard.get(x-1).get(y-1).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x-1).get(y-1))){
                //Check tile above left of tile is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x-1).get(y-1));
               return true;
            } else if(gameBoard.get(x-1).get(y).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x-1).get(y))) {
                //Check tile above right of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x-1).get(y));
                return true;
            } else if(gameBoard.get(x).get(y-1).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x).get(y-1))) {
                //Check tile to the left of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x).get(y-1));
                return true;
            } else if(gameBoard.get(x).get(y+1).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x).get(y+1))) {
                //Check tile to the right of ti is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x).get(y+1));
                return true;
            } else if(gameBoard.get(x+1).get(y-1).getType() == Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x+1).get(y-1))) {
                //Check tile below left of tile is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x+1).get(y-1));
                return true;
            } else if(gameBoard.get(x+1).get(y).getType() != Tile.Bug.EMPTY &&
                    nextTo(tile, gameBoard.get(x+1).get(y))) {
                //Check tile below right of til is empty and nextTo
                //If true, add tile to ArrayList<Tile> potentials
                potentialMoves.add(gameBoard.get(x+1).get(y));
                return true;
            }

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
    /**ant tile movement
     *
     */
    public boolean antMove(Tile tile,int x,int y){
        if(antValidMove(tile,x,y))
        {
            //makeMove(tile, x, y);
            return true;
        }
        return false;
    }
    //determines if the ant tile is on edge
    private boolean antValidMove(Tile tile,int x,int y) {
        Tile nextile = gameBoard.get(x).get(y);
        //create arraylist of potential invalid moves

        ArrayList<int[]> inValidMoves = new ArrayList<int[]>();
        for (int s = 0; s < 14; s++) {
            for (int j = 0; j < 14; j++) {
                int[] inValid = new int[2];
                if (s == 0 || s == 14 || j == 0 || j == 14) {
                    inValid[0] = s;
                    inValid[1] = j;
                    inValidMoves.add(inValid);
                }
            }
        }
        int[] potentialMove = new int[2];
        potentialMove[0] = nextile.getIndexX();
        potentialMove[1] = nextile.getIndexY();
        if(inValidMoves.contains(potentialMove)){
            return false;
        }
        return true;
    }

    public boolean spiderSearch (Tile tile ) {
        // The tile the spider is on
        int m = tile.getIndexX();
        int n = tile.getIndexY();

        ArrayList<int[]> ValidMoves = new ArrayList<int[]>();
        for (int s = m - 3; s < m + 3; m++) {
            for (int j = n - 3; j < n + 3; n++) {
                int[] Valid = new int[2];

                if (s == m - 3 || s == m + 3 || j == n - 3 || j == n + 3) {

                    Valid[0] = s;
                    Valid[1] = j;
                    Tile newTile = new Tile(s, j, tile.getPlayerPiece());
                    // check if valid move breaks bfs

                    if (breakHive(newTile)){
                        break;
                    }

                    potentialMoves.add(newTile);

                }

            }
        }
        return true;
    }

    /**
     * Ensures Tile tile is empty and next to an occupied Tile that is not the selected Tile.
     * Helper function for piece search functions
     *
     * @param selectedTile the tile the player wishes to move
     * @param tile the tile to be checked against
     * @return
     */
    public boolean nextTo(Tile selectedTile, Tile tile) {
        if (tile.getType() == Tile.Bug.EMPTY) {
            // Assign tile coordinates to integer values for ease of handling
            int x = tile.getIndexX();
            int y = tile.getIndexY();

            if (x % 2 == 0) {
                // For even rows

                if(gameBoard.get(x-1).get(y).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x-1).get(y) != selectedTile){
                    //Check tile above left of tile
                    return true;
                } else if(gameBoard.get(x-1).get(y+1).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x-1).get(y+1) != selectedTile) {
                    //Check tile above right of tile
                    return true;
                } else if(gameBoard.get(x).get(y-1).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x).get(y-1) != selectedTile) {
                    //Check tile to the left of tile
                    return true;
                } else if(gameBoard.get(x).get(y+1).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x).get(y+1) != selectedTile) {
                    //Check tile to the right of tile
                    return true;
                } else if(gameBoard.get(x+1).get(y).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x+1).get(y) != selectedTile) {
                    //Check tile below left of tile
                    return true;
                } else if(gameBoard.get(x+1).get(y+1).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x+1).get(y+1) != selectedTile) {
                    //Check tile below right of tile
                    return true;
                }
                } else {
                // For odd rows

                if(gameBoard.get(x-1).get(y-1).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x-1).get(y-1) != selectedTile ){
                    //Check tile above left of tile
                    return true;
                } else if(gameBoard.get(x-1).get(y).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x-1).get(y) != selectedTile ) {
                    //Check tile above right of tile
                    return true;
                } else if(gameBoard.get(x).get(y-1).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x).get(y-1) != selectedTile ) {
                    //Check tile to the left of tile
                    return true;
                } else if(gameBoard.get(x).get(y+1).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x).get(y+1) != selectedTile ) {
                    //Check tile to the right of tile
                    return true;
                } else if(gameBoard.get(x+1).get(y-1).getType() != Tile.Bug.EMPTY &&
                        gameBoard.get(x+1).get(y-1) != selectedTile) {
                    //Check tile below left of tile
                    return true;
                } else if(gameBoard.get(x+1).get(y).getType() != Tile.Bug.EMPTY &&
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
     * @return
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
        if(newXIndex >= gameBoard.size() || newYIndex >= gameBoard.size() * 2){
            return false; //out of bounds!
        }
        //if potentialMoves holds tile at newPosition then swap
        if(potentialMoves.contains(gameBoard.get(newXIndex).get(newYIndex))) {

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
            potentialMoves = new ArrayList<Tile>(); //reset potentialMoves
            return true;
        }
        potentialMoves = new ArrayList<Tile>(); //reset potentialMoves
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
     * Returns how many pieces left of bug type in player hand
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
            if(gameBoard.get(x-1).get(y).getPlayerPiece() == other){
                //Check tile above left of tile
                return false;
            }
            else if(gameBoard.get(x-1).get(y+1).getPlayerPiece() == other){
                //Check tile above right of tile
                return false;
            }
            else if(gameBoard.get(x).get(y-1).getPlayerPiece() == other){
                //Check tile to the left of tile
                return false;
            }
            else if(gameBoard.get(x).get(y+1).getPlayerPiece() == other){
                //Check tile to the right of tile
                return false;
            }
            else if(gameBoard.get(x+1).get(y).getPlayerPiece() == other){
                //Check tile below left of tile
                return false;
            }
            else if(gameBoard.get(x+1).get(y+1).getPlayerPiece() == other){
                //Check tile below right of tile
                return false;
            }
        }
        else {
            // For odd rows
            if(gameBoard.get(x-1).get(y-1).getPlayerPiece() == other){
                //Check tile above left of tile
                return false;
            }
            else if(gameBoard.get(x-1).get(y).getPlayerPiece() == other){
                //Check tile above right of tile
                return false;
            }
            else if(gameBoard.get(x).get(y-1).getPlayerPiece() == other){
                //Check tile to the left of tile
                return false;
            }
            else if(gameBoard.get(x).get(y+1).getPlayerPiece() == other){
                //Check tile to the right of tile
                return false;
            }
            else if(gameBoard.get(x+1).get(y-1).getPlayerPiece() == other){
                //Check tile below left of tile
                return false;
            }
            else if(gameBoard.get(x+1).get(y).getPlayerPiece() == other){
                //Check tile below right of tile
                return false;
            }
        }
        return true;
    }

    /**
     * Updates potentialMoves based on tile selected by player to be placed
     * @param inTile
     * for condition 0 it is the only tile on the gameboard
     * for condition 1 it is the tile selected from the players hand
     * @param toggle
     * 0 - only one tile on game board, so potentials all positions surrounding tile
     * 1 - multiple tiles, so potentials empty spaces around tiles with same selected color
     *     that are not touching other players color
     */
    public void updatePotentialsForPlacing(Tile inTile, int toggle){
        if(toggle == 0){
            surroundingTiles(inTile, toggle);
        }
        else{
            for (int i = 0; i < GBSIZE; i++) {
                for (int j = 0; j < GBSIZE * 2; j++) {
                    if (gameBoard.get(i).get(j).getPlayerPiece() == inTile.getPlayerPiece()){
                        surroundingTiles(gameBoard.get(i).get(j), toggle);
                    }
                }
            }
        }
    }

    /**
     * updates potentialMoves based on tile on board
     * @param placedTile
     * @param toggle
     */
    public void surroundingTiles(Tile placedTile, int toggle){ //known bugs, doesn't work if you pass in corner or side pieces due to no bounds checking
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
        switch (toggle) {
            case 0:
                if (x % 2 == 0){ //even row
                    potentialMoves.add(gameBoard.get(x-1).get(y)); //tile above left of tile
                    potentialMoves.add(gameBoard.get(x-1).get(y+1)); //tile above right of tile
                    potentialMoves.add(gameBoard.get(x).get(y-1)); //tile to the left of tile
                    potentialMoves.add(gameBoard.get(x).get(y+1)); //tile to the right of tile
                    potentialMoves.add(gameBoard.get(x+1).get(y)); //tile below left of tile
                    potentialMoves.add(gameBoard.get(x+1).get(y+1)); //tile below right of tile
                }
                else{
                    potentialMoves.add(gameBoard.get(x-1).get(y-1)); //tile above left of tile
                    potentialMoves.add(gameBoard.get(x-1).get(y)); //tile above right of tile
                    potentialMoves.add(gameBoard.get(x).get(y-1)); //tile to the left of tile
                    potentialMoves.add(gameBoard.get(x).get(y+1)); //tile to the right of tile
                    potentialMoves.add(gameBoard.get(x+1).get(y-1)); //tile below left of tile
                    potentialMoves.add(gameBoard.get(x+1).get(y)); //tile below right of tile
                }
                break;
            case 1:
                if (x % 2 == 0) {
                    // For even rows
                    if(gameBoard.get(x-1).get(y).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x-1, y, otherColor)){
                        potentialMoves.add(gameBoard.get(x-1).get(y-1)); //tile above left of tile
                    }
                    else if(gameBoard.get(x-1).get(y+1).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x-1, y+1, otherColor)){
                        potentialMoves.add(gameBoard.get(x-1).get(y+1)); //tile above right of tile
                    }
                    else if(gameBoard.get(x).get(y-1).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x, y-1, otherColor)){
                        potentialMoves.add(gameBoard.get(x).get(y-1)); //tile to the left of tile
                    }
                    else if(gameBoard.get(x).get(y+1).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x, y+1, otherColor)){
                        potentialMoves.add(gameBoard.get(x).get(y+1)); //tile to the right of tile
                    }
                    else if(gameBoard.get(x+1).get(y).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x+1, y, otherColor)){
                        potentialMoves.add(gameBoard.get(x+1).get(y)); //tile below left of tile
                    }
                    else if(gameBoard.get(x+1).get(y+1).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x-1, y+1, otherColor)){
                        potentialMoves.add(gameBoard.get(x-1).get(y+1)); //tile below right of tile
                    }
                }
                else {
                    // For odd rows
                    if(gameBoard.get(x-1).get(y-1).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x-1, y-1, otherColor)){
                        potentialMoves.add(gameBoard.get(x-1).get(y-1)); //tile above left of tile
                    }
                    else if(gameBoard.get(x-1).get(y).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x-1, y, otherColor)){
                        potentialMoves.add(gameBoard.get(x-1).get(y)); //tile above right of tile
                    }
                    else if(gameBoard.get(x).get(y-1).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x, y-1, otherColor)){
                        potentialMoves.add(gameBoard.get(x).get(y-1)); //tile to the left of tile
                    }
                    else if(gameBoard.get(x).get(y+1).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x, y+1, otherColor)){
                        potentialMoves.add(gameBoard.get(x).get(y+1)); //tile to the right of tile
                    }
                    else if(gameBoard.get(x+1).get(y-1).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x+1, y-1, otherColor)){
                        potentialMoves.add(gameBoard.get(x+1).get(y-1)); //tile below left of tile
                    }
                    else if(gameBoard.get(x+1).get(y).getType() == Tile.Bug.EMPTY &&
                            touchingOtherColor(x-1, y, otherColor)){
                        potentialMoves.add(gameBoard.get(x+1).get(y-1)); //tile below right of tile
                    }
                }
                break;
        }
    }

    //testing class for playing Oracle
    public void addTile(Tile newTile){
        gameBoard.get(newTile.getIndexX()).set(newTile.getIndexY(), newTile);
    }

    /**
     *  returns null if the get request is out of bounds
     * @param x
     * @param y
     * @return
     */
    public Tile getTile(int x, int y){
        if(x >= gameBoard.size() || y >= gameBoard.size() * 2){
            return null;
        }
        return gameBoard.get(x).get(y);
    }

    public ArrayList<ArrayList<Tile>> getGameBoard(){
        return gameBoard;
    }

    public ArrayList<ArrayList<Tile>> getDisplayBoard(){
        return displayBoard;
    }

    public ArrayList<Tile> getPotentialMoves() {
        return potentialMoves;
    }

    public int[][] getPiecesRemain(){
        return piecesRemain;
    }

    public void setWhoseTurn(int turn){
        whoseTurn = turn;
    }

    public int getWhoseTurn(){
        return whoseTurn;
    }

    public int getBoardSize(){
        return GBSIZE;
    }

    public int getCurrentIdSelected(){
        return currentIdSelected;
    }
    public void setCurrentIdSelected(int id){
        currentIdSelected = id;
    }

}

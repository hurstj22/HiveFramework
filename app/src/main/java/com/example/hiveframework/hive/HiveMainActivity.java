package com.example.hiveframework.hive;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.LocalGame;
import com.example.hiveframework.GameFramework.gameConfiguration.GameConfig;
import com.example.hiveframework.GameFramework.gameConfiguration.GamePlayerType;
import com.example.hiveframework.GameFramework.infoMessage.GameState;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.GameFramework.utilities.Logger;
import com.example.hiveframework.GameFramework.utilities.Saving;
import com.example.hiveframework.hive.players.HiveHumanPlayer1;
import com.example.hiveframework.hive.players.HiveComputerPlayer1;
import com.example.hiveframework.hive.players.HiveComputerPlayer2;

import java.util.ArrayList;

import edu.up.cs301.game.R;

/**
 * this is the primary activity for Hive game
 *
 * @author Isaac Reinhard
 * @author Kelly Ngyuen
 * @author Ali Sheehan
 * @author James Hurst
 * @version November 2021
 */
public class HiveMainActivity extends GameMainActivity {
    //Tag for logging
    private static final String TAG = "HiveMainActivity";
    public static final int PORT_NUMBER = 5213;

    /**
     * a Hive game is for two players. The default is human vs. computer
     */
    @Override
    public GameConfig createDefaultConfig() {

        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // Hive gui
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new HiveHumanPlayer1(name, R.layout.activity_main);
            }
        });

        // dumb computer player
        playerTypes.add(new GamePlayerType("Computer Player (smartish)") {
            public GamePlayer createPlayer(String name) {
                return new HiveComputerPlayer1(name);
            }
        });

        // smarter computer player
        playerTypes.add(new GamePlayerType("Computer Player (dumb)") {
            public GamePlayer createPlayer(String name) {
                return new HiveComputerPlayer2(name);
            }
        });

        // Create a game configuration class for Hive
        GameConfig defaultConfig = new GameConfig(playerTypes, 2,2, "Hive", PORT_NUMBER);

        // Add the default players
        defaultConfig.addPlayer("Friendly Human", 0); // Default GUI
        defaultConfig.addPlayer("Computer Overlord", 1); // dumb computer player

        // Set the initial information for the remote player
        defaultConfig.setRemoteData("Remote Player", "", 1); // GUI

        //done!
        return defaultConfig;

    }//createDefaultConfig


    /**
     * createLocalGame
     *
     * Creates a new game that runs on the server tablet,
     * @param gameState
     * 				the gameState for this game or null for a new game
     *
     * @return a new, game-specific instance of a sub-class of the LocalGame
     *         class.
     */
    @Override
    public LocalGame createLocalGame(GameState gameState){
        if(gameState == null)
            return new HiveLocalGame();
        return new HiveLocalGame((HiveGameState) gameState);
    }

    /**
     * saveGame, adds this games prepend to the filename
     *
     * @param gameName
     * 				Desired save name
     * @return String representation of the save
     */
    @Override
    public GameState saveGame(String gameName) {
        return super.saveGame(getGameString(gameName));
    }

    /**
     * loadGame, adds this games prepend to the desire file to open and creates the game specific state
     * @param gameName
     * 				The file to open
     * @return The loaded GameState
     */
    @Override
    public GameState loadGame(String gameName){
        String appName = getGameString(gameName);
        super.loadGame(appName);
        Logger.log(TAG, "Loading: " + gameName);
        return (GameState) new HiveGameState((HiveGameState) Saving.readFromFile(appName, this.getApplicationContext()));
    }

}
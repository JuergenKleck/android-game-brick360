package com.juergenkleck.android.game.brick360.storage;

import com.juergenkleck.android.appengine.AppEngineConstants;
import com.juergenkleck.android.appengine.storage.dto.Configuration;
import com.juergenkleck.android.game.brick360.Constants;
import com.juergenkleck.android.game.brick360.SystemHelper;
import com.juergenkleck.android.game.brick360.storage.dto.CurrentGame;
import com.juergenkleck.android.game.brick360.storage.dto.Inventory;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class StoreData extends com.juergenkleck.android.appengine.storage.StoreData {

    public Inventory inventory;
    public CurrentGame currentGame;

    /**
     *
     */
    private static final long serialVersionUID = 2982830586304674266L;

    public static StoreData getInstance() {
        return (StoreData) com.juergenkleck.android.appengine.storage.StoreData.getInstance();
    }

    @Override
    public boolean update() {
        boolean persist = false;

        // Release 1 - 1.0
        if (migration < 1) {
            inventory = new Inventory();
            inventory.ballColorR = Constants.DEFAULT_CONFIG_BALL_COLOR_R;
            inventory.ballColorG = Constants.DEFAULT_CONFIG_BALL_COLOR_G;
            inventory.ballColorB = Constants.DEFAULT_CONFIG_BALL_COLOR_B;
            inventory.boardColorR = Constants.DEFAULT_CONFIG_BOARD_COLOR_R;
            inventory.boardColorG = Constants.DEFAULT_CONFIG_BOARD_COLOR_G;
            inventory.boardColorB = Constants.DEFAULT_CONFIG_BOARD_COLOR_B;
            inventory.coins = Constants.DEFAULT_CONFIG_COINS;
            inventory.gamesWon = Constants.DEFAULT_CONFIG_GAMES_WON;
            inventory.gamesLost = Constants.DEFAULT_CONFIG_GAMES_LOST;
            inventory.gameTime = Constants.DEFAULT_CONFIG_GAME_TIME;
            inventory.invertDirection = Constants.DEFAULT_CONFIG_INVERT_DIRECTION;
            currentGame = new CurrentGame();
            persist = true;
        }

        // Release 2 - 1.0
        if (migration < 2) {
            // with ads
        }
        // Release 2 - 1.0.1
        if (migration < 2) {
            // with ads
        }
        // Release 3 + 4 - 1.0.2
        if (migration < 3) {
            // with admob ads
        }

        // Release 5 - 1.0.3
        if (migration < 5) {
            SystemHelper.setConfiguration(new Configuration(AppEngineConstants.CONFIG_ON_SERVER, AppEngineConstants.DEFAULT_CONFIG_ON_SERVER));
            SystemHelper.setConfiguration(new Configuration(AppEngineConstants.CONFIG_FORCE_UPDATE, AppEngineConstants.DEFAULT_CONFIG_FORCE_UPDATE));
            SystemHelper.setConfiguration(new Configuration(AppEngineConstants.CONFIG_LAST_CHECK, AppEngineConstants.DEFAULT_CONFIG_LAST_CHECK));
            persist = true;
        }

        // Release 6 - Play Store failure
        // Release 7 - 1.1.0
        if (migration < 7) {
            persist = true;
        }

        migration = 7;
        return persist;
    }

}

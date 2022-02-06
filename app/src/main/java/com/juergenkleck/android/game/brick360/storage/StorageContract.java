package com.juergenkleck.android.game.brick360.storage;

import android.provider.BaseColumns;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class StorageContract extends
        com.juergenkleck.android.appengine.storage.StorageContract {

    public static abstract class TableInventory implements BaseColumns {
        public static final String TABLE_NAME = "inventory";
        public static final String COLUMN_COINS = "coins";
        public static final String COLUMN_GAMESWON = "gameswon";
        public static final String COLUMN_GAMESLOST = "gameslost";
        public static final String COLUMN_GAME_TIME = "gametime";
        public static final String COLUMN_BOARD_COLOR_R = "boardcolorr";
        public static final String COLUMN_BOARD_COLOR_G = "boardcolorg";
        public static final String COLUMN_BOARD_COLOR_B = "boardcolorb";
        public static final String COLUMN_BALL_COLOR_R = "ballcolorr";
        public static final String COLUMN_BALL_COLOR_G = "ballcolorg";
        public static final String COLUMN_BALL_COLOR_B = "ballcolorb";
        public static final String COLUMN_INVERT_DIRECTION = "invertdirection";
    }

    public static abstract class TableCurrentGame implements BaseColumns {
        public static final String TABLE_NAME = "currentgame";
        public static final String COLUMN_CURRENTROUND = "currentround";
        public static final String COLUMN_LIFE = "life";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_BRICKS = "bricks";
        public static final String COLUMN_CURRENT_TIME = "currentime";
        public static final String COLUMN_PREVIOUS_TIME = "previoustime";
    }

}

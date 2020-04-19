package info.simplyapps.game.brick360.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import info.simplyapps.appengine.storage.dto.BasicTable;
import info.simplyapps.game.brick360.storage.dto.CurrentGame;
import info.simplyapps.game.brick360.storage.dto.Inventory;

public class DBDriver extends info.simplyapps.appengine.storage.DBDriver {

    private static final String SQL_CREATE_INVENTORY =
            "CREATE TABLE " + StorageContract.TableInventory.TABLE_NAME + " (" +
                    StorageContract.TableInventory._ID + " INTEGER PRIMARY KEY," +
                    StorageContract.TableInventory.COLUMN_COINS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_BOARD_COLOR_R + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_BOARD_COLOR_G + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_BOARD_COLOR_B + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_BALL_COLOR_R + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_BALL_COLOR_G + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_BALL_COLOR_B + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAMESWON + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAMESLOST + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_GAME_TIME + TYPE_INT + COMMA_SEP +
                    StorageContract.TableInventory.COLUMN_INVERT_DIRECTION + TYPE_INT +
                    " );";
    private static final String SQL_DELETE_INVENTORY =
            "DROP TABLE IF EXISTS " + StorageContract.TableInventory.TABLE_NAME;

    private static final String SQL_CREATE_CURRENTGAME =
            "CREATE TABLE " + StorageContract.TableCurrentGame.TABLE_NAME + " (" +
                    StorageContract.TableCurrentGame._ID + " INTEGER PRIMARY KEY," +
                    StorageContract.TableCurrentGame.COLUMN_CURRENTROUND + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_LIFE + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_POINTS + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_LEVEL + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_BRICKS + TYPE_TEXT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_CURRENT_TIME + TYPE_INT + COMMA_SEP +
                    StorageContract.TableCurrentGame.COLUMN_PREVIOUS_TIME + TYPE_INT +
                    " );";
    private static final String SQL_DELETE_CURRENTGAME =
            "DROP TABLE IF EXISTS " + StorageContract.TableCurrentGame.TABLE_NAME;

    public DBDriver(String dataBaseName, int dataBaseVersion, Context context) {
        super(dataBaseName, dataBaseVersion, context);
    }

    public static DBDriver getInstance() {
        return (DBDriver) info.simplyapps.appengine.storage.DBDriver.getInstance();
    }

    @Override
    public void createTables(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_INVENTORY);
        db.execSQL(SQL_CREATE_CURRENTGAME);
    }


    @Override
    public void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(SQL_DELETE_INVENTORY);
//        db.execSQL(SQL_DELETE_CURRENTGAME);
    }

    @Override
    public String getExtendedTable(BasicTable data) {
        return Inventory.class.isInstance(data) ? StorageContract.TableInventory.TABLE_NAME :
                CurrentGame.class.isInstance(data) ? StorageContract.TableCurrentGame.TABLE_NAME : null;
    }

    @Override
    public void storeExtended(info.simplyapps.appengine.storage.StoreData data) {
        store(StoreData.class.cast(data).inventory);
        store(StoreData.class.cast(data).currentGame);
    }

    @Override
    public void readExtended(info.simplyapps.appengine.storage.StoreData data, SQLiteDatabase db) {
        readInventory(StoreData.class.cast(data), db);
        readCurrentGame(StoreData.class.cast(data), db);
    }

    @Override
    public info.simplyapps.appengine.storage.StoreData createStoreData() {
        return new StoreData();
    }

    public boolean store(Inventory data) {
        ContentValues values = new ContentValues();
        values.put(StorageContract.TableInventory.COLUMN_COINS, data.coins);
        values.put(StorageContract.TableInventory.COLUMN_GAMESWON, data.gamesWon);
        values.put(StorageContract.TableInventory.COLUMN_GAMESLOST, data.gamesLost);
        values.put(StorageContract.TableInventory.COLUMN_GAME_TIME, data.gameTime);
        values.put(StorageContract.TableInventory.COLUMN_BOARD_COLOR_R, data.boardColorR);
        values.put(StorageContract.TableInventory.COLUMN_BOARD_COLOR_G, data.boardColorG);
        values.put(StorageContract.TableInventory.COLUMN_BOARD_COLOR_B, data.boardColorB);
        values.put(StorageContract.TableInventory.COLUMN_BALL_COLOR_R, data.ballColorR);
        values.put(StorageContract.TableInventory.COLUMN_BALL_COLOR_G, data.ballColorG);
        values.put(StorageContract.TableInventory.COLUMN_BALL_COLOR_B, data.ballColorB);
        values.put(StorageContract.TableInventory.COLUMN_INVERT_DIRECTION, data.invertDirection);
        return persist(data, values, StorageContract.TableInventory.TABLE_NAME);
    }

    private void readInventory(StoreData data, SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                StorageContract.TableInventory._ID,
                StorageContract.TableInventory.COLUMN_COINS,
                StorageContract.TableInventory.COLUMN_GAMESWON,
                StorageContract.TableInventory.COLUMN_GAMESLOST,
                StorageContract.TableInventory.COLUMN_GAME_TIME,
                StorageContract.TableInventory.COLUMN_BOARD_COLOR_R,
                StorageContract.TableInventory.COLUMN_BOARD_COLOR_G,
                StorageContract.TableInventory.COLUMN_BOARD_COLOR_B,
                StorageContract.TableInventory.COLUMN_BALL_COLOR_R,
                StorageContract.TableInventory.COLUMN_BALL_COLOR_G,
                StorageContract.TableInventory.COLUMN_BALL_COLOR_B,
                StorageContract.TableInventory.COLUMN_INVERT_DIRECTION
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        Cursor c = db.query(
                StorageContract.TableInventory.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        boolean hasResults = c.moveToFirst();
        while (hasResults) {
            Inventory i = new Inventory();
            i.id = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory._ID));
            i.coins = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_COINS));
            i.gamesWon = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAMESWON));
            i.gamesLost = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAMESLOST));
            i.gameTime = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_GAME_TIME));
            i.boardColorR = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_BOARD_COLOR_R));
            i.boardColorG = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_BOARD_COLOR_G));
            i.boardColorB = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_BOARD_COLOR_B));
            i.ballColorR = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_BALL_COLOR_R));
            i.ballColorG = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_BALL_COLOR_G));
            i.ballColorB = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_BALL_COLOR_B));
            i.invertDirection = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableInventory.COLUMN_INVERT_DIRECTION)) == 1;
            data.inventory = i;
            hasResults = c.moveToNext();
        }
        c.close();
    }

    public boolean store(CurrentGame data) {
        ContentValues values = new ContentValues();
        values.put(StorageContract.TableCurrentGame.COLUMN_CURRENTROUND, data.currentRound);
        values.put(StorageContract.TableCurrentGame.COLUMN_LEVEL, data.level);
        values.put(StorageContract.TableCurrentGame.COLUMN_LIFE, data.life);
        values.put(StorageContract.TableCurrentGame.COLUMN_POINTS, data.points);
        values.put(StorageContract.TableCurrentGame.COLUMN_BRICKS, intToString(data.bricks));
        values.put(StorageContract.TableCurrentGame.COLUMN_CURRENT_TIME, data.currentTime);
        values.put(StorageContract.TableCurrentGame.COLUMN_PREVIOUS_TIME, data.previousTime);
        return persist(data, values, StorageContract.TableCurrentGame.TABLE_NAME);
    }

    private void readCurrentGame(StoreData data, SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                StorageContract.TableCurrentGame._ID,
                StorageContract.TableCurrentGame.COLUMN_BRICKS,
                StorageContract.TableCurrentGame.COLUMN_CURRENTROUND,
                StorageContract.TableCurrentGame.COLUMN_LEVEL,
                StorageContract.TableCurrentGame.COLUMN_LIFE,
                StorageContract.TableCurrentGame.COLUMN_POINTS,
                StorageContract.TableCurrentGame.COLUMN_CURRENT_TIME,
                StorageContract.TableCurrentGame.COLUMN_PREVIOUS_TIME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;
        Cursor c = db.query(
                StorageContract.TableCurrentGame.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        boolean hasResults = c.moveToFirst();
        while (hasResults) {
            CurrentGame i = new CurrentGame();
            i.id = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame._ID));
            i.currentRound = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_CURRENTROUND));
            i.level = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_LEVEL));
            i.life = c.getFloat(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_LIFE));
            i.points = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_POINTS));
            i.bricks = stringToInt(c.getString(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_BRICKS)));
            i.currentTime = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_CURRENT_TIME));
            i.previousTime = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableCurrentGame.COLUMN_PREVIOUS_TIME));
            data.currentGame = i;
            hasResults = c.moveToNext();
        }
        c.close();
    }

}

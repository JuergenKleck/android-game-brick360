package com.juergenkleck.android.game.brick360;

import com.juergenkleck.android.game.brick360.storage.StoreData;
import com.juergenkleck.android.game.brick360.storage.dto.CurrentGame;
import com.juergenkleck.android.game.brick360.storage.dto.Inventory;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class SystemHelper extends com.juergenkleck.android.appengine.SystemHelper {

    public synchronized static final Inventory getInventory() {
        return StoreData.getInstance().inventory;
    }

    public synchronized static final CurrentGame getCurrentGame() {
        return StoreData.getInstance().currentGame;
    }

}

package info.simplyapps.game.brick360;

import info.simplyapps.game.brick360.storage.StoreData;
import info.simplyapps.game.brick360.storage.dto.CurrentGame;
import info.simplyapps.game.brick360.storage.dto.Inventory;

public class SystemHelper extends info.simplyapps.appengine.SystemHelper {

    public synchronized static final Inventory getInventory() {
        return StoreData.getInstance().inventory;
    }

    public synchronized static final CurrentGame getCurrentGame() {
        return StoreData.getInstance().currentGame;
    }

}

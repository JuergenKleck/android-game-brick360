package info.simplyapps.game.brick360;


public class Constants {

    public static final String DATABASE = "brick360.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CONFIG_SHOW_CROSSHAIR = "extcrosshair";

    public static final int DEFAULT_CONFIG_BOARD_COLOR_R = 255;
    public static final int DEFAULT_CONFIG_BOARD_COLOR_G = 251;
    public static final int DEFAULT_CONFIG_BOARD_COLOR_B = 202;
    public static final int DEFAULT_CONFIG_BALL_COLOR_R = 11;
    public static final int DEFAULT_CONFIG_BALL_COLOR_G = 155;
    public static final int DEFAULT_CONFIG_BALL_COLOR_B = 255;
    public static final int DEFAULT_CONFIG_GAMES_WON = 0;
    public static final int DEFAULT_CONFIG_GAMES_LOST = 0;
    public static final int DEFAULT_CONFIG_GAME_TIME = 0;
    // start with free tokens
    public static final int DEFAULT_CONFIG_COINS = 7500;
    public static final boolean DEFAULT_CONFIG_INVERT_DIRECTION = false;
    public static final String DEFAULT_CONFIG_SHOW_CROSSHAIR = Boolean.TRUE.toString();

    public static final String PREFERENCE_NS = "http://info.simplyapps.game.brick360.rendering";

    public static final int ACHIEVEMENT_CROSSHAIR = 250;

    public static final int[][] ACHIEVEMENT_REQUIREMENTS = {
            {R.drawable.badge_1b, 10}
            ,{R.drawable.badge_1s, 50}
            ,{R.drawable.badge_1g, 100}
            ,{R.drawable.badge_2b, 250}
            ,{R.drawable.badge_2s, 500}
            ,{R.drawable.badge_2g, 750}
            ,{R.drawable.badge_3b, 1000}
            ,{R.drawable.badge_3s, 2000}
            ,{R.drawable.badge_3g, 3000}
    };

    public static final long ACHIEVEMENT_MIN_GAMES_PITY_STAR = 500l;
    public static final float ACHIEVEMENT_RATE_WHAT_A_PITY = 0.75f;
    public static final float ACHIEVEMENT_RATE_LUCKY_STAR = 0.75f;

    // Database name values
    public static final String EXTENSION_ITEM_FIREBALL = "fireball";
    public static final String EXTENSION_ITEM_INVISIBLEBALL = "invisibleball";
    public static final String EXTENSION_ITEM_BIGBALL = "bigball";
    public static final String EXTENSION_ITEM_BALLSET = "ballset";
    public static final String EXTENSION_ITEM_COUNTER_MEASURE = "countermeasure";
    public static final String EXTENSION_ITEM_CROSSHAIR = "crosshair";
    public static final String EXTENSION_ITEM_GLUEBALL = "glueball";
    public static final String EXTENSION_ITEM_WALL = "wall";

    public static final int EXTENSION_NUMBER_FIREBALL = 3;
    public static final int EXTENSION_NUMBER_INVISIBLEBALL = 4;
    public static final int EXTENSION_NUMBER_BIGBALL = 1;
    public static final int EXTENSION_NUMBER_BALLSET = 0;
    public static final int EXTENSION_NUMBER_COUNTER_MEASURE = 2;
    public static final int EXTENSION_NUMBER_CROSSHAIR = 5;
    public static final int EXTENSION_NUMBER_GLUEBALL = 6;
    public static final int EXTENSION_NUMBER_WALL = 7;

    public static final int ACTION_HOME = 300;

    public static final long buttonPressTime = 2000l;

    public static final int spaceLR = 10;
    public static final int spaceTB = 8;

    public static final float CHAR_SPACING = 0.35f;

    public enum RenderMode {
        HOME, GAME
        , OPTIONS
        , WAIT;
    }

    public enum SubRenderMode {
        COLORS, UPGRADE, MISC;
    }

}

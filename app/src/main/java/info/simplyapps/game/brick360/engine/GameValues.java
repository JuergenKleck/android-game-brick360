package info.simplyapps.game.brick360.engine;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;

import info.simplyapps.game.brick360.Constants;
import info.simplyapps.game.brick360.R;

public class GameValues extends info.simplyapps.gameengine.system.GameValues {

    public static final int[] PRICES_PER_EXTENSION = {
            500 // Extension ballset
            , 750 // Big ball
            , 500 // counter measure
            , 1000 // fire ball
            , 750 // invisible ball
            , 2000 // cross hair
            , 500 // glue ball
            , 1000 // wall
    };
    public static final int[] MAX_PER_EXTENSION = {
            3 // Extension ballset
            , 99 // Big ball
            , 99 // counter measure
            , 99 // fire ball
            , 99 // invisible ball
            , 1 // cross hair
            , 99 // glue ball
            , 99 // wall
    };
    public static final long[] TIME_PER_EXTENSION = {
            0 // Extension ballset - passive
            , 30000 // Big ball
            , 0 // counter measure
            , 30000 // fire ball
            , 30000 // invisible ball
            , 0 // cross hair - passive
            , 30000 // glue ball
            , 30000 // wall
    };

    public static final int[] extensionExplanation = {
            R.string.message_explanation_ballset
            , R.string.message_explanation_bigball
            , R.string.message_explanation_counter_measure
            , R.string.message_explanation_fireball
            , R.string.message_explanation_invisibleball
            , R.string.message_explanation_crosshair
            , R.string.message_explanation_glueball
            , R.string.message_explanation_wall
    };

    public static final int[] extensionTitle = {
            R.string.menubutton_expansion_ballset
            , R.string.menubutton_expansion_bigball
            , R.string.menubutton_expansion_counter_measure
            , R.string.menubutton_expansion_fireball
            , R.string.menubutton_expansion_invisibleball
            , R.string.menubutton_expansion_crosshair
            , R.string.menubutton_expansion_glueball
            , R.string.menubutton_expansion_wall
    };

    public static final String[] extensionName = {
            Constants.EXTENSION_ITEM_BALLSET
            , Constants.EXTENSION_ITEM_BIGBALL
            , Constants.EXTENSION_ITEM_COUNTER_MEASURE
            , Constants.EXTENSION_ITEM_FIREBALL
            , Constants.EXTENSION_ITEM_INVISIBLEBALL
            , Constants.EXTENSION_ITEM_CROSSHAIR
            , Constants.EXTENSION_ITEM_GLUEBALL
            , Constants.EXTENSION_ITEM_WALL
    };

    public static final int BRICK_NORMAL = 0;
    public static final int BRICK_EXPLOSIVE = 1;

    public static final float totalLife = 3.0f;

    public static final long gameRoundDelay = 2500l;

    public static final long roundTime = 3600000l;

    public static final float ballSize = 0.04f;
    public static final float bigBallSize = 0.08f;

    public static final int boardNoMove = 0;
    public static final int boardMovement = 4;
    public static final int ballMovement = 2;

    public static final int circleSteps = 360;
    public static final int ballSteps = 360;

    public static final float commandSpace = 0.35f;
    public static final int levelWidth = 9;
    public static final int levelHeight = 14;

    public static final long brickExplosiveTime = 10000l;
    // the state switching time
    public static final long brickExplosiveState = 500l;
    // the remaining time of the explosive until the fast state is activated
    public static final long brickExplosiveStateRemaining = 3500l;
    public static final long brickExplosiveStateFast = 250l;
    // the final explosion time
    public static final long brickExplosiveAnim = 450l;
    public static final int explosiveGrid = 1;

    public static final int scoreDividerSinglePlayer = 8;

    public static final int systemBackground = R.drawable.bg8;
    public static final int[] backgrounds = {R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5, R.drawable.bg6, R.drawable.bg7, R.drawable.bg8, R.drawable.bg9, R.drawable.bg10};
    public static final int[] levels = {R.array.level1, R.array.level2, R.array.level3, R.array.level4, R.array.level5,
            R.array.level6, R.array.level7, R.array.level8, R.array.level9, R.array.level10, R.array.level11, R.array.level12,
            R.array.level13, R.array.level14, R.array.level15, R.array.level16, R.array.level17, R.array.level18, R.array.level19,
            R.array.level20, R.array.level21, R.array.level22, R.array.level23, R.array.level24, R.array.level25};

    public static ColorFilter cFilterBlue = new LightingColorFilter(Color.parseColor("#4dcaff"), 255);
    public static ColorFilter cFilterRed = new LightingColorFilter(Color.parseColor("#fe5858"), 1);
    public static ColorFilter cFilterGreen = new LightingColorFilter(Color.parseColor("#67fe44"), 1);
    public static ColorFilter cFilterYellow = new LightingColorFilter(Color.parseColor("#fdff63"), 1);
    public static ColorFilter cFilterOrange = new LightingColorFilter(Color.parseColor("#ffa735"), 1);
    public static ColorFilter cFilterDarkRed = new LightingColorFilter(Color.parseColor("#cb2525"), 1);

    public static ColorFilter cFilterBrick1 = new LightingColorFilter(Color.parseColor("#05adff"), 1);
    public static ColorFilter cFilterBrick2 = new LightingColorFilter(Color.parseColor("#0560ff"), 1);
    public static ColorFilter cFilterBrick3 = new LightingColorFilter(Color.parseColor("#0519ff"), 1);
    public static ColorFilter cFilterBrick4 = new LightingColorFilter(Color.parseColor("#fffc26"), 1);

    // first int is the brick ID
    public static final int[][] brickTypes = {
            {0, BRICK_NORMAL}
            , {1, BRICK_NORMAL}
            , {2, BRICK_NORMAL}
            , {3, BRICK_NORMAL}
            , {100, BRICK_EXPLOSIVE}
    };
    public static final int[][] brickPoints = {
            {0, 15}
            , {1, 30}
            , {2, 40}
            , {3, 50}
            , {100, 5}
    };
    private static final ColorFilter[] brickFilter = {
            cFilterBrick1
            , cFilterBrick2
            , cFilterBrick3
            , cFilterBrick4
            , cFilterBrick1
    };

    private static final int[][] brickFilterReference = {
            {0, 0}
            , {1, 1}
            , {2, 2}
            , {3, 3}
            , {100, 4}
    };

    public static int getBrickPoints(int layer) {
        int value = -1;
        for (int[] values : brickPoints) {
            if (values[0] == layer) {
                value = values[1];
                break;
            }
        }
        return value;
    }

    public static int getBrickType(int layer) {
        int value = -1;
        for (int[] values : brickTypes) {
            if (values[0] == layer) {
                value = values[1];
                break;
            }
        }
        return value;
    }

    public static ColorFilter getBrickFilterReference(int layer) {
        ColorFilter value = cFilterBrick1;
        for (int[] values : brickFilterReference) {
            if (values[0] == layer) {
                value = brickFilter[values[1]];
                break;
            }
        }
        return value;
    }

}

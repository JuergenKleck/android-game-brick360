package info.simplyapps.game.brick360.rendering.objects;

import android.graphics.Path;
import android.graphics.PathMeasure;

import info.simplyapps.gameengine.rendering.objects.Graphic;
import info.simplyapps.gameengine.util.MultiPlayer;

public class BrickPlayer {

    // the board and ball graphic
    public Graphic gBoard;
    public Graphic gBall;

    // board information
    public int boardMove = 0;
    public float boardRotation = 0.0f;
    public float lastRotation = 0.0f;
    public int boardPositionOnCircle = 0;

    // ball information
    public boolean ballGlued = true;
    public int ballPosOnPath;
    public Path ballPath;
    public PathMeasure ballPathMeasure;
    public int[] ballSource = new int[2];
    // target for multi player only
    public int[] ballTarget = new int[2];
    public boolean ballBlocked = true;

    public int ballEffect;

    public String name;
    public int rankId = 0;
    public int score = 0;

}

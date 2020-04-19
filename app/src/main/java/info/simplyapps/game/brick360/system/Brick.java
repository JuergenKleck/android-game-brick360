package info.simplyapps.game.brick360.system;

import android.graphics.Rect;

import info.simplyapps.gameengine.rendering.objects.Coord;

public class Brick {

    // the graphic reference
    public int gReference;
    // the position on the screen
    public Coord coord;
    // the size on the screen
    public Rect rect;
    // the rendering layer on which level this brick is located
    public int layer;

    // for effects
    public int h;
    public int v;
    // for explosive
    public long expiry;
    public long stateChange;
    public boolean state;

    // inherited from Obstacle
    public boolean hit;

}

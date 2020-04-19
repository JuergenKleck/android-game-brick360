package info.simplyapps.game.brick360.system;

import java.util.ArrayList;
import java.util.List;

import info.simplyapps.gameengine.system.GameRound;
import info.simplyapps.gameengine.system.OnlineGame;

public class BrickGame extends OnlineGame {

    public List<Brick> bricks;
    public boolean lifeLost;
    public boolean won;
    public boolean complete;

    public BrickGame(GameRound[] rounds) {
        super(rounds);
        bricks = new ArrayList<Brick>();
    }

    public Brick getBrick(int h, int v) {
        for (Brick brick : bricks) {
            if (brick.h == h && brick.v == v) {
                return brick;
            }
        }
        return null;
    }

    public void reset() {
        super.reset();
        won = false;
        lifeLost = false;
        bricks.clear();
        life = 0.0f;
    }

}

package com.juergenkleck.android.game.brick360.system;

import java.util.ArrayList;
import java.util.List;

import com.juergenkleck.android.gameengine.system.GameRound;
import com.juergenkleck.android.gameengine.system.OnlineGame;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
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

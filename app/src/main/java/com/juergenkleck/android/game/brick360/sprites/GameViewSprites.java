package com.juergenkleck.android.game.brick360.sprites;

import android.graphics.Rect;

import com.juergenkleck.android.game.brick360.rendering.objects.BrickPlayer;
import com.juergenkleck.android.gameengine.rendering.objects.Animation;
import com.juergenkleck.android.gameengine.rendering.objects.Graphic;
import com.juergenkleck.android.gameengine.sprites.ViewSprites;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class GameViewSprites implements ViewSprites {

    public Graphic gBackground;

    public Graphic gMoveLeft;
    public Graphic gMoveRight;

    public Graphic gBrick;

    public Rect rMsgGameState;
    public Rect rBtnLaunch;
    public Rect rBtnPause;
    public Rect rBtnResume;
    public Rect rBtnBack;

    public Graphic gExtBigBall;
    public Graphic gExtFireball;
    public Graphic gExtInvisibleBall;
    public Graphic gExtCounterMeasure;
    public Graphic gExtWall;
    public Graphic gExtGlueBall;

    public Graphic gEffectFireball;
    public Graphic gEffectWall;

    public Graphic gButton;
    public Graphic gButtonOverlay;

    public Graphic gRankPlayer;
    public Graphic gRankOpponent;

    public Animation aBrickExplosion;
    public Graphic[] gBrickExplosion;

    public BrickPlayer pPlayer;

    public Rect rMsgPlayerName;
    public Rect rMsgScoreAward;
    public Rect rMsgLives;

    @Override
    public void init() {
    }

    @Override
    public void clean() {
    }

}

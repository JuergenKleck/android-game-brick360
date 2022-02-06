package com.juergenkleck.android.game.brick360.rendering.objects;

import android.graphics.Path;
import android.graphics.PathMeasure;

import com.juergenkleck.android.gameengine.rendering.objects.Graphic;
import com.juergenkleck.android.gameengine.util.MultiPlayer;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
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

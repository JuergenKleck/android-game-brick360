package com.juergenkleck.android.game.brick360.storage.dto;

import java.io.Serializable;

import com.juergenkleck.android.appengine.storage.dto.BasicTable;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class CurrentGame extends BasicTable implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6974204243261183587L;
    public int currentRound;
    // the available life
    public float life;
    // the points in the level
    public int points;

    // the level number
    public int level;
    // the current brick levels as per xml definition
    public int[] bricks;

    public long currentTime;
    public long previousTime;

}

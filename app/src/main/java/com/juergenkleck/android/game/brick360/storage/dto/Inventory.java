package com.juergenkleck.android.game.brick360.storage.dto;

import java.io.Serializable;

import com.juergenkleck.android.appengine.storage.dto.BasicTable;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class Inventory extends BasicTable implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6974204243261183587L;
    public long coins;
    public long gameTime;
    public long gamesWon;
    public long gamesLost;

    public int boardColorR;
    public int boardColorG;
    public int boardColorB;

    public int ballColorR;
    public int ballColorG;
    public int ballColorB;

    public boolean invertDirection;

}

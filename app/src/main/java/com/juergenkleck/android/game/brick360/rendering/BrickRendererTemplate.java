package com.juergenkleck.android.game.brick360.rendering;

import android.content.Context;

import java.util.Properties;

import com.juergenkleck.android.game.brick360.Constants;
import com.juergenkleck.android.gameengine.rendering.GenericRendererTemplate;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public abstract class BrickRendererTemplate extends GenericRendererTemplate {

    public BrickRendererTemplate(Context context, Properties properties) {
        super(context, properties);
    }

    public boolean logEnabled() {
        return false;
    }

    @Override
    public float getCharSpacing() {
        return Constants.CHAR_SPACING;
    }

}

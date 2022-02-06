package com.juergenkleck.android.game.brick360.screens;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Properties;

import com.juergenkleck.android.game.brick360.Constants;
import com.juergenkleck.android.game.brick360.rendering.HomeRenderer;
import com.juergenkleck.android.gameengine.EngineConstants;
import com.juergenkleck.android.gameengine.RenderingSystem;
import com.juergenkleck.android.gameengine.rendering.GenericViewTemplate;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class HomeView extends GenericViewTemplate {

    public HomeView(Context context) {
        super(context);
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void createThread(Context context) {
        Properties p = new Properties();
        p.put(EngineConstants.GameProperties.RENDERING_SYSTEM, RenderingSystem.SINGLE_PLAYER);
        p.put(EngineConstants.GameProperties.SCREEN_SCALE, 0);
        p.put(EngineConstants.GameProperties.LEVEL, 0);
        setBasicEngine(new HomeRenderer(getContext(), p));
    }

    @Override
    public boolean isDragable() {
        return true;
    }

    @Override
    public String getNameSpace() {
        return Constants.PREFERENCE_NS;
    }

}

package com.juergenkleck.android.game.brick360.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import java.util.Properties;

import com.juergenkleck.android.game.brick360.Constants;
import com.juergenkleck.android.game.brick360.Constants.RenderMode;
import com.juergenkleck.android.game.brick360.R;
import com.juergenkleck.android.game.brick360.engine.BrickEngine;
import com.juergenkleck.android.game.brick360.rendering.GameRenderer;
import com.juergenkleck.android.game.brick360.rendering.HomeRenderer;
import com.juergenkleck.android.game.brick360.storage.StorageUtil;
import com.juergenkleck.android.gameengine.EngineConstants;
import com.juergenkleck.android.gameengine.RenderingSystem;
import com.juergenkleck.android.gameengine.screens.HomeScreenTemplate;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class HomeScreen extends HomeScreenTemplate {

    public static String ICICLE_KEY = "brick360-view";

    public static boolean mGameModeContinue = false;

    RenderMode mLastRenderMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // super create must be the first call for android > 4.0
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getViewKey() {
        return ICICLE_KEY;
    }

    @Override
    public int getScreenLayout() {
        return R.layout.homescreen;
    }

    @Override
    public int getViewLayoutId() {
        return R.id.homeview;
    }

    @Override
    public void prepareStorage(Context context) {
        StorageUtil.prepareStorage(context);
    }


    @Override
    public void actionNewGame() {
        mGameModeContinue = false;
        startGameScreen();
    }

    @Override
    public void actionContinueGame() {
    }

    @Override
    public void actionOptions() {
        HomeRenderer.class.cast(getScreenView().getBasicEngine()).updateRenderMode(RenderMode.OPTIONS);
    }

    @Override
    public void actionQuit() {
        finish();
    }

    @Override
    public void actionAdditionalAction(int action) {
        switch (action) {
            case Constants.ACTION_HOME:
                actionHome();
                break;
        }
    }

    public void actionHome() {
        if (!isHomeActive()) {
            getScreenView().changeEngine(new HomeRenderer(this, getEngineProperties()));
        } else {
            HomeRenderer.class.cast(getScreenView().getBasicEngine()).updateRenderMode(RenderMode.HOME);
        }
    }

    @Override
    public void doUpdateChecks() {
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (isGameActive()) {
            getScreenView().getBasicEngine().pause(); // pause game when Activity pauses
            mLastRenderMode = RenderMode.GAME;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isGameActive()) {
            getScreenView().getBasicEngine().unpause();//resume();
        } else if (!isGameActive() && RenderMode.GAME.equals(mLastRenderMode)) {
            loadGameEngine();
        } else if (mLastRenderMode != null) {
            HomeRenderer.class.cast(getScreenView().getBasicEngine()).updateRenderMode(mLastRenderMode);
        }
        mLastRenderMode = null;
    }

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state. (Done via HOME button or another activity popping up
     *
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        getScreenView().getBasicEngine().saveState(outState);
    }

    private void startGameScreen() {
        activateGame();
    }

    public void activateGame() {
        mLastRenderMode = RenderMode.GAME;
        loadGameEngine();
        // start the game
        getScreenView().getBasicEngine().doStart();
    }

    private void loadGameEngine() {
        getScreenView().changeEngine(new GameRenderer(this, getEngineProperties()));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean isHomeActive() {
        return getScreenView() != null && HomeRenderer.class.isInstance(getScreenView().getBasicEngine());
    }

    private boolean isGameActive() {
        return getScreenView() != null && GameRenderer.class.isInstance(getScreenView().getBasicEngine());
    }

    private boolean isHome() {
        return HomeRenderer.class.isInstance(getScreenView().getBasicEngine()) && Constants.RenderMode.HOME == HomeRenderer.class.cast(getScreenView().getBasicEngine()).mRenderMode;
    }

    private boolean isOptions() {
        return HomeRenderer.class.isInstance(getScreenView().getBasicEngine()) && Constants.RenderMode.OPTIONS == HomeRenderer.class.cast(getScreenView().getBasicEngine()).mRenderMode;
    }

    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (isHomeActive()) {
            if (isHome()) {
                return super.onKeyDown(keyCode, e);
            } else if (isOptions()) {
                actionHome();
            }
        } else if (isGameActive()) {
            getEngine().pause();
        }
        return false;
    }

    public BrickEngine getEngine() {
        if (BrickEngine.class.isInstance(getScreenView().getBasicEngine())) {
            return BrickEngine.class.cast(getScreenView().getBasicEngine());
        }
        return null;
    }

    @Override
    public Properties getEngineProperties() {
        Properties p = new Properties();
        p.put(EngineConstants.GameProperties.RENDERING_SYSTEM, RenderingSystem.SINGLE_PLAYER);
        p.put(EngineConstants.GameProperties.SCREEN_SCALE, 0);
        p.put(EngineConstants.GameProperties.LEVEL, 0);
        p.put(EngineConstants.GameProperties.SPACE_LR, Constants.spaceLR);
        p.put(EngineConstants.GameProperties.SPACE_TB, Constants.spaceTB);
        return p;
    }

}
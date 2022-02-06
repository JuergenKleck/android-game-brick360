
package com.juergenkleck.android.game.brick360.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.juergenkleck.android.appengine.storage.dto.Configuration;
import com.juergenkleck.android.appengine.storage.dto.Extensions;
import com.juergenkleck.android.game.brick360.Constants;
import com.juergenkleck.android.game.brick360.R;
import com.juergenkleck.android.game.brick360.SystemHelper;
import com.juergenkleck.android.game.brick360.engine.BrickEngine;
import com.juergenkleck.android.game.brick360.engine.GameValues;
import com.juergenkleck.android.game.brick360.rendering.objects.BrickPlayer;
import com.juergenkleck.android.game.brick360.screens.HomeScreen;
import com.juergenkleck.android.game.brick360.sprites.GameViewSprites;
import com.juergenkleck.android.game.brick360.storage.DBDriver;
import com.juergenkleck.android.game.brick360.storage.dto.CurrentGame;
import com.juergenkleck.android.game.brick360.system.Brick;
import com.juergenkleck.android.game.brick360.system.BrickGame;
import com.juergenkleck.android.gameengine.EngineConstants;
import com.juergenkleck.android.gameengine.rendering.kits.AnimationKit;
import com.juergenkleck.android.gameengine.rendering.kits.Renderkit;
import com.juergenkleck.android.gameengine.rendering.kits.ScreenKit;
import com.juergenkleck.android.gameengine.rendering.kits.ScreenKit.ScreenPosition;
import com.juergenkleck.android.gameengine.rendering.objects.Animation;
import com.juergenkleck.android.gameengine.rendering.objects.Graphic;
import com.juergenkleck.android.gameengine.system.BasicGame;
import com.juergenkleck.android.gameengine.system.GameRound;
import com.juergenkleck.android.gameengine.system.GameState;
import com.juergenkleck.android.gameengine.system.GameSubState;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class GameRenderer extends BrickRendererTemplate implements BrickEngine {

    /**
     * The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN
     */
    private GameState mMode;
    private GameSubState mSubMode;

    private BrickGame mGame;

    Random rnd;

    private long delay = 0l;
    private long lastTime;

    final float standardNumberWidth = 0.05f;
    final float standardNumberHeight = 1.20f;

    Path gamePath;
    PathMeasure gamePathMeasure;
    Path mCrossHair;
    Paint gamePaint;
    RectF gameBounds;
    double gameDistance;

    boolean isMedium = false;
    boolean isHard = false;

    ColorFilter mBoardFilter;
    ColorFilter mBallFilter;
    ColorFilter mBoardFilterOpponent;
    ColorFilter mBallFilterOpponent;

    boolean sync = false;
    long firstSync;
    long lastSync;
    long pingSelf;
    long pingOpponent;

    int directionLeft;
    int directionRight;

    int[] mOrgBallSize;

    int[] mExtensions;
    long mExtTime = 0l;

    boolean mShowCrossHair;

    int mRounds;

    Paint mLayer;
    Paint mLayerBorder;

    public GameRenderer(Context context, Properties p) {
        super(context, p);
    }

    @Override
    public void doInitThread(long time) {

        rnd = new Random();

        sprites = new GameViewSprites();
        mMode = GameState.NONE;
        mSubMode = GameSubState.NONE;

        if (mGame == null) {
            createGame();
        }

        // create background
        if (mGame.hasGame()) {
            updateRoundGraphic();
        }

        getSprites().pPlayer = new BrickPlayer();
        getSprites().pPlayer.score = 0;
        getSprites().pPlayer.name = getString(R.string.player_score);

        long gamesWon = SystemHelper.getInventory().gamesWon;
        long gamesLost = SystemHelper.getInventory().gamesLost;

        int achievementId = 0;
        for (int[] reqs : Constants.ACHIEVEMENT_REQUIREMENTS) {
            if (reqs[1] < gamesWon + gamesLost) {
                achievementId = reqs[0];
            }
        }
        if (achievementId > 0) {
            getSprites().pPlayer.rankId = achievementId;
        }

        Configuration cDifficulty = SystemHelper.getConfiguration(EngineConstants.CONFIG_DIFFICULTY, EngineConstants.DEFAULT_CONFIG_DIFFICULTY);
        isMedium = Integer.valueOf(cDifficulty.value) == GameValues.DIFFICULTY_MEDIUM;
        isHard = Integer.valueOf(cDifficulty.value) == GameValues.DIFFICULTY_HARD;

        // load background for level choosing
        getSprites().gBackground = loadGraphic(GameValues.backgrounds[rnd.nextInt(GameValues.backgrounds.length)], 0, 0);

        getSprites().gMoveLeft = loadGraphic(R.drawable.move_left, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.15f, 15, 15, getSprites().gMoveLeft);

        getSprites().gMoveRight = loadGraphic(R.drawable.move_right, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.15f, 15, 15, getSprites().gMoveRight);

        getSprites().gBrick = loadGraphic(R.drawable.brick, 0, 0);

        getSprites().gBrickExplosion = new Graphic[3];
        getSprites().gBrickExplosion[0] = loadGraphic(R.drawable.brick_explosion, 0, 0);
        getSprites().gBrickExplosion[1] = loadGraphic(R.drawable.brick_explosion2, 0, 0);
        getSprites().gBrickExplosion[2] = loadGraphic(R.drawable.brick_explosion3, 0, 0);
        // create cat animation
        getSprites().aBrickExplosion = new Animation(true);
        AnimationKit.addAnimation(getSprites().aBrickExplosion, 0, 150);
        AnimationKit.addAnimation(getSprites().aBrickExplosion, 1, 150);
        AnimationKit.addAnimation(getSprites().aBrickExplosion, 2, 150);


        getSprites().pPlayer.gBoard = loadGraphic(R.drawable.board, 0, 0);
        getSprites().pPlayer.gBall = loadGraphic(R.drawable.ball, 0, 0);
        mOrgBallSize = new int[2];
        mOrgBallSize[0] = getSprites().pPlayer.gBall.image.getBounds().width();
        mOrgBallSize[1] = getSprites().pPlayer.gBall.image.getBounds().height();
        if (getSprites().pPlayer.rankId > -1) {
            getSprites().gRankPlayer = loadGraphic(achievementId, 0, 0);
            ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.06f, 0, 0, getSprites().gRankPlayer);
        }
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.15f, 0, 0, getSprites().pPlayer.gBoard);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, GameValues.ballSize, 0, 0, getSprites().pPlayer.gBall);

        getSprites().rMsgGameState = new Rect(0, 0, screenWidth, screenHeight);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.5f, 0, 0, getSprites().rMsgGameState);
        getSprites().rMsgGameState.bottom -= getSprites().rMsgGameState.height() / 3;

        getSprites().gButton = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.button_flat, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonOverlay = loadGraphic(R.drawable.button_flat_white, 0, 0);

        getSprites().rBtnLaunch = getSprites().gButton.image.copyBounds();
        getSprites().rBtnPause = getSprites().gButton.image.copyBounds();
        getSprites().rBtnResume = getSprites().gButton.image.copyBounds();
        getSprites().rBtnBack = getSprites().gButton.image.copyBounds();

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.25f, 15, 15, getSprites().rBtnLaunch);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.20f, 15, 215, getSprites().rBtnPause);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.20f, 15, 215, getSprites().rBtnResume);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.30f, 50, 25, getSprites().rBtnBack);

        getSprites().rMsgPlayerName = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.15f, 15, 15, getSprites().rMsgPlayerName);

        getSprites().rMsgLives = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.20f, 150, 15, getSprites().rMsgLives);

        getSprites().gExtBigBall = loadGraphic(R.drawable.ext_bigball, 0, 0);
        getSprites().gExtCounterMeasure = loadGraphic(R.drawable.ext_countermeasure, 0, 0);
        getSprites().gExtFireball = loadGraphic(R.drawable.ext_fireball, 0, 0);
        getSprites().gExtGlueBall = loadGraphic(R.drawable.ext_glueball, 0, 0);
        getSprites().gExtInvisibleBall = loadGraphic(R.drawable.ext_invisibleball, 0, 0);
        getSprites().gExtWall = loadGraphic(R.drawable.ext_wall, 0, 0);

        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.07f, 125, 250, getSprites().gExtBigBall);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.07f, 70, 250, getSprites().gExtCounterMeasure);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.07f, 15, 250, getSprites().gExtFireball);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.07f, 125, 440, getSprites().gExtGlueBall);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.07f, 70, 440, getSprites().gExtInvisibleBall);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.07f, 15, 440, getSprites().gExtWall);

        getSprites().gEffectFireball = loadGraphic(R.drawable.effect_fireball, 0, 0);
        getSprites().gEffectWall = loadGraphic(R.drawable.effect_wall, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.08f, 0, 0, getSprites().gEffectFireball);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, 0.01f, 0, 0, getSprites().gEffectWall);

        getSprites().rMsgScoreAward = new Rect(0, 0, screenWidth, screenHeight);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER_BOTTOM, 0.3f, 0, 130, getSprites().rMsgScoreAward);
        getSprites().rMsgScoreAward.bottom -= getSprites().rMsgScoreAward.height() / 2;

        directionLeft = SystemHelper.getInventory().invertDirection ? -1 : 1;
        directionRight = SystemHelper.getInventory().invertDirection ? 1 : -1;

        mBoardFilter = new LightingColorFilter(Color.rgb(SystemHelper.getInventory().boardColorR, SystemHelper.getInventory().boardColorG, SystemHelper.getInventory().boardColorB), 1);
        mBallFilter = new LightingColorFilter(Color.rgb(SystemHelper.getInventory().ballColorR, SystemHelper.getInventory().ballColorG, SystemHelper.getInventory().ballColorB), 1);

        initGamingArea();
        // align commands to gaming area
        getSprites().gMoveLeft.image.getBounds().offsetTo(Float.valueOf(gameBounds.left).intValue(), Float.valueOf(gameBounds.bottom).intValue() - getSprites().gMoveLeft.image.getBounds().height());
        getSprites().gMoveRight.image.getBounds().offsetTo(Float.valueOf(gameBounds.right).intValue() - getSprites().gMoveRight.image.getBounds().width(), Float.valueOf(gameBounds.bottom).intValue() - getSprites().gMoveRight.image.getBounds().height());

        createBricks();
        initBoardPhysics();

        // collect stored data
        mExtensions = new int[GameValues.extensionName.length];
        int i = 0;
        for (; i < mExtensions.length; i++) {
            Extensions ext = SystemHelper.getExtensions(GameValues.extensionName[i]);
            mExtensions[i] = ext.amount + 1;
        }

        if (mExtensions[Constants.EXTENSION_NUMBER_CROSSHAIR] > 0) {
            Configuration c = SystemHelper.getConfiguration(Constants.CONFIG_SHOW_CROSSHAIR, Constants.DEFAULT_CONFIG_SHOW_CROSSHAIR);
            mShowCrossHair = Boolean.valueOf(c.value).booleanValue();
            calculateCrossHair();
        }
        mRounds = 0;
        firstSync = 0l;
        pingSelf = 0l;
        pingOpponent = 0l;

        mLayer = new Paint();
        mLayer.setColor(Color.WHITE);
        mLayer.setAlpha(75);
        mLayer.setStyle(Style.FILL_AND_STROKE);
        mLayerBorder = new Paint();
        mLayerBorder.setColor(Color.BLACK);
        mLayerBorder.setAlpha(100);
        mLayerBorder.setStyle(Style.STROKE);
    }

    private void createGame() {
        mGame = new BrickGame(new GameRound[]{
                new GameRound(0, GameValues.roundTime, -1)
        });
        setGameDelay();
        mGame.life = GameValues.totalLife;
    }

    private void setGameDelay() {
    }

    /**
     * Create the play area
     * Do this only once per game
     */
    private void initGamingArea() {
        int height = screenHeight - scaleHeight(30) - scaleHeight(30);
        int brickWidthOrg = Float.valueOf((height / GameValues.levelHeight) * (Float.valueOf(getSprites().gBrick.image.getBounds().width()) / Float.valueOf(getSprites().gBrick.image.getBounds().height()))).intValue();

        // calculate boundary on the side for commands
        int brickWidth = (screenWidth - Float.valueOf(screenWidth * GameValues.commandSpace).intValue()) / GameValues.levelWidth;
        // update height to real value
        height = height * (brickWidth * 100 / brickWidthOrg) / 100;

        ScreenKit.scaleImage(brickWidth, (height / GameValues.levelHeight), ScreenPosition.CENTER, 1.0f, -1, -1, getSprites().gBrick);

        ScreenKit.scaleImage(brickWidth, (height / GameValues.levelHeight), ScreenPosition.CENTER, 1.0f, -1, -1, getSprites().gBrickExplosion);
        getSprites().aBrickExplosion.rect = getSprites().gBrickExplosion[0].image.copyBounds();
        getSprites().aBrickExplosion.coord.x = getSprites().gBrickExplosion[0].image.getBounds().left;
        getSprites().aBrickExplosion.coord.y = getSprites().gBrickExplosion[0].image.getBounds().top;

        // center value
        int TB = (screenHeight - height) / 2;

        Path p = new Path();
        p.addOval(new RectF(scaleWidth(15),
                TB,
                scaleWidth(15) + (brickWidth * GameValues.levelWidth),
                TB + height), Direction.CW);
        p.close();
        gamePath = p;
        gamePathMeasure = new PathMeasure(gamePath, true);

        gamePaint = new Paint();
        gamePaint.setColor(Color.WHITE);
        gamePaint.setStyle(Paint.Style.STROKE);

        gameBounds = new RectF();
        p.computeBounds(gameBounds, true);

        float[] pos = getCirclePosition(0);
        gameDistance = calculateDistance(gameBounds.centerX(), gameBounds.centerY(), pos[0], pos[1]);
    }

    /**
     * Reset the current game to add a new round
     */
    private void resetGame() {
        mLevel = rnd.nextInt(GameValues.levels.length);
        getSprites().gBackground = loadGraphic(GameValues.backgrounds[rnd.nextInt(GameValues.backgrounds.length)], 0, 0);
        createBricks();
        initBoardPhysics();
        getSprites().pPlayer.ballGlued = true;
        if (getSprites().pPlayer.ballPath != null) {
            getSprites().pPlayer.ballPath.reset();
        }
    }

    private void createBricks(int[] level) {
        Rect brickSize = getSprites().gBrick.image.copyBounds();
        mGame.bricks.clear();
        for (int i = 0, h = 0, v = 0; i < level.length; i++) {
            if (level[i] > 0 && !isOutsideGamingArea(h, v)) {
                Brick brick = new Brick();
                brick.layer = level[i];
                if (GameValues.BRICK_EXPLOSIVE == GameValues.getBrickType(brick.layer)) {
                    brick.expiry = GameValues.brickExplosiveTime;
                    brick.stateChange = GameValues.brickExplosiveState;
                    brick.state = true;
                } else {
                    // subtract 1 to create the 3rd dimension
                    brick.layer -= 1;
                }
                brick.h = h;
                brick.v = v;
                brick.rect = new Rect(brickSize);
                brick.rect.offsetTo(
                        Float.valueOf(gameBounds.left + brickSize.width() * h).intValue(),
                        Float.valueOf(gameBounds.top + brickSize.height() * v).intValue());
                mGame.bricks.add(brick);
            }
            h++;
            if (h >= GameValues.levelWidth) {
                h = 0;
                v++;
            }
            if (v > GameValues.levelHeight) {
                break;
            }
        }
    }

    private void createBricks() {
        int[] level = mContext.getResources().getIntArray(GameValues.levels[mLevel]);
        createBricks(level);
    }

    private boolean isOutsideGamingArea(int h, int v) {
        return h > GameValues.levelWidth || v > GameValues.levelHeight;
    }

    /**
     * Initialize only
     */
    private void initBoardPhysics() {
        getSprites().pPlayer.boardPositionOnCircle = rnd.nextInt(GameValues.circleSteps);
        // get path middle bottom
        float[] pos = getCirclePosition(getSprites().pPlayer.boardPositionOnCircle);
        Rect r = getSprites().pPlayer.gBoard.image.copyBounds();
        r.offsetTo(Float.valueOf(pos[0]).intValue() - r.width() / 2, Float.valueOf(pos[1]).intValue() - r.height() / 2);
        getSprites().pPlayer.gBoard.image.setBounds(r);
        getSprites().pPlayer.boardRotation = calculateRotation(gameBounds.centerX(),
                gameBounds.centerY(), getSprites().pPlayer.gBoard.image.getBounds().centerX(),
                getSprites().pPlayer.gBoard.image.getBounds().centerY());

        glueBallToBoard(getSprites().pPlayer, pos);
    }

    private void glueBallToBoard(BrickPlayer p, float[] pos) {
        if (pos == null) {
            pos = getCirclePosition(p.boardPositionOnCircle);
        }
        Rect rBall = p.gBall.image.copyBounds();
        rBall.offsetTo(Float.valueOf(pos[0]).intValue() - rBall.width() / 2, Float.valueOf(pos[1]).intValue() - rBall.height() / 2);
        p.gBall.image.setBounds(rBall);
    }

    public synchronized BasicGame getGame() {
        return mGame;
    }

    /**
     * Starts the game, setting parameters for the current difficulty.
     */
    public void doStart() {
        if (mMode == GameState.NONE) {
            setMode(GameState.INIT);
        }
    }

    /**
     * Pauses the physics update & animation.
     */
    public synchronized void pause() {
        getSprites().pPlayer.gBall.image.setAlpha(255);
        saveGameState();
        setSubMode(GameSubState.PAUSE);
    }

    /**
     * Resumes from a pause.
     */
    public synchronized void unpause() {
        //set state back to running
        lastTime = System.currentTimeMillis();
        setSubMode(GameSubState.NONE);
    }

    public synchronized void exit() {
        super.exit();
        getSprites().clean();
    }

    public synchronized void create() {
        delay = GameValues.gameRoundDelay;
        super.create();
    }

    public synchronized void restoreGameState() {
        log("restoreGameState()");

        CurrentGame cg = SystemHelper.getCurrentGame();
        if (cg.life > 0.0f) {
            mGame.currentRound = cg.currentRound;
            if (cg.currentRound > 0) {
                for (int i = 0; i < cg.currentRound; i++) {
                    mGame.addRound(new GameRound(i, i == 0 ? cg.previousTime : 0, 0));
                }
            }
            mGame.getCurrentRound().time = cg.currentTime;
            mLevel = cg.level;
            mGame.life = cg.life;
            getSprites().pPlayer.score = cg.points;
            int[] level = cg.bricks;
            createBricks(level);
        }

        if (mGame.hasGame()) {
            updateRoundGraphic();
        }
    }

    public synchronized void saveGameState() {
        log("saveGameState()");
        CurrentGame cg = SystemHelper.getCurrentGame();
        cg.currentRound = mGame.currentRound;
        cg.currentTime = mGame.currentRound > -1 ? mGame.getCurrentRound().time : 0l;
        long totalTime = 0l;
        for (GameRound round : mGame.rounds) {
            totalTime += GameValues.roundTime - round.time;
        }
        cg.previousTime = totalTime;
        cg.level = mLevel;
        cg.life = mGame.life;
        cg.points = getSprites().pPlayer.score;
        // generate level array
        int[] level = mContext.getResources().getIntArray(GameValues.levels[mLevel]);
        for (int i = 0, h = 0, v = 0; i < level.length; i++) {
            if (level[i] > 0 && !isOutsideGamingArea(h, v)) {
                Brick brick = mGame.getBrick(h, v);
                if (brick != null) {
                    level[i] = brick.layer;
                    if (GameValues.BRICK_EXPLOSIVE != GameValues.getBrickType(brick.layer)) {
                        level[i] += 1;
                    }
                    // convert back into layer
                } else {
                    level[i] = 0;
                }
            }
            h++;
            if (h >= GameValues.levelWidth) {
                h = 0;
                v++;
            }
            if (v > GameValues.levelHeight) {
                break;
            }
        }
        cg.bricks = level;
        DBDriver.getInstance().store(cg);
    }

    /**
     * Restores game state from the indicated Bundle. Typically called when the
     * Activity is being restored after having been previously destroyed.
     *
     * @param savedState Bundle containing the game state
     */
    public synchronized void restoreState(Bundle savedState) {
        setMode(GameState.INIT);
        restoreGameState();
    }

    /**
     * Dump game state to the provided Bundle. Typically called when the
     * Activity is being suspended.
     *
     * @return Bundle with this view's state
     */
    public Bundle saveState(Bundle map) {
        if (map != null) {
            saveGameState();
        }
        return map;
    }

    @Override
    public void doUpdateRenderState() {
        final long time = System.currentTimeMillis();

        if (delay > 0l && lastTime > 0l) {
            delay -= time - lastTime;
        }

        switch (mMode) {
            case NONE: {
                // move to initialization
                setMode(GameState.INIT);
            }
            break;
            case INIT: {

                if (mGame.currentRound < 0) {
                    mGame.currentRound = 0;
                    // generate custom level id if we are playing single mode
                    mLevel = rnd.nextInt(GameValues.levels.length);
                    createBricks();
                }

                setMode(GameState.READY);

            }
            break;
            case READY: {
                setMode(GameState.PLAY);
            }
            break;
            case PLAY: {
                // active gameplay
                // calculate game time
                if (delay <= 0l && lastTime > 0l) {
                    if (mSubMode == GameSubState.NONE) {
                        mGame.getCurrentRound().time -= time - lastTime;
                        if (mExtTime > 0l) {
                            mExtTime -= time - lastTime;
                        }
                    }
                }

                // update graphic positions
                updatePhysics();

                // end game
                if (mGame.getCurrentRound().time < 0) {
                    setMode(GameState.END);
                }
                if (mGame.life <= 0.0f) {
                    triggerExtension(Constants.EXTENSION_ITEM_BALLSET, Constants.EXTENSION_NUMBER_BALLSET);
                    // check again
                    if (mGame.life <= 0.0f) {
                        mGame.lifeLost = true;
                        setMode(GameState.END);
                        mGame.complete = true;
                        updateStatistics();
                    }
                }
                if (mGame.bricks.isEmpty()) {
                    setMode(GameState.END);
                }

            }
            break;
            case END: {

                if (mGame.lifeLost) {
                    getSprites().pPlayer.ballGlued = true;
                    mGame.life -= 1.00f;
                    glueBallToBoard(getSprites().pPlayer, null);
                    mGame.lifeLost = false;
                    // switch to play state again
                    setMode(GameState.PLAY);
                } else if (!mGame.finished()) {
                    mRounds++;
                    mGame.addRound(new GameRound(0, GameValues.roundTime, -1));
                    mGame.currentRound += 1;
                    resetGame();
                    if (mGame.hasGame() && mGame.getCurrentRound() != null) {
                        delay = GameValues.gameRoundDelay;
                        mGame.getCurrentRound().fpsdelay = 0;
                        setMode(GameState.READY);
                        // remove all obstacles to re-create the new round
                    } else {
                        setMode(GameState.END);
                        mGame.won = true;
                        // update statistics
                        mGame.complete = true;
                        updateStatistics();
                        mGame.reset();
                        mGame.life = 0.0f;
                    }
                }
                // game round ended - show stats, move to next round, end game
                if (!mGame.complete && (mGame.finished() || mSubMode == GameSubState.TIMEOUT)) {
                    setMode(GameState.END);
                    mGame.complete = true;
                    updateStatistics();
                    mGame.reset();
                }
            }
            break;
            default:
                setMode(GameState.NONE);
                break;
        }

        lastTime = time;
    }

    private void updateRoundGraphic() {
    }

    private void updateStatistics() {
        log("updateStatistics()");
        SystemHelper.getInventory().gamesWon += 1;
        SystemHelper.getInventory().coins += getSprites().pPlayer.score / GameValues.scoreDividerSinglePlayer;
        long totalTime = 0l;
        for (GameRound round : mGame.rounds) {
            totalTime += GameValues.roundTime - round.time;
        }
        SystemHelper.getInventory().gameTime += totalTime;
        DBDriver.getInstance().store(SystemHelper.getInventory());

    }

    /**
     * Used to signal the thread whether it should be running or not. Passing
     * true allows the thread to run; passing false will shut it down if it's
     * already running. Calling start() after this was most recently called with
     * false will result in an immediate shutdown.
     *
     * @param b true to run, false to shut down
     */
    public void setRunning(boolean b) {
        super.setRunning(b);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean blocked = false;

        if (mMode == GameState.PLAY) {
            if (mSubMode == GameSubState.NONE) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (containsClick(getSprites().gMoveLeft, event.getX(), event.getY())) {
                            getSprites().pPlayer.boardMove = GameValues.boardMovement * directionLeft;
                        }
                        if (containsClick(getSprites().gMoveRight, event.getX(), event.getY())) {
                            getSprites().pPlayer.boardMove = GameValues.boardMovement * directionRight;
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (containsClick(getSprites().rBtnPause, event.getX(), event.getY())) {
                            setSubMode(GameSubState.PAUSE);
                            blocked = true;
                        }

                        if (getSprites().pPlayer.ballGlued && containsClick(getSprites().rBtnLaunch, event.getX(), event.getY())) {
                            getSprites().pPlayer.ballGlued = false;
                            double[] target = calculatePointInDistance(getSprites().pPlayer.gBall.image.getBounds().centerX(), getSprites().pPlayer.gBall.image.getBounds().centerY(), gameBounds.centerX(), gameBounds.centerY());
                            getSprites().pPlayer.ballTarget[0] = Double.valueOf(target[0]).intValue();
                            getSprites().pPlayer.ballTarget[1] = Double.valueOf(target[1]).intValue();
                            calculateBallPath(getSprites().pPlayer, target[0], target[1]);
                        }

                        if (containsClick(getSprites().gExtBigBall, event.getX(), event.getY())) {
                            triggerExtension(Constants.EXTENSION_ITEM_BIGBALL, Constants.EXTENSION_NUMBER_BIGBALL);
                        }
                        if (containsClick(getSprites().gExtCounterMeasure, event.getX(), event.getY())) {
                            triggerExtension(Constants.EXTENSION_ITEM_COUNTER_MEASURE, Constants.EXTENSION_NUMBER_COUNTER_MEASURE);
                        }
                        if (containsClick(getSprites().gExtFireball, event.getX(), event.getY())) {
                            triggerExtension(Constants.EXTENSION_ITEM_FIREBALL, Constants.EXTENSION_NUMBER_FIREBALL);
                        }
                        if (containsClick(getSprites().gExtGlueBall, event.getX(), event.getY())) {
                            triggerExtension(Constants.EXTENSION_ITEM_GLUEBALL, Constants.EXTENSION_NUMBER_GLUEBALL);
                        }
                        if (containsClick(getSprites().gExtInvisibleBall, event.getX(), event.getY())) {
                            triggerExtension(Constants.EXTENSION_ITEM_INVISIBLEBALL, Constants.EXTENSION_NUMBER_INVISIBLEBALL);
                        }
                        if (containsClick(getSprites().gExtWall, event.getX(), event.getY())) {
                            triggerExtension(Constants.EXTENSION_ITEM_WALL, Constants.EXTENSION_NUMBER_WALL);
                        }

                        getSprites().pPlayer.boardMove = GameValues.boardNoMove;

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // move
                        break;
                }
            }
        }

        if (mMode == GameState.END && mGame.finished()) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                        delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                    }
            }
        }

        if (!blocked && mSubMode == GameSubState.PAUSE) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    if (containsClick(getSprites().rBtnResume, event.getX(), event.getY())) {
                        setSubMode(GameSubState.NONE);
                    }
                    if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                        delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                    }
                    break;
            }
        }

        return true;
    }

    private void triggerExtension(String extName, int extNumber) {
        Extensions ext = SystemHelper.getExtensions(extName);
        if (ext.amount > -1) {
            ext.amount -= 1;
            SystemHelper.setExtensions(ext);
            mExtensions[extNumber] -= 1;
            DBDriver.getInstance().store(ext);

            resetExtensionImmediately(extNumber, getSprites().pPlayer);

            // do not block other extensions if it is a one-shot only
            if (extNumber != Constants.EXTENSION_NUMBER_COUNTER_MEASURE && extNumber != Constants.EXTENSION_NUMBER_BALLSET) {
                mExtTime = GameValues.TIME_PER_EXTENSION[extNumber];
                getSprites().pPlayer.ballEffect = extNumber;
            }

            triggerExtensionImmediately(extNumber, getSprites().pPlayer);

        }
    }

    private void resetExtensionImmediately(int effect, BrickPlayer player) {
        if (effect != Constants.EXTENSION_NUMBER_COUNTER_MEASURE && effect != Constants.EXTENSION_NUMBER_BALLSET) {
            if (effect != Constants.EXTENSION_NUMBER_INVISIBLEBALL && player.ballEffect == Constants.EXTENSION_NUMBER_INVISIBLEBALL) {
                player.gBall.image.setAlpha(255);
            }
            if (effect != Constants.EXTENSION_NUMBER_BIGBALL && player.ballEffect == Constants.EXTENSION_NUMBER_BIGBALL) {
                player.gBall.image.setBounds(0, 0, mOrgBallSize[0], mOrgBallSize[1]);
                ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, GameValues.ballSize, 0, 0, player.gBall);
                if (getSprites().pPlayer.ballGlued) {
                    glueBallToBoard(player, getCirclePosition(player.boardPositionOnCircle));
                }
            }
        }
    }

    private void triggerExtensionImmediately(int effect, BrickPlayer player) {
        switch (effect) {
            // apply immediate effects
            case Constants.EXTENSION_NUMBER_COUNTER_MEASURE:
                for (Brick brick : mGame.bricks) {
                    if (GameValues.BRICK_EXPLOSIVE == GameValues.getBrickType(brick.layer)) {
                        if (brick.expiry > 0l) {
                            // disarm
                            brick.layer = 0;
                        }
                    }
                }
                break;
            case Constants.EXTENSION_NUMBER_BALLSET:
                mGame.life = GameValues.totalLife;
                break;
            case Constants.EXTENSION_NUMBER_BIGBALL:
                player.gBall.image.setBounds(0, 0, mOrgBallSize[0], mOrgBallSize[1]);
                ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.CENTER, GameValues.bigBallSize, 0, 0, player.gBall);
                if (player.ballGlued) {
                    glueBallToBoard(player, getCirclePosition(player.boardPositionOnCircle));
                }
                break;
        }
    }

    private void calculateCrossHair() {
        if (mShowCrossHair && getSprites().pPlayer.ballGlued) {
            if (mCrossHair == null) {
                mCrossHair = new Path();
            } else {
                mCrossHair.reset();
            }
            mCrossHair.moveTo(getSprites().pPlayer.gBall.image.getBounds().exactCenterX(), getSprites().pPlayer.gBall.image.getBounds().exactCenterY());
            mCrossHair.lineTo(gameBounds.centerX(), gameBounds.centerY());
            mCrossHair.close();
        }
    }

    private void calculateBallPath(BrickPlayer player, int sourceX, int sourceY, int targetX, int targetY, int ballPosOnPath) {
        if (player.ballPath == null) {
            player.ballPath = new Path();
        } else {
            player.ballPath.reset();
        }
        player.ballPath.moveTo(sourceX, sourceY);
        player.ballPath.lineTo(targetX, targetY);
        player.ballPath.close();
        // update the measure object
        if (player.ballPathMeasure == null) {
            player.ballPathMeasure = new PathMeasure(player.ballPath, true);
        } else {
            player.ballPathMeasure.setPath(player.ballPath, true);
        }

        player.ballPosOnPath = ballPosOnPath;

        player.ballSource[0] = sourceX;
        player.ballSource[1] = sourceY;
        player.ballTarget[0] = targetX;
        player.ballTarget[1] = targetY;
    }

    private void calculateBallPath(BrickPlayer player, double destX, double destY) {
        calculateBallPath(player, player.gBall.image.getBounds().centerX(), player.gBall.image.getBounds().centerY(), Double.valueOf(destX).intValue(), Double.valueOf(destY).intValue(), 0);
    }

    /**
     * Update the graphic x/y values in real time. This is called before the
     * draw() method
     */
    private void updatePhysics() {

        // the fixed time for drawing this frame
        final long time = System.currentTimeMillis();

        if (mMode == GameState.PLAY) {

            if (mSubMode == GameSubState.NONE) {

                boolean ballBlocked = false;

                List<Brick> removalList = new ArrayList<Brick>();

                if (getSprites().pPlayer.boardMove != GameValues.boardNoMove) {

                    float[] pos = getCirclePosition(getSprites().pPlayer.boardPositionOnCircle);
                    getSprites().pPlayer.boardPositionOnCircle += getSprites().pPlayer.boardMove;

                    if (getSprites().pPlayer.boardPositionOnCircle > GameValues.circleSteps) {
                        getSprites().pPlayer.boardPositionOnCircle = 0;
                    }
                    if (getSprites().pPlayer.boardPositionOnCircle < 0) {
                        getSprites().pPlayer.boardPositionOnCircle = GameValues.circleSteps;
                    }

                    moveBoardOnPath(getSprites().pPlayer, pos);

                    if (getSprites().pPlayer.ballGlued) {
                        glueBallToBoard(getSprites().pPlayer, pos);
                    }

                    if (mExtensions[Constants.EXTENSION_NUMBER_CROSSHAIR] > 0) {
                        calculateCrossHair();
                    }
                }

                if (!getSprites().pPlayer.ballGlued) {
                    moveBallOnPath(getSprites().pPlayer);
                } else {
                    ballBlocked = true;
                }

                // update exploding bricks
                for (Brick brick : mGame.bricks) {
                    if (GameValues.BRICK_EXPLOSIVE == GameValues.getBrickType(brick.layer)) {
                        brick.expiry -= time - lastTime;
                        brick.stateChange -= time - lastTime;
                        if (brick.stateChange < 0l) {
                            brick.stateChange = brick.expiry < GameValues.brickExplosiveStateRemaining ? GameValues.brickExplosiveStateFast : GameValues.brickExplosiveState;
                            brick.state = !brick.state;
                        }
                        if (brick.expiry < 0l) {
                            // Explode
                            brick.hit = true;
                            removalList.add(brick);
                            // TODO explode animation
                            for (int h = brick.h - GameValues.explosiveGrid; h <= brick.h + GameValues.explosiveGrid; h++) {
                                for (int v = brick.v - GameValues.explosiveGrid; v <= brick.v + GameValues.explosiveGrid; v++) {
                                    if (mGame.getBrick(h, v) != null) {
                                        mGame.getBrick(h, v).hit = true;
                                        removalList.add(mGame.getBrick(h, v));
                                    }
                                }
                            }

                        }
                    }
                }

                // calculate ball crossing a brick
                if (!getSprites().pPlayer.ballBlocked) {
                    Brick target = null;
                    for (Brick brick : mGame.bricks) {
                        if (Rect.intersects(scaleBrickInnerRect(brick.rect), scaleBallInnerRect(getSprites().pPlayer.gBall.image.getBounds())) && !brick.hit) {
                            getSprites().pPlayer.score += GameValues.getBrickPoints(brick.layer);
                            if (GameValues.BRICK_NORMAL == GameValues.getBrickType(brick.layer)) {
                                brick.layer -= 1;
                            } else {
                                // destroy other brick types
                                brick.layer = -1;
                            }

                            // remove brick if no layer is left
                            if (brick.layer < 0) {
                                brick.hit = true;
                                removalList.add(brick);
                            }
                            // ignore ball direction update if fireball is active
                            if (getSprites().pPlayer.ballEffect != Constants.EXTENSION_NUMBER_FIREBALL) {
                                ballBlocked = true;
                                if (target == null) {
                                    target = brick;
                                }
                            }
                        }
                    }
                    // only allow one direction update
                    if (target != null) {
                        updateBallDirection(getSprites().pPlayer, target.rect);
                    }
                }

                // TODO add removal animation for bricks
                mGame.bricks.removeAll(removalList);

                // update ball physics on gaming area border
                if (!getSprites().pPlayer.ballGlued
                        && Rect.intersects(getSprites().pPlayer.gBall.image.getBounds(), getSprites().pPlayer.gBoard.image.getBounds())) {
                    // the ball is near the board, do a more detailed scan
                    // scan board boundary
                    float[][] hit = ballHitsBoard(getSprites().pPlayer);
                    if (hit != null) {
                        ballBlocked = true;
                        if (!getSprites().pPlayer.ballBlocked) {
                            if (getSprites().pPlayer.ballEffect == Constants.EXTENSION_NUMBER_GLUEBALL) {
                                getSprites().pPlayer.ballGlued = true;
                            } else {
                                updateBallDirection(getSprites().pPlayer, hit[0], hit[1], getSprites().pPlayer.boardPositionOnCircle + 90);
                            }
                        }
                    }
                }
                // scan for wall
                if (!getSprites().pPlayer.ballGlued && !ballBlocked && getSprites().pPlayer.ballEffect == Constants.EXTENSION_NUMBER_WALL) {
                    // top
                    if (!ballBlocked && getSprites().pPlayer.gBall.image.getBounds().contains(getSprites().pPlayer.gBall.image.getBounds().centerX(), Float.valueOf(gameBounds.top).intValue())) {
                        ballBlocked = true;
                        if (!getSprites().pPlayer.ballBlocked) {
                            updateBallDirection(getSprites().pPlayer, new float[]{getSprites().pPlayer.gBall.image.getBounds().centerX(), gameBounds.top}, new float[]{gameBounds.right, gameBounds.top}, 0);
                        }
                    }
                    // bottom
                    if (!ballBlocked && getSprites().pPlayer.gBall.image.getBounds().contains(getSprites().pPlayer.gBall.image.getBounds().centerX(), Float.valueOf(gameBounds.bottom).intValue())) {
                        ballBlocked = true;
                        if (!getSprites().pPlayer.ballBlocked) {
                            updateBallDirection(getSprites().pPlayer, new float[]{getSprites().pPlayer.gBall.image.getBounds().centerX(), gameBounds.bottom}, new float[]{gameBounds.left, gameBounds.bottom}, 180);
                        }
                    }
                    // left
                    if (!ballBlocked && getSprites().pPlayer.gBall.image.getBounds().contains(Float.valueOf(gameBounds.left).intValue(), getSprites().pPlayer.gBall.image.getBounds().centerY())) {
                        ballBlocked = true;
                        if (!getSprites().pPlayer.ballBlocked) {
                            updateBallDirection(getSprites().pPlayer, new float[]{gameBounds.left, getSprites().pPlayer.gBall.image.getBounds().centerY()}, new float[]{gameBounds.left, gameBounds.top}, 270);
                        }
                    }
                    // right
                    if (!ballBlocked && getSprites().pPlayer.gBall.image.getBounds().contains(Float.valueOf(gameBounds.right).intValue(), getSprites().pPlayer.gBall.image.getBounds().centerY())) {
                        ballBlocked = true;
                        if (!getSprites().pPlayer.ballBlocked) {
                            updateBallDirection(getSprites().pPlayer, new float[]{gameBounds.right, getSprites().pPlayer.gBall.image.getBounds().centerY()}, new float[]{gameBounds.right, gameBounds.bottom}, 90);
                        }
                    }
                }

                getSprites().pPlayer.ballBlocked = ballBlocked;

                if (!getSprites().pPlayer.ballGlued && !getSprites().pPlayer.ballBlocked
                        && !RectF.intersects(gameBounds, new RectF(getSprites().pPlayer.gBall.image.getBounds()))) {
                    mGame.lifeLost = true;
                    setMode(GameState.END);
                }

                boolean hasBricks = false;
                for (Brick brick : mGame.bricks) {
                    if (!brick.hit) {
                        hasBricks = true;
                    }
                }
                if (!hasBricks) {
                    setMode(GameState.END);
                }

                if (mExtTime <= 0l && getSprites().pPlayer.ballEffect != -1) {
                    // Revert effects only if effects are applied
                    resetExtensionImmediately(-1, getSprites().pPlayer);
                    // clear effects
                    getSprites().pPlayer.ballEffect = -1;
                }
            }

        }
    }

    private void moveBoardOnPath(BrickPlayer player, float[] pos) {
        // move along the path
        Rect r = player.gBoard.image.copyBounds();
        r.offsetTo(Float.valueOf(pos[0]).intValue() - r.width() / 2, Float.valueOf(pos[1]).intValue() - r.height() / 2);
        player.gBoard.image.setBounds(r);

        // calculate rotation
        player.boardRotation = player.boardPositionOnCircle + 90;
    }

    private void moveBallOnPath(BrickPlayer player) {
        // move ball toward calculated target
        Rect rBall = player.gBall.image.copyBounds();
        // Calculate ball movement on the path
        float segments = player.ballPathMeasure.getLength() / GameValues.ballSteps;
        float[] pos = {0f, 0f};
        player.ballPathMeasure.getPosTan(segments * player.ballPosOnPath, pos, null);
        rBall.offsetTo(Float.valueOf(pos[0]).intValue() - rBall.width() / 2, Float.valueOf(pos[1]).intValue() - rBall.height() / 2);
        player.gBall.image.setBounds(rBall);
        if (player.ballPosOnPath < GameValues.ballSteps / 2) {
            player.ballPosOnPath += GameValues.ballMovement;
        }
    }

    private Rect scaleBrickInnerRect(Rect r) {
        Rect rect = new Rect(r);
        rect.left += scaleWidth(2);
        rect.right -= scaleWidth(2);
        rect.top += scaleHeight(2);
        rect.bottom -= scaleHeight(2);
        return rect;
    }

    private Rect scaleBallInnerRect(Rect r) {
        Rect rect = new Rect(r);
        rect.left += scaleWidth(3);
        rect.right -= scaleWidth(3);
        rect.top += scaleHeight(3);
        rect.bottom -= scaleHeight(3);
        return rect;
    }

    private float[][] ballHitsBoard(BrickPlayer player) {
        float[][] target = null;

        int center = player.boardPositionOnCircle;
        int top = -1;
        int mod = -1;
        int bottom = -1;
        int i = center;
        // find top
        float[] pos = null;
        while (top == -1) {
            i++;
            if (i > GameValues.circleSteps) {
                // overjump 1 step
                i = 1;
            }
            pos = getCirclePosition(i);
            if (!player.gBoard.image.getBounds().contains(Float.valueOf(pos[0]).intValue(), Float.valueOf(pos[1]).intValue())) {
                top = i--;
            }
        }
        // find bottom
        i = center;
        while (bottom == -1) {
            i--;
            if (i < 0) {
                // overjump 1 step
                i = GameValues.circleSteps - 1;
            }
            pos = getCirclePosition(i);
            if (!player.gBoard.image.getBounds().contains(Float.valueOf(pos[0]).intValue(), Float.valueOf(pos[1]).intValue())) {
                bottom = i++;
            }
        }
        // modify if top is smaller bottom - on the right side of the game board
        mod = top < bottom ? top + 360 : top;

        // scan from top to bottom
        for (i = mod; i >= bottom; i--) {
            pos = getCirclePosition(i);
            if (player.gBall.image.getBounds().contains(Float.valueOf(pos[0]).intValue(), Float.valueOf(pos[1]).intValue())) {
                target = new float[2][2];
                target[0] = pos;
                target[1] = getCirclePosition(i == bottom ? top : bottom);
                break;
            }
        }

        return target;
    }

    private void updateBallDirection(BrickPlayer player, Rect brickRect) {

        float[] hit = new float[2];
        float[] corner = new float[2];
        int mod = 0;
        // determine hit side on the brick rect
        // top side
        if (brickRect.contains(player.gBall.image.getBounds().centerX(), player.gBall.image.getBounds().bottom)) {
            corner[0] = brickRect.left;
            corner[1] = brickRect.top;
            hit[0] = player.gBall.image.getBounds().exactCenterX();
            hit[1] = brickRect.top;
            mod = 180;
        }
        // left side
        else if (brickRect.contains(player.gBall.image.getBounds().right, player.gBall.image.getBounds().centerY())) {
            corner[0] = brickRect.left;
            corner[1] = brickRect.bottom;
            hit[0] = brickRect.left;
            hit[1] = player.gBall.image.getBounds().exactCenterY();
            mod = 90;
        }
        // bottom side
        else if (brickRect.contains(player.gBall.image.getBounds().centerX(), player.gBall.image.getBounds().top)) {
            corner[0] = brickRect.right;
            corner[1] = brickRect.bottom;
            hit[0] = player.gBall.image.getBounds().exactCenterX();
            hit[1] = brickRect.bottom;
            mod = 0;
        }
        // right side
        else if (brickRect.contains(player.gBall.image.getBounds().left, player.gBall.image.getBounds().centerY())) {
            corner[0] = brickRect.right;
            corner[1] = brickRect.top;
            hit[0] = brickRect.right;
            hit[1] = player.gBall.image.getBounds().exactCenterY();
            mod = 270;
        }

        // modify by angle depending on hit side
        updateBallDirection(player, hit, corner, mod);
    }

    private double[] calculatePointInDistance(float srcX, float srcY, float trgX, float trgY) {
        float dx = trgX - srcX;
        float dy = trgY - srcY;
        // * 4 is one circle radius
        double k = Math.sqrt((Math.pow(gameDistance, 2) * 6) / (Math.pow(dx, 2) + Math.pow(dy, 2)));
        return new double[]{srcX + dx * k, srcY + dy * k};
    }

    private void updateBallDirection(BrickPlayer player, float[] hit, float[] corner, int mod) {
        double ball2BallSrc = calculateDistance(hit[0], hit[1], player.ballSource[0], player.ballSource[1]);
        double ball2Corner = calculateDistance(hit[0], hit[1], corner[0], corner[1]);
        double corner2BallSrc = calculateDistance(corner[0], corner[1], player.ballSource[0], player.ballSource[1]);

        double angleRad = Math.acos((Math.pow(corner2BallSrc, 2) - Math.pow(ball2BallSrc, 2) - Math.pow(ball2Corner, 2)) / (-2 * ball2BallSrc * ball2Corner));

        double targetDeg = 180 - (angleRad * 180 / Math.PI) + mod;
        // TODO check if necessary
//        if(targetDeg == 180) {
//            targetDeg = 0.0;
//        }
        double targetRad = targetDeg * Math.PI / 180;
        double targetX = hit[0] + Math.cos(targetRad) * gameDistance * 2.5;//ball2BallSrc;
        double targetY = hit[1] + Math.sin(targetRad) * gameDistance * 2.5;
        calculateBallPath(player, Double.valueOf(targetX).intValue(), Double.valueOf(targetY).intValue());
    }

    private double calculateDistance(float x2, float y2, float x1, float y1) {
        return Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(y2 - y1), 2));
    }

    private float calculateRotation(float cx, float cy, float p1x, float p1y) {
        float p0x = cx;
        float p0y = Double.valueOf(cy - Math.sqrt(Math.abs(p1x - cx) * Math.abs(p1x - cx) + Math.abs(p1y - cy) * Math.abs(p1y - cy))).floatValue();
        return Double.valueOf((2 * Math.atan2(p1y - p0y, p1x - p0x)) * 180 / Math.PI).floatValue();
    }

    private float[] getCirclePosition(int position) {
        float segments = gamePathMeasure.getLength() / GameValues.circleSteps;
        float pos[] = {0f, 0f};
        gamePathMeasure.getPosTan(segments * position, pos, null);
        return pos;
    }

    private int drawNumber(Canvas canvas, Drawable d, int pos, int logoWidth, int logoHeight, int scaleWidth, int scaleHeight) {
        int scaleLeft = scaleWidth(scaleWidth) - pos * logoWidth - scaleWidth(5);
        d.setBounds(scaleLeft, scaleHeight(scaleHeight), scaleLeft + logoWidth, scaleHeight(scaleHeight) + logoHeight);
        d.draw(canvas);
        return d.getBounds().top;
    }

    /**
     * Draws the graphics onto the Canvas.
     */
    @Override
    public void doDrawRenderer(Canvas canvas) {
        // Draw the background image. Operations on the Canvas accumulate
        // so this is like clearing the screen.

        if (getSprites().gBackground != null) {
            // draw image across screen
            int h = 0;
            int v = 0;
            Rect r = getSprites().gBackground.image.copyBounds();
            r.offsetTo(0, 0);
            getSprites().gBackground.image.setBounds(r);
            getSprites().gBackground.image.draw(canvas);
            while (h < screenWidth && v < screenHeight) {
                r = getSprites().gBackground.image.copyBounds();
                h = r.right;
                if (h > screenWidth) {
                    v = r.bottom;
                    h = 0;
                }
                r.offsetTo(h, v);
                getSprites().gBackground.image.setBounds(r);
                if (v > screenHeight) {
                    r.offsetTo(0, 0);
                    getSprites().gBackground.image.setBounds(r);
                    break;
                }
                getSprites().gBackground.image.draw(canvas);
            }
        }

// DEBUG MODUS        
//        if (gamePath != null) {
//            canvas.drawPath(gamePath, gamePaint);
//        }
//        if(gameBounds != null) {
//            canvas.drawRect(gameBounds, gamePaint);
//        }
//        if(getSprites().pPlayer.ballPath != null) {
//            canvas.drawPath(getSprites().pPlayer.ballPath, gamePaint);
//        }


        // the fixed time for drawing this frame
        final long time = System.currentTimeMillis();

        if (mMode == GameState.PLAY) {

            if (mSubMode == GameSubState.NONE) {

                drawLayer(canvas);

                if (mShowCrossHair && mCrossHair != null && getSprites().pPlayer.ballGlued && mExtensions[Constants.EXTENSION_NUMBER_CROSSHAIR] > 0) {
                    canvas.drawPath(mCrossHair, gamePaint);
                }

                if (getSprites().pPlayer.ballEffect == Constants.EXTENSION_NUMBER_WALL) {
                    drawGraphic(canvas, getSprites().gEffectWall, Float.valueOf(gameBounds.left).intValue(), Float.valueOf(gameBounds.top).intValue(), Float.valueOf(gameBounds.left).intValue(), Float.valueOf(gameBounds.bottom).intValue());
                    drawGraphic(canvas, getSprites().gEffectWall, Float.valueOf(gameBounds.right).intValue(), Float.valueOf(gameBounds.top).intValue(), Float.valueOf(gameBounds.right).intValue(), Float.valueOf(gameBounds.bottom).intValue());
                    drawGraphic(canvas, getSprites().gEffectWall, Float.valueOf(gameBounds.left).intValue(), Float.valueOf(gameBounds.top).intValue(), Float.valueOf(gameBounds.right).intValue(), Float.valueOf(gameBounds.top).intValue());
                    drawGraphic(canvas, getSprites().gEffectWall, Float.valueOf(gameBounds.left).intValue(), Float.valueOf(gameBounds.bottom).intValue(), Float.valueOf(gameBounds.right).intValue(), Float.valueOf(gameBounds.bottom).intValue());
                }

                getSprites().gMoveLeft.image.draw(canvas);
                getSprites().gMoveRight.image.draw(canvas);

                choiceDraw(canvas, getSprites().rBtnLaunch, getSprites().gButton, getSprites().gButton, getSprites().pPlayer.ballGlued, false, GameValues.cFilterYellow);
                getSprites().gButton.image.setBounds(new Rect(getSprites().rBtnPause));
                getSprites().gButton.image.draw(canvas);

                drawText(canvas, getSprites().rBtnPause, getString(R.string.menubutton_pause), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawText(canvas, getSprites().rBtnLaunch, getString(R.string.menubutton_launch), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

                choiceDraw(canvas, getSprites().gExtBigBall.image.copyBounds(), getSprites().gButton, getSprites().gButton, mExtensions[Constants.EXTENSION_NUMBER_BIGBALL], 0, getSprites().pPlayer.ballEffect, Constants.EXTENSION_NUMBER_BIGBALL, GameValues.cFilterRed, GameValues.cFilterGreen);
                getSprites().gExtBigBall.image.draw(canvas);
                choiceDraw(canvas, getSprites().gExtCounterMeasure.image.copyBounds(), getSprites().gButton, getSprites().gButton, mExtensions[Constants.EXTENSION_NUMBER_COUNTER_MEASURE], 0, getSprites().pPlayer.ballEffect, Constants.EXTENSION_NUMBER_COUNTER_MEASURE, GameValues.cFilterRed, GameValues.cFilterGreen);
                getSprites().gExtCounterMeasure.image.draw(canvas);
                choiceDraw(canvas, getSprites().gExtFireball.image.copyBounds(), getSprites().gButton, getSprites().gButton, mExtensions[Constants.EXTENSION_NUMBER_FIREBALL], 0, getSprites().pPlayer.ballEffect, Constants.EXTENSION_NUMBER_FIREBALL, GameValues.cFilterRed, GameValues.cFilterGreen);
                getSprites().gExtFireball.image.draw(canvas);
                choiceDraw(canvas, getSprites().gExtGlueBall.image.copyBounds(), getSprites().gButton, getSprites().gButton, mExtensions[Constants.EXTENSION_NUMBER_GLUEBALL], 0, getSprites().pPlayer.ballEffect, Constants.EXTENSION_NUMBER_GLUEBALL, GameValues.cFilterRed, GameValues.cFilterGreen);
                getSprites().gExtGlueBall.image.draw(canvas);
                choiceDraw(canvas, getSprites().gExtInvisibleBall.image.copyBounds(), getSprites().gButton, getSprites().gButton, mExtensions[Constants.EXTENSION_NUMBER_INVISIBLEBALL], 0, getSprites().pPlayer.ballEffect, Constants.EXTENSION_NUMBER_INVISIBLEBALL, GameValues.cFilterRed, GameValues.cFilterGreen);
                getSprites().gExtInvisibleBall.image.draw(canvas);
                choiceDraw(canvas, getSprites().gExtWall.image.copyBounds(), getSprites().gButton, getSprites().gButton, mExtensions[Constants.EXTENSION_NUMBER_WALL], 0, getSprites().pPlayer.ballEffect, Constants.EXTENSION_NUMBER_WALL, GameValues.cFilterRed, GameValues.cFilterGreen);
                getSprites().gExtWall.image.draw(canvas);

                drawNumbers(canvas, ScreenKit.unscaleWidth(getSprites().gExtBigBall.image.getBounds().centerX(), screenWidth), ScreenKit.unscaleHeight(getSprites().gExtBigBall.image.getBounds().top, screenHeight), mExtensions[Constants.EXTENSION_NUMBER_BIGBALL], null, standardNumberWidth * 0.7f, standardNumberHeight * 0.7f);
                drawNumbers(canvas, ScreenKit.unscaleWidth(getSprites().gExtCounterMeasure.image.getBounds().centerX(), screenWidth), ScreenKit.unscaleHeight(getSprites().gExtCounterMeasure.image.getBounds().top, screenHeight), mExtensions[Constants.EXTENSION_NUMBER_COUNTER_MEASURE], null, standardNumberWidth * 0.7f, standardNumberHeight * 0.7f);
                drawNumbers(canvas, ScreenKit.unscaleWidth(getSprites().gExtFireball.image.getBounds().centerX(), screenWidth), ScreenKit.unscaleHeight(getSprites().gExtFireball.image.getBounds().top, screenHeight), mExtensions[Constants.EXTENSION_NUMBER_FIREBALL], null, standardNumberWidth * 0.7f, standardNumberHeight * 0.7f);
                drawNumbers(canvas, ScreenKit.unscaleWidth(getSprites().gExtGlueBall.image.getBounds().centerX(), screenWidth), ScreenKit.unscaleHeight(getSprites().gExtGlueBall.image.getBounds().top, screenHeight), mExtensions[Constants.EXTENSION_NUMBER_GLUEBALL], null, standardNumberWidth * 0.7f, standardNumberHeight * 0.7f);
                drawNumbers(canvas, ScreenKit.unscaleWidth(getSprites().gExtInvisibleBall.image.getBounds().centerX(), screenWidth), ScreenKit.unscaleHeight(getSprites().gExtInvisibleBall.image.getBounds().top, screenHeight), mExtensions[Constants.EXTENSION_NUMBER_INVISIBLEBALL], null, standardNumberWidth * 0.7f, standardNumberHeight * 0.7f);
                drawNumbers(canvas, ScreenKit.unscaleWidth(getSprites().gExtWall.image.getBounds().centerX(), screenWidth), ScreenKit.unscaleHeight(getSprites().gExtWall.image.getBounds().top, screenHeight), mExtensions[Constants.EXTENSION_NUMBER_WALL], null, standardNumberWidth * 0.7f, standardNumberHeight * 0.7f);


                // draw bricks
                if (mGame.bricks != null) {
                    for (Brick brick : mGame.bricks) {
                        canvas.save();
                        Rect r = getSprites().gBrick.image.copyBounds();
                        r.offsetTo(brick.rect.left, brick.rect.top);
                        getSprites().gBrick.image.setBounds(r);
                        // apply explosive color filter
                        if (GameValues.BRICK_EXPLOSIVE == GameValues.getBrickType(brick.layer)) {
                            if (brick.state) {
                                getSprites().gBrick.image.setColorFilter(GameValues.cFilterRed);
                            } else {
                                getSprites().gBrick.image.setColorFilter(GameValues.cFilterDarkRed);
                            }
                        } else {
                            // apply default color filter
                            getSprites().gBrick.image.setColorFilter(GameValues.getBrickFilterReference(brick.layer));
                        }
                        getSprites().gBrick.image.draw(canvas);
                        // revert color filter for next brick
                        //                    if(GameValues.BRICK_EXPLOSIVE == GameValues.getBrickType(brick.layer)) {
                        getSprites().gBrick.image.clearColorFilter();
                        //                    }

                        if (GameValues.BRICK_EXPLOSIVE == GameValues.getBrickType(brick.layer) && brick.expiry <= GameValues.brickExplosiveAnim) {
                            getSprites().aBrickExplosion.nextFrame(time);
                            getSprites().aBrickExplosion.rect.offsetTo(brick.rect.left, brick.rect.top);
                            getSprites().gBrickExplosion[getSprites().aBrickExplosion.nextFrame().gReference].image.setBounds(r);
                            getSprites().gBrickExplosion[getSprites().aBrickExplosion.nextFrame().gReference].image.draw(canvas);
                        }

                        canvas.restore();
                    }
                }

                if (getSprites().gRankOpponent != null) {
                    getSprites().gRankOpponent.image.draw(canvas);
                }
                if (getSprites().gRankPlayer != null) {
                    getSprites().gRankPlayer.image.draw(canvas);
                }
                drawPlayerNames(canvas);
                // draw board
                drawPlayer(canvas, getSprites().pPlayer, mBoardFilter, mBallFilter, true);
            }

            if (mGame.hasGame() && mGame.getCurrentRound() != null) {
                // Draw Time
                if (mGame.getCurrentRound().time > -1l) {
                    long minutes = (mGame.getCurrentRound().time) / 60000;
                    long seconds = (mGame.getCurrentRound().time) / 1000;
                    if (seconds > 60) {
                        seconds = seconds - (minutes * 60);
                    }
                    if (seconds == 60) {
                        seconds = 0;
                    }
                    String strValue = MessageFormat.format("{0}:{1,number,00}", minutes, seconds);
                    drawNumbers(canvas, 100, 45, strValue, null, standardNumberWidth * 0.7f, standardNumberHeight * 0.7f);
                }

                if (mExtTime > 0l) {
                    long seconds = (mExtTime) / 1000;
                    String strValue = MessageFormat.format("{0,number,00}", seconds);
                    drawNumbers(canvas, 45, 110, strValue, null, standardNumberWidth * 0.7f, standardNumberHeight * 0.7f);
                }
            }

            drawNumbers(canvas, 420, 10, getSprites().pPlayer.score, null, standardNumberWidth, standardNumberHeight);
        }

        if (mSubMode == GameSubState.PAUSE) {
            drawLayer(canvas);

            drawPlayerNames(canvas);
            choiceBaseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonOverlay, getSprites().gButton, activeButton, Constants.ACTION_HOME, GameValues.cFilterGreen);

            getSprites().gButton.image.setBounds(new Rect(getSprites().rBtnResume));
            getSprites().gButton.image.draw(canvas);

            drawText(canvas, getSprites().rBtnResume, getString(R.string.menubutton_resume), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
            drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_cancel), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
        }

        if (mMode == GameState.READY) {
            drawText(canvas, getSprites().rMsgGameState, MessageFormat.format(getString(R.string.message_gameround), mGame.currentRound + 1), 0, 0);
        }

        if (mMode == GameState.END && (mGame.finished() || mGame.complete)) {
            drawPlayerNames(canvas);
            choiceBaseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonOverlay, getSprites().gButton, activeButton, Constants.ACTION_HOME, GameValues.cFilterGreen);
            drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_back), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

            if (mGame.won) {
                drawText(canvas, getSprites().rMsgGameState, getString(R.string.message_gamewon), 0, 0);
            } else {
                drawText(canvas, getSprites().rMsgGameState, getString(R.string.message_gamelost), 0, 0);
                drawText(canvas, getSprites().rMsgScoreAward, MessageFormat.format(getString(R.string.message_bonus_coins), getSprites().pPlayer.score / GameValues.scoreDividerSinglePlayer), 0, 0, GameValues.cFilterYellow);
            }

            // draw score
            drawNumbers(canvas, 420, 10, getSprites().pPlayer.score, null, standardNumberWidth, standardNumberHeight);
        }


    }

    private void drawPlayerNames(Canvas canvas) {

        drawText(canvas, getSprites().rMsgPlayerName, getSprites().pPlayer.name, ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight), GameValues.cFilterYellow);

        drawText(canvas, getSprites().rMsgLives, MessageFormat.format(getString(R.string.message_lives), mGame.life), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight), GameValues.cFilterYellow);
    }

    private void drawPlayer(Canvas canvas, BrickPlayer player, ColorFilter filterBoard, ColorFilter filterBall, boolean self) {
        player.gBoard.image.setColorFilter(filterBoard);
        player.gBall.image.setColorFilter(filterBall);

        canvas.save();
        canvas.rotate(player.boardRotation,
                Float.valueOf(player.gBoard.image.getBounds().centerX()),
                Float.valueOf(player.gBoard.image.getBounds().centerY()));
        player.gBoard.image.draw(canvas);

        // restore canvas already before effects are applied
        if (!player.ballGlued) {
            canvas.restore();
        }
        player.gBall.image.setAlpha(255);
        if (player.ballEffect > -1) {
            switch (player.ballEffect) {
                case Constants.EXTENSION_NUMBER_FIREBALL:
                    Rect r = getSprites().gEffectFireball.image.copyBounds();
                    // offest to ball and correct for border
                    r.offsetTo(player.gBall.image.getBounds().left, player.gBall.image.getBounds().top);
                    r.offsetTo(r.left - ((r.width() - player.gBall.image.getBounds().width()) / 2), r.top - ((r.height() - player.gBall.image.getBounds().height()) / 2));
                    getSprites().gEffectFireball.image.setBounds(r);
                    canvas.save();
                    canvas.rotate(mExtTime * 90f, getSprites().gEffectFireball.image.getBounds().exactCenterX(), getSprites().gEffectFireball.image.getBounds().exactCenterY());
                    getSprites().gEffectFireball.image.draw(canvas);
                    canvas.restore();
                    break;
                case Constants.EXTENSION_NUMBER_INVISIBLEBALL:
                    if (self) {
                        // more visibility for own ball
                        player.gBall.image.setAlpha(75);
                    } else {
                        player.gBall.image.setAlpha(5);
                    }
                    break;
            }
        }

        // glue ball to board
        if (player.ballGlued) {
            player.gBall.image.draw(canvas);
            canvas.restore();
        } else {
            player.gBall.image.draw(canvas);
        }
    }

    public synchronized void setMode(GameState mode) {
        synchronized (mMode) {
            mMode = mode;
        }
    }

    public synchronized GameState getMode() {
        return mMode;
    }

    public synchronized void setSubMode(GameSubState mode) {
        mSubMode = mode;
    }

    public synchronized GameSubState getSubMode() {
        return mSubMode;
    }

    public void setBonus(boolean b) {
    }

    @Override
    public void actionHandler(int action) {
        // handle click actions directly to the game screen
        getScreen().actionHandler(action);
    }

    public GameViewSprites getSprites() {
        return GameViewSprites.class.cast(super.sprites);
    }

    private void drawLayer(Canvas canvas) {
//        canvas.drawRect(ScreenKit.scaleWidth(375, screenWidth), ScreenKit.scaleHeight(10, screenHeight), screenWidth - ScreenKit.scaleWidth(5, screenWidth), screenHeight - ScreenKit.scaleHeight(10, screenHeight), mLayer);
//        canvas.drawRect(ScreenKit.scaleWidth(375, screenWidth), ScreenKit.scaleHeight(10, screenHeight), screenWidth - ScreenKit.scaleWidth(5, screenWidth), screenHeight - ScreenKit.scaleHeight(10, screenHeight), mLayerBorder);
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    public HomeScreen getHomeScreen() {
        return (HomeScreen) getScreen();
    }

}

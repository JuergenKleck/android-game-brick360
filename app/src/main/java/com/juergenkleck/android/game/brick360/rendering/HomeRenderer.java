package com.juergenkleck.android.game.brick360.rendering;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.Random;

import com.juergenkleck.android.appengine.storage.dto.Configuration;
import com.juergenkleck.android.appengine.storage.dto.Extensions;
import com.juergenkleck.android.game.brick360.Constants;
import com.juergenkleck.android.game.brick360.Constants.RenderMode;
import com.juergenkleck.android.game.brick360.Constants.SubRenderMode;
import com.juergenkleck.android.game.brick360.R;
import com.juergenkleck.android.game.brick360.SystemHelper;
import com.juergenkleck.android.game.brick360.engine.GameValues;
import com.juergenkleck.android.game.brick360.sprites.HomeViewSprites;
import com.juergenkleck.android.game.brick360.storage.DBDriver;
import com.juergenkleck.android.game.brick360.storage.dto.CurrentGame;
import com.juergenkleck.android.gameengine.EngineConstants;
import com.juergenkleck.android.gameengine.rendering.kits.Renderkit;
import com.juergenkleck.android.gameengine.rendering.kits.ScreenKit;
import com.juergenkleck.android.gameengine.rendering.kits.ScreenKit.ScreenPosition;

/**
 * Android App - Brick360
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class HomeRenderer extends BrickRendererTemplate {

    private Random rnd;

    public RenderMode mRenderMode;
    private SubRenderMode mSubRenderMode;
    private int expansion;
    private int colorMode;
    private boolean hasCrosshair;

    private final int boardTB = 250;
    private final int ballTB = 250;
    private final float boardSize = 0.30f;
    private final float ballSize = 0.075f;

    private int changeR = 0;
    private int changeG = 0;
    private int changeB = 0;
    private long pressTime = 0L;

    private ColorFilter mFilterBoard;
    private NumberFormat mRGBFormat;
    private Paint mLayer;
    private Paint mLayerBorder;

    public HomeRenderer(Context context, Properties p) {
        super(context, p);
        mRenderMode = RenderMode.HOME;
        mSubRenderMode = SubRenderMode.COLORS;
        expansion = 0;
        colorMode = 0;
        mRGBFormat = new DecimalFormat("000");
    }

    private HomeViewSprites getSprites() {
        return (HomeViewSprites) super.sprites;
    }

    @Override
    public void doStart() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void unpause() {
    }

    private void updatePressTime() {
        if (pressTime <= 0L) {
            pressTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (delayedAction == EngineConstants.ACTION_NONE) {
            // determine button click

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_MOVE:
                    // move
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (Constants.RenderMode.OPTIONS == mRenderMode) {
                        if (Constants.SubRenderMode.COLORS == mSubRenderMode) {
                            if (containsClick(getSprites().rSlideColorRPlus, event.getX(), event.getY())) {
                                changeR = 1;
                                updatePressTime();
                            }
                            if (containsClick(getSprites().rSlideColorGPlus, event.getX(), event.getY())) {
                                changeG = 1;
                                updatePressTime();
                            }
                            if (containsClick(getSprites().rSlideColorBPlus, event.getX(), event.getY())) {
                                changeB = 1;
                                updatePressTime();
                            }
                            if (containsClick(getSprites().rSlideColorRMinus, event.getX(), event.getY())) {
                                changeR = -1;
                                updatePressTime();
                            }
                            if (containsClick(getSprites().rSlideColorGMinus, event.getX(), event.getY())) {
                                changeG = -1;
                                updatePressTime();
                            }
                            if (containsClick(getSprites().rSlideColorBMinus, event.getX(), event.getY())) {
                                changeB = -1;
                                updatePressTime();
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    if (Constants.RenderMode.HOME == mRenderMode) {
                        if (containsClick(getSprites().rBtnStart, event.getX(), event.getY())) {
                            delayedActionHandler(EngineConstants.ACTION_START, EngineConstants.ACTION_START);
                        }
                        if (containsClick(getSprites().rBtnOptions, event.getX(), event.getY())) {
                            delayedActionHandler(EngineConstants.ACTION_OPTIONS, EngineConstants.ACTION_OPTIONS);
                        }

                    } else if (Constants.RenderMode.OPTIONS == mRenderMode) {
                        if (Constants.SubRenderMode.COLORS == mSubRenderMode) {

                            if (containsClick(getSprites().rSlideColorRPlus, event.getX(), event.getY()) || containsClick(getSprites().rSlideColorRMinus, event.getX(), event.getY())) {
                                changeR = 0;
                                pressTime = 0L;
                                DBDriver.getInstance().store(SystemHelper.getInventory());
                            }
                            if (containsClick(getSprites().rSlideColorGPlus, event.getX(), event.getY()) || containsClick(getSprites().rSlideColorGMinus, event.getX(), event.getY())) {
                                changeG = 0;
                                pressTime = 0L;
                                DBDriver.getInstance().store(SystemHelper.getInventory());
                            }
                            if (containsClick(getSprites().rSlideColorBPlus, event.getX(), event.getY()) || containsClick(getSprites().rSlideColorBMinus, event.getX(), event.getY())) {
                                changeB = 0;
                                pressTime = 0L;
                                DBDriver.getInstance().store(SystemHelper.getInventory());
                            }

                            // switch between board (0) and ball (1)
                            if (containsClick(getSprites().rButtonColorLeft, event.getX(), event.getY())) {
                                colorMode -= 1;
                                if (colorMode < 0) {
                                    colorMode = 0;
                                }
                                mFilterBoard = new LightingColorFilter(Color.rgb(SystemHelper.getInventory().boardColorR, SystemHelper.getInventory().boardColorG, SystemHelper.getInventory().boardColorB), 1);
                            }
                            if (containsClick(getSprites().rButtonColorRight, event.getX(), event.getY())) {
                                colorMode += 1;
                                if (colorMode > 1) {
                                    colorMode = 1;
                                }
                                mFilterBoard = new LightingColorFilter(Color.rgb(SystemHelper.getInventory().ballColorR, SystemHelper.getInventory().ballColorG, SystemHelper.getInventory().ballColorB), 1);
                            }

                        } else if (Constants.SubRenderMode.MISC == mSubRenderMode) {
                            // invert direction
                            if (containsClick(getSprites().rMsgInvertDirection, event.getX(), event.getY())) {
                                SystemHelper.getInventory().invertDirection = !SystemHelper.getInventory().invertDirection;
                                DBDriver.getInstance().store(SystemHelper.getInventory());
                            }
                            if (containsClick(getSprites().rMsgToggleCrosshair, event.getX(), event.getY())) {
                                if (hasCrosshair) {
                                    Configuration c = SystemHelper.getConfiguration(Constants.CONFIG_SHOW_CROSSHAIR, Constants.DEFAULT_CONFIG_SHOW_CROSSHAIR);
                                    c.value = Boolean.toString(!Boolean.parseBoolean(c.value));
                                    DBDriver.getInstance().store(c);
                                    SystemHelper.setConfiguration(c);
                                } else {
                                    Toast.makeText(mContext, R.string.achievement_not_met, Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (containsClick(getSprites().rMsgResetData, event.getX(), event.getY())) {
                                CurrentGame c = SystemHelper.getCurrentGame();
                                c.bricks = null;
                                c.currentRound = 0;
                                c.level = 0;
                                c.life = 0f;
                                c.points = 0;
                                DBDriver.getInstance().store(c);
                                Toast.makeText(mContext, R.string.reset_complete, Toast.LENGTH_SHORT).show();
                            }
                        } else if (Constants.SubRenderMode.UPGRADE == mSubRenderMode) {
                            if (containsClick(getSprites().rButtonExpLeft, event.getX(), event.getY())) {
                                expansion -= 1;
                                if (expansion < 0) {
                                    expansion = 0;
                                }
                            }
                            if (containsClick(getSprites().rButtonExpRight, event.getX(), event.getY())) {
                                expansion += 1;
                                if (expansion >= GameValues.extensionName.length) {
                                    expansion = GameValues.extensionName.length - 1;
                                }
                            }

                            if (containsClick(getSprites().rButtonExpansionBuy, event.getX(), event.getY())) {
                                Extensions ext = SystemHelper.getExtensions(GameValues.extensionName[expansion]);
                                if (SystemHelper.getInventory().coins >= GameValues.PRICES_PER_EXTENSION[expansion] && ext.amount + 1 < GameValues.MAX_PER_EXTENSION[expansion]) {
                                    ext.amount += 1;
                                    DBDriver.getInstance().store(ext);
                                    SystemHelper.setExtensions(ext);
                                    SystemHelper.getInventory().coins -= GameValues.PRICES_PER_EXTENSION[expansion];
                                    DBDriver.getInstance().store(SystemHelper.getInventory());
                                    if (Constants.EXTENSION_NUMBER_CROSSHAIR == expansion) {
                                        hasCrosshair = true;
                                    }
                                }
                            }

                        }

                        if (containsClick(getSprites().rButtonColors, event.getX(), event.getY())) {
                            mSubRenderMode = SubRenderMode.COLORS;
                        }
                        if (containsClick(getSprites().rButtonUpgrade, event.getX(), event.getY())) {
                            mSubRenderMode = SubRenderMode.UPGRADE;
                        }
                        if (containsClick(getSprites().rButtonMisc, event.getX(), event.getY())) {
                            mSubRenderMode = SubRenderMode.MISC;
                        }
                        // back
                        if (containsClick(getSprites().rBtnBack, event.getX(), event.getY())) {
                            delayedActionHandler(Constants.ACTION_HOME, Constants.ACTION_HOME);
                        }
                    }

                    break;
            }

        }

        return true;
    }

    @Override
    public void doUpdateRenderState() {

        if (Constants.RenderMode.OPTIONS == mRenderMode) {
            if (Constants.SubRenderMode.COLORS == mSubRenderMode) {
                if (changeR != 0 || changeG != 0 || changeB != 0) {

                    final long time = System.currentTimeMillis();

                    int mod = 1;
                    if (pressTime + Constants.buttonPressTime < time) {
                        mod = 10;
                    }
                    if (colorMode == 0) {
                        SystemHelper.getInventory().boardColorR += changeR * mod;
                        if (SystemHelper.getInventory().boardColorR > 255) {
                            SystemHelper.getInventory().boardColorR = 255;
                        }
                        if (SystemHelper.getInventory().boardColorR < 0) {
                            SystemHelper.getInventory().boardColorR = 0;
                        }
                        SystemHelper.getInventory().boardColorG += changeG * mod;
                        if (SystemHelper.getInventory().boardColorG > 255) {
                            SystemHelper.getInventory().boardColorG = 255;
                        }
                        if (SystemHelper.getInventory().boardColorG < 0) {
                            SystemHelper.getInventory().boardColorG = 0;
                        }
                        SystemHelper.getInventory().boardColorB += changeB * mod;
                        if (SystemHelper.getInventory().boardColorB > 255) {
                            SystemHelper.getInventory().boardColorB = 255;
                        }
                        if (SystemHelper.getInventory().boardColorB < 0) {
                            SystemHelper.getInventory().boardColorB = 0;
                        }
                        mFilterBoard = new LightingColorFilter(Color.rgb(SystemHelper.getInventory().boardColorR, SystemHelper.getInventory().boardColorG, SystemHelper.getInventory().boardColorB), 1);
                    } else {
                        SystemHelper.getInventory().ballColorR += changeR * mod;
                        if (SystemHelper.getInventory().ballColorR > 255) {
                            SystemHelper.getInventory().ballColorR = 255;
                        }
                        if (SystemHelper.getInventory().ballColorR < 0) {
                            SystemHelper.getInventory().ballColorR = 0;
                        }
                        SystemHelper.getInventory().ballColorG += changeG * mod;
                        if (SystemHelper.getInventory().ballColorG > 255) {
                            SystemHelper.getInventory().ballColorG = 255;
                        }
                        if (SystemHelper.getInventory().ballColorG < 0) {
                            SystemHelper.getInventory().ballColorG = 0;
                        }
                        SystemHelper.getInventory().ballColorB += changeB * mod;
                        if (SystemHelper.getInventory().ballColorB > 255) {
                            SystemHelper.getInventory().ballColorB = 255;
                        }
                        if (SystemHelper.getInventory().ballColorB < 0) {
                            SystemHelper.getInventory().ballColorB = 0;
                        }
                        mFilterBoard = new LightingColorFilter(Color.rgb(SystemHelper.getInventory().ballColorR, SystemHelper.getInventory().ballColorG, SystemHelper.getInventory().ballColorB), 1);
                    }
                }
            }
        } else if (Constants.RenderMode.GAME == mRenderMode) {
            mRenderMode = Constants.RenderMode.HOME;
        }
    }

    @Override
    public void doDrawRenderer(Canvas canvas) {

        if (getSprites().gBackground != null) {
            // draw image across screen
            int h = 0;
            int v = 0;
            while (h < screenWidth && v < screenHeight) {
                getSprites().gBackground.image.draw(canvas);
                Rect r = getSprites().gBackground.image.copyBounds();
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
            }
        }

        if (Constants.RenderMode.HOME == mRenderMode) {
            getSprites().gLogo.image.draw(canvas);

            // draw buttons last to overlay the background items
            choiceBaseDraw(canvas, getSprites().rBtnStart, getSprites().gButtonOverlay, getSprites().gButton, activeButton, EngineConstants.ACTION_START, GameValues.cFilterGreen);
            choiceBaseDraw(canvas, getSprites().rBtnOptions, getSprites().gButtonOverlay, getSprites().gButton, activeButton, EngineConstants.ACTION_OPTIONS, GameValues.cFilterGreen);

            drawText(canvas, getSprites().rBtnStart, getString(R.string.menubutton_start), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
            drawText(canvas, getSprites().rBtnOptions, getString(R.string.menubutton_options), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

            if( getSprites().gAchievement != null) {
                drawText(canvas, getSprites().rAchievement, getString(R.string.rank), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                getSprites().gAchievement.image.draw(canvas);
            }
            if( getSprites().gAchievement2 != null) {
                getSprites().gAchievement2.image.draw(canvas);
            }

        } else if (Constants.RenderMode.OPTIONS == mRenderMode) {
            drawLayer(canvas);

            if (Constants.SubRenderMode.COLORS == mSubRenderMode) {

                if (colorMode > 0) {
                    getSprites().gButtonLeft.image.setBounds(getSprites().rButtonColorLeft);
                    getSprites().gButtonLeft.image.draw(canvas);
                } else {
                    getSprites().gButtonLeft.image.setBounds(getSprites().rButtonColorRight);
                    canvas.save();
                    canvas.rotate(180f, getSprites().gButtonLeft.image.getBounds().exactCenterX(), getSprites().gButtonLeft.image.getBounds().exactCenterY());
                    getSprites().gButtonLeft.image.draw(canvas);
                    canvas.restore();
                }

                drawText(canvas, getSprites().rSlideColorRPlus, "+", ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawText(canvas, getSprites().rSlideColorRMinus, "-", ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawText(canvas, getSprites().rSlideColorRValue, mRGBFormat.format(colorMode == 0 ? SystemHelper.getInventory().boardColorR : SystemHelper.getInventory().ballColorR), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawText(canvas, getSprites().rSlideColorGPlus, "+", ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawText(canvas, getSprites().rSlideColorGMinus, "-", ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawText(canvas, getSprites().rSlideColorGValue, mRGBFormat.format(colorMode == 0 ? SystemHelper.getInventory().boardColorG : SystemHelper.getInventory().ballColorG), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawText(canvas, getSprites().rSlideColorBPlus, "+", ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawText(canvas, getSprites().rSlideColorBMinus, "-", ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawText(canvas, getSprites().rSlideColorBValue, mRGBFormat.format(colorMode == 0 ? SystemHelper.getInventory().boardColorB : SystemHelper.getInventory().ballColorB), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

                drawText(canvas, getSprites().rSlideColorNameR, getString(R.string.message_red), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight), GameValues.cFilterRed);
                drawText(canvas, getSprites().rSlideColorNameG, getString(R.string.message_green), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight), GameValues.cFilterGreen);
                drawText(canvas, getSprites().rSlideColorNameB, getString(R.string.message_blue), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

                if (colorMode == 0) {
                    getSprites().gBoard.image.setColorFilter(mFilterBoard);
                    getSprites().gBoard.image.draw(canvas);
                    drawText(canvas, getSprites().rMsgBoardColor, getString(R.string.message_boardcolor), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                } else {
                    getSprites().gBall.image.setColorFilter(mFilterBoard);
                    getSprites().gBall.image.draw(canvas);
                    drawText(canvas, getSprites().rMsgBallColor, getString(R.string.message_ballcolor), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                }
            } else if (Constants.SubRenderMode.UPGRADE == mSubRenderMode) {

                if (expansion > 0) {
                    getSprites().gButtonLeft.image.setBounds(getSprites().rButtonExpLeft);
                    getSprites().gButtonLeft.image.draw(canvas);
                }
                if (expansion + 1 < GameValues.extensionName.length) {
                    getSprites().gButtonLeft.image.setBounds(getSprites().rButtonExpRight);
                    canvas.save();
                    canvas.rotate(180f, getSprites().gButtonLeft.image.getBounds().exactCenterX(), getSprites().gButtonLeft.image.getBounds().exactCenterY());
                    getSprites().gButtonLeft.image.draw(canvas);
                    canvas.restore();
                }

                drawTextUnbounded(canvas, getSprites().rButtonExpansion, getString(GameValues.extensionTitle[expansion]), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

                Extensions ext = SystemHelper.getExtensions(GameValues.extensionName[expansion]);
                if (ext.amount + 1 >= GameValues.MAX_PER_EXTENSION[expansion]) {
                    getSprites().gButtonOverlay.image.setBounds(getSprites().rButtonExpansionBuy);
                    getSprites().gButtonOverlay.image.setColorFilter(GameValues.cFilterRed);
                }
                choiceDraw(canvas, getSprites().rButtonExpansionBuy, getSprites().gButtonOverlay, getSprites().gButton, false, true);
                if (ext.amount + 1 >= GameValues.MAX_PER_EXTENSION[expansion]) {
                    getSprites().gButtonOverlay.image.draw(canvas);
                    getSprites().gButtonOverlay.image.clearColorFilter();
                }

                if (expansion == Constants.EXTENSION_NUMBER_BIGBALL) {
                    multiDraw(canvas, getSprites().gExtBigBall, getSprites().gExtBigBall.image.copyBounds(), getSprites().gButton);
                } else if (expansion == Constants.EXTENSION_NUMBER_COUNTER_MEASURE) {
                    multiDraw(canvas, getSprites().gExtCounterMeasure, getSprites().gExtCounterMeasure.image.copyBounds(), getSprites().gButton);
                } else if (expansion == Constants.EXTENSION_NUMBER_FIREBALL) {
                    multiDraw(canvas, getSprites().gExtFireball, getSprites().gExtFireball.image.copyBounds(), getSprites().gButton);
                } else if (expansion == Constants.EXTENSION_NUMBER_GLUEBALL) {
                    multiDraw(canvas, getSprites().gExtGlueBall, getSprites().gExtGlueBall.image.copyBounds(), getSprites().gButton);
                } else if (expansion == Constants.EXTENSION_NUMBER_INVISIBLEBALL) {
                    multiDraw(canvas, getSprites().gExtInvisibleBall, getSprites().gExtInvisibleBall.image.copyBounds(), getSprites().gButton);
                } else if (expansion == Constants.EXTENSION_NUMBER_WALL) {
                    multiDraw(canvas, getSprites().gExtWall, getSprites().gExtWall.image.copyBounds(), getSprites().gButton);
                }

                drawText(canvas, getSprites().rButtonExpansionBuy, getString(R.string.menubutton_expansion_buy), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawTextUnbounded(canvas, getSprites().rMsgExpansionExplanation, getString(GameValues.extensionExplanation[expansion]), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawTextUnbounded(canvas, getSprites().rMsgExpansionCosts, MessageFormat.format(getString(R.string.message_costs), GameValues.PRICES_PER_EXTENSION[expansion]), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                drawTextUnbounded(canvas, getSprites().rMsgCoinsAvailable, MessageFormat.format(getString(R.string.message_coins), SystemHelper.getInventory().coins), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

            } else if (Constants.SubRenderMode.MISC == mSubRenderMode) {
                choiceBaseDraw(canvas, getSprites().rMsgInvertDirection, getSprites().gButtonOverlay, getSprites().gButton, SystemHelper.getInventory().invertDirection, true, GameValues.cFilterGreen);
                drawText(canvas, getSprites().rMsgInvertDirection, getString(R.string.message_invertdirection), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                if (hasCrosshair) {
                    Configuration c = SystemHelper.getConfiguration(Constants.CONFIG_SHOW_CROSSHAIR, Constants.DEFAULT_CONFIG_SHOW_CROSSHAIR);
                    choiceBaseDraw(canvas, getSprites().rMsgToggleCrosshair, getSprites().gButtonOverlay, getSprites().gButton, Boolean.valueOf(c.value), true, GameValues.cFilterGreen);
                    drawText(canvas, getSprites().rMsgToggleCrosshair, getString(R.string.message_showcrosshair), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                } else {
                    choiceBaseDraw(canvas, getSprites().rMsgToggleCrosshair, getSprites().gButtonOverlay, getSprites().gButton, true, true, GameValues.cFilterRed);
                    drawText(canvas, getSprites().rMsgToggleCrosshair, getString(R.string.message_showcrosshair), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
                }

                getSprites().gButton.image.setBounds(new Rect(getSprites().rMsgResetData));
                getSprites().gButton.image.draw(canvas);
                drawText(canvas, getSprites().rMsgResetData, getString(R.string.message_resetdata), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
            }

            choiceBaseDraw(canvas, getSprites().rButtonColors, getSprites().gButtonOverlay, getSprites().gButton, mSubRenderMode, SubRenderMode.COLORS, GameValues.cFilterGreen);
            choiceBaseDraw(canvas, getSprites().rButtonUpgrade, getSprites().gButtonOverlay, getSprites().gButton, mSubRenderMode, SubRenderMode.UPGRADE, GameValues.cFilterGreen);
            choiceBaseDraw(canvas, getSprites().rButtonMisc, getSprites().gButtonOverlay, getSprites().gButton, mSubRenderMode, SubRenderMode.MISC, GameValues.cFilterGreen);
            drawText(canvas, getSprites().rButtonColors, getString(R.string.menubutton_option_color), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
            drawText(canvas, getSprites().rButtonUpgrade, getString(R.string.menubutton_option_upgrade), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
            drawText(canvas, getSprites().rButtonMisc, getString(R.string.menubutton_option_misc), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));

            choiceBaseDraw(canvas, getSprites().rBtnBack, getSprites().gButtonOverlay, getSprites().gButton, activeButton, Constants.ACTION_HOME, GameValues.cFilterGreen);
            drawText(canvas, getSprites().rBtnBack, getString(R.string.menubutton_back), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
        } else if (Constants.RenderMode.WAIT == mRenderMode) {
            drawText(canvas, getSprites().rMsgWait, getString(R.string.message_loading), ScreenKit.scaleWidth(Constants.spaceLR, screenWidth), ScreenKit.scaleHeight(Constants.spaceTB, screenHeight));
        }

    }


    @Override
    public void restoreGameState() {

    }

    @Override
    public void saveGameState() {
    }

    @Override
    public void doInitThread(long time) {
        super.sprites = new HomeViewSprites();

        rnd = new Random();

        getSprites().gBackground = Renderkit.loadGraphic(mContext.getResources(),
                GameValues.systemBackground, 0, 0);
//				GameValues.backgrounds[rnd.nextInt(GameValues.backgrounds.length)], 0, 0);

        getSprites().gLogo = loadGraphic(R.drawable.ic_icon, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.45f, 10, 10, getSprites().gLogo);

        // button backgrounds
        getSprites().gButton = Renderkit.loadButtonGraphic(mContext.getResources(), R.drawable.bg_board_2, 0, 0, EngineConstants.ACTION_NONE);
        getSprites().gButtonOverlay = loadGraphic(R.drawable.button_flat_white, 0, 0);

        // navigation and text buttons
        getSprites().rBtnBack = getSprites().gButton.image.copyBounds();
        getSprites().rBtnStart = getSprites().gButton.image.copyBounds();
        getSprites().rBtnOptions = getSprites().gButton.image.copyBounds();

        getSprites().rMsgWait = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.35f, 50, 100, getSprites().rBtnStart);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.35f, 50, 450, getSprites().rBtnOptions);

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.20f, 50, 25, getSprites().rBtnBack);

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.CENTER, 0.80f, 0, 0, getSprites().rMsgWait);

        // Option menu graphics
        getSprites().rButtonColors = getSprites().gButton.image.copyBounds();
        getSprites().rButtonUpgrade = getSprites().gButton.image.copyBounds();
        getSprites().rButtonMisc = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.25f, 30, 375, getSprites().rButtonColors);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.25f, 30, 575, getSprites().rButtonUpgrade);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.25f, 30, 775, getSprites().rButtonMisc);

        getSprites().gButtonLeft = loadGraphic(R.drawable.leftbutton, 0, 0);
        getSprites().rButtonColorLeft = getSprites().gButtonLeft.image.copyBounds();
        getSprites().rButtonColorRight = getSprites().gButtonLeft.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.08f, 180, ballTB - 100, getSprites().rButtonColorLeft);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.08f, 10, ballTB - 100, getSprites().rButtonColorRight);

        // Option colors
        getSprites().gBoard = loadGraphic(R.drawable.board, 0, 0);
        getSprites().gBall = loadGraphic(R.drawable.ball, 0, 0);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, boardSize, 275, boardTB, getSprites().gBoard);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, ballSize, 325, ballTB, getSprites().gBall);

        getSprites().rMsgBoardColor = getSprites().gButton.image.copyBounds();
        getSprites().rMsgBallColor = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.40f, 255, boardTB - 175, getSprites().rMsgBoardColor);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.40f, 255, ballTB - 175, getSprites().rMsgBallColor);

        getSprites().rSlideColorRPlus = getSprites().gButtonLeft.image.copyBounds();
        getSprites().rSlideColorRMinus = getSprites().gButtonLeft.image.copyBounds();
        getSprites().rSlideColorGPlus = getSprites().gButtonLeft.image.copyBounds();
        getSprites().rSlideColorGMinus = getSprites().gButtonLeft.image.copyBounds();
        getSprites().rSlideColorBPlus = getSprites().gButtonLeft.image.copyBounds();
        getSprites().rSlideColorBMinus = getSprites().gButtonLeft.image.copyBounds();
        getSprites().rSlideColorRValue = getSprites().gButton.image.copyBounds();
        getSprites().rSlideColorGValue = getSprites().gButton.image.copyBounds();
        getSprites().rSlideColorBValue = getSprites().gButton.image.copyBounds();
        getSprites().rSlideColorNameR = getSprites().gButton.image.copyBounds();
        getSprites().rSlideColorNameG = getSprites().gButton.image.copyBounds();
        getSprites().rSlideColorNameB = getSprites().gButton.image.copyBounds();

        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.10f, 315, 450, getSprites().rSlideColorRPlus);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.10f, 415, 450, getSprites().rSlideColorRMinus);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.15f, 355, 500, getSprites().rSlideColorRValue);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.20f, 215, 500, getSprites().rSlideColorNameR);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.10f, 315, 600, getSprites().rSlideColorGPlus);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.10f, 415, 600, getSprites().rSlideColorGMinus);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.15f, 355, 650, getSprites().rSlideColorGValue);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.20f, 215, 650, getSprites().rSlideColorNameG);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.10f, 315, 750, getSprites().rSlideColorBPlus);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.10f, 415, 750, getSprites().rSlideColorBMinus);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.15f, 355, 800, getSprites().rSlideColorBValue);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.20f, 215, 800, getSprites().rSlideColorNameB);

        // Option misc
        getSprites().rMsgInvertDirection = getSprites().gButton.image.copyBounds();
        getSprites().rMsgToggleCrosshair = getSprites().gButton.image.copyBounds();
        getSprites().rMsgResetData = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.50f, 200, 50, getSprites().rMsgInvertDirection);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.50f, 200, 325, getSprites().rMsgToggleCrosshair);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.50f, 200, 600, getSprites().rMsgResetData);

        // option upgrade
        getSprites().rButtonExpansion = getSprites().gButton.image.copyBounds();
        getSprites().rButtonExpansionBuy = getSprites().gButton.image.copyBounds();
        getSprites().rMsgExpansionExplanation = getSprites().gButton.image.copyBounds();
        getSprites().rMsgExpansionCosts = getSprites().gButton.image.copyBounds();
        getSprites().rMsgCoinsAvailable = getSprites().gButton.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.20f, 220, 50, getSprites().rButtonExpansion);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.25f, 50, 750, getSprites().rButtonExpansionBuy);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.30f, 180, 250, getSprites().rMsgExpansionExplanation);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.13f, 180, 500, getSprites().rMsgExpansionCosts);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.13f, 180, 410, getSprites().rMsgCoinsAvailable);
        getSprites().rButtonExpLeft = getSprites().gButtonLeft.image.copyBounds();
        getSprites().rButtonExpRight = getSprites().gButtonLeft.image.copyBounds();
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_LEFT, 0.08f, 190, 50, getSprites().rButtonExpLeft);
        ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.TOP_RIGHT, 0.08f, 10, 50, getSprites().rButtonExpRight);

        getSprites().gExtBigBall = loadGraphic(R.drawable.ext_bigball, 0, 0);
        getSprites().gExtCounterMeasure = loadGraphic(R.drawable.ext_countermeasure, 0, 0);
        getSprites().gExtFireball = loadGraphic(R.drawable.ext_fireball, 0, 0);
        getSprites().gExtGlueBall = loadGraphic(R.drawable.ext_glueball, 0, 0);
        getSprites().gExtInvisibleBall = loadGraphic(R.drawable.ext_invisibleball, 0, 0);
        getSprites().gExtWall = loadGraphic(R.drawable.ext_wall, 0, 0);

        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.07f, 220, 50, getSprites().gExtBigBall);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.07f, 220, 50, getSprites().gExtCounterMeasure);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.07f, 220, 50, getSprites().gExtFireball);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.07f, 220, 50, getSprites().gExtGlueBall);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.07f, 220, 50, getSprites().gExtInvisibleBall);
        ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_RIGHT, 0.07f, 220, 50, getSprites().gExtWall);

        // flatten images to appropriate display size
        getSprites().rMsgBoardColor.bottom -= getSprites().rMsgBoardColor.height() / 2;
        getSprites().rMsgBallColor.bottom -= getSprites().rMsgBallColor.height() / 2;
        getSprites().rMsgInvertDirection.bottom -= getSprites().rMsgInvertDirection.height() / 2;
        getSprites().rMsgToggleCrosshair.bottom -= getSprites().rMsgToggleCrosshair.height() / 2;
        getSprites().rMsgResetData.bottom -= getSprites().rMsgResetData.height() / 2;
        getSprites().rMsgExpansionExplanation.bottom -= getSprites().rMsgExpansionExplanation.height() / 2;

        long gamesWon = SystemHelper.getInventory().gamesWon;
        long gamesLost = SystemHelper.getInventory().gamesLost;

        int achievementId = 0;
        for (int[] reqs : Constants.ACHIEVEMENT_REQUIREMENTS) {
            if (reqs[1] < gamesWon + gamesLost) {
                achievementId = reqs[0];
            }
        }
        if (achievementId > 0) {
            getSprites().gAchievement = loadGraphic(achievementId, 0, 0);
            ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.12f, 175, 50, getSprites().gAchievement);

            getSprites().rAchievement = getSprites().gButton.image.copyBounds();
            ScreenKit.scaleRect(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.25f, 50, 65, getSprites().rAchievement);

            /*
            TODO design secondary achievement
            if (gamesWon + gamesLost >= Constants.ACHIEVEMENT_MIN_GAMES_PITY_STAR) {
                if (gamesWon * 100 / gamesWon + gamesLost >= Constants.ACHIEVEMENT_RATE_LUCKY_STAR) {
                    getSprites().gAchievement2 = loadGraphic(R.drawable.badge_2b, 0, 0);
                    ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.12f, 220, 50, getSprites().gAchievement2);
                } else if (gamesLost * 100 / gamesWon + gamesLost >= Constants.ACHIEVEMENT_RATE_WHAT_A_PITY) {
                    getSprites().gAchievement2 = loadGraphic(R.drawable.badge_3b, 0, 0);
                    ScreenKit.scaleImage(screenWidth, screenHeight, ScreenPosition.BOTTOM_LEFT, 0.12f, 220, 50, getSprites().gAchievement2);
                }
            }
             */
        }

        mFilterBoard = new LightingColorFilter(Color.rgb(SystemHelper.getInventory().boardColorR, SystemHelper.getInventory().boardColorG, SystemHelper.getInventory().boardColorB), 1);

        mLayer = new Paint();
        mLayer.setColor(Color.WHITE);
        mLayer.setAlpha(75);
        mLayer.setStyle(Style.FILL_AND_STROKE);
        mLayerBorder = new Paint();
        mLayerBorder.setColor(Color.BLACK);
        mLayerBorder.setAlpha(100);
        mLayerBorder.setStyle(Style.STROKE);

        hasCrosshair = gamesLost + gamesWon > Constants.ACHIEVEMENT_CROSSHAIR;
    }

    public synchronized void updateRenderMode(RenderMode renderMode) {
        mRenderMode = renderMode;
    }

    private void drawLayer(Canvas canvas) {
        canvas.drawRect(ScreenKit.scaleWidth(175, screenWidth), ScreenKit.scaleHeight(10, screenHeight), screenWidth - ScreenKit.scaleWidth(5, screenWidth), screenHeight - ScreenKit.scaleHeight(10, screenHeight), mLayer);
        canvas.drawRect(ScreenKit.scaleWidth(175, screenWidth), ScreenKit.scaleHeight(10, screenHeight), screenWidth - ScreenKit.scaleWidth(5, screenWidth), screenHeight - ScreenKit.scaleHeight(10, screenHeight), mLayerBorder);
    }

    @Override
    public void reset() {
    }

}

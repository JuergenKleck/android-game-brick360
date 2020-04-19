package info.simplyapps.game.brick360.sprites;

import android.graphics.Rect;

import info.simplyapps.gameengine.rendering.objects.Graphic;
import info.simplyapps.gameengine.sprites.ViewSprites;

public class HomeViewSprites implements ViewSprites {

    // main
    public Graphic gBackground;
    public Graphic gLogo;

    // generic buttons
    public Graphic gButton;
    public Graphic gButtonOverlay;

    // options
    public Graphic gBoard;
    public Graphic gBall;

    public Graphic gButtonLeft;
    public Rect rButtonColorLeft;
    public Rect rButtonColorRight;

    public Rect rMsgBoardColor;
    public Rect rMsgBallColor;

    public Rect rSlideColorRValue;
    public Rect rSlideColorRPlus;
    public Rect rSlideColorRMinus;
    public Rect rSlideColorGValue;
    public Rect rSlideColorGPlus;
    public Rect rSlideColorGMinus;
    public Rect rSlideColorBValue;
    public Rect rSlideColorBPlus;
    public Rect rSlideColorBMinus;
    public Rect rSlideColorNameR;
    public Rect rSlideColorNameG;
    public Rect rSlideColorNameB;

    public Rect rMsgInvertDirection;
    public Rect rMsgToggleCrosshair;
    public Rect rMsgResetData;

    // option menu system
    public Rect rButtonColors;
    public Rect rButtonUpgrade;
    public Rect rButtonMisc;

    // option menu upgrade
    public Rect rButtonExpLeft;
    public Rect rButtonExpRight;
    public Rect rButtonExpansion;

    public Rect rButtonExpansionBuy;
    public Rect rMsgExpansionExplanation;
    public Rect rMsgExpansionCosts;
    public Rect rMsgCoinsAvailable;

    public Graphic gExtBigBall;
    public Graphic gExtFireball;
    public Graphic gExtInvisibleBall;
    public Graphic gExtCounterMeasure;
    public Graphic gExtWall;
    public Graphic gExtGlueBall;

    public Rect rAchievement;
    public Graphic gAchievement;
    public Graphic gAchievement2;

    // main menu system
    public Rect rBtnStart;
    public Rect rBtnOptions;
    public Rect rBtnBack;
    public Rect rMsgWait;

    @Override
    public void init() {
    }

    @Override
    public void clean() {
    }

}

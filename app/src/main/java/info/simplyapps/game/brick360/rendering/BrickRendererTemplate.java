package info.simplyapps.game.brick360.rendering;

import android.content.Context;

import java.util.Properties;

import info.simplyapps.game.brick360.Constants;
import info.simplyapps.gameengine.rendering.GenericRendererTemplate;

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

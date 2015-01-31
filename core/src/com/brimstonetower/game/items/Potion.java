package com.brimstonetower.game.items;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.helpers.ColorHelper;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.mapgeneration.RandomGen;


public class Potion extends Item
{
    private String _stringColor;
    private Color _color;

    private Effect _effect;
    public Effect getEffect(){return _effect;}

    public Color getColor()
    {
        return _color;
    }
    public String getStringColor()
    {
        return _stringColor;
    }

    public Potion(Effect effect,boolean isIdentified,TextureRegion textureRegion,Color color)
    {
        super("Potion of "+effect.getName(), effect.getEffectDescription(), isIdentified, textureRegion, true, false);
        _effect=effect;
        _color=color;
        _stringColor = ColorHelper.convertColorToString(color);
    }


    public Potion(Potion toCopy)
    {
        this(toCopy.getEffect(),toCopy.isIdentified(), toCopy.getTextureRegion(),toCopy.getColor());
    }

    public String getName()
    {
        if (_isIdentified)
        {
            return super.getName();
        }
        else
        {
            return _stringColor + " potion";
        }
    }

    public String getDescription()
    {
        if (_isIdentified)
        {
            return super.getDescription();
        }
        else
        {
            return "The effect of this potion is not known";
        }
    }

    @Override
    public void draw(SpriteBatch batch, float x, float y)
    {
        super.draw(batch, x, y);
    }

    public void draw(SpriteBatch batch, float x, float y, float scale)
    {
        super.draw(batch, x, y, scale);
    }
}

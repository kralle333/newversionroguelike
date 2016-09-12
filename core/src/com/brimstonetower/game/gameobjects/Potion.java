package com.brimstonetower.game.gameobjects;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.helpers.ColorHelper;
import com.brimstonetower.game.helpers.Effect;


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

    public Potion(String description,String identifiedName,Effect effect,boolean isIdentified,TextureRegion textureRegion,Color color,int typeId)
    {
        super(identifiedName, description, isIdentified, textureRegion,false,typeId);
        _effect=effect;
        _color=color;
        _stringColor = ColorHelper.convertColorToString(color);
    }


    public Potion(Potion toCopy)
    {
        this(toCopy.getDescription(),toCopy.getName(), toCopy.getEffect(),toCopy.isIdentified(), toCopy.getTextureRegion(),toCopy.getColor(),toCopy.getTypeId());
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

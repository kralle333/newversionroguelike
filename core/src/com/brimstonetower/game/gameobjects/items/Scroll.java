package com.brimstonetower.game.gameobjects.items;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Scroll extends Item
{

    private String _unidentifiedName;

    public String getUnidentifiedName()
    {
        return _unidentifiedName;
    }

    private ScrollType _scrollType;

    public ScrollType getType()
    {
        return _isIdentified ? _scrollType : ScrollType.Unidentified;
    }

    public enum ScrollType
    {
        Unidentified, Identify, Mapping, Teleport, RemoveCurse
    }

    public Scroll(String name, String description, boolean isIdentified, TextureRegion textureRegion, String unIdentifiedName,int id)
    {
        super(name, description, isIdentified, textureRegion, true,id);
        _unidentifiedName = unIdentifiedName;
        _scrollType = getScrollType(name);
    }

    public Scroll(Scroll toCopy)
    {
        this(toCopy._name, toCopy.getIdentifiedDescription(), toCopy.isIdentified(), toCopy.getTextureRegion(), toCopy.getUnidentifiedName(),toCopy.getTypeId());
    }
    public String getName()
    {
        if (_isIdentified)
        {
            return super.getName();
        }
        else
        {
            return _unidentifiedName + " scroll";
        }
    }
    private ScrollType getScrollType(String name)
    {
        if (name.equals("Scroll of Identify"))
        {
            return ScrollType.Identify;
        }
        else if (name.equals("Scroll of Teleport"))
        {
            return ScrollType.Teleport;
        }
        else if (name.equals("Scroll of Mapping"))
        {
            return ScrollType.Mapping;
        }
        else if (name.equals("Scroll of Remove Curse"))
        {
            return ScrollType.RemoveCurse;
        }
        else
        {
            throw new IllegalArgumentException(name + " does not name a scroll");
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
            return "A scroll with the description " + _unidentifiedName;
        }
    }

    @Override
    public void draw(SpriteBatch batch, float x, float y)
    {
        super.draw(batch, x, y);
    }
}

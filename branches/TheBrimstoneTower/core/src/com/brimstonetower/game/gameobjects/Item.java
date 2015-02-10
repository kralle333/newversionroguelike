package com.brimstonetower.game.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item
{
    protected String _name;

    public String getName()
    {
        return _name;
    }

    private String _description;

    public String getDescription()
    {
        return _description;
    }

    protected boolean _isIdentified = false;
    public boolean isIdentified()
    {
        return _isIdentified;
    }

    protected TextureRegion _textureRegion;
    public TextureRegion getTextureRegion()
    {
        return _textureRegion;
    }

    private boolean _hasCurse;
    public boolean hasCurse()
    {
        return _hasCurse;
    }
    public void removeCurse()
    {
        _hasCurse = false;
    }
    protected boolean _isCurseKnown = false;
    public void showCurse()
    {
        _isCurseKnown = true;
    }
    public void curse(){_isCurseKnown=false;_hasCurse = true;}

    private boolean _stackable = false;
    public boolean isStackable()
    {
        return _stackable;
    }

    private int _typeId;
    public int getTypeId()
    {
        return _typeId;
    }

    private static int itemIdCounter = 1;
    private int _uniqueId;
    public int getUniqueId(){return _uniqueId;}

    public Item(String name, String description, boolean isIdentified, TextureRegion textureRegion, boolean stackable, int typeId)
    {
        _name = name;
        _description = description;
        _isIdentified = isIdentified;
        _textureRegion = textureRegion;
        _stackable = stackable;
        _typeId = typeId;
        _uniqueId = itemIdCounter++;
    }


    public void identify()
    {
        _isIdentified = true;
        showCurse();
    }


    public String getIdentifiedDescription()
    {
        return _description;
    }

    public boolean isIdentical(Item item)
    {
        if (_typeId == item.getTypeId())
        {
            return true;
        }
        return false;
    }

    public void draw(SpriteBatch batch, float x, float y)
    {
        batch.draw(_textureRegion, x, y);
    }

    public void draw(SpriteBatch batch, float x, float y, float scale)
    {
        batch.draw(_textureRegion, x, y, 0, 0, _textureRegion.getRegionWidth(), _textureRegion.getRegionHeight(), scale, scale, 0);
    }
}

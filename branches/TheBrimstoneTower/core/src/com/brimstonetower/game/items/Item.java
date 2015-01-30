package com.brimstonetower.game.items;

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
    public boolean isCurseKnown()
    {
        return _isCurseKnown;
    }

    private boolean _stackable = false;
    public boolean isStackable()
    {
        return _stackable;
    }

    private static int currentId = 0;
    private int _id;

    public int getId()
    {
        return _id;
    }

    public Item(String name, String description, boolean isIdentified, TextureRegion textureRegion, boolean stackable, boolean hasCurse)
    {
        _name = name;
        _description = description;
        _isIdentified = isIdentified;
        _textureRegion = textureRegion;
        _stackable = stackable;
        _hasCurse = hasCurse;
        _id = currentId++;
    }


    public void identify()
    {
        _isIdentified = true;
    }

    public String getIdentifiedName()
    {
        return _name;
    }

    public String getIdentifiedDescription()
    {
        return _description;
    }

    public boolean isIdentical(Item item)
    {
        if (item.getIdentifiedName() == _name)
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

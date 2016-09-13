package com.brimstonetower.game.gameobjects.scrolls;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.gamestateupdating.GameStateUpdater;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.managers.ItemManager;
import com.brimstonetower.game.map.Tile;

public class Scroll extends Item
{
    public enum Type{OnTile,OnItem,Instant}
    protected Type type;
    public Type getType(){return type;}


    protected boolean _isUsed = false;
    public boolean isUsed(){return _isUsed;}

    private String _unidentifiedName;
    public String getUnidentifiedName()
    {
        return _unidentifiedName;
    }



    protected Scroll(String name, String description, boolean isIdentified, TextureRegion textureRegion, String unIdentifiedName,Type type,int scrollID)
    {
        super(name, description, isIdentified, textureRegion, true,scrollID);
        this.type = type;
        _unidentifiedName = unIdentifiedName;
    }

    public String getName()
    {
        if (_isIdentified)
        {
            return super.getName();
        }
        else
        {
            return _unidentifiedName + "scroll";
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

    //Overridden methods
    public void use()
    {
    }
    public void useOnItem(Item item)
    {

    }
    public void useOnTile(Tile tile)
    {

    }
    public void upgrade()
    {

    }

    @Override
    public void draw(SpriteBatch batch, float x, float y)
    {
        super.draw(batch, x, y);
    }
}

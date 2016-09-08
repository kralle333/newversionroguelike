package com.brimstonetower.game.gameobjects.scrolls;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.map.Tile;

public class Scroll extends Item
{
    public enum Type{OnTile,OnItem,Instant}
    protected Type type;
    public Type getType(){return type;}

    public boolean canBeUsed(){return _tokensUsed>=_tokensToLearn;}

    //Tokens used
    private int _tokensUsed = 0;

    //Tokens needed to learn how to use the scroll
    private int _tokensToLearn;
    protected int tokensForLearning(){return _tokensToLearn;}

    //Number of times the scroll can be upgraded
    private int _tokensForUpgrading;
    protected int tokensForUpgrading(){return _tokensForUpgrading;}

    protected boolean _isUsed = false;
    public boolean isUsed(){return _isUsed;}

    private String _unidentifiedName;
    public String getUnidentifiedName()
    {
        return _unidentifiedName;
    }


    protected Scroll(String name, String description, boolean isIdentified, TextureRegion textureRegion, String unIdentifiedName,Type type,int tokensToLearn, int tokensForUpgrading,int id)
    {
        super(name, description, isIdentified, textureRegion, true,id);
        this.type = type;
        _tokensToLearn = tokensToLearn;
        _tokensForUpgrading = tokensForUpgrading;
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
    public void useToken()
    {
        if(_tokensUsed<_tokensToLearn)
        {
            _tokensUsed++;
        }
        else if(_tokensUsed<_tokensForUpgrading+_tokensToLearn)
        {
            _tokensUsed++;
            upgrade();
        }
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

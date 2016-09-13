package com.brimstonetower.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gamestateupdating.GameStateUpdater;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;

public class BreakableObject
{
    protected String _name;
    protected TextureRegion _textureRegion;
    protected boolean isBroken = false;
    protected Tile tile;
    public Tile getTile(){return tile;}
    protected Vector2 _worldPosition= new Vector2();
    public Vector2 getWorldPosition(){return _worldPosition;}

    private boolean _wasSeen =false;
    public boolean wasSeen(){return _wasSeen;}
    public void reveal()
    {
        _wasSeen = true;
    }
    public BreakableObject(String name, TextureRegion region)
    {
        _name = name;
        _textureRegion=region;
    }

    public void update()
    {
        if(!_wasSeen && GameStateUpdater.player.getCurrentTile().distanceTo(tile)<= GameStateUpdater.player.getViewDistance())
        {
            _wasSeen=true;
        }
    }
    public void placeOnTile(Tile tile)
    {
        this.tile = tile;
        tile.setObject(this);
        _worldPosition = new Vector2(tile.getTileX() * DungeonMap.TileSize, tile.getTileY() * DungeonMap.TileSize);
    }

    public void destroy()
    {
        isBroken=true;
        tile.removeObject();
    }

    public void draw(SpriteBatch batch)
    {
        if (!isBroken)
        {
            if(tile.getLightAmount() == Tile.LightAmount.Light ||
                    (tile.getLightAmount() == Tile.LightAmount.Shadow && _wasSeen))
            {
                batch.draw(_textureRegion,_worldPosition.x,_worldPosition.y);
            }
        }
    }
}

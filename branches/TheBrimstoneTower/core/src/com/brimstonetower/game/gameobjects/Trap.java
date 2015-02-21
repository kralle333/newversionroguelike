package com.brimstonetower.game.gameobjects;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gamestateupdating.GameCharacter;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;

public class Trap
{
    private Tile _occupiedTile;
    private Effect _effect;
    private Color _color;
    private TextureRegion _textureRegion;

    private boolean _hasBeenDiscovered = false;
    private boolean _hasBeenActivated = false;

    public boolean hasBeenActivated()
    {
        return _hasBeenActivated;
    }
    private Gas _createdGas;

    public boolean hasCreatedGas()
    {
        return _createdGas != null;
    }

    public Gas retrieveCreatedGas()
    {
        Gas toReturn = _createdGas;
        _createdGas = null;
        return toReturn;
    }

    public Trap(Effect effect)
    {
        _effect = effect;
        _textureRegion = AssetManager.getTextureRegion("misc","trap",DungeonMap.TileSize,DungeonMap.TileSize);
        _color= _effect.isGas()?_effect.getColor():Color.GRAY;
    }



    public void reveal()
    {
        if (!_hasBeenActivated)
        {
            _hasBeenDiscovered = true;
        }
    }

    public void activate()
    {
        if (!_hasBeenActivated)
        {
            if (_hasBeenDiscovered)
            {
                _hasBeenDiscovered = false;
            }
            GameCharacter affectedCharacter = _occupiedTile.getCharacter();
            if (_effect.isGas())
            {
                _createdGas = new Gas(_occupiedTile, _effect);
            }
            if (affectedCharacter != null)
            {
                affectedCharacter.giveEffect(_effect);
            }

            _hasBeenActivated = true;
        }
    }

    public void placeOnTile(Tile tile)
    {
        _occupiedTile = tile;
        tile.setTrap(this);
    }

    public void draw(SpriteBatch batch)
    {
        if (_hasBeenDiscovered)
        {
            batch.setColor(_color);
            batch.draw(_textureRegion, _occupiedTile.getTileX() * DungeonMap.TileSize, _occupiedTile.getTileY() * DungeonMap.TileSize);
            batch.setColor(Color.WHITE);
        }
        else if (hasBeenActivated())
        {
            batch.setColor(Color.GRAY);
            batch.draw(_textureRegion, _occupiedTile.getTileX() * DungeonMap.TileSize, _occupiedTile.getTileY() * DungeonMap.TileSize);
            batch.setColor(Color.WHITE);
        }
    }
}

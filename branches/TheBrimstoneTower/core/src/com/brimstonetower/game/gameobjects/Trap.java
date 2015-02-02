package com.brimstonetower.game.gameobjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.helpers.AssetManager;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.mapgeneration.DungeonMap;
import com.brimstonetower.game.mapgeneration.Tile;

public class Trap
{
    private Tile _occupiedTile;
    private Effect _effect;

    private boolean _hasBeenDiscovered = false;
    private boolean _hasBeenActivated = false;

    public boolean hasBeenActivated()
    {
        return _hasBeenActivated;
    }

    private Texture _texture;
    private Color _color;
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
            batch.draw(_texture, _occupiedTile.getX() * DungeonMap.TileSize, _occupiedTile.getY() * DungeonMap.TileSize);
            batch.setColor(Color.WHITE);
        }
        else if (hasBeenActivated())
        {
            batch.setColor(Color.BLACK);
            batch.draw(_texture, _occupiedTile.getX() * DungeonMap.TileSize, _occupiedTile.getY() * DungeonMap.TileSize);
            batch.setColor(Color.WHITE);
        }
    }
}

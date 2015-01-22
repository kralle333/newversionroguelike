package uni.aau.game.gameobjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import uni.aau.game.gui.GameConsole;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.mapgeneration.DungeonMap;
import uni.aau.game.mapgeneration.Tile;

public class Trap
{
    private Tile _occupiedTile;
    private Character.StatusEffect _statusEffect;
    private int _damage;
    private int _effectTime;
    private boolean _isGas;
    private boolean _hasBeenDiscovered = false;
    private boolean _hasBeenActivated = false;
    public boolean hasBeenActivated(){return _hasBeenActivated;}
    private Texture _texture;
    private Color _color;
    private Gas _createdGas;
    public boolean hasCreatedGas(){return _createdGas!=null;}
    public Gas retrieveCreatedGas()
    {
        Gas toReturn = _createdGas;
        _createdGas = null;
        return toReturn;
    }
    public Trap(int damage, Character.StatusEffect effect)
    {
        _damage = damage;
        _statusEffect = effect;
        _texture = AssetManager.getTexture("trap");
        _isGas = false;
        if(effect == Character.StatusEffect.Paralysed)
        {
            _color = Color.YELLOW;
        }
        else if(effect == Character.StatusEffect.Poisoned)
        {
            _color = new Color(0.6f,0f,0.6f,1);
        }
        else
        {
            _color = Color.GRAY;
        }
    }
    public Trap(Character.StatusEffect gasEffect, int gasTimer)
    {
        _damage = 0;
        _statusEffect = gasEffect;
        _effectTime = gasTimer;
        _isGas=true;
        _texture = new Texture(Gdx.files.internal("data/trap.png"));
        if(gasEffect == Character.StatusEffect.Paralysed)
        {
            _color = Color.YELLOW;
        }
        else if(gasEffect == Character.StatusEffect.Poisoned)
        {
            _color = new Color(0.6f,0f,0.6f,1);
        }
        else
        {
            _color = Color.GRAY;
        }
    }

    public void reveal()
    {
        if(!_hasBeenActivated)
        {
            _hasBeenDiscovered = true;
        }
    }
    public void activate()
    {
        if(!_hasBeenActivated)
        {
            if(_hasBeenDiscovered)
            {
                _hasBeenDiscovered = false;
            }
            GameConsole.addMessage("Trap was triggered");
            Character affectedCharacter = _occupiedTile.getCharacter();
            if(affectedCharacter != null)
            {
                if(_damage>0)
                {
                    affectedCharacter.damage(_damage);
                }
                if(!_isGas && _statusEffect != null)
                {
                    affectedCharacter.giveStatusEffect(_statusEffect,_effectTime);
                }
            }
            if(_isGas)
            {
                _createdGas = new Gas(_occupiedTile,_statusEffect,_effectTime);
            }
            _hasBeenActivated = true;
        }
    }

    public void placeOnTile(Tile tile)
    {
        _occupiedTile=tile;
        tile.setTrap(this);
    }

    public void draw(SpriteBatch batch)
    {
        if(_hasBeenDiscovered)
        {
            batch.setColor(_color);
            batch.draw(_texture, _occupiedTile.getX() * DungeonMap.TileSize, _occupiedTile.getY() * DungeonMap.TileSize);
            batch.setColor(Color.WHITE);
        }
        else if(hasBeenActivated())
        {
            batch.setColor(Color.BLACK);
            batch.draw(_texture, _occupiedTile.getX() * DungeonMap.TileSize, _occupiedTile.getY() * DungeonMap.TileSize);
            batch.setColor(Color.WHITE);
        }
    }
}

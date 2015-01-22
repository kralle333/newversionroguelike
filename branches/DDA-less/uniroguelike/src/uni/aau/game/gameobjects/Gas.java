package uni.aau.game.gameobjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.mapgeneration.DungeonMap;
import uni.aau.game.mapgeneration.RandomGen;
import uni.aau.game.mapgeneration.Tile;

import java.util.ArrayList;
import java.util.HashMap;

public class Gas
{
    private HashMap<Tile,Integer> _gasTimerMap = new HashMap<Tile, Integer>();
    private HashMap<Tile,TextureRegion> _gasTextures = new HashMap<Tile, TextureRegion>();
    private Character.StatusEffect _effect;
    private Color _color;
    public boolean hasDisappeared(){return _gasTimerMap.isEmpty();}
    private final int _getSmallerChance = 40;
    private final int _spreadChance = 75;
    private int _effectTimer;
    public Gas(Tile tile,Character.StatusEffect effect, int effectTimer)
    {
        _gasTimerMap.put(tile,effectTimer);
        _effect=effect;
        _effectTimer=effectTimer;
        _gasTextures.put(tile,AssetManager.getTextureRegion("gas", RandomGen.getRandomInt(0, 2),0,32,32));
        switch (_effect)
        {
            case Poisoned:_color=new Color(0.87f,0,1f,1);break;
            case Paralysed: _color = new Color(0.9f,0.9f,0,1);break;
            default: Gdx.app.log("Gas","Unknown cloud type! "+_effect);
        }
    }


    private ArrayList<Tile> _tilesToAdd = new ArrayList<Tile>();
    private ArrayList<Tile> _tilesToRemove = new ArrayList<Tile>();
    public void update()
    {
        for(Tile tile : _gasTimerMap.keySet())
        {
            for(Tile neighbour : tile.getWalkableNeighbours())
            {
                if(_gasTimerMap.get(tile)>0)
                {
                    if(RandomGen.getRandomInt(0, 100)<=_spreadChance && neighbour.getType() == Tile.Types.Floor)
                    {
                        //Chance of spreading to neighbour
                        if(_gasTimerMap.get(neighbour) == null)
                        {
                            if( _effectTimer>0)
                            {
                                _tilesToAdd.add(neighbour);
                            }
                        }
                    }
                }
                else
                {
                    _tilesToRemove.add(tile);
                    break;
                }
            }
            //Chance of getting smaller
            if(RandomGen.getRandomInt(0, 100)<=_getSmallerChance)
            {
                _gasTimerMap.put(tile,_gasTimerMap.get(tile)-1);
                _gasTextures.put(tile, AssetManager.getTextureRegion("gas",(int)(_gasTimerMap.get(tile)/4), 0, 32, 32));
            }
        }

        for(Tile tile : _tilesToAdd)
        {
            _gasTimerMap.put(tile, _effectTimer);
            _gasTextures.put(tile, AssetManager.getTextureRegion("gas",(int)(_gasTimerMap.get(tile)/4), 0, 32, 32));
        }
        for(Tile tile : _tilesToRemove)
        {
            _gasTextures.remove(tile);
            _gasTimerMap.remove(tile);
        }
        _tilesToAdd.clear();
        _tilesToRemove.clear();

        for(Tile tile : _gasTimerMap.keySet())
        {
            Character affectedCharacter = tile.getCharacter();
            if(affectedCharacter != null)
            {
                switch (_effect)
                {
                    case Poisoned:affectedCharacter.giveStatusEffect(Character.StatusEffect.Poisoned,3);break;
                    case Paralysed:affectedCharacter.giveStatusEffect(Character.StatusEffect.Paralysed,1);break;
                }
            }
        }
        _effectTimer--;
    }
    public void draw(SpriteBatch batch)
    {
        batch.setColor(_color);
        int index = 0;
        for(Tile tile : _gasTimerMap.keySet())
        {
            if(tile.getLightAmount()== Tile.LightAmount.Light)
            {
                batch.draw(_gasTextures.get(tile), tile.getX() * DungeonMap.TileSize, tile.getY() * DungeonMap.TileSize);
            }
            index++;
        }
        batch.setColor(Color.WHITE);
    }
}

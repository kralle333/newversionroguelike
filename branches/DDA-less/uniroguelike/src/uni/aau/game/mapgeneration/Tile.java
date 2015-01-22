package uni.aau.game.mapgeneration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import uni.aau.game.gameobjects.*;
import uni.aau.game.gameobjects.Character;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.helpers.PathFinder;
import uni.aau.game.items.Item;

import java.util.ArrayList;


public class Tile
{
    //Tile attributes
    public enum Types{Wall,Floor,Door,StairCase,WallLight,FloorLight,Empty}
    public enum LightAmount {Non,Shadow,Light}

    private Types _type;
    public Types getType(){return _type;}
    private TextureRegion _textureRegion;
    public void setTextureRegion(Vector2 tileCoordinates)
    {
        _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(),(int)tileCoordinates.x,(int)tileCoordinates.y,DungeonMap.TileSize,DungeonMap.TileSize);
        _textureRegion.flip(false,true);
    }

    private float _x;
    private float _y;
    public float getX(){return _x;}
    public float getY(){return _y;}
    private Vector2 _tilePosition;
    public Vector2 getTilePosition(){return _tilePosition;}
    private LightAmount _lightAmount = LightAmount.Light;
    public LightAmount getLightAmount(){return _lightAmount;}

    private ArrayList<Tile> walkableNeighbours = new ArrayList<Tile>();
    public ArrayList<Tile> getWalkableNeighbours(){return walkableNeighbours;}
    private ArrayList<Tile> nonWalkableNeighbours = new ArrayList<Tile>();

    //Items
    //private Item _item;
    private ArrayList<Item> _items = new ArrayList<Item>();
    public ArrayList<Item> getItems(){return _items;}
    public boolean containsItem(){return _items.size()>0;}
    public Item pickupItem()
    {
        Item itemToReturn = _items.remove(_items.size()-1);
        return itemToReturn;
    }
    public void addItem(Item item)
    {
        _items.add(item);
    }

    //Characters
    private uni.aau.game.gameobjects.Character _character;
    public void setCharacter(Character character)
    {
        if(_character != null)
        {
            Gdx.app.log("Tile", "Tile already contains character!");
        }
        _character = character;
        if( _character instanceof Player)
        {
            if(_corridorOwner != null)
            {
                _corridorOwner.playerHasEntered(this);
            }
        }
    }
    public void removeCharacter()
    {
        _character = null;
    }
    public Character getCharacter(){return _character;}
    public boolean isEmpty(){return _character == null;}

    private Room _roomOwner;
    public Room getRoom(){return _roomOwner;}
    private Corridor _corridorOwner;
    public Corridor getCorridor(){return _corridorOwner;}

    private Trap _trap;
    public Trap getTrap(){return _trap;}
    public void setTrap(Trap trap)
    {
        if(_trap == null)
        {
            _trap = trap;
        }
        else
        {
            Gdx.app.log("Tile","Tile already contains trap");
        }
    }


    public Tile(Types type,float x,float y)
    {
        _type = type;
        if(type == Types.Floor)
        {
            _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(),1,1,DungeonMap.TileSize,DungeonMap.TileSize);
        }
        else if(type == Types.StairCase)
        {
            _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(),3,2,DungeonMap.TileSize,DungeonMap.TileSize);
        }
        _tilePosition = new Vector2(x,y);
        _x=x;
        _y=y;
        _lightAmount = LightAmount.Non;
    }

    public Tile(Types type,int x, int y, Corridor owner,Vector2 tileTexture)
    {
        _tilePosition = new Vector2(x,y);
        _x=x;
        _y=y;
        _lightAmount = LightAmount.Non;
        _type = type;
        _corridorOwner = owner;
        _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(),(int)tileTexture.x,(int)tileTexture.y,DungeonMap.TileSize,DungeonMap.TileSize);
        _textureRegion.flip(false,true);
    }

    public Tile(Types type, int x, int y, Room owner,Vector2 tileTexture)
    {
        _tilePosition = new Vector2(x,y);
        _x=x;
        _y=y;
        _lightAmount = LightAmount.Non;
        _type = type;
        _roomOwner = owner;
        _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(),(int)tileTexture.x,(int)tileTexture.y,DungeonMap.TileSize,DungeonMap.TileSize);
        _textureRegion.flip(false,true);
    }
    public void setType(Types type)
    {
        _type = type;
    }

    public void setLight(LightAmount light)
    {
        _lightAmount = light;
    }
    public void setLight(LightAmount light,int strength,int currentStrength)
    {
        _lightAmount = light;
        if(currentStrength>0)
        {
            if(_type != Types.Door || (_type == Types.Door &&strength == currentStrength))
            {
                {
                    for (Tile n : walkableNeighbours)
                    {
                        if ((n.getX() == _x || n.getY() == _y))
                        {
                            n.setLight(light, strength, currentStrength - 1);
                        }
                    }
                    for (Tile n : nonWalkableNeighbours)
                    {
                        if ((n.getX() == _x || n.getY() == _y))
                        {
                            n.setLight(light, strength, currentStrength - 1);
                        }
                    }
                }
            }
        }
    }

    public void setWalkableNeighbours(ArrayList<Tile> tiles)
    {
        walkableNeighbours = tiles;
    }
    public void setNonWalkableNeighbours(ArrayList<Tile> tiles)
    {
        nonWalkableNeighbours = tiles;
    }
    public void addWalkableNeighbour(Tile neighbour)
    {
        walkableNeighbours.add(neighbour);
    }
    public Boolean isAdjacent(Tile otherTile)
    {
        for(Tile t : walkableNeighbours)
        {
            if(t == otherTile)
            {
                return true;
            }
        }
        return false;
    }


    public void draw(SpriteBatch batch)
    {
        switch (_lightAmount)
        {
            case Non:return;
            case Shadow:batch.setColor(Color.GRAY);break;
        }
        batch.draw(_textureRegion,_x*DungeonMap.TileSize,_y*DungeonMap.TileSize);
        if(_lightAmount == LightAmount.Light)
        {
            for (Item item : _items)
            {
                item.draw(batch, _x * DungeonMap.TileSize, _y * DungeonMap.TileSize);
            }
        }
        batch.setColor(Color.WHITE);
    }
    public void draw(SpriteBatch batch,LightAmount light)
    {
        switch (light)
        {
            case Non:return;
            case Shadow:batch.setColor(Color.GRAY);break;
        }
        batch.draw(_textureRegion,_x*DungeonMap.TileSize,_y*DungeonMap.TileSize);
        batch.setColor(Color.WHITE);
    }
    public float distanceTo(Tile otherTile)
    {
        return PathFinder.getEuclideanDistance(this, otherTile);
    }
}

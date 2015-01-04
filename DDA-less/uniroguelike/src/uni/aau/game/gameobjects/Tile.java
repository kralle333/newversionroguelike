package uni.aau.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.helpers.PathFinder;
import uni.aau.game.items.Item;
import uni.aau.game.mapgeneration.Corridor;
import uni.aau.game.mapgeneration.MapGenerator;
import uni.aau.game.mapgeneration.Room;
import uni.aau.game.screens.PlayScreen;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Tile
{
    //Tile attributes
    public enum Types{Wall,Floor,Door,StairCase,Empty}
    public enum LightAmount {Non,Shadow,Light}

    private Types _type;
    public Types getType(){return _type;}
    private TextureRegion _textureRegion;

    private float _x;
    private float _y;
    public float getX(){return _x;}
    public float getY(){return _y;}
    private Vector2 _tilePosition;
    public Vector2 getTilePosition(){return _tilePosition;}
    private LightAmount _lightAmount = LightAmount.Light;
    public LightAmount getLightAmount(){return _lightAmount;}

    private ArrayList<Tile> neighbours = new ArrayList<Tile>();
    public ArrayList<Tile> getNeighbours(){return neighbours;}


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
    private Character _character;
    public void setCharacter(Character character)
    {
        if(_character != null)
        {
            Gdx.app.log("Tile", "Tile already contains character!");
        }
        _character = character;
        if( _character instanceof Player)
        {
            if(_roomOwner != null)
            {
                _roomOwner.playerHasEntered(this);
            }
            else if(_corridorOwner != null)
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

    public void setRoomOwner(Room room)
    {
        _roomOwner = room;
    }
    public void setCorridorOwner(Corridor corridor){_corridorOwner = corridor;}


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
        setType(type);
        _tilePosition = new Vector2(x,y);
        _x=x;
        _y=y;
        _lightAmount = LightAmount.Non;
    }

    public Tile(Types type,int x, int y, Corridor owner)
    {
        this(type,x,y);
        _corridorOwner = owner;
    }
    public Tile(Types type, int x, int y, Room owner)
    {
        this(type,x,y);
        _roomOwner = owner;
    }

    public void setType(Types type)
    {
        _type = type;
        if(type == Types.Wall)
        {
            if(PlayScreen.getDepth()<20)
            {
                _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(),type.ordinal(), PlayScreen.getDepth()/5,DungeonMap.TileSize,DungeonMap.TileSize);
            }
            else
            {
                _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(),3, 3,DungeonMap.TileSize,DungeonMap.TileSize);            }
        }
        else
        {
            _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(),type.ordinal(),0,DungeonMap.TileSize,DungeonMap.TileSize);
        }

    }

    public void setLight(LightAmount light)
    {
        _lightAmount = light;
    }

    public void setNeighbours(ArrayList<Tile> tiles)
    {
        neighbours = tiles;
    }
    public void addNeighbour(Tile neighbour)
    {
        neighbours.add(neighbour);
    }
    public Boolean isAdjacent(Tile otherTile)
    {
        for(Tile t : neighbours)
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
        for(Item item : _items)
        {
            item.draw(batch,_x*DungeonMap.TileSize,_y*DungeonMap.TileSize);
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

package com.brimstonetower.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.helpers.PathFinder;
import com.brimstonetower.game.helpers.TileSetCoordinate;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.gameobjects.*;
import com.brimstonetower.game.gamestateupdating.GameCharacter;
import javafx.scene.effect.Light;

import java.util.ArrayList;
import java.util.HashSet;


public class Tile
{
    //Tile attributes
    public enum Types
    {
        Wall,SubWall, Floor, Door, StairCase, Empty
    }
    public boolean isWalkable(){return _type==Types.Floor || _type == Types.Door || _type == Types.StairCase;}

    public enum LightAmount
    {
        Non, Shadow,DarkShadow, Light
    }
    private Color _lightColor = Color.BLACK;
    private Color _lightChangeToColor = Color.BLACK;
    private LightAmount _lightToChangeTo;
    private float _lightTimer = 0;
    private final float lightChangeTime = 0.15f;
    private LightAmount _lightAmount = LightAmount.Light;
    public LightAmount getLightAmount()
    {
        return _lightAmount;
    }
    public LightAmount getLightAmountChangingTo()
    {
        return _lightToChangeTo;
    }
    private boolean _wasEverLight = false;

    private Types _type;
    public Types getType()
    {
        return _type;
    }

    private TextureRegion _textureRegion;
    public void setTextureRegion(TileSetCoordinate tileCoordinates)
    {
        _textureRegion = AssetManager.getTextureRegion("tile", tileCoordinates.x, tileCoordinates.y, DungeonMap.TileSize, DungeonMap.TileSize);
    }

    private int _x;
    private int _y;
    public int getTileX()
    {
        return _x;
    }
    public int getTileY()
    {
        return _y;
    }

    private Vector2 _tilePosition;
    public Vector2 getTilePosition()
    {
        return _tilePosition;
    }

    private Vector2 _worldPosition;
    public Vector2 getWorldPosition()
    {
        if(_worldPosition == null)
        {
            _worldPosition= new Vector2(_tilePosition.x*DungeonMap.TileSize,_tilePosition.y*DungeonMap.TileSize);
        }
        return _worldPosition;
    }

    private ArrayList<Tile> walkableNeighbours = new ArrayList<Tile>();
    public ArrayList<Tile> getWalkableNeighbours()
    {
        return walkableNeighbours;
    }
    private ArrayList<Tile> nonWalkableNeighbours = new ArrayList<Tile>();

    //Items
    private ArrayList<Item> _items = new ArrayList<Item>();
    public boolean containsItem()
    {
        return _items.size() > 0;
    }
    public Item pickupItem()
    {
        Item itemToReturn = _items.remove(_items.size() - 1);
        return itemToReturn;
    }
    public void addItem(Item item)
    {
        _items.add(item);
    }

    //Characters
    private GameCharacter _character;
    public void setCharacter(GameCharacter character)
    {
        if (_character != null)
        {
            Gdx.app.log("Tile", "Tile already contains character!");
        }
        _character = character;
    }
    public void removeCharacter()
    {
        _character = null;
    }
    public GameCharacter getCharacter()
    {
        return _character;
    }

    //Breakable Object
    private BreakableObject _object;
    public BreakableObject getObject(){return _object;}
    public void setObject(BreakableObject object){_object=object;}
    public void removeObject()
    {
        if(_type == Types.Door)
        {
            _type = Types.Floor;
        }
        _object=null;
    }


    public boolean isEmpty()
    {
        return (_character == null || _character.isDead()) && _object==null;
    }

    private Trap _trap;
    public Trap getTrap()
    {
        return _trap;
    }
    public void setTrap(Trap trap)
    {
        if (_trap == null)
        {
            _trap = trap;
        }
        else
        {
            Gdx.app.log("Tile", "Tile already contains trap");
        }
    }

    private static TextureRegion _overlay;

    public Tile(Types type, int x, int y,TileSetCoordinate tileTexture)
    {
        if(_overlay==null)
        {
            Pixmap pixmap =new Pixmap(DungeonMap.TileSize,DungeonMap.TileSize, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            for(int pX = 0;pX<DungeonMap.TileSize;pX++)
            {
                for(int pY = 0;pY<DungeonMap.TileSize;pY++)
                {

                    pixmap.drawPixel(pX,pY);
                }
            }
            _overlay = new TextureRegion(new Texture(pixmap));
        }
        _tilePosition = new Vector2(x, y);
        _x = x;
        _y = y;
        _lightAmount = LightAmount.Non;
        _lightToChangeTo=LightAmount.Non;
        _type = type;
        _textureRegion = AssetManager.getTextureRegion("tile", tileTexture.x, tileTexture.y, DungeonMap.TileSize, DungeonMap.TileSize);
    }

    public void placeDoor(String type)
    {
        Door door = new Door(type);
        door.placeOnTile(this);
        _object=door;
        setType(Tile.Types.Door);
        setTextureRegion( AssetManager.getTileSetPosition("floor-shiny-1"));
    }
    public void setType(Types type)
    {
        _type = type;
    }

    public void changeLight(LightAmount light)
    {
        _lightToChangeTo = light;
        if(light == LightAmount.Light)
        {
            _wasEverLight=true;
        }
        else if(light == LightAmount.Shadow && _wasEverLight==false)
        {
            _lightToChangeTo= LightAmount.DarkShadow;
        }

    }
    public void changeLight(LightAmount light, float brightness)
    {
        _lightToChangeTo = light;
        if(light == LightAmount.Light)
        {
            _wasEverLight=true;
        }
        else if(light == LightAmount.Shadow && _wasEverLight==false)
        {
            _lightToChangeTo= LightAmount.DarkShadow;
        }
        if(_lightToChangeTo != _lightAmount || _lightToChangeTo == LightAmount.Light)
        {
            switch(_lightToChangeTo)
            {
                case Non: _lightChangeToColor= Color.BLACK;break;
                case Shadow:_lightChangeToColor= Color.DARK_GRAY;break;
                case DarkShadow:_lightChangeToColor= new Color(0.1f,0.1f,0.1f,1);break;
                case Light:
                    float gray = brightness;
                    _lightChangeToColor = new Color(gray, gray, gray, 1);
                    break;
            }
        }

    }
    public void updateLight(Player player)
    {
        setLight(LightAmount.Shadow, player.getLanternStrength()*2,  player.getCurrentTile());
        setLight(Tile.LightAmount.Light, player.getLanternStrength(), player.getCurrentTile());
    }
    public void setLight(LightAmount light,int strength,Tile lightSource)
    {
        setLight(light,strength,strength,lightSource);
    }
    private void setLight(LightAmount light, int strength, int currentStrength,Tile lightSource)
    {
        changeLight(light);
        if(_lightToChangeTo != _lightAmount || _lightToChangeTo == LightAmount.Light)
        {
            switch(_lightToChangeTo)
            {
                case Non: _lightChangeToColor= Color.BLACK;break;
                case Shadow:_lightChangeToColor= Color.DARK_GRAY;break;
                case DarkShadow:_lightChangeToColor= new Color(0.1f,0.1f,0.1f,1);break;
                case Light:
                    float gray = MathUtils.lerp(1, 0.4f, lightSource.distanceTo(this) / strength);
                    _lightChangeToColor = new Color(gray, gray, gray, 1);
                    break;
            }
        }
        if (currentStrength > 0)
        {
            if ((_type != Types.Door && _type != Types.SubWall) || (_type == Types.Door && strength == currentStrength))
            {
                {
                    for (Tile n : walkableNeighbours)
                    {
                        if ((n.getTileX() == _x || n.getTileY() == _y))
                        {
                            if(n.isEmpty())
                            {
                                n.setLight(light, strength, currentStrength - 1, lightSource);
                            }
                            else
                            {
                                n.setLight(light, strength, 0,lightSource);
                            }
                        }
                    }
                    for (Tile n : nonWalkableNeighbours)
                    {
                        n.setLight(light, strength, 0,lightSource);
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
        for (Tile tile : walkableNeighbours)
        {
            if (tile == otherTile)
            {
                return true;
            }
        }
        return false;
    }


    public void draw(SpriteBatch batch)
    {

        if(_lightChangeToColor!=_lightColor && _lightTimer<lightChangeTime)
        {
            Color newColor =_lightChangeToColor;
            float progress = MathUtils.clamp(_lightTimer/lightChangeTime,0,1);
            Color toDraw = new Color();
            toDraw.r = MathUtils.lerp(_lightColor.r,newColor.r,progress);
            toDraw.g = MathUtils.lerp(_lightColor.g,newColor.g,progress);
            toDraw.b = MathUtils.lerp(_lightColor.b,newColor.b,progress);
            toDraw.a = 1;

            batch.setColor(toDraw);
            batch.draw(_textureRegion, _x * DungeonMap.TileSize, _y * DungeonMap.TileSize);
            if(_type == Types.Door)
            {
                _object.draw(batch);
            }
            _lightTimer+=Gdx.graphics.getDeltaTime();
            if(_lightTimer>lightChangeTime)
            {
                _lightTimer=0;
                _lightAmount=_lightToChangeTo;
                _lightColor=_lightChangeToColor;
            }
        }
        else
        {
            batch.setColor(_lightColor);
            batch.draw(_textureRegion, _x * DungeonMap.TileSize, _y * DungeonMap.TileSize);
            if(_type == Types.Door)
            {
                _object.draw(batch);
            }
        }

        if (_lightAmount != LightAmount.Non)
        {
            for (Item item : _items)
            {
                item.draw(batch, _x * DungeonMap.TileSize, _y * DungeonMap.TileSize);
            }
        }
        batch.setColor(Color.WHITE);
    }
    public void drawOverLay(SpriteBatch batch, Color color)
    {
        batch.setColor(color);
        batch.draw(_overlay, _x * DungeonMap.TileSize, _y * DungeonMap.TileSize);
        batch.setColor(Color.WHITE);
    }
    public void drawOverLay(SpriteBatch batch,int minRange,int maxRange,Color color)
    {
        final HashSet<Tile> drawnTiles = new HashSet<Tile>();
        final HashSet<Tile> previousTiles = new HashSet<Tile>();
        final HashSet<Tile> currentNeighbours = new HashSet<Tile>();
        final HashSet<Tile> nextRangeNeighbours = new HashSet<Tile>();
        drawnTiles.clear();
        nextRangeNeighbours.clear();
        currentNeighbours.clear();
        previousTiles.clear();

        previousTiles.add(this);
        currentNeighbours.addAll(walkableNeighbours);

        for(int i = 1;i<=maxRange;i++)
        {
            currentNeighbours.addAll(nextRangeNeighbours);
            nextRangeNeighbours.clear();
            for (Tile n : currentNeighbours)
            {
                if(!previousTiles.contains(n) && n.getLightAmount() != LightAmount.Non)
                {
                    nextRangeNeighbours.addAll(n.getWalkableNeighbours());
                    if (i >= minRange)
                    {
                        drawnTiles.add(n);
                    }
                }
            }
            previousTiles.addAll(currentNeighbours);
            currentNeighbours.clear();
        }

        for(Tile tile : drawnTiles)
        {
            tile.drawOverLay(batch,color);
        }
    }



    public float distanceTo(Tile otherTile)
    {
        return PathFinder.getEuclideanDistance(this, otherTile);
    }
}

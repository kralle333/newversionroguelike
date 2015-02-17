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
import com.brimstonetower.game.map.mapgeneration.Corridor;
import javafx.scene.effect.Light;

import java.util.ArrayList;


public class Tile
{
    //Tile attributes
    public enum Types
    {
        Wall, Floor, Door, StairCase, Empty
    }
    public boolean isWalkable(){return _type==Types.Floor || _type == Types.Door || _type == Types.StairCase;}
    public enum LightAmount
    {
        Non, Shadow,DarkShadow, Light
    }

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

    private Types _type;
    public Types getType()
    {
        return _type;
    }

    private TextureRegion _textureRegion;
    public void setTextureRegion(TileSetCoordinate tileCoordinates)
    {
        _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(), tileCoordinates.x, tileCoordinates.y, DungeonMap.TileSize, DungeonMap.TileSize);
    }

    private float _x;
    private float _y;
    public float getX()
    {
        return _x;
    }
    public float getY()
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
        if(_type==Types.Door)
        {
            _type = Types.Floor;
        }
    }
    public GameCharacter getCharacter()
    {
        return _character;
    }
    public boolean isEmpty()
    {
        return _character == null;
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
            Texture overLayTexture = new Texture(new Pixmap(DungeonMap.TileSize,DungeonMap.TileSize, Pixmap.Format.RGB888));
            _overlay = new TextureRegion(overLayTexture);
        }
        _tilePosition = new Vector2(x, y);
        _x = x;
        _y = y;
        _lightAmount = LightAmount.Non;
        _lightToChangeTo=LightAmount.Non;
        _type = type;
        _textureRegion = AssetManager.getTextureRegion(DungeonMap.getTileMapPath(), tileTexture.x, tileTexture.y, DungeonMap.TileSize, DungeonMap.TileSize);
    }

    public void placeDoor(TileSetCoordinate doorRegion)
    {
        GameCharacter door = new GameCharacter("Locked door",0,0,1,AssetManager.getTextureRegion("tile",doorRegion,DungeonMap.TileSize,DungeonMap.TileSize));
        door.placeOnTile(this);
        setCharacter(door);
    }
    public void setType(Types type)
    {
        _type = type;
    }

    public void changeLight(LightAmount light)
    {
        _lightToChangeTo = light;
    }

    public void setLight(LightAmount light, int strength, int currentStrength)
    {
        changeLight(light);
        if (currentStrength > 0)
        {
            if (_type != Types.Door || (_type == Types.Door && strength == currentStrength))
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

        Color usedColor =getColorFromLight(_lightAmount);
        if(_lightToChangeTo!=_lightAmount && _lightTimer<lightChangeTime)
        {
            Color newColor =getColorFromLight(_lightToChangeTo);;
            float progress = MathUtils.clamp(_lightTimer/lightChangeTime,0,1);
            Color toDraw = new Color();
            toDraw.r = MathUtils.lerp(usedColor.r,newColor.r,progress);
            toDraw.g = MathUtils.lerp(usedColor.g,newColor.g,progress);
            toDraw.b = MathUtils.lerp(usedColor.b,newColor.b,progress);
            toDraw.a = 1;

            batch.setColor(toDraw);
            batch.draw(_textureRegion, _x * DungeonMap.TileSize, _y * DungeonMap.TileSize);
            if(_type == Types.Door)
            {
                _character.draw(batch);
            }
            _lightTimer+=Gdx.graphics.getDeltaTime();
            if(_lightTimer>lightChangeTime)
            {
                _lightTimer=0;
                _lightAmount=_lightToChangeTo;
            }
        }
        else
        {
            batch.setColor(usedColor);
            batch.draw(_textureRegion, _x * DungeonMap.TileSize, _y * DungeonMap.TileSize);
            if(_type == Types.Door)
            {
                _character.draw(batch);
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

    private Color getColorFromLight(LightAmount lightAmount)
    {
        switch (lightAmount)
        {
            case Non:return Color.BLACK;
            case Shadow:return Color.GRAY;
            case DarkShadow:return Color.DARK_GRAY;
            case Light:return Color.WHITE;
        }
        return Color.MAGENTA;
    }

    public float distanceTo(Tile otherTile)
    {
        return PathFinder.getEuclideanDistance(this, otherTile);
    }
}

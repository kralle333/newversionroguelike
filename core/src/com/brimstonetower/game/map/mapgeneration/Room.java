package com.brimstonetower.game.map.mapgeneration;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.helpers.TileSetCoordinate;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.Tile;
import com.brimstonetower.game.helpers.RandomGen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Room
{
    protected Tile[][] _tiles;
    public Tile[][] getTiles(){return _tiles;}
    protected ArrayList<Tile> _doors = new ArrayList<Tile>();
    protected ArrayList<Corridor> _corridors = new ArrayList<Corridor>();
    public void addCorridor(Corridor corridor)
    {
        _corridors.add(corridor);
    }

    public static final int minSubRoomWidth =4;
    public static final int minSubRoomHeight = 4;

    protected HashSet<Integer> _usedWallsX = new HashSet<Integer>();
    protected HashSet<Integer> _usedWallsY = new HashSet<Integer>();

    protected int _x;
    public int getX()
    {
        return _x;
    }

    protected int _y;
    public int getY()
    {
        return _y;
    }

    protected int _width;
    public int getWidth()
    {
        return _width;
    }

    protected int _height;
    public int getHeight()
    {
        return _height;
    }

    public int getRightSide()
    {
        return _x + _width - 1;
    }
    public int getBottomSide()
    {
        return _y + _height - 1;
    }

    public boolean isLeftOf(Room otherRoom)
    {
        return getRightSide() < otherRoom.getX();
    }
    public boolean isAboveOf(Room otherRoom)
    {
        return getBottomSide() < otherRoom.getY();
    }

    public Room(int x, int y, int width, int height)
    {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _tiles = new Tile[_width][_height];
    }


    public void setWallsAndFloor()
    {

    }

    private Vector2[] directions = new Vector2[]{new Vector2(-1, 0), new Vector2(0, -1),
            new Vector2(1, 0), new Vector2(0, 1),
            new Vector2(-1, -1), new Vector2(-1, 1),
            new Vector2(1, -1), new Vector2(1, 1)};

    protected void setNeighbours(Tile tile)
    {
        int x = tile.getTileX() - _x;
        int y = tile.getTileY() - _y;

        ArrayList<Tile> walkableNeighbours = new ArrayList<Tile>();
        ArrayList<Tile> nonWalkableNeighbours = new ArrayList<Tile>();
        for (int d = 0; d < directions.length; d++)
        {
            int nX = x + (int) directions[d].x;
            int nY = y + (int) directions[d].y;
            if(nX >= 0 && nX < _width && nY >= 0 && nY < _height)
            {
                if (_tiles[nX][nY].isWalkable())
                {
                    walkableNeighbours.add(_tiles[nX][nY]);
                }
                else
                {
                    nonWalkableNeighbours.add(_tiles[nX][nY]);
                }
            }

        }
        tile.setNonWalkableNeighbours(nonWalkableNeighbours);
        tile.setWalkableNeighbours(walkableNeighbours);
    }

    public enum WallSide
    {
        West, East, South, North
    }

    public Tile getRandomEmptyWall(WallSide side)
    {
        Tile returnedTile = null;
        int randomTile=0;
        Gdx.app.log("Room","Finding empty wall");
        switch (side)
        {
            case West:
                do
                {
                    randomTile = RandomGen.getRandomInt(1, _height - 2);
                    returnedTile = _tiles[0][randomTile];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall || _usedWallsY.contains(randomTile));
                break;
            case East:
                do
                {
                    randomTile=RandomGen.getRandomInt(1, _height - 2);
                    returnedTile = _tiles[_width - 1][randomTile];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall|| _usedWallsY.contains(randomTile));
                break;
            case South:
                do
                {
                    randomTile=RandomGen.getRandomInt(1, _width - 2);
                    returnedTile = _tiles[randomTile][_height - 1];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall|| _usedWallsX.contains(randomTile));
                break;
            case North:
                do
                {
                    randomTile=RandomGen.getRandomInt(1, _width - 2);
                    returnedTile = _tiles[randomTile][0];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall|| _usedWallsX.contains(randomTile));
                break;
        }
        Gdx.app.log("Room","Done finding empty wall");
        return returnedTile;
    }


    public void createDoorAndConnect(Tile doorTile, Tile corridorTile, WallSide orientation)
    {

        switch (orientation)
        {
            case West:
                doorTile.setTextureRegion(AssetManager.getTileSetPosition("nCorridor"));
                //doorTile.placeDoor(AssetManager.getTileSetPosition("wDoor"));
                break;
            case East:
                doorTile.setTextureRegion(AssetManager.getTileSetPosition("nCorridor"));
                //doorTile.placeDoor(AssetManager.getTileSetPosition("eDoor"));
                break;
            case North:
                doorTile.setTextureRegion(AssetManager.getTileSetPosition("eCorridor"));
                //doorTile.placeDoor(RandomGen.getRandomInt(0, 1) == 1 ?
                //AssetManager.getTileSetPosition("nDoor-1") : AssetManager.getTileSetPosition("nDoor-2"));
                break;
            case South:
                doorTile.setTextureRegion(AssetManager.getTileSetPosition("wCorridor"));
                //doorTile.placeDoor(AssetManager.getTileSetPosition("sDoor"));
                break;
        }
        //doorTile.setType(Tile.Types.Door);

        //Get local coordinates
        int doorX = (int) doorTile.getTileX() - _x;
        int doorY = (int) doorTile.getTileY() - _y;

        //Find the tile that is adjacent to the door
        ArrayList<Tile> adjacentToDoor = new ArrayList<Tile>();
        if (doorX == 0)//Left side
        {
            adjacentToDoor.add(_tiles[doorX + 1][doorY]);
            for(int y = -1;y<=1;y++)
            {
                //The tile in front of the door is added first because otherwise paths look weird
                // and y!=0 is used to not add it two times
                if(y != 0 && _tiles[doorX+1][doorY+y].isWalkable())
                {
                    adjacentToDoor.add(_tiles[doorX+1][doorY+y]);
                }
            }
        }
        else if (doorY == 0)//Top side
        {
            adjacentToDoor.add(_tiles[doorX][doorY + 1]);
            for(int x = -1;x<=1;x++)
            {
                if(x != 0 && _tiles[doorX+x][doorY+1].isWalkable())
                {
                    adjacentToDoor.add(_tiles[doorX+x][doorY+1]);
                }
            }
        }
        else if (doorX == _width - 1)//Right side
        {
            adjacentToDoor.add( _tiles[doorX - 1][doorY]);
            for(int y = -1;y<=1;y++)
            {
                if(y!= 0 && _tiles[doorX-1][doorY+y].isWalkable())
                {
                    adjacentToDoor.add(_tiles[doorX-1][doorY+y]);
                }
            }
        }
        else if (doorY == _height - 1)//Left side
        {
            adjacentToDoor.add( _tiles[doorX][doorY - 1]);
            for(int x = -1;x<=1;x++)
            {
                if(x!= 0 && _tiles[doorX+x][doorY-1].isWalkable())
                {
                    adjacentToDoor.add(_tiles[doorX+x][doorY-1]);
                }
            }
        }
        //Add neighbours
        for(Tile tile : adjacentToDoor)
        {
            tile.addWalkableNeighbour(doorTile);
            doorTile.addWalkableNeighbour(tile);
        }

        doorTile.addWalkableNeighbour(corridorTile);
        corridorTile.addWalkableNeighbour(doorTile);
        _doors.add(doorTile);

    }

    public void draw(SpriteBatch batch)
    {
        for (int x = 0; x < _width; x++)
        {
            for (int y = 0; y < _height; y++)
            {
                _tiles[x][y].draw(batch);
            }
        }
        for (Tile door : _doors)
        {
            door.draw(batch);
        }

        batch.setColor(Color.WHITE);
    }
}

package com.brimstonetower.game.map.mapgeneration.rooms;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.BreakableObject;
import com.brimstonetower.game.gameobjects.Chest;
import com.brimstonetower.game.gameobjects.Monster;
import com.brimstonetower.game.gameobjects.Trap;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.Tile;
import com.brimstonetower.game.map.mapgeneration.Corridor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class Room
{
    private Vector2[] directions = new Vector2[]{new Vector2(-1, 0), new Vector2(0, -1),
            new Vector2(1, 0), new Vector2(0, 1),
            new Vector2(-1, -1), new Vector2(-1, 1),
            new Vector2(1, -1), new Vector2(1, 1)};

    public enum WallSide
    {
        West, East, South, North
    }

    protected Tile[][] _tiles;
    public Tile[][] getTiles(){return _tiles;}
    protected ArrayList<Tile> _doors = new ArrayList<Tile>();
    protected ArrayList<Corridor> _corridors = new ArrayList<Corridor>();
    public void addCorridor(Corridor corridor)
    {
        _corridors.add(corridor);
    }

    public static final int minSubRoomWidth =5;
    public static final int minSubRoomHeight = 5;

    protected HashSet<Integer> _usedWallsX = new HashSet<Integer>();
    protected HashSet<Integer> _usedWallsY = new HashSet<Integer>();

    private int _x;
    public int getX()
    {
        return _x;
    }
    private int _y;
    public int getY()
    {
        return _y;
    }

    public Vector2 getCenterWorldPos()
    {
        int cX = _x+(int)(_width/2f);
        int cY = _y+(int)(_height/2f);

        return new Vector2(cX,cY);
    }
    public Vector2 getCenterPos()
    {
        int cX = (int)(_width/2f);
        int cY = (int)(_height/2f);

        return new Vector2(cX,cY);
    }

    private int _width;
    public int getWidth()
    {
        return _width;
    }
    private int _height;
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

    protected ArrayList<Monster> _monsters = new ArrayList<>();
    public int getMonsterCount(){return _monsters.size();}
    public ArrayList<Monster> getMonsters(){return _monsters;}
    protected ArrayList<Trap> _traps = new ArrayList<>();
    public ArrayList<Trap> getTraps(){return _traps;}
    protected ArrayList<Chest> _chests = new ArrayList<>();
    public ArrayList<Chest> getChests(){return _chests;}
    public int getChestCount(){return _chests.size();}
    protected ArrayList<BreakableObject> _breakableObjects = new ArrayList<>();
    public ArrayList<BreakableObject> getBreakableObjects(){return _breakableObjects;}

    public Room(int x, int y, int width, int height)
    {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _tiles = new Tile[_width][_height];
    }

    //Setting up the room
    public void setWallsAndFloorTiles()
    {
        //Top wall
        _tiles[0][0] = new Tile(Tile.Types.Wall, _x, _y, AssetManager.getTileSetPosition("nwWall"));
        _tiles[1][0] = new Tile(Tile.Types.Wall, 1+_x, _y, AssetManager.getTileSetPosition("nwRoomWall"));
        _tiles[_width - 1][0] = new Tile(Tile.Types.Wall,_width - 1+ _x, _y, AssetManager.getTileSetPosition("neWall"));
        _tiles[_width - 2][0] = new Tile(Tile.Types.Wall, _width - 2+ _x, _y, AssetManager.getTileSetPosition("neRoomWall"));
        for(int x = 2;x<_width-2;x++)
        {
            _tiles[x][0] = new Tile(Tile.Types.Wall, x + _x,  _y, AssetManager.getTileSetPosition("nRoomWall"));
        }

        //Bottom wall
        _tiles[0][_height - 1] = new Tile(Tile.Types.Wall, _x, _height - 1+ _y, AssetManager.getTileSetPosition("swWall"));
        _tiles[_width - 1][_height - 1] = new Tile(Tile.Types.Wall, _width - 1 + _x, _height - 1 + _y, AssetManager.getTileSetPosition("seWall"));
        for(int x = 1;x<_width-1;x++)
        {
            _tiles[x][_height - 1] = new Tile(Tile.Types.Wall, x + _x, _height - 1 + _y, AssetManager.getTileSetPosition("sWall"));
        }

        //West Wall
        for(int y = 1;y<_height-1;y++)
        {
            _tiles[0][y] = new Tile(Tile.Types.Wall, _x, y + _y, AssetManager.getTileSetPosition("wWall"));
        }

        //East wall
        for(int y = 1;y<_height-1;y++)
        {
            _tiles[_width-1][y] = new Tile(Tile.Types.Wall, _width-1+_x, y + _y, AssetManager.getTileSetPosition("eWall"));
        }

        //Floor
        String floorType = "shiny";
        for(int x =1;x<_width-1;x++)
        {
            for(int y=1;y<_height-1;y++)
            {
                    //Random floor
                    _tiles[x][y] = new Tile(Tile.Types.Floor, x + _x, y + _y, AssetManager.getTileSetPosition("floor-"+floorType+"-"+String.valueOf(RandomGen.getRandomInt(1, 4))));
            }
        }

        finalize();


        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                setNeighbours(_tiles[x][y]);
            }
        }



    }
    private void setNeighbours(Tile tile)
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

    //Finishing touches
    public void addChests(Chest chest)
    {
        Tile randTile = getRandomEmptyFloorTile();
        _chests.add(chest);
        chest.placeOnTile(randTile);
    }
    public void addMonsters(ArrayList<Monster> monsters)
    {
        for(int i = 0;i<monsters.size();i++)
        {
            Monster monster = monsters.get(i);
            Tile randTile = getRandomEmptyFloorTile();
            monster.placeOnTile(randTile);
            _monsters.add(monster);
        }
    }
    public void addTraps(ArrayList<Trap> traps)
    {
        for(int i = 0;i<traps.size();i++)
        {
            Trap trap = traps.get(i);
            Tile randTile = getRandomEmptyFloorTile();
            while(randTile.getTrap()!=null)
            {
                randTile = getRandomEmptyFloorTile();
            }
            trap.placeOnTile(randTile);
            _traps.add(trap);
        }
    }
    public Boolean isConnected(Room room)
    {
        for(Corridor corridor : _corridors)
        {
            if((corridor.getRoom1()==this && corridor.getRoom2()==room)||
               (corridor.getRoom2()==this && corridor.getRoom1()==room))
            {
                return true;
            }
        }
        return false;
    }

    protected void finalize()
    {

    }

    //Helpers
    public Tile getRandomEmptyWall(WallSide side)
    {
        Tile returnedTile = null;
        int randomTile=0;
        switch (side)
        {
            case West:
                do
                {
                    randomTile = RandomGen.getRandomInt(1, _height - 2);
                    returnedTile = _tiles[0][randomTile];
                } while (returnedTile.getType() != Tile.Types.Wall || _usedWallsY.contains(randomTile));
                break;
            case East:
                do
                {
                    randomTile=RandomGen.getRandomInt(1, _height - 2);
                    returnedTile = _tiles[_width - 1][randomTile];
                } while (returnedTile.getType() != Tile.Types.Wall|| _usedWallsY.contains(randomTile));
                break;
            case South:
                do
                {
                    randomTile=RandomGen.getRandomInt(1, _width - 2);
                    returnedTile = _tiles[randomTile][_height - 1];
                } while (returnedTile.getType() != Tile.Types.Wall|| _usedWallsX.contains(randomTile));
                break;
            case North:
                do
                {
                    randomTile=RandomGen.getRandomInt(1, _width - 2);
                    returnedTile = _tiles[randomTile][0];
                } while (returnedTile.getType() != Tile.Types.Wall|| _usedWallsX.contains(randomTile));
                break;
        }
        return returnedTile;
    }
    public Tile getRandomEmptyFloorTile()
    {
        Tile returnedTile;
        int randomX=0;
        int randomY=0;
        do
        {
            randomX = RandomGen.getRandomInt(1, _width - 2);
            randomY = RandomGen.getRandomInt(1, _height - 2);
            returnedTile = _tiles[randomX][randomY];
        } while (returnedTile.getType() != Tile.Types.Floor && returnedTile.getTrap() != null && returnedTile.containsItem() && returnedTile.getCharacter()!=null);
        return returnedTile;
    }
    public int getNumberOfFloorTiles()
    {
        int total = _width*2;
        total+=(_height*2)-4;
        return total;
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

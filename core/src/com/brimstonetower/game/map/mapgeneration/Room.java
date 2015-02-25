package com.brimstonetower.game.map.mapgeneration;


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
    private Tile[][] _tiles;
    public Tile[][] getTiles(){return _tiles;}
    private ArrayList<Tile> _doors = new ArrayList<Tile>();
    private ArrayList<Corridor> _corridors = new ArrayList<Corridor>();
    public void addCorridor(Corridor corridor)
    {
        _corridors.add(corridor);
    }

    public static final int minSubRoomWidth =5;
    public static final int minSubRoomHeight = 5;

    private HashSet<Integer> _usedWallsX = new HashSet<Integer>();
    private HashSet<Integer> _usedWallsY = new HashSet<Integer>();

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
                    _tiles[x][y] = new Tile(Tile.Types.Floor, x + _x, y + _y, AssetManager.getTileSetPosition("floor-"+floorType+"-"+String.valueOf(RandomGen.getRandomInt(1, 2))));
            }
        }

        makeSubRooms(0,0,_width,_height,_width/2,false);

        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                setNeighbours(_tiles[x][y]);
            }
        }



    }

    private void makeSubRooms(int x,int y,int width, int height, int previousDoorPos,boolean previousWasHor)
    {
        //Make some smaller rooms:
        int minSplitX = x + Room.minSubRoomWidth;
        int maxSplitX = x + width - Room.minSubRoomWidth;
        int minSplitY = y + Room.minSubRoomHeight;
        int maxSplitY = y + height - Room.minSubRoomHeight;

        int split;
        int door;
        if(maxSplitY-minSplitY>3 && !previousWasHor)//Horizontal
        {
            do
            {
                split = RandomGen.getRandomInt(minSplitY, maxSplitY - 1);
            }while(split==previousDoorPos);

            door =RandomGen.getRandomInt(x+1,x+width-2);


            for(int wX = 1;wX<width-1;wX++)
            {
                if(x+wX==door-1)
                {
                    _tiles[wX+x][split].setType(Tile.Types.SubWall);
                    _tiles[wX+x][split].setTextureRegion(AssetManager.getTileSetPosition("nRoomWall"));
                }
                else if(x+wX==door+1)
                {
                    _tiles[wX+x][split].setTextureRegion(AssetManager.getTileSetPosition("nRoomWall"));
                    _tiles[wX+x][split].setType(Tile.Types.SubWall);
                }
                else if(x+wX==door)
                {
                    _tiles[door][split].setTextureRegion( AssetManager.getTileSetPosition("floor-shiny-1"));
                    _tiles[door][split].placeDoor(AssetManager.getTileSetPosition("nDoor-1"));
                    _tiles[door][split].setType(Tile.Types.Door);
                }
                else
                {
                    _tiles[wX+x][split].setTextureRegion(AssetManager.getTileSetPosition("nRoomWall"));
                    _tiles[wX+x][split].setType(Tile.Types.SubWall);
                }
            }

            makeSubRooms(x, y, width, split - y+1,door,true);
            makeSubRooms(x, split, width, height- (split - y),door,true);
            _usedWallsY.add(split);
        }
        else if(maxSplitX-minSplitX>3&& previousWasHor)//Vertical
        {
            do
            {
                split = RandomGen.getRandomInt(minSplitX,maxSplitX-1);
            }while(split==previousDoorPos);

            door =RandomGen.getRandomInt(y+1,y+height-2);

            //Make the wall
            for(int wY = 1;wY<height-1;wY++)
            {
                if(y+wY==door-1)
                {
                    _tiles[split][y+wY].setType(Tile.Types.SubWall);
                    _tiles[split][y+wY].setTextureRegion(AssetManager.getTileSetPosition("verticalWallBottom"));
                }
                else if(y+wY==door+1)
                {
                    _tiles[split][y+wY].setType(Tile.Types.SubWall);
                    _tiles[split][y+wY].setTextureRegion(AssetManager.getTileSetPosition("verticalWallTop"));
                }
                else if(y+wY==door)
                {
                    //Place a door in the wall
                    _tiles[split][door].placeDoor(AssetManager.getTileSetPosition("wDoor"));
                    _tiles[split][door].setType(Tile.Types.Door);
                    _tiles[split][door].setTextureRegion(AssetManager.getTileSetPosition("floor-shiny-1"));
                }
                else
                {
                    _tiles[split][y+wY].setType(Tile.Types.SubWall);
                    _tiles[split][y+wY].setTextureRegion(AssetManager.getTileSetPosition("verticalWall"));
                }
            }



            //Determine whether or not to put a top tile or a normal one, depends on if the wall intersects with another
            if(_tiles[split][y].getType() != Tile.Types.SubWall)
            {
                _tiles[split][y].setType(Tile.Types.SubWall);
                _tiles[split][y].setTextureRegion(AssetManager.getTileSetPosition("verticalWallTop"));
            }
            else
            {
                _tiles[split][y].setTextureRegion(AssetManager.getTileSetPosition("verticalWall"));
            }

            //Recursively make smaller subrooms
            makeSubRooms(x, y, split - x+1, height,door,false);
            makeSubRooms(split, y, width - (split - x), height,door,false);
            _usedWallsX.add(split);
        }
        else
        {
            decorateSubRoom(x,y,width,height);
        }



    }

    enum SubRoomSize {Small,Medium,Large};
    private void decorateSubRoom(int x,int y,int width, int height)
    {
        SubRoomSize size;
        if((width+height/2)<=minSubRoomWidth*3/2){size = SubRoomSize.Small;}
        else if((width+height/2)<=minSubRoomWidth*3){size = SubRoomSize.Medium;}
        else{size = SubRoomSize.Large;}

        switch(size)
        {
            case Small:break;
            case Medium:break;
            case Large:break;
        }
    }

    private Vector2[] directions = new Vector2[]{new Vector2(-1, 0), new Vector2(0, -1),
            new Vector2(1, 0), new Vector2(0, 1),
            new Vector2(-1, -1), new Vector2(-1, 1),
            new Vector2(1, -1), new Vector2(1, 1)};

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

    public enum WallSide
    {
        West, East, South, North
    }

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

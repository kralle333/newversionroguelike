package com.brimstonetower.game.map.mapgeneration;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.helpers.TileSetCoordinate;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.Tile;
import com.brimstonetower.game.map.mapgeneration.rooms.Room;

import java.util.ArrayList;
import java.util.Collections;

public class Corridor
{
    private Room _room1;
    public Room getRoom1(){return _room1;}
    private Tile _door1;
    private Room _room2;
    public Room getRoom2(){return _room2;}
    private Tile _door2;

    private ArrayList<Tile> _tiles = new ArrayList<Tile>();
    public ArrayList<Tile> getTiles(){return _tiles;}

    public Corridor(Room room1, Room room2)
    {
        _room1 = room1;
        _room1.addCorridor(this);
        _room2 = room2;
        _room2.addCorridor(this);
    }

    public Tile getRandomTile()
    {
        return _tiles.get(RandomGen.getRandomInt(0, _tiles.size() - 1));
    }

    public void generate(Tile[][] map)
    {
        boolean verticalRooms =  _room1.isAboveOf(_room2) || _room2.isAboveOf(_room1);
        boolean horizontalRooms =_room1.isLeftOf(_room2) || _room2.isLeftOf(_room1);
        boolean splitHorizontally = false;
        if(!verticalRooms && !horizontalRooms)
        {
            splitHorizontally=RandomGen.getRandomInt(0,1)==0;
        }
        else if(verticalRooms)
        {
            splitHorizontally=true;
        }
        else if(horizontalRooms)
        {
            splitHorizontally = false;
        }

        int split =0;
        if(splitHorizontally)
        {
            if(_room1.isAboveOf(_room2))
            {
                split = RandomGen.getRandomInt(_room1.getBottomSide()+1,_room2.getY()-1);
            }
            else
            {
                Room temp = _room1;
                _room1 = _room2;
                _room2 = temp;
                split = RandomGen.getRandomInt(_room2.getBottomSide()+1,_room1.getY()-1);
            }
            connectRoomsVertical(split,map);
        }
        else
        {
            if(_room1.isLeftOf(_room2))
            {
                split = RandomGen.getRandomInt(_room1.getRightSide()+1,_room2.getX()-1);
            }
            else
            {
                Room temp = _room1;
                _room1 = _room2;
                _room2 = temp;
                split = RandomGen.getRandomInt(_room2.getRightSide()+1,_room1.getX()-1);
            }
            connectRoomsHorizontal(split,map);
        }
    }

    public void connectRoomsVertical(int junctionPoint, Tile[][] map)
    {
        Room.WallSide door1Ori = _room1.isAboveOf(_room2) ? Room.WallSide.South : Room.WallSide.North;
        Room.WallSide door2Ori = door1Ori == Room.WallSide.South ? Room.WallSide.North : Room.WallSide.South;
        _door1 = _room1.getRandomEmptyWall(door1Ori);
        _door2 = _room2.getRandomEmptyWall(door2Ori);
        createVerticalLine( _door1.getTileX(),  _door1.getTileY(), _door2.getTileX(), _door2.getTileY(),junctionPoint,map);
        _room1.createDoorAndConnect(_door1, _tiles.get(0), door1Ori);
        _room2.createDoorAndConnect(_door2, _tiles.get(_tiles.size() - 1), door2Ori);
        addNeighbours();
    }

    public void connectRoomsHorizontal(int junctionPoint, Tile[][] map)
    {
        Room.WallSide door1Ori = _room1.isLeftOf(_room2) ? Room.WallSide.East : Room.WallSide.West;
        Room.WallSide door2Ori = door1Ori == Room.WallSide.East ? Room.WallSide.West : Room.WallSide.East;
        _door1 = _room1.getRandomEmptyWall(door1Ori);
        _door2 = _room2.getRandomEmptyWall(door2Ori);
        createHorizontalLine(_door1.getTileX(), _door1.getTileY(), _door2.getTileX(), _door2.getTileY(),junctionPoint,map);
        _room1.createDoorAndConnect(_door1, _tiles.get(0), door1Ori);
        _room2.createDoorAndConnect(_door2, _tiles.get(_tiles.size() - 1), door2Ori);
        addNeighbours();
    }

    private void createHorizontalLine(int x0, int y0, int x1, int y1,int junctionPoint, Tile[][] map)
    {

        /*                   |....|
        |........|     jbbbbx|....|
        |........|xaaaaj     |....|
        |--------|
         */


        for (int a = x0 + 1; a < junctionPoint; a++)
        {
            Tile tile =new Tile(Tile.Types.Floor, a, y0, AssetManager.getTileSetPosition("nCorridor"));
            _tiles.add(tile);
            map[a][y0] =tile;
        }
        if (Math.abs(y0 - y1) > 0)//Multiple tiles in junction
        {
            int smallestY = Math.min(y0, y1);
            int biggestY = Math.max(y0, y1);
            ArrayList<Tile> junction = new ArrayList<Tile>();
            for (int y = smallestY; y <= biggestY; y++)
            {
                String tileUsed ="";
                if (y == smallestY && smallestY == y0) {tileUsed="neCorridor";}
                else if (y == smallestY && smallestY == y1) {tileUsed="nwCorridor";}
                else if (y == biggestY && biggestY == y1){tileUsed="swCorridor";}
                else if (y == biggestY && biggestY == y0){tileUsed="seCorridor";}
                else{tileUsed="wCorridor";}

                Tile tile = new Tile(Tile.Types.Floor, junctionPoint, y, AssetManager.getTileSetPosition(tileUsed));
                junction.add(tile);
                map[junctionPoint][y] = tile;
            }
            //To ensure that the path is in the correct order
            if (smallestY == y1)
            {
                Collections.reverse(junction);
            }
            _tiles.addAll(junction);
        }
        else//Only one in junction
        {
            Tile tile = new Tile(Tile.Types.Floor, junctionPoint, y0, AssetManager.getTileSetPosition("nCorridor"));
            map[junctionPoint][y0] = tile;
            _tiles.add(tile);
        }

        for (int b = junctionPoint + 1; b < x1; b++)
        {
            Tile tile =new Tile(Tile.Types.Floor, b, y1, AssetManager.getTileSetPosition("nCorridor"));
            map[b][y1]=tile;
            _tiles.add(tile);
        }

    }

    private void createVerticalLine(int x0, int y0, int x1, int y1,int junctionPoint, Tile[][] map)
    {

        /*
                  |....|
                  |....|
                  |....|
                    y0
                    a
                    a
             jjjjjjjj
             b
             b
             y1
        |........|
        |........|
         */


        for (int a = y0 + 1; a < junctionPoint; a++)
        {
            Tile tile = new Tile(Tile.Types.Floor, x0, a, AssetManager.getTileSetPosition("wCorridor"));
            _tiles.add(tile);
            map[x0][a] =tile;
        }

        if (Math.abs(x1 - x0) > 0)//Multiple in junction
        {
            int smallestX = Math.min(x0, x1);
            int biggestX = Math.max(x0, x1);
            ArrayList<Tile> junction = new ArrayList<Tile>();
            for (int x = smallestX; x <= biggestX; x++)
            {
                String tileUsed = "";
                if (x == smallestX && smallestX == x1) {tileUsed="nwCorridor";}
                else if (x == smallestX && smallestX == x0) {tileUsed="swCorridor";}
                else if (x == biggestX && biggestX == x1){tileUsed="neCorridor";}
                else if (x == biggestX && biggestX == x0){tileUsed="seCorridor";}
                else{tileUsed="nCorridor";}
                Tile tile = new Tile(Tile.Types.Wall,x,junctionPoint,AssetManager.getTileSetPosition(tileUsed));
                junction.add(tile);
                map[x][junctionPoint] =tile;
            }

            if (smallestX == x1)
            {
                Collections.reverse(junction);
            }
            _tiles.addAll(junction);
        }
        else//Only one in junction point
        {
            Tile tile = new Tile(Tile.Types.Wall,x0,junctionPoint,AssetManager.getTileSetPosition("wCorridor"));
            _tiles.add(tile);
            map[x0][junctionPoint] =tile;
        }

        for (int b = junctionPoint + 1; b < y1; b++)
        {
            Tile tile = new Tile(Tile.Types.Wall,x1,b,AssetManager.getTileSetPosition("wCorridor"));
            _tiles.add(tile);
            map[x1][b] =tile;
        }
    }

    private void addNeighbours()
    {
        for (int i = 1; i < _tiles.size(); i++)
        {
            _tiles.get(i - 1).addWalkableNeighbour(_tiles.get(i));
            _tiles.get(i).addWalkableNeighbour(_tiles.get(i - 1));
        }
    }

    public Tile getTile(int x, int y)
    {
        for (Tile t : _tiles)
        {
            if (t.getTileX() == x && t.getTileY() == y)
            {
                return t;
            }
        }
        return null;
    }



    public void draw(SpriteBatch batch)
    {

        for (int i = 0; i < _tiles.size(); i++)
        {
            _tiles.get(i).draw(batch);
        }
    }
}

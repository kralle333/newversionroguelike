package com.brimstonetower.game.map.mapgeneration;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.Room;
import com.brimstonetower.game.map.Tile;

import java.util.ArrayList;
import java.util.Collections;

public class Corridor
{
    private Room _room1;
    private Tile _door1;
    private Room _room2;
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


    public void connectRoomsVertical(int junctionPoint)
    {
        Room.WallSide door1Ori = _room1.isAboveOf(_room2) ? Room.WallSide.South : Room.WallSide.North;
        Room.WallSide door2Ori = door1Ori == Room.WallSide.South ? Room.WallSide.North : Room.WallSide.South;
        _door1 = _room1.getRandomEmptyWall(door1Ori);
        _door2 = _room2.getRandomEmptyWall(door2Ori);
        createVerticalLine( _door1.getTileX(),  _door1.getTileY(), _door2.getTileX(), _door2.getTileY(),junctionPoint);
        _room1.createDoorAndConnect(_door1, _tiles.get(0), door1Ori);
        _room2.createDoorAndConnect(_door2, _tiles.get(_tiles.size() - 1), door2Ori);
        addNeighbours();
    }

    public void connectRoomsHorizontal(int junctionPoint)
    {
        Room.WallSide door1Ori = _room1.isLeftOf(_room2) ? Room.WallSide.East : Room.WallSide.West;
        Room.WallSide door2Ori = door1Ori == Room.WallSide.East ? Room.WallSide.West : Room.WallSide.East;
        _door1 = _room1.getRandomEmptyWall(door1Ori);
        _door2 = _room2.getRandomEmptyWall(door2Ori);
        createHorizontalLine(_door1.getTileX(), _door1.getTileY(), _door2.getTileX(), _door2.getTileY(),junctionPoint);
        _room1.createDoorAndConnect(_door1, _tiles.get(0), door1Ori);
        _room2.createDoorAndConnect(_door2, _tiles.get(_tiles.size() - 1), door2Ori);
        addNeighbours();
    }

    private void createHorizontalLine(int x0, int y0, int x1, int y1,int junctionPoint)
    {

        /*                   |....|
        |........|     jbbbbx|....|
        |........|xaaaaj     |....|
        |--------|
         */


        for (int a = x0 + 1; a < junctionPoint; a++)
        {
            _tiles.add(new Tile(Tile.Types.Floor, a, y0, AssetManager.getTileSetPosition("nCorridor")));
        }
        if (Math.abs(y0 - y1) > 0)//Multiple tiles in junction
        {
            int smallestY = Math.min(y0, y1);
            int biggestY = Math.max(y0, y1);
            ArrayList<Tile> junction = new ArrayList<Tile>();
            for (int y = smallestY; y <= biggestY; y++)
            {
                if (y == smallestY && smallestY == y0)
                {
                    junction.add(new Tile(Tile.Types.Floor, junctionPoint, y, AssetManager.getTileSetPosition("neCorridor")));
                }
                else if (y == smallestY && smallestY == y1)
                {
                    junction.add(new Tile(Tile.Types.Floor, junctionPoint, y, AssetManager.getTileSetPosition("nwCorridor")));
                }
                else if (y == biggestY && biggestY == y1)
                {
                    junction.add(new Tile(Tile.Types.Floor, junctionPoint, y, AssetManager.getTileSetPosition("swCorridor")));
                }
                else if (y == biggestY && biggestY == y0)
                {
                    junction.add(new Tile(Tile.Types.Floor, junctionPoint, y, AssetManager.getTileSetPosition("seCorridor")));
                }
                else
                {
                    junction.add(new Tile(Tile.Types.Floor, junctionPoint, y, AssetManager.getTileSetPosition("wCorridor")));
                }
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
            _tiles.add(new Tile(Tile.Types.Floor, junctionPoint, y0, AssetManager.getTileSetPosition("nCorridor")));
        }

        for (int b = junctionPoint + 1; b < x1; b++)
        {
            _tiles.add(new Tile(Tile.Types.Floor, b, y1, AssetManager.getTileSetPosition("nCorridor")));
        }
    }

    private void createVerticalLine(int x0, int y0, int x1, int y1,int junctionPoint)
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
            _tiles.add(new Tile(Tile.Types.Floor, x0, a, AssetManager.getTileSetPosition("wCorridor")));
        }

        if (Math.abs(x1 - x0) > 0)//Multiple in junction
        {
            int smallestX = Math.min(x0, x1);
            int biggestX = Math.max(x0, x1);
            ArrayList<Tile> junction = new ArrayList<Tile>();
            for (int x = smallestX; x <= biggestX; x++)
            {
                if (x == smallestX && smallestX == x1)
                {
                    junction.add(new Tile(Tile.Types.Floor, x, junctionPoint, AssetManager.getTileSetPosition("nwCorridor")));
                }
                else if (x == smallestX && smallestX == x0)
                {
                    junction.add(new Tile(Tile.Types.Floor, x, junctionPoint, AssetManager.getTileSetPosition("swCorridor")));
                }
                else if (x == biggestX && biggestX == x1)
                {
                    junction.add(new Tile(Tile.Types.Floor, x, junctionPoint, AssetManager.getTileSetPosition("neCorridor")));
                }
                else if (x == biggestX && biggestX == x0)
                {
                    junction.add(new Tile(Tile.Types.Floor, x, junctionPoint, AssetManager.getTileSetPosition("seCorridor")));
                }
                else
                {
                    junction.add(new Tile(Tile.Types.Floor, x, junctionPoint, AssetManager.getTileSetPosition("nCorridor")));
                }
            }
            if (smallestX == x1)
            {
                Collections.reverse(junction);
            }
            _tiles.addAll(junction);
        }
        else//Only one in junction point
        {
            _tiles.add(new Tile(Tile.Types.Floor, x0, junctionPoint, AssetManager.getTileSetPosition("wCorridor")));
        }

        for (int b = junctionPoint + 1; b < y1; b++)
        {
            _tiles.add(new Tile(Tile.Types.Floor, x1, b, AssetManager.getTileSetPosition("wCorridor")));
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

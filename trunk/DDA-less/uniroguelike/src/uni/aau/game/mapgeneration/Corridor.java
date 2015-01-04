package uni.aau.game.mapgeneration;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import uni.aau.game.gameobjects.Monster;
import uni.aau.game.gameobjects.Player;
import uni.aau.game.gameobjects.Tile;

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
    private Tile _playerTile;

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
    public ArrayList<Monster> getMonsters()
    {
        ArrayList<Monster> monstersInCorridor = new ArrayList<Monster>();
        for(Tile tile : _tiles)
        {
            if(tile.getCharacter() instanceof Monster)
            {
                monstersInCorridor.add((Monster)tile.getCharacter());
            }
        }
        return monstersInCorridor;
    }
    public ArrayList<Tile> getTilesWithItems()
    {
        ArrayList<Tile> tilesWithItems = new ArrayList<Tile>();
        for(Tile tile : _tiles)
        {
            if(tile.containsItem())
            {
                tilesWithItems.add(tile);
            }
        }
        return tilesWithItems;
    }

    public void connectRoomsVertical()
    {
        if (_room1.isAboveOf(_room2)) //Room1 is above of room2
        {
            _door1 = _room1.getRandomEmptyWall(Room.WallSide.South);
            _door2 = _room2.getRandomEmptyWall(Room.WallSide.North);
        }
        else //if (room2.isLeftOf(room1)) //Room2 to the left of room1
        {
            _door1 = _room2.getRandomEmptyWall(Room.WallSide.South);
            _door2 = _room1.getRandomEmptyWall(Room.WallSide.North);

        }
        createVerticalLine((int)_door1.getX(),(int) _door1.getY(),(int) _door2.getX(),(int) _door2.getY());
        _room1.createDoorAndConnect(_door1,_tiles.get(0));
        _room2.createDoorAndConnect(_door2,_tiles.get(_tiles.size()-1));
        addNeighbours();
    }
    public void connectRoomsHorizontal()
    {
        if (_room1.isLeftOf(_room2)) //Room1 to the left of room2
        {
            _door1 = _room1.getRandomEmptyWall(Room.WallSide.East);
            _door2 = _room2.getRandomEmptyWall(Room.WallSide.West);
        }
        else //if (room2.isLeftOf(room1)) //Room2 to the left of room1
        {
            _door1 = _room2.getRandomEmptyWall(Room.WallSide.East);
            _door2 = _room1.getRandomEmptyWall(Room.WallSide.West);

        }
        createHorizontalLine((int)_door1.getX(),(int) _door1.getY(),(int) _door2.getX(),(int) _door2.getY());
        _room1.createDoorAndConnect(_door1,_tiles.get(0));
        _room2.createDoorAndConnect(_door2,_tiles.get(_tiles.size()-1));
        addNeighbours();
    }

    private void createHorizontalLine(int x0,int y0,int x1,int y1)
    {

        /*                   |....|
        |........|     jbbbbx|....|
        |........|xaaaaj     |....|
        |--------|
         */

        int junctionPoint = RandomGen.getRandomInt(x0 + 1, x1 - 1);

        for(int a=x0+1;a<junctionPoint;a++)
        {
            _tiles.add(new Tile(Tile.Types.Floor, a, y0,this));
        }
        if(Math.abs(y0-y1)>0)//Multiple tiles in junction
        {
            int smallestY = Math.min(y0,y1);
            int biggestY = Math.max(y0,y1);
            ArrayList<Tile> junction = new ArrayList<Tile>();
                for(int y = smallestY;y<=biggestY;y++)
                {
                    junction.add(new Tile(Tile.Types.Floor, junctionPoint, y,this));
                }
            //To ensure that the path is in the correct order
            if(smallestY == y1)
            {
                Collections.reverse(junction);
            }
            _tiles.addAll(junction);
        }
        else//Only one in junction
        {
            _tiles.add(new Tile(Tile.Types.Floor, junctionPoint, y0,this));
        }

        for(int b=junctionPoint+1;b<x1;b++)
        {
            _tiles.add(new Tile(Tile.Types.Floor, b, y1,this));
        }
    }
    private void createVerticalLine(int x0, int y0, int x1, int y1)
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

        int junctionPoint = RandomGen.getRandomInt(y0 + 1, y1 - 1);

        for(int a=y0+1;a<junctionPoint;a++)
        {
            _tiles.add(new Tile(Tile.Types.Floor, x0, a,this));
        }

        if(Math.abs(x1-x0)>0)//Multiple in junction
        {
            int smallestX = Math.min(x0,x1);
            int biggestX = Math.max(x0,x1);
            ArrayList<Tile> junction = new ArrayList<Tile>();
            for(int x = smallestX;x<=biggestX;x++)
            {
                junction.add(new Tile(Tile.Types.Floor, x, junctionPoint,this));
            }
            if(smallestX == x1)
            {
                Collections.reverse(junction);
            }
            _tiles.addAll(junction);
        }
        else//Only one in junction point
        {
            _tiles.add(new Tile(Tile.Types.Floor, x0, junctionPoint,this));
        }

        for(int b=junctionPoint+1;b<y1;b++)
        {
            _tiles.add(new Tile(Tile.Types.Floor, x1, b,this));
        }
    }

    private void addNeighbours()
    {
        for(int i = 1;i<_tiles.size();i++)
        {
            _tiles.get(i-1).addNeighbour(_tiles.get(i));
            _tiles.get(i).addNeighbour(_tiles.get(i-1));
        }
    }

    public Tile getTile(int x, int y)
    {
        for(Tile t : _tiles)
        {
            if(t.getX()==x && t.getY() == y)
            {
                return t;
            }
        }
        return null;
    }
    public void playerHasEntered(Tile tile)
    {
        _playerTile = tile;
        if(tile.isAdjacent(_door1)){_door1.setLight(Tile.LightAmount.Light);}
        else if(_door1.getLightAmount() == Tile.LightAmount.Light){_door1.setLight(Tile.LightAmount.Shadow);}
        if(tile.isAdjacent(_door2)){_door2.setLight(Tile.LightAmount.Light);}
        else if(_door2.getLightAmount() == Tile.LightAmount.Light){_door2.setLight(Tile.LightAmount.Shadow);}


        for(Tile t : _tiles)
        {
            if(t.isAdjacent(tile) || t == tile)
            {
                t.setLight(Tile.LightAmount.Light);
            }
            else if(t.getLightAmount() == Tile.LightAmount.Light)
            {
                t.setLight(Tile.LightAmount.Shadow);
            }
        }
    }

    public void reveal()
    {
        for(Tile tile : _tiles)
        {
            if(tile.getLightAmount() == Tile.LightAmount.Non)
            {
                tile.setLight(Tile.LightAmount.Shadow);
            }
        }
    }

    public void draw(SpriteBatch batch)
    {
        if(_playerTile != null && !(_playerTile.getCharacter() instanceof Player))
        {
            _playerTile.setLight(Tile.LightAmount.Shadow);
            for(Tile neighbour : _playerTile.getNeighbours())
            {
                neighbour.setLight(Tile.LightAmount.Shadow);
            }
            _playerTile = null;
        }
        for(int i = 0;i<_tiles.size();i++)
        {
            _tiles.get(i).draw(batch);
        }
    }
}

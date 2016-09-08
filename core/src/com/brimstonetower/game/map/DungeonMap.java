package com.brimstonetower.game.map;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.gameobjects.Chest;
import com.brimstonetower.game.gameobjects.Monster;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.gameobjects.Trap;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.managers.MonsterManager;
import com.brimstonetower.game.map.mapgeneration.BSPMapNode;
import com.brimstonetower.game.map.mapgeneration.ChestGenerator;
import com.brimstonetower.game.map.mapgeneration.MapGenerator;
import com.brimstonetower.game.map.mapgeneration.TrapGenerator;
import com.brimstonetower.game.map.mapgeneration.rooms.Room;

import java.util.ArrayList;

public class DungeonMap
{

    public static int TileSize = 32;
    private static String _tileMapPath;

    private Tile[][] _tiles;
    private int _tileWidth;
    private int _tileHeight;

    private ArrayList<Room> _rooms;
    public ArrayList<Room> getRooms(){return _rooms;}

    public DungeonMap(String texturePath,int width, int height)
    {
        _tileMapPath = texturePath;
        _tiles = MapGenerator.generateMap(width,height);
        _rooms = MapGenerator.getRooms();
        _tileWidth = _tiles.length;
        _tileHeight = _tiles[0].length;
    }

    public void reveal(Tile tile, int diameter)
    {
        //Not implemented - Requires tile based map
    }
    public void revealAll()
    {
        for(int x = 0;x<_tileWidth;x++)
        {
            for(int y = 0;y<_tileHeight;y++)
            {
                if(_tiles[x][y]!=null && _tiles[x][y].getLightAmount() == Tile.LightAmount.Non)
                {
                    _tiles[x][y].changeLight(Tile.LightAmount.DarkShadow);
                    if(!_tiles[x][y].isEmpty())
                    {
                        _tiles[x][y].getCharacter().reveal();
                    }
                }
            }
        }
    }


    public Tile getTouchedTile(int tileX, int tileY)
    {
        if(tileX>=0 && tileX<_tileWidth && tileY>=0 && tileY<_tileHeight)
        {
            return _tiles[tileX][tileY];
        }
        return null;
    }
    public Tile getTouchedTile(float windowX, float windowY)
    {
        return getTouchedTile((int) (windowX / (float) TileSize), (int) (windowY / (float) TileSize));
    }



    public void addChests(int depth)
    {
        for(int i = 0;i<_rooms.size();i++)
        {
            int numberOfChests = 0;
            int roomSize = _rooms.get(i).getNumberOfFloorTiles();
            if(roomSize>=20)
            {
                numberOfChests +=RandomGen.getRandomInt(0,2);
            }
            else if(roomSize>=10 && roomSize<20)
            {
                numberOfChests +=RandomGen.getRandomInt(0,1);
            }

            _rooms.get(i).addChests(ChestGenerator.generateChests(depth));
        }
    }
    public void addMonsters(int depth)
    {
        for(int i = 0;i<_rooms.size();i++)
        {
            int numberOfMonsters = 0;
            int roomSize = _rooms.get(i).getNumberOfFloorTiles();
            int numberOfChests = _rooms.get(i).getChestCount();
            numberOfMonsters+=numberOfChests;
            if(roomSize>=30)
            {
                numberOfMonsters +=RandomGen.getRandomInt(2,5);
            }
            else if(roomSize>=20 && roomSize<30)
            {
                numberOfMonsters +=RandomGen.getRandomInt(1,4);
            }
            else if(roomSize>=10 && roomSize<20)
            {
                numberOfMonsters +=RandomGen.getRandomInt(0,3);
            }
            else
            {
                numberOfMonsters=RandomGen.getRandomInt(0,1);
            }

            _rooms.get(i).addMonsters(MonsterManager.generateMonsters(numberOfMonsters,depth));
        }
    }

    public void addTraps(int depth)
    {
        for(int i = 0;i<_rooms.size();i++)
        {
            int numberOfTraps = 0;
            int roomSize =  _rooms.get(i).getNumberOfFloorTiles();
            int numberOfChests = _rooms.get(i).getChestCount();
            numberOfTraps+=numberOfChests;
            if(roomSize>=20)
            {
                numberOfTraps +=RandomGen.getRandomInt(1,3);
            }
            else if(roomSize>=20 && roomSize<30)
            {
                numberOfTraps +=RandomGen.getRandomInt(0,2);
            }
            else if(roomSize>=10 && roomSize<20)
            {
                numberOfTraps +=RandomGen.getRandomInt(0,1);
            }
            else
            {
                numberOfTraps=RandomGen.getRandomInt(0,1);
            }

            numberOfTraps-=_rooms.get(i).getMonsterCount();
            if(numberOfTraps>0)
            {
                _rooms.get(i).addTraps(TrapGenerator.generateTraps(numberOfTraps, depth));
            }
        }

    }
    public void addPlayer(Player player)
    {
        Tile emptyTile = getRandomEmptyFloorTile();
        if (emptyTile == null)
        {
            Gdx.app.log("Creating player", "No empty tile found - Aborting");
        }
        else
        {
            player.placeOnTile(emptyTile);
        }
    }


    public void createStairs()
    {
        Tile stairTile = getRandomEmptyFloorTile();
        stairTile.setType(Tile.Types.StairCase);
        stairTile.setTextureRegion(AssetManager.getTileSetPosition("stairs"));
    }
    public Tile getRandomEmptyRoomTile()
    {
        Tile randomTile;
        do
        {
            Room room = _rooms.get(RandomGen.getRandomInt(0, _rooms.size() - 1));

            randomTile = room.getRandomEmptyFloorTile();

        }while(randomTile == null || !randomTile.isWalkable()  || !randomTile.isEmpty());


        return randomTile;
    }

    public Tile getRandomEmptyFloorTile()
    {
        Tile randomTile;
        do
        {
            randomTile = _tiles[RandomGen.getRandomInt(0,_tileWidth-1)][RandomGen.getRandomInt(0,_tileHeight-1)];

        }while(randomTile == null || !randomTile.isWalkable()  || !randomTile.isEmpty());


        return randomTile;
    }

    public void draw(SpriteBatch spriteBatch)
    {
        for(int x = 0;x<_tileWidth;x++)
        {
            for(int y = 0;y<_tileHeight;y++)
            {
                if(_tiles[x][y] != null)
                {
                    _tiles[x][y].draw(spriteBatch);
                }
            }
        }
    }

}

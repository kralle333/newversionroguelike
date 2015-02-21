package com.brimstonetower.game.map;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.gameobjects.*;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.mapgeneration.BSPMapNode;

import java.util.ArrayList;

public class DungeonMap
{

    public static int TileSize = 32;
    private static String _tileMapPath;

    public static String getTileMapPath()
    {
        return _tileMapPath;
    }

    private Tile[][] _tiles;
    private int _tileWidth;
    private int _tileHeight;

    private ArrayList<Monster> _monsters = new ArrayList<Monster>();
    public ArrayList<Monster> getMonsters()
    {
        return _monsters;
    }

    private ArrayList<Trap> _traps = new ArrayList<Trap>();
    public ArrayList<Trap> getTraps()
    {
        return _traps;
    }

    private ArrayList<Chest> _chests =new ArrayList<Chest>();
    public ArrayList<Chest> getChests(){return _chests;}

    public DungeonMap(String texturePath, BSPMapNode bspMapNode)
    {
        _tileMapPath = texturePath;
        _tiles = bspMapNode.convertToDoubleArray();
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

    public void addChests(ArrayList<Chest> chests)
    {

            for (Chest chest : chests)
            {
                Tile emptyTile = getRandomEmptyFloorTile();
                if (emptyTile == null)
                {
                    Gdx.app.log("Item", "No empty tile could be found - Aborting");
                }
                else
                {
                    chest.placeOnTile(emptyTile);
                }
                _chests.add(chest);
            }
    }
    public void addMonsters(ArrayList<Monster> monsters)
    {
        for (Monster monster : monsters)
        {
            Tile emptyTile = getRandomEmptyFloorTile();
            if (emptyTile == null)
            {
                Gdx.app.log("Creating monsters", "No empty tile could be found - Aborting");
            }
            else
            {
                monster.placeOnTile(emptyTile);

                _monsters.add(monster);
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

    public void addTraps(ArrayList<Trap> traps)
    {
        for (Trap trap : traps)
        {
            Tile emptyTile = getRandomEmptyFloorTile();
            if (emptyTile == null)
            {
                Gdx.app.log("Creating monsters", "No empty tile could be found - Aborting");
            }
            else
            {
                trap.placeOnTile(emptyTile);
                _traps.add(trap);
            }
        }
    }


    public void createStairs()
    {
        Tile stairTile = getRandomEmptyFloorTile();
        stairTile.setType(Tile.Types.StairCase);
        stairTile.setTextureRegion(AssetManager.getTileSetPosition("stairs"));
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

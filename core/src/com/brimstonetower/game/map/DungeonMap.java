package com.brimstonetower.game.map;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gameobjects.*;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.gameobjects.items.Item;
import com.brimstonetower.game.map.mapgeneration.BSPMapNode;
import com.brimstonetower.game.map.mapgeneration.Corridor;

import java.util.ArrayList;

public class DungeonMap extends BSPMapNode
{

    public static int TileSize = 32;
    private static String _tileMapPath;

    public static String getTileMapPath()
    {
        return _tileMapPath;
    }

    private TextureRegion _stairTextureRegion;
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

    public DungeonMap(int width, int height, String texturePath)
    {
        super(0, 0, width, height, 0, null);
        _tileMapPath = texturePath;
        _stairTextureRegion = AssetManager.getTextureRegion("tile", "stairs", TileSize, TileSize);
        _stairTextureRegion.flip(false, true);
    }


    public void revealAll()
    {
        revealChildren(getLeftNode());
        revealChildren(getRightNode());
    }

    private void revealChildren(BSPMapNode node)
    {
        if (node.isLeaf())
        {
            node.getRoom().reveal();
        }
        else
        {
            for (Corridor corridor : node.getCorridors())
            {
                corridor.reveal();
            }

            revealChildren(node.getLeftNode());
            revealChildren(node.getRightNode());
        }
    }

    public Tile getTouchedTile(int tileX, int tileY)
    {
        if(tileX>=0 && tileX<getWidth() && tileY>=0 && tileY<getHeight())
        {
            return getTile(tileX,tileY,this);
        }
        return null;
    }
    public Tile getTouchedTile(float windowX, float windowY)
    {
        if (windowX < getWidth() * TileSize && windowY < getHeight() * TileSize)
        {
            return getTile((int) (windowX / (float) TileSize), (int) (windowY / (float) TileSize), this);
        }

        return null;
    }

    private Tile getTile(int x, int y, BSPMapNode parent)
    {
        if (parent.isLeaf())
        {
            return parent.getRoom().getTile(x, y);
        }
        for (Corridor c : parent.getCorridors())
        {
            Tile t = c.getTile(x, y);
            if (t != null)
            {
                return t;
            }
        }

        if (parent.wasVerticallySplit())
        {
            if (parent.getLeftNode().getX() <= x && parent.getLeftNode().getX() + parent.getLeftNode().getWidth() > x)
            {
                return getTile(x, y, parent.getLeftNode());
            }
            else
            {
                return getTile(x, y, parent.getRightNode());
            }
        }
        else
        {
            if (parent.getLeftNode().getY() <= y && parent.getLeftNode().getY() + parent.getLeftNode().getHeight() > y)
            {
                return getTile(x, y, parent.getLeftNode());
            }
            else
            {
                return getTile(x, y, parent.getRightNode());
            }
        }
    }


    public void addChests(ArrayList<Chest> chests)
    {

            for (Chest chest : chests)
            {
                Tile emptyTile = getRandomEmptyTile();
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
            Tile emptyTile = getRandomEmptyTile();
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
        Tile emptyTile = getRandomEmptyTile();
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
            Tile emptyTile = getRandomEmptyTile();
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
        Tile stairTile = getRandomEmptyTile();
        stairTile.setType(Tile.Types.StairCase);
        stairTile.setTextureRegion(AssetManager.getTileSetPosition("stairs"));
    }

    public Tile getRandomEmptyTile()
    {
        return getRandomEmptyTile(this);
    }

    private Tile getRandomEmptyTile(BSPMapNode parent)
    {
        if (parent.isLeaf())
        {
            int randX = RandomGen.getRandomInt(parent.getRoom().getX() + 1, parent.getRoom().getRightSide() - 1);
            int randY = RandomGen.getRandomInt(parent.getRoom().getY() + 1, parent.getRoom().getBottomSide() - 1);
            Tile randomTile = parent.getRoom().getTile(randX, randY);
            //Might become a problem - Might not be any empty tiles here
            while (randomTile.getCharacter() != null || randomTile.containsItem() || randomTile.getTrap() != null ||
                    randomTile.getType() == Tile.Types.StairCase)
            {
                randX = RandomGen.getRandomInt(parent.getRoom().getX() + 1, parent.getRoom().getRightSide() - 1);
                randY = RandomGen.getRandomInt(parent.getRoom().getY() + 1, parent.getRoom().getBottomSide() - 1);
                randomTile = parent.getRoom().getTile(randX, randY);
            }
            return randomTile;
        }
        if (RandomGen.getRandomInt(0, 1) == 1)
        {
            return getRandomEmptyTile(parent.getLeftNode());
        }
        else
        {
            return getRandomEmptyTile(parent.getRightNode());
        }
    }
}

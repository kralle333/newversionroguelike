package com.brimstonetower.game.gameobjects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;

import java.util.ArrayList;

public class Chest extends BreakableObject
{
    private boolean _wasSeen =false;
    private ArrayList<Item> _droppedItems = new ArrayList<Item>();

    public Chest(int type)
    {
        super("Chest",AssetManager.getTextureRegion("tile","chest-"+String.valueOf(type), DungeonMap.TileSize,DungeonMap.TileSize));
    }
    public void addItemToDrop(Item item)
    {
        _droppedItems.add(item);
    }

    public void reveal()
    {
        _wasSeen = true;
    }

    public void update(Player player)
    {
        if(!_wasSeen && player.getCurrentTile().distanceTo(tile)<=player.getViewDistance())
        {
            _wasSeen=true;
        }
    }
    public void draw(SpriteBatch batch)
    {
        if (!isBroken)
        {
            if(tile.getLightAmount() == Tile.LightAmount.Light ||
                    (tile.getLightAmount() == Tile.LightAmount.Shadow && _wasSeen))
            {
                super.draw(batch);
            }
        }
    }
    @Override
    public void destroy()
    {
        super.destroy();

        GameConsole.addMessage("The chest was smashed open");
        if (_droppedItems.size() > 0)
        {
            for (Item item : _droppedItems)
            {
                tile.addItem(item);
            }

        }
    }
}

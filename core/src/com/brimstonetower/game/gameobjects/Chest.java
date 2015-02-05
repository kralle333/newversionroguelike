package com.brimstonetower.game.gameobjects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.gameobjects.items.Item;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;

import java.util.ArrayList;

public class Chest extends GameCharacter
{
    private boolean _wasSeen =false;
    private ArrayList<Item> _droppedItems = new ArrayList<Item>();

    public Chest(int type)
    {
        super("Chest",0,5,1, AssetManager.getTextureRegion("tile","chest-"+String.valueOf(RandomGen.getRandomInt(1,3)), DungeonMap.TileSize,DungeonMap.TileSize));
        _texture.flip(false, true);
    }
    public void addItemToDrop(Item item)
    {
        _droppedItems.add(item);
    }
    public void update(Player player)
    {
        if(!_wasSeen && player.getCurrentTile().distanceTo(currentTile)<=player.getLanternStrength())
        {
            _wasSeen=true;
        }
    }
    public void draw(SpriteBatch batch)
    {
        if (!_isDead)
        {
            if(currentTile.getLightAmount() == Tile.LightAmount.Light ||
                    (currentTile.getLightAmount() == Tile.LightAmount.Shadow && _wasSeen))
            {
                super.draw(batch);
            }
        }
    }
    @Override
    public void kill()
    {
        super.kill();
        if (_droppedItems.size() > 0)
        {
            for (Item item : _droppedItems)
            {
                currentTile.addItem(item);
            }

        }
    }
}

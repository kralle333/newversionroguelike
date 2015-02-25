package com.brimstonetower.game.gameobjects;

import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.DungeonMap;


public class Door extends BreakableObject
{
    public Door(String doorType)
    {
        super("Locked Door", AssetManager.getTextureRegion("tile",doorType, DungeonMap.TileSize,DungeonMap.TileSize));
    }

    @Override
    public void destroy()
    {
        super.destroy();
        GameConsole.addMessage("The door was kicked in");
    }
}

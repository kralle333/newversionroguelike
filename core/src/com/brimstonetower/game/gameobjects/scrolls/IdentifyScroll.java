package com.brimstonetower.game.gameobjects.scrolls;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.gamestateupdating.GameStateUpdater;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.managers.ItemManager;

public class IdentifyScroll extends Scroll
{
    public IdentifyScroll(TextureRegion textureRegion,String unidentifiedName)
    {
        super("Identify","Identifies a single item",false,textureRegion,unidentifiedName,Type.OnItem,0,0);
    }

    @Override
    public void use()
    {
        GameConsole.addMessage("Select item to identify:");
    }

    @Override
    public void useOnItem(Item item)
    {
        if (item != null && !item.isIdentified())
        {
            String oldName = item.getName();
            item.identify();
            GameConsole.addMessage(oldName + " was identified to be " + item.getName());
            GameStateUpdater.inventory.identifyItems(item);
            ItemManager.identifyItem(item);
            _isUsed = true;
        }
    }


}

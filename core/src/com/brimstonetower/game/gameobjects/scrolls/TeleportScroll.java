package com.brimstonetower.game.gameobjects.scrolls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gamestateupdating.GameStateUpdater;
import com.brimstonetower.game.gui.GameConsole;


public class TeleportScroll extends Scroll
{
    public TeleportScroll(TextureRegion region,String unidentifiedName )
    {
        super("Teleport","Teleports you to a random place",false, region,unidentifiedName,Type.Instant,0,0,2);

    }

    @Override
    public void use()
    {
        GameStateUpdater.player.moveTo(GameStateUpdater.playedMap.getRandomEmptyTile());
        GameConsole.addMessage("Player was teleported to a random tile");
    }
}

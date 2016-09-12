package com.brimstonetower.game.gameobjects.scrolls;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gamestateupdating.GameStateUpdater;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.map.Tile;


public class TeleportScroll extends Scroll
{
    public TeleportScroll(TextureRegion region,String unidentifiedName )
    {
        super("Teleport","Teleports you to a random place",false, region,unidentifiedName,Type.Instant,0,0);

    }

    @Override
    public void use()
    {
        Tile teleportedToTile = GameStateUpdater.playedMap.getRandomEmptyFloorTile();
        GameStateUpdater.player.moveTo(teleportedToTile);
        teleportedToTile.updateLight(GameStateUpdater.player);
        GameConsole.addMessage("Player was teleported to a random tile");
    }
}

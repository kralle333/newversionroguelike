package com.brimstonetower.game.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.map.mapgeneration.rooms.DoomRoom;
public class DoomTrap extends Trap
{
    private DoomRoom _doomRoom;
    public DoomTrap(DoomRoom room)
    {
        super(Effect.createPermanentEffect("Creak","Doom",0,0,0,0,0,0,0,0,false, Color.WHITE));
        _doomRoom = room;
    }

    @Override
    public void activate()
    {
        _doomRoom.activate();
    }
}

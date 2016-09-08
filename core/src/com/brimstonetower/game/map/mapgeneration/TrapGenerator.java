package com.brimstonetower.game.map.mapgeneration;


import com.badlogic.gdx.graphics.Color;
import com.brimstonetower.game.gameobjects.Trap;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.helpers.RandomGen;

import java.util.ArrayList;

public class TrapGenerator
{
    public static ArrayList<Trap> generateTraps(int numberOfTraps, int depth)
    {
        ArrayList<Trap> traps = new ArrayList<>();
        for(int i = 0;i<numberOfTraps;i++)
        {
            traps.add(createTrap(depth));
        }
        return traps;
    }

    private static Trap createTrap(int depth)
    {
        if(depth<5)
        {
            int rand = RandomGen.getRandomInt(depth,10);
            if(rand<8)
            {
                return new Trap(Effect.createPermanentEffect("Spikes","Hurts",RandomGen.getRandomInt(depth,depth+5)*-2,0,0,0,0,0,0,0,false,null));
            }
            else
            {
                return new Trap(Effect.createPermanentEffect("Poison", "Your lungs and nostrils hurt from breathing the gas", RandomGen.getRandomInt(depth,depth+5)*-1, 0, 0, 0, 0, 0, 0, 0,true, Color.PURPLE));
            }
        }
        //Implement stuff here
        return null;
    }
}

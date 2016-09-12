package com.brimstonetower.game.map.mapgeneration;


import com.badlogic.gdx.graphics.Color;
import com.brimstonetower.game.gameobjects.Trap;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.helpers.RandomGen;

public class TrapGenerator
{
    public static Trap generateTrap(int depth)
    {

        Effect effect;

        int randomEffect = RandomGen.getRandomInt(0,1);
        if(randomEffect == 0)
        {
            effect=Effect.createPermanentEffect("Metal Spikes", "Metal spikes emerge from the ground", -10 * (depth/3), 0, 0, 0, 0, 0, 0,true, Color.PURPLE);
        }
        else
        {
            effect=Effect.createPermanentEffect("Poison", "Your lungs and nostrils hurt from breathing the gas", -3 * depth, 0, 0, 0, 0, 0, 0,true, Color.PURPLE);
        }


        return new Trap(effect);
    }
}

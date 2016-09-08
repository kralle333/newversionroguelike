package com.brimstonetower.game.map.mapgeneration;


import com.badlogic.gdx.graphics.Color;
import com.brimstonetower.game.gameobjects.Trap;
import com.brimstonetower.game.helpers.Effect;

public class TrapGenerator
{
    public static Trap generateTrap(int depth)
    {
        //Only one type right now!

        Effect effect =Effect.createPermanentEffect("Poison", "Your lungs and nostrils hurt from breathing the gas", -3 * depth, 0, 0, 0, 0, 0, 0, 0,true, Color.PURPLE);
        return new Trap(effect);
    }
}

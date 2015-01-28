package com.brimstonetower.game.mapgeneration;


import com.brimstonetower.game.gameobjects.GameCharacter;
import com.brimstonetower.game.gameobjects.Trap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TrapGenerator
{
    private static final List<GameCharacter.StatusEffect> statusEffects = Collections.unmodifiableList(Arrays.asList(GameCharacter.StatusEffect.values()));
    private static final int effectsCount = statusEffects.size();

    public static Trap createTrap(int potency)
    {
        GameCharacter.StatusEffect randomEffect = statusEffects.get(RandomGen.getRandomInt(0, effectsCount - 1));
        Trap toReturn;
        if (randomEffect == GameCharacter.StatusEffect.Healthy || RandomGen.getRandomInt(0, 1) == 1)
        {
            toReturn = new Trap(potency, randomEffect);
        }
        else
        {
            toReturn = new Trap(randomEffect, potency);
        }
        return toReturn;
    }
}

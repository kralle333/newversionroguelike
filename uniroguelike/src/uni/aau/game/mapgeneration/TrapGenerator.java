package uni.aau.game.mapgeneration;


import uni.aau.game.gameobjects.Character;
import uni.aau.game.gameobjects.Trap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TrapGenerator
{
    private static final List<Character.StatusEffect> statusEffects = Collections.unmodifiableList(Arrays.asList(Character.StatusEffect.values()));
    private static final int effectsCount = statusEffects.size();

    public static Trap createTrap(int potency)
    {
        Character.StatusEffect randomEffect = statusEffects.get(RandomGen.getRandomInt(0, effectsCount - 1));
        Trap toReturn;
        if(randomEffect == Character.StatusEffect.Healthy || RandomGen.getRandomInt(0, 1)==1)
        {
            toReturn= new Trap(potency,randomEffect);
        }
        else
        {
            toReturn= new Trap(randomEffect, potency);
        }
        return toReturn;
    }
}

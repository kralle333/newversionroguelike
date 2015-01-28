package com.brimstonetower.game.mapgeneration;

import java.security.SecureRandom;

public class RandomGen
{
    private static SecureRandom random = new SecureRandom();

    public static int getRandomInt(int min, int max)
    {
        return min + (int) (random.nextFloat() * ((max - min) + 1));
    }

    public static float getRandomFloat(float min, float max)
    {
        return min + random.nextFloat() * ((max - min) + 1);
    }

}

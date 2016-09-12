package com.brimstonetower.game.helpers;


import com.badlogic.gdx.graphics.Color;

public class ColorHelper
{

    public static String convertColorToString(Color color)
    {
        if (color == Color.RED)
        {
            return "Red";
        }
        else if (color == Color.BLUE)
        {
            return "Blue";
        }
        else if (color == Color.WHITE)
        {
            return "White";
        }
        else if (color == Color.GREEN)
        {
            return "Green";
        }
        else if(color == Color.BLACK)
        {
            return "Black";
        }
        return "unknown";
    }
}

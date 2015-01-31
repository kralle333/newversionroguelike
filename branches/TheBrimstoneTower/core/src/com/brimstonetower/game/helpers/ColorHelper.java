package com.brimstonetower.game.helpers;


import com.badlogic.gdx.graphics.Color;

public class ColorHelper
{

    public static String convertColorToString(Color color)
    {
        if (color == Color.RED)
        {
            return "red";
        }
        else if (color == Color.BLUE)
        {
            return "blue";
        }
        else if (color == Color.WHITE)
        {
            return "white";
        }
        else if (color == Color.GREEN)
        {
            return "green";
        }
        else if(color == Color.BLACK)
        {
            return "black";
        }
        return "unknown";
    }
}

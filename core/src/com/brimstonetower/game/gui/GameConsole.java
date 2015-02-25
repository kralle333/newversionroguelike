package com.brimstonetower.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.managers.AssetManager;

import java.util.ArrayList;

public class GameConsole
{
    private static ArrayList<String> messages = new  ArrayList<String>();
    private static Vector2 _position;
    public static Vector2 getPosition(){return _position;}
    public static int getWidth(){return _frame.getWidth();}
    public static int getHeight(){return _frame.getHeight();}
    private static int _linesToShow;
    private static BitmapFont _font;
    private static float _fontHeight;
    private static Window _frame;
    private static Vector2 _textOffset;

    public static void setup(int x, int y, int width, int height)
    {
        _textOffset=new Vector2(Gdx.graphics.getWidth()/128,Gdx.graphics.getWidth()/128);
        _font = AssetManager.getFont("description");
        _fontHeight=_font.getBounds("Test console message").height;
        _linesToShow = (int)((height)/(_textOffset.y+(_fontHeight*1.5f)));

        _position = new Vector2(x, Gdx.graphics.getHeight()-(_textOffset.y + (_fontHeight*1.5f* (_linesToShow))));
        _frame = new Window(x, (int)_position.y, width,height, new Color(0.3f, 0.3f, 0.3f, 0.2f), 2, new Color(0.4f, 0.4f, 0.4f, 0.2f));
        _frame.show();

    }
    public static void reposition(int x, int y, int width, int height)
    {
        _textOffset.x = Gdx.graphics.getWidth()/128;
        _textOffset.y = Gdx.graphics.getWidth()/128;
        _fontHeight=_font.getBounds("Test console message").height;
        _linesToShow = (int)((height)/(_textOffset.y+(_fontHeight*1.5f)));

        _position.x = x;
        _position.y = Gdx.graphics.getHeight()-(_textOffset.y + (_fontHeight*1.5f* (_linesToShow)));
        _frame.reposition(x, (int)_position.y, width,height);
        _frame.show();

    }

    public static void reset()
    {
        messages.clear();
    }

    public static void addMessage(String message)
    {
        messages.add(message);
    }

    public static void render(SpriteBatch batch, ShapeRenderer shapeRenderer)
    {

        //_frame.draw(batch, shapeRenderer);
        batch.begin();
        int startIndex = Math.max(messages.size() - _linesToShow, 0);
        for(int i = startIndex;i<Math.min(startIndex+_linesToShow,messages.size());i++)
        {
            _font.draw(batch, messages.get(i), _position.x + _textOffset.x, _position.y + _textOffset.y + (_fontHeight*1.5f* (i - startIndex)));

        }
        batch.end();
    }
}

package com.brimstonetower.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.helpers.AssetManager;
import com.brimstonetower.game.helpers.GameStateUpdater;

public class GameConsole
{
    private static String[] messages = new String[100000];
    private static Vector2 _position;
    public static Vector2 getPosition(){return _position;}
    public static int getWidth(){return _frame.getWidth();}
    public static int getHeight(){return _frame.getHeight();}
    private static int _linesToShow;
    private static int drawIndex;
    private static int messagesAdded = 0;
    private static BitmapFont _font;
    private static Window _frame;
    private static Vector2 _textOffset;

    public static void setup(int x, int y, int width, int height)
    {
        _position = new Vector2(x, y);
        _font = AssetManager.getFont("description");
        _frame = new Window(x, y, width,height, new Color(0.3f, 0.3f, 0.3f, 0.5f), 2, new Color(0.4f, 0.4f, 0.4f, 0.5f));
        _frame.show();
        _textOffset=new Vector2(Gdx.graphics.getWidth()/128,Gdx.graphics.getWidth()/128);
        _linesToShow = (int)((height)/((_textOffset.y*2f)+_font.getBounds("Test console message").height));
        drawIndex = -_linesToShow;
    }

    public static void reset()
    {
        messages = new String[100000];
        drawIndex = -_linesToShow;
        messagesAdded = 0;
    }

    public static void addMessage(String message)
    {
        messages[messagesAdded] = "[" + GameStateUpdater.getTurn() + "]" + message;
        messagesAdded++;
        drawIndex++;
    }

    public static void render(SpriteBatch batch, ShapeRenderer shapeRenderer)
    {

        _frame.draw(batch, shapeRenderer);
        batch.begin();
        int startIndex = Math.max(drawIndex, 0);
        for (int i = startIndex; i < drawIndex + _linesToShow; i++)
        {
            _font.draw(batch, messages[i], _position.x + _textOffset.x, _position.y + _textOffset.y + ((_textOffset.y*3.5f) * (i - startIndex)));
        }
        batch.end();
    }
}

package uni.aau.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.helpers.GameStateUpdater;

public class GameConsole
{
    private static String[] messages = new String[100000];
    private static Vector2 _position;
    private static final int _linesToShow = 3;
    private static int drawIndex =-_linesToShow;
    private static int messagesAdded = 0;
    private static BitmapFont _font;
    private static Window _frame;

    public static void setup(int x, int y)
    {
        _position = new Vector2(x,y);
        _font= AssetManager.getFont("description");
        _font.setColor(Color.WHITE);
        _frame = new Window(x,y, Gdx.graphics.getWidth()-(3*(Gdx.graphics.getHeight()/5+16)),Gdx.graphics.getHeight()/4,new Color(0.3f,0.3f,0.3f,0.5f),2,new Color(0.4f,0.4f,0.4f,0.5f));
        _frame.show();
    }
    public static void reset()
    {
        messages = new String[100000];
        drawIndex = -_linesToShow;
        messagesAdded=0;
    }
    public static void addMessage(String message)
    {
        messages[messagesAdded] = "["+ GameStateUpdater.getTurn()+"] "+message;
        messagesAdded++;
        drawIndex++;
    }

    public static void render(SpriteBatch batch,ShapeRenderer shapeRenderer)
    {

        _frame.draw(batch,shapeRenderer);
        batch.begin();
        int startIndex = Math.max(drawIndex,0);
        for(int i = startIndex;i<drawIndex+_linesToShow;i++)
        {
            _font.draw(batch,messages[i],_position.x+10,_position.y+10+(30*(i-startIndex)));
        }
        batch.end();
    }
}

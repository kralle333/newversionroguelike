package uni.aau.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import uni.aau.game.RoguelikeGame;
import uni.aau.game.gui.Button;
import uni.aau.game.gui.Window;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.mapgeneration.RandomGen;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuScreen implements Screen, GestureDetector.GestureListener
{
    private Button startButton;
    private Button highScoreButton;
    private Button exitGameButton;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera guiCamera;
    private BitmapFont _font;
    private Input.TextInputListener inputListener;
    private boolean _useBot = false;
    private boolean _isShowingTextInput = false;

    public MenuScreen()
    {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float buttonWidth = w*0.2f;
        float buttonHeight = h*0.15f;
        AssetManager.initialize();
        inputListener = new Input.TextInputListener()
        {
            @Override
            public void input(String s)
            {
                RoguelikeGame.getGameInstance().setScreen(new PlayScreen(s));
            }

            @Override
            public void canceled()
            {
                _isShowingTextInput=false;
            }
        };
        startButton = new Button((w*0.5f-(buttonWidth/2)),(h*0.5f-buttonHeight),buttonWidth,buttonHeight,"Start Game", Color.RED);
        highScoreButton = new Button((w*0.5f-(buttonWidth/2)),(h*0.7f-buttonHeight),buttonWidth,buttonHeight,"High scores",Color.RED);
        exitGameButton = new Button((w*0.5f-(buttonWidth/2)),(h*0.9f-buttonHeight),buttonWidth,buttonHeight,"Exit Game",Color.RED);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        guiCamera = new OrthographicCamera(w, h);
        guiCamera.setToOrtho(true, w, h);
        _font = AssetManager.getFont("description");
        Gdx.input.setInputProcessor(new GestureDetector(this));

    }

    @Override
    public void render(float v)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        guiCamera.update();
        guiCamera.apply(Gdx.graphics.getGL10());

        batch.setProjectionMatrix(guiCamera.combined);
        shapeRenderer.setProjectionMatrix(guiCamera.combined);
        startButton.draw(batch,shapeRenderer);
        highScoreButton.draw(batch,shapeRenderer);
        exitGameButton.draw(batch,shapeRenderer);
        batch.begin();
        _font.setScale(2);
        _font.draw(batch,"Dungeons of SP8",Gdx.graphics.getWidth()/2-(_font.getBounds("Dungeons of SP8").width/2),_font.getBounds("Dungeons of SP8").height+10);
        _font.setScale(1);
        _font.draw(batch,"Everything made by Kristian Pilegaard Jensen",Gdx.graphics.getWidth()/2-(_font.getBounds("Everything made by Kristian Pilegaard Jensen").width/2),Gdx.graphics.getHeight()-_font.getBounds("Everything").height);
        batch.end();
    }

    @Override
    public boolean touchDown(float v, float v2, int i, int i2)
    {
        return false;
    }


    @Override
    public boolean tap(float x, float y, int i, int i2)
    {
        if(!_isShowingTextInput)
        {
            if(startButton.isTouched(x, y))
            {

                _isShowingTextInput = true;
                Gdx.input.getTextInput(inputListener, "Input name", "Hero");
            } else if(highScoreButton.isTouched(x, y))
            {
                RoguelikeGame.getGameInstance().setScreen(new HighScoreScreen());
            } else if(exitGameButton.isTouched(x, y))
            {
                Gdx.app.exit();
            }
        }
        return true;
    }

    @Override
    public boolean longPress(float v, float v2)
    {
        return false;
    }

    @Override
    public boolean fling(float v, float v2, int i)
    {
        return false;
    }

    @Override
    public boolean pan(float v, float v2, float v3, float v4)
    {
        return false;
    }

    @Override
    public boolean panStop(float v, float v2, int i, int i2)
    {
        return false;
    }

    @Override
    public boolean zoom(float v, float v2)
    {
        return false;
    }

    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24)
    {
        return false;
    }



    @Override
    public void resize(int i, int i2)
    {


    }

    @Override
    public void show()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {
        batch.dispose();
        shapeRenderer.dispose();

    }
}

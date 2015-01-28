package com.brimstonetower.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.TheBrimstoneTowerGame;
import com.brimstonetower.game.gui.Button;
import com.brimstonetower.game.helpers.AssetManager;

public class MenuScreen implements Screen, GestureDetector.GestureListener
{
    private Button startButton;
    private Button highScoreButton;
    private Button exitGameButton;
    private Color _buttonColor = new Color(0.6f, 0.07f, 0.07f, 1f);

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera guiCamera;
    private BitmapFont _font;
    private final String _gameTitle;
    private Vector2 _titlePosition;

    public MenuScreen()
    {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float buttonWidth = w * 0.2f;
        float buttonHeight = h * 0.12f;
        AssetManager.initialize();

        startButton = new Button((w * 0.5f - (buttonWidth / 2)), (h * 0.55f - buttonHeight), buttonWidth, buttonHeight, "Start Game", _buttonColor);
        highScoreButton = new Button((w * 0.5f - (buttonWidth / 2)), (h * 0.7f - buttonHeight), buttonWidth, buttonHeight, "High scores", _buttonColor);
        exitGameButton = new Button((w * 0.5f - (buttonWidth / 2)), (h * 0.85f - buttonHeight), buttonWidth, buttonHeight, "Exit Game", _buttonColor);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        guiCamera = new OrthographicCamera(w, h);
        guiCamera.setToOrtho(true, w, h);

        _gameTitle = "The Brimstone Tower";
        _font = AssetManager.getFont("description");
        _titlePosition = new Vector2(Gdx.graphics.getWidth() / 2 - _font.getBounds(_gameTitle).width/2, Gdx.graphics.getHeight() * 0.15f);
        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void render(float v)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiCamera.update();

        batch.setProjectionMatrix(guiCamera.combined);
        shapeRenderer.setProjectionMatrix(guiCamera.combined);
        startButton.draw(batch, shapeRenderer);
        highScoreButton.draw(batch, shapeRenderer);
        exitGameButton.draw(batch, shapeRenderer);
        batch.begin();
        _font.draw(batch, _gameTitle, _titlePosition.x, _titlePosition.y);
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
        if (startButton.isTouched(x, y))
        {
            TheBrimstoneTowerGame.getGameInstance().setScreen(new StoryScreen());
        }
        else if (highScoreButton.isTouched(x, y))
        {
            TheBrimstoneTowerGame.getGameInstance().setScreen(new HighScoreScreen());
        }
        else if (exitGameButton.isTouched(x, y))
        {
            Gdx.app.exit();
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

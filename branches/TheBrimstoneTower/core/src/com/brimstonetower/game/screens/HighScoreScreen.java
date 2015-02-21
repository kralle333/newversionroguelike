package com.brimstonetower.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.helpers.HighScoreIO;

public class HighScoreScreen implements Screen, GestureDetector.GestureListener
{
    private BitmapFont _font;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private OrthographicCamera guiCamera;
    private Button mainMenuButton;
    private Button clearScoresButton;

    @Override
    public boolean tap(float x, float y, int i, int i2)
    {
        if (mainMenuButton.isTouched(x, y))
        {
            TheBrimstoneTowerGame.getGameInstance().setScreen(new MenuScreen());
        }
        if(clearScoresButton.isTouched(x,y))
        {
            HighScoreIO.clearScores();
        }
        return true;
    }

    public HighScoreScreen()
    {
        _font = AssetManager.getFont("description");
        batch = new SpriteBatch();
        int bW = Gdx.graphics.getWidth() / 4;
        int bH = Gdx.graphics.getHeight() / 8;
        mainMenuButton = new Button((Gdx.graphics.getWidth() / 2) -(bW*3/2), (Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 6) - (bH / 2)), bW, bH, "Main Menu", Color.BLUE);
        clearScoresButton = new Button((Gdx.graphics.getWidth() / 2)+bW/2 , (Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 6) - (bH / 2)), bW, bH, "Clear Scores", Color.GRAY);
        renderer = new ShapeRenderer();
        guiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        guiCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void render(float v)
    {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiCamera.update();
        batch.setProjectionMatrix(guiCamera.combined);
        Vector2 pos = new Vector2(w *0.1f, h * 0.3f);
        batch.begin();

        _font.draw(batch, "High Scores:", (w / 2) - (_font.getBounds("High Scores:").width / 2), h * 0.1f);
        _font.draw(batch, "Rank:", pos.x, pos.y);
        _font.draw(batch, "Name:", pos.x + w * 0.1f, pos.y);
        _font.draw(batch, "Score:", pos.x + w * 0.7f, pos.y);
        for (int i = 0; i < 10; i++)
        {
            float offSetY = (i + 1) * (_font.getBounds("2").height + (h / 100));
            _font.draw(batch, (i + 1) + "", pos.x, pos.y + offSetY);
            _font.draw(batch, HighScoreIO.getScoreText(i), pos.x + w * 0.1f, pos.y + offSetY);
            _font.draw(batch, HighScoreIO.getScoreValue(i) + "", pos.x + w * 0.7f, pos.y + offSetY);
        }
        batch.end();
        renderer.setProjectionMatrix(guiCamera.combined);
        mainMenuButton.draw(batch, renderer);
        clearScoresButton.draw(batch,renderer);
        if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
        {
            TheBrimstoneTowerGame.getGameInstance().setScreen(new MenuScreen());
        }
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
    public boolean touchDown(float v, float v2, int i, int i2)
    {
        return false;
    }

    @Override
    public void show()
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
    public void resize(int i, int i2)
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {

    }
}

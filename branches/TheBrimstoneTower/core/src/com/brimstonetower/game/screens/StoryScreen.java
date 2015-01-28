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
import com.brimstonetower.game.helpers.AssetManager;
import com.brimstonetower.game.mapgeneration.RandomGen;

public class StoryScreen implements Screen, GestureDetector.GestureListener
{
    private final String _storyToBeShown = "400 years ago, the evil necromancer Xthal Saladom was vanquished and his remains sealed in The Brimstone Tower. " +
            "Since then there have been peace in the kingdom of Nasia, " +
            "but recently there have been many reports of monsters attacking travellers on the roads close to the tower. " +
            "Some farmers in the area even claim to have seen mystical flashes of red light coming from the tower.\n\n" +
            "Jasmal, the queen of Nasia, has tasked you with investigating these occurrences and to determine the cause of these phenomenons such that travellers again can travel these lands without fear of being harmed.";

    private BitmapFont _font;
    private ShapeRenderer _shapeRenderer;
    private SpriteBatch _spriteBatch;
    private OrthographicCamera _camera;
    private Input.TextInputListener _inputListener;
    private String _nameOfPlayer;
    private String[] randomNames = new String[]{"Altair", "Dunaar", "Styx", "Grim", "Evendur", "Vienna", "Nieere"};

    private Button _goBackButton;
    private Button _nameButton;
    private Button _playGameButton;
    private Color _buttonColor = new Color(0.6f, 0.07f, 0.07f, 1f);

    public StoryScreen()
    {
        _spriteBatch = new SpriteBatch();
        _shapeRenderer = new ShapeRenderer();
        _nameOfPlayer = randomNames[RandomGen.getRandomInt(0, randomNames.length - 1)];
        _font = AssetManager.getFont("description");
        _camera = new OrthographicCamera();
        _camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float buttonWidth = Gdx.graphics.getWidth() * 0.2f;
        float buttonHeight = Gdx.graphics.getHeight() * 0.12f;
        _nameButton = new Button(Gdx.graphics.getWidth() / 2 - buttonWidth / 2, Gdx.graphics.getHeight() * 0.85f, buttonWidth, buttonHeight, "Name: " + _nameOfPlayer, _buttonColor);
        _goBackButton = new Button(_nameButton.getX() - buttonWidth * 1.5f, Gdx.graphics.getHeight() * 0.85f, buttonWidth, buttonHeight, "Go back", _buttonColor);
        _playGameButton = new Button(_nameButton.getX() + buttonWidth * 1.5f, Gdx.graphics.getHeight() * 0.85f, buttonWidth, buttonHeight, "Play", _buttonColor);

        Gdx.input.setInputProcessor(new GestureDetector(this));

        _inputListener = new Input.TextInputListener()
        {
            @Override
            public void input(String s)
            {
                _nameOfPlayer = s;
                _nameButton.setText(_nameOfPlayer);
            }

            @Override
            public void canceled()
            {

            }
        };
    }


    @Override
    public void render(float v)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        _camera.update();

        _spriteBatch.setProjectionMatrix(_camera.combined);
        _spriteBatch.begin();
        _font.drawWrapped(_spriteBatch, _storyToBeShown, Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.6f);
        _spriteBatch.end();

        _shapeRenderer.setProjectionMatrix(_camera.combined);
        _playGameButton.draw(_spriteBatch, _shapeRenderer);
        _nameButton.draw(_spriteBatch, _shapeRenderer);
        _goBackButton.draw(_spriteBatch, _shapeRenderer);

    }

    @Override
    public boolean tap(float x, float y, int i, int i1)
    {
        if (_playGameButton.isTouched(x, y))
        {
            TheBrimstoneTowerGame.getGameInstance().setScreen(new PlayScreen(_nameOfPlayer));
        }
        else if (_nameButton.isTouched(x, y))
        {
            Gdx.input.getTextInput(_inputListener, "Enter Name", _nameOfPlayer,"");
        }
        else if (_goBackButton.isTouched(x, y))
        {
            TheBrimstoneTowerGame.getGameInstance().setScreen(new MenuScreen());
        }
        return true;
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
    public void hide()
    {

    }

    @Override
    public void resize(int i, int i1)
    {

    }

    @Override
    public void dispose()
    {

    }

    @Override
    public boolean touchDown(float v, float v1, int i, int i1)
    {
        return false;
    }


    @Override
    public boolean longPress(float v, float v1)
    {
        return false;
    }

    @Override
    public boolean fling(float v, float v1, int i)
    {
        return false;
    }

    @Override
    public boolean pan(float v, float v1, float v2, float v3)
    {
        return false;
    }

    @Override
    public boolean panStop(float v, float v1, int i, int i1)
    {
        return false;
    }

    @Override
    public boolean zoom(float v, float v1)
    {
        return false;
    }

    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector21, Vector2 vector22, Vector2 vector23)
    {
        return false;
    }
}

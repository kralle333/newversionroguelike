package com.brimstonetower.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.TheBrimstoneTowerGame;
import com.brimstonetower.game.gui.Button;
import com.brimstonetower.game.managers.AssetManager;

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
    private final String _versionString;
    private Vector2 _titlePosition;
    private Sprite bg;
    Vector2 crop = new Vector2();
    public MenuScreen()
    {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        int buttonWidth = (int)(w * 0.2f);
        int buttonHeight = (int)(h * 0.12f);
        setupBackground(w,h);

        startButton = new Button((w/2 - (buttonWidth / 2)),(int) (h * 0.55f - buttonHeight), buttonWidth, buttonHeight, "Start Game", _buttonColor);
        highScoreButton = new Button((w/2 - (buttonWidth / 2)),(int) (h * 0.7f - buttonHeight), buttonWidth, buttonHeight, "High scores", _buttonColor);
        exitGameButton = new Button((w/2 - (buttonWidth / 2)),(int) (h * 0.85f - buttonHeight), buttonWidth, buttonHeight, "Exit Game", _buttonColor);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        guiCamera = new OrthographicCamera(w, h);
        guiCamera.setToOrtho(true, w, h);

        _versionString=TheBrimstoneTowerGame.version + " " + TheBrimstoneTowerGame.versionState;
        _gameTitle = "The Brimstone Tower";
        _font = AssetManager.getFont("description");
        _titlePosition = new Vector2(Gdx.graphics.getWidth() / 2 - _font.getBounds(_gameTitle).width/2, Gdx.graphics.getHeight() * 0.15f);
        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    private void setupBackground(int screenWidth, int screenHeight)
    {
        float bgScale = (float)1280/(float)960;
        float screenScale = (float)screenWidth/(float)screenHeight;

        float scale= 1;
        if(bgScale>screenScale)
        {
            scale = 960/(float)screenHeight;
            crop.x = ((float)1280/scale)-screenWidth;
        }
        else if(bgScale<screenScale)
        {
            scale = 1280/(float)screenWidth;
            crop.y = ((float)960/scale)-screenHeight;
        }
        else
        {
            scale = 1280/(float)screenWidth;
        }
        int x=(int)crop.x;
        int y=(int)crop.y;
        int bW = (int)((float)1280/scale);
        int bH = (int)((float)960/scale);

        TextureRegion bgRegion = new TextureRegion(AssetManager.getBackgroundMenu(),0,0,1280,960);
        bgRegion.flip(false, true);
        bg = new Sprite(bgRegion);
        bg.setSize(bW, bH);
        bg.setPosition(-x, -y);
    }
    @Override
    public void render(float v)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(guiCamera.combined);
        shapeRenderer.setProjectionMatrix(guiCamera.combined);
        batch.begin();
        bg.draw(batch);
        _font.draw(batch, _gameTitle, _titlePosition.x, _titlePosition.y);
        _font.draw(batch,
                TheBrimstoneTowerGame.version + " " + TheBrimstoneTowerGame.versionState,
                Gdx.graphics.getWidth() - (_font.getBounds(_versionString).width*1.1f),
                Gdx.graphics.getHeight()-_font.getBounds(_versionString).height*1.1f);

        batch.end();
        guiCamera.update();



        startButton.draw(batch, shapeRenderer);
        highScoreButton.draw(batch, shapeRenderer);
        exitGameButton.draw(batch, shapeRenderer);

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

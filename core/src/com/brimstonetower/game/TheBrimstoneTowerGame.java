package com.brimstonetower.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.helpers.AssetManager;
import com.brimstonetower.game.screens.MenuScreen;
import com.brimstonetower.game.screens.PlayScreen;
import com.brimstonetower.game.screens.StoryScreen;

public class TheBrimstoneTowerGame extends Game
{

    private static TheBrimstoneTowerGame _gameInstance;
    public final static String versionState = "alpha";
    public final static float version = 0.11f;
    public static TheBrimstoneTowerGame getGameInstance()
    {
        if (_gameInstance == null)
        {
            _gameInstance = new TheBrimstoneTowerGame();
        }
        return _gameInstance;
    }

    private TheBrimstoneTowerGame()
    {

    }


    @Override
    public void create()
    {
        Gdx.input.setCatchBackKey(true);
        AssetManager.initialize();
        setScreen(new MenuScreen());
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public void render()
    {
        super.render();
    }

    @Override
    public void resize(int width, int height)
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
}

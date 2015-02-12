package com.brimstonetower.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.brimstonetower.game.helpers.HighScoreIO;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.managers.ItemManager;
import com.brimstonetower.game.screens.MenuScreen;

public class TheBrimstoneTowerGame extends Game
{

    private static TheBrimstoneTowerGame _gameInstance;
    public final static String versionState = "alpha";
    public final static float version = 0.26f;

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
        ItemManager.initialize();
        HighScoreIO.initialize();

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

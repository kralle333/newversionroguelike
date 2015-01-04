package uni.aau.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import uni.aau.game.screens.MenuScreen;
import uni.aau.game.screens.PlayScreen;

public class RoguelikeGame extends Game
{

    private static RoguelikeGame _gameInstance;

    public static RoguelikeGame getGameInstance()
    {
        if(_gameInstance == null)
        {
            _gameInstance = new RoguelikeGame();
        }
        return _gameInstance;
    }
    private RoguelikeGame()
    {

    }

	@Override
	public void create()
    {
        Gdx.input.setCatchBackKey(true);
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
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}

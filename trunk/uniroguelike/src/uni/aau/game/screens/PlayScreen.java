package uni.aau.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import uni.aau.game.RoguelikeGame;
import uni.aau.game.dda.Bot;
import uni.aau.game.dda.FitnessCalculator;
import uni.aau.game.gameobjects.*;
import uni.aau.game.gameobjects.Character;
import uni.aau.game.gui.*;
import uni.aau.game.helpers.*;
import uni.aau.game.items.Item;
import uni.aau.game.items.ItemManager;
import uni.aau.game.dda.DungeonGenerator;
import uni.aau.game.mapgeneration.DungeonCandidate;
import uni.aau.game.mapgeneration.RandomGen;

import java.io.*;
import java.util.ArrayList;

public class PlayScreen implements Screen, GestureDetector.GestureListener
{
    private final OrthographicCamera mainCamera;
    private final OrthographicCamera guiCamera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    //Game Info
    private static int _depth = 1;
    public static int getDepth(){return _depth;}
    private DungeonMap _currentDungeonMap;
    private Player _player;
    private final GameStateUpdater _gameStateUpdater = new GameStateUpdater();
    private Item _itemToThrow;

    //Gui elements
    private Inventory _inventory;
    private Button _waitActionButton;
    private Button _openInventoryButton;
    private Button _searchFloorButton;
    private SelectedItemWindow _selectedItemWindow;
    private Window _playerInfoWindowFrame;
    private Window _gameOverWindow;
    private Window _goToMainMenuPrompt;
    private final String[] randomNames = new String[]
            {
                    "Dan",
                    "Martin",
                    "Bjarne",
                    "Daniel",
                    "Kristian",
                    "Nicolai",
                    "Anders",
                    "Kenneth",
            };


    private boolean isRecordingStats = true;
    private boolean showGraphics = true;
    private boolean isDebugging = true;
    private boolean useBot = false;
    private enum ScreenState
    {
        InventoryOpen, SelectingTarget, Moving, ShowingItem, ShowingAnimation, GameOver, GameWon
    }

    private ScreenState _currentScreenState = ScreenState.Moving;
    private final BitmapFont _font;
    private String playerName;

    public PlayScreen(boolean useBot,String playerName)
    {
        this.useBot = useBot;
        if(useBot)
        {
            this.playerName = randomNames[RandomGen.getRandomInt(0, randomNames.length - 1)];
        }
        else
        {
            this.playerName = playerName;
        }

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        mainCamera = new OrthographicCamera(w,h);
        mainCamera.zoom = 1f;
        mainCamera.setToOrtho(true, w, h);
        guiCamera = new OrthographicCamera(w, h);
        guiCamera.setToOrtho(true, w, h);

        AssetManager.initialize();
        ItemManager.initialize();
        FitnessCalculator.initialize();
        HighScoreIO.initialize();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        _font = AssetManager.getFont("description");
        Gdx.input.setInputProcessor(new GestureDetector(this));
        initializeGuiElements((int) w, (int) h);
        createNewDungeon();
    }

    private void initializeGuiElements(int width, int height)
    {
        float buttonWidth = (height/5);
        float buttonHeight = (height/5);
        GameConsole.setup(4,(int)(height-buttonHeight));
        _openInventoryButton = new Button(width - buttonWidth, height - buttonHeight, buttonWidth,buttonHeight, "Inventory", new Color(0.6f, 0.2f, 0, 1));
        _waitActionButton = new Button(_openInventoryButton.getX() - buttonWidth - 16, height - buttonHeight, buttonWidth,buttonHeight, "Wait", new Color(0.5f, 0.5f, 0.5f, 1));
        _searchFloorButton = new Button(_waitActionButton.getX() -buttonWidth - 16, height - buttonHeight, buttonWidth, buttonHeight, "Search", new Color(0, 0.6f, 0.2f, 1));
        _playerInfoWindowFrame = new Window(2,2,width-4,height/10-4,new Color(0.3f,0.3f,0.3f,0.5f),2,new Color(0.4f,0.4f,0.4f,0.5f));
        _playerInfoWindowFrame.show();

        _inventory = new Inventory(width - (2 * height / 3) - 4, height - (2 * height / 3) - (height / 5) - 4, 2 * height / 3, 2 * height / 3, Color.GRAY, 2, Color.BLUE);
        _selectedItemWindow = new SelectedItemWindow(width / 2 - width / 4, height / 2 - height / 4, width / 2, height / 2, Color.GRAY, 2, Color.GRAY);

        _gameOverWindow = new Window(width / 2 - width / 4, height / 2 - height / 4, width / 2, height / 2, Color.GRAY, 2, Color.BLUE);
        _gameOverWindow.addButton("Main Menu",0,0,0.33f,0.2f,Color.RED);
        _gameOverWindow.addButton("High Scores",0,0,0.33f,0.2f,Color.BLUE);
        _gameOverWindow.addButton("Play Again",0,0,0.33f,0.2f, Color.GREEN);

        _gameOverWindow.arrangeButtons(0.001f,0.7f,0.01f,0,3);

        _goToMainMenuPrompt = new Window(width / 2 - width / 4, height / 2 - height / 4, width / 2, height / 2, Color.GRAY, 2, Color.BLUE);
        _goToMainMenuPrompt.addButton("Cancel",0,0,0.33f,0.2f,Color.BLUE);
        _goToMainMenuPrompt.addButton("Go to main menu",0,0,0.33f,0.2f,Color.RED);
        _goToMainMenuPrompt.arrangeButtons(0.1f,0.7f,0.1f,0,2);
    }
    boolean prevGPressed = false;
    boolean gPressed = false;
    boolean prevPlusPressed = false;
    boolean plusPressed = false;
    boolean prevMinusPressed = false;
    boolean minusPressed = false;

    private void update()
    {
        if(_currentScreenState==ScreenState.Moving)
        {
            _gameStateUpdater.updateGameState();
        }
        else if(_currentScreenState ==ScreenState.ShowingAnimation)
        {
            _gameStateUpdater.updateGameState();
            if(!_gameStateUpdater.isShowingAnimation())
            {
                _currentScreenState = ScreenState.Moving;
            }
        }
        if(_currentScreenState == ScreenState.GameWon && _player instanceof Bot)
        {
            restartGame();
        }
        if(_player.isDead())
        {
            if(_currentScreenState !=  ScreenState.GameOver)
            {
                _currentScreenState = ScreenState.GameOver;
                DungeonCandidate oldCandidate = DungeonGenerator.getCurrentBestCandidate();
                FitnessCalculator.updateWeights(oldCandidate, DungeonGenerator.getBestNewDungeonCandidate(_player, _inventory));
                _gameOverWindow.show();
                HighScoreIO.putScore(_player.getKilledBy(), playerName, _depth, (int) _player.calculateScore() * _depth);
                if(_player instanceof Bot)
                {
                    restartGame();
                }
            }
        }
        if(_player instanceof Bot && ((Bot) _player).isLookingForExit() && ((Bot) _player).isStandingOnExit())
        {
            ((Bot) _player).resetVariables();
            GameConsole.addMessage("Player went down the stairs");
            createNewDungeon();
        }
        if(isDebugging)
        {
            gPressed = Gdx.input.isKeyPressed(Input.Keys.G);
            if(gPressed && !prevGPressed)
            {
                showGraphics = !showGraphics;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.F))
            {
                _currentDungeonMap.revealAll();
            }
            prevGPressed = gPressed;
        }
        minusPressed = Gdx.input.isKeyPressed(Input.Keys.COMMA);
        plusPressed = Gdx.input.isKeyPressed(Input.Keys.PERIOD);
        if(minusPressed)
        {
            mainCamera.zoom = Math.min(mainCamera.zoom+0.1f,2f);
        }
        else if(plusPressed )
        {
            mainCamera.zoom = Math.max(mainCamera.zoom-0.1f,0.2f);
        }
        prevMinusPressed = minusPressed;
        prevPlusPressed = plusPressed;
        if(Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
        {
            if(_player.isMoving())
            {
                _player.clearQueue();
            }
            _goToMainMenuPrompt.show();
        }

    }
    @Override
    public void render(float v)
    {
        update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        if(showGraphics)
        {

            mainCamera.update();
            mainCamera.apply(Gdx.graphics.getGL10());

            //World
            batch.setProjectionMatrix(mainCamera.combined);
            batch.begin();
            _gameStateUpdater.drawGameState(batch);
            _gameStateUpdater.drawBattleAnimations(batch);
            batch.end();

            guiCamera.update();
            guiCamera.apply(Gdx.graphics.getGL10());

            //GUI
            batch.setProjectionMatrix(guiCamera.combined);
            shapeRenderer.setProjectionMatrix(guiCamera.combined);
            _playerInfoWindowFrame.draw(batch,shapeRenderer);
            _openInventoryButton.draw(batch, shapeRenderer);
            _waitActionButton.draw(batch, shapeRenderer);
            _searchFloorButton.draw(batch, shapeRenderer);
            _inventory.draw(batch, shapeRenderer);
            _selectedItemWindow.draw(batch, shapeRenderer);
            _goToMainMenuPrompt.draw(batch,shapeRenderer);

            GameConsole.render(batch,shapeRenderer);
            if(_currentScreenState == ScreenState.GameOver)
            {
                _gameOverWindow.draw(batch, shapeRenderer);
            }
            batch.begin();
            renderPlayerInfo(batch);
            if(_currentScreenState == ScreenState.GameOver)
            {
                _font.draw(batch, "Game Over", Gdx.graphics.getWidth() / 2 - 40, Gdx.graphics.getHeight() / 4 + 10);
            }
            else if(_currentScreenState == ScreenState.GameWon)
            {
                final String gameWonText = "Congratulations adventurer! - Your quest is over";
                _font.draw(batch, gameWonText, Gdx.graphics.getWidth() / 2 - (_font.getBounds(gameWonText).width/2), Gdx.graphics.getHeight() / 4 + 10);
            }
            batch.end();
            _gameStateUpdater.drawSelectItemDialog(batch, shapeRenderer);
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    private void renderPlayerInfo(SpriteBatch batch)
    {

        _font.draw(batch,"Level "+_player.getLevel(),10,10);
        _font.draw(batch,"Hitpoints: "+_player.getHitpoints()+"/"+_player.getMaxHitPoints(),200,10);
        _font.draw(batch,"Strength: "+_player.getCurrentStr()+"/"+_player.getMaxStr(),450,10);
        _font.draw(batch,"Experience: "+_player.getExperience()+"/"+_player.getExperienceToNextLevel(),650,10);
        _font.draw(batch,"Depth: "+_depth,900,10);

    }

    private void createNewDungeon()
    {
        if(_player == null)
        {
            if(useBot)
            {
                _player = new Bot(5,100,playerName);
            }
            else
            {
                _player = new Player(5,100,playerName);
            }

            _inventory.reset(_player);
            _selectedItemWindow.reset(_player);
            if(_player instanceof Bot)
            {
                ((Bot) _player).setInventory(_inventory);
            }
        }
        else
        {
            _depth++;
            DungeonCandidate oldCandidate = DungeonGenerator.getCurrentBestCandidate();
            FitnessCalculator.updateWeights(oldCandidate,DungeonGenerator.getBestNewDungeonCandidate(_player,_inventory));
            if(_depth == 22)
            {
                _gameOverWindow.show();
                HighScoreIO.putScore(null,playerName,_depth,(int)_player.calculateScore()*_depth);
                _currentScreenState = ScreenState.GameWon;
                return;
            }
        }
        FitnessCalculator.resetStatePlus(_player, _inventory);
        _currentDungeonMap= DungeonGenerator.generateDungeonFromCandidate(DungeonGenerator.getBestNewDungeonCandidate(_player, _inventory));
        _currentDungeonMap.addPlayer(_player);
        _gameStateUpdater.setGameState(_currentDungeonMap.getMonsters(), _player, _inventory, _currentDungeonMap,_currentDungeonMap.getTraps());
        mainCamera.position.x = _player.getPosition().x;
        mainCamera.position.y = _player.getPosition().y;
    }
    private void restartGame()
    {
        if(isRecordingStats)
        {
            PrintWriter out = null;
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/playthroughStats/playStats-resetweights-alpha"+FitnessCalculator.getAlpha(), true)));
                out.println(_depth);
            }catch (IOException e) {
                System.err.println(e);
            }finally{
                if(out != null){
                    out.close();
                }
            }
            FitnessCalculator.initialize();
        }
        if(useBot)
        {
            this.playerName = randomNames[RandomGen.getRandomInt(0, randomNames.length - 1)];
        }
        _gameOverWindow.hide();
        ItemManager.initialize();
        _depth = 1;
        _player = null;
        createNewDungeon();
        _currentScreenState = ScreenState.Moving;
        GameConsole.reset();
    }
    @Override
    public boolean tap(float x, float y, int count, int button)
    {
        if(_goToMainMenuPrompt.isOpen())
        {
            if(_goToMainMenuPrompt.isPressed("Go to main menu", x, y))
            {
                RoguelikeGame.getGameInstance().setScreen(new MenuScreen());
            }
            else if(_goToMainMenuPrompt.isPressed("Cancel",x,y))
            {
                _goToMainMenuPrompt.hide();
            }
            return false;
        }

        switch(_currentScreenState)
        {
            case GameOver:
                if(_gameOverWindow.getButton("Play Again").isTouched(x,y))
                {
                    restartGame();
                }
                else if(_gameOverWindow.getButton("High Scores").isTouched(x,y))
                {
                    RoguelikeGame.getGameInstance().setScreen(new HighScoreScreen());
                }
                else if(_gameOverWindow.getButton("Main Menu").isTouched(x,y))
                {
                    RoguelikeGame.getGameInstance().setScreen(new MenuScreen());
                }
                break;
            case InventoryOpen:
                _inventory.tap(x,y);
                if(_inventory.hasItemBeenSelected())
                {
                    _inventory.hide();
                    _selectedItemWindow.show(_inventory.retrieveItem());
                    _currentScreenState = ScreenState.ShowingItem;
                }
                else if(!_inventory.isOpen())
                {
                    _currentScreenState=_currentScreenState.Moving;
                }
                break;
            case Moving:
                if(_openInventoryButton.isTouched(x,y))
                {
                    _currentScreenState = ScreenState.InventoryOpen;
                    _inventory.show();
                }
                else if(_waitActionButton.isTouched(x, y))
                {
                    _player.clearQueueAndSetAction(new GameAction(_player, GameAction.Type.Wait, _player.getCurrentTile(), null));
                }
                else if(_searchFloorButton.isTouched(x,y))
                {
                    _player.clearQueueAndSetAction(new GameAction(_player,GameAction.Type.Search,_player.getCurrentTile(),null));
                }
                else if(_gameStateUpdater.isSelectingItem())
                {
                   _gameStateUpdater.tap(x,y);
                }
                else
                {
                    movingStateTap(x,y);
                }
                break;
            case ShowingItem:
                _selectedItemWindow.tap(x, y);
                if(!_selectedItemWindow.isOpen())
                {
                    _currentScreenState = ScreenState.Moving;
                }
                else if(_selectedItemWindow.hasAction())
                {
                    GameAction selectedAction = _selectedItemWindow.retrieveAction();

                    if(selectedAction.getType() == GameAction.Type.Throw)
                    {
                        _itemToThrow = selectedAction.getTargetItem();
                        _currentScreenState = ScreenState.SelectingTarget;

                        _selectedItemWindow.hide();
                    }
                    else
                    {
                        if(_player.getCurrentStatusEffect() != Character.StatusEffect.Paralysed)
                        {
                            if(selectedAction.getType() == GameAction.Type.Equip)
                            {
                                _inventory.equip(_selectedItemWindow.retrieveItem());
                            } else if(selectedAction.getType() == GameAction.Type.Unequip)
                            {
                                _inventory.unequip(_selectedItemWindow.retrieveItem());
                            } else if(selectedAction.getType() == GameAction.Type.Drop)
                            {
                                if(selectedAction.getTargetItem() == _player.getEquippedArmor() || selectedAction.getTargetItem() == _player.getEquippedWeapon())
                                {
                                    _inventory.unequip(selectedAction.getTargetItem());
                                }
                                _inventory.removeItem(selectedAction.getTargetItem());
                            } else
                            {
                                _inventory.removeItem(_selectedItemWindow.retrieveItem());
                            }
                        }
                        _player.clearQueueAndSetAction(selectedAction);
                        _selectedItemWindow.hide();
                        _currentScreenState = ScreenState.Moving;
                    }
                }
                break;
            case SelectingTarget:
                Vector3 touchedPosition =new Vector3(x,y,0);
                mainCamera.unproject(touchedPosition);
                Tile touchedTile = _currentDungeonMap.getTouchedTile(touchedPosition.x,touchedPosition.y);
                if(touchedTile != null && touchedTile.getLightAmount() == Tile.LightAmount.Light)
                {
                    _player.setThrowAction(_itemToThrow,touchedTile);
                    _itemToThrow = null;
                    _currentScreenState = ScreenState.ShowingAnimation;
                }
                break;

        }
        return true;
    }

    private void movingStateTap(float x,float y)
    {
        if(_player.isMoving())
        {
            _player.clearQueue();
            return;
        }
        Vector3 touchedPosition =new Vector3(x,y,0);
        mainCamera.unproject(touchedPosition);
        Tile touchedTile = _currentDungeonMap.getTouchedTile(touchedPosition.x,touchedPosition.y);
        if(touchedTile != null)
        {
            if(_itemToThrow != null)
            {
                _player.setThrowAction(_itemToThrow,touchedTile);
            }
            else if(touchedTile == _player.getCurrentTile())
            {
                if(touchedTile.containsItem())
                {
                    if(_inventory.isFull())
                    {
                        GameConsole.addMessage("Can not pick up item, inventory is full");
                    }
                    else
                    {
                        _player.clearQueueAndSetAction(new GameAction(_player, GameAction.Type.PickUp, touchedTile, null));
                    }
                }
                else if(touchedTile.getType() == Tile.Types.StairCase)
                {
                    GameConsole.addMessage("Player went down the stairs");
                    createNewDungeon();
                }
            }
            else if( touchedTile.getCharacter() != null && touchedTile.isAdjacent(_player.getCurrentTile()))
            {
                _player.setAttackAction(touchedTile);
            }
            else
            {
                ArrayList<Tile> playerPath = PathFinder.getPath(_player.getCurrentTile(),touchedTile);

                if(playerPath != null)
                {
                    _player.setMovementActions(playerPath);
                }
            }
        }
    }

    @Override
    public boolean pan(float x, float y, float dx, float dy)
    {
        mainCamera.translate(-dx, -dy);
        mainCamera.update();
        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance)
    {
        float ratio = (initialDistance / distance);

        //Clamp range and set zoom
        mainCamera.zoom = MathUtils.clamp((MathUtils.clamp(mainCamera.zoom * ratio, 0.2f, 2f)),mainCamera.zoom-0.01f,mainCamera.zoom+0.01f);

        return true;
    }


    //NOT CURRENTLY USED

    @Override
    public void resize(int w, int h)
    {
    }

    @Override
    public boolean touchDown(float v, float v2, int i, int i2)
    {
        return false;
    }

    @Override
    public boolean longPress(float v, float v2) {
        return true;
    }

    @Override
    public boolean fling(float v, float v2, int i) {
        return false;
    }


    @Override
    public boolean panStop(float v, float v2, int i, int i2) {
        return true;
    }


    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24) {
        return false;
    }
    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose()
    {
        batch.dispose();
        shapeRenderer.dispose();
        AssetManager.disposeAll();
    }
}


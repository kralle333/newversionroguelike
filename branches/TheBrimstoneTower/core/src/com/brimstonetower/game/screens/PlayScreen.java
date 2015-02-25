package com.brimstonetower.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.brimstonetower.game.TheBrimstoneTowerGame;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.gameobjects.equipment.Weapon;
import com.brimstonetower.game.gameobjects.scrolls.Scroll;
import com.brimstonetower.game.gamestateupdating.GameAction;
import com.brimstonetower.game.gamestateupdating.GameStateUpdater;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.mapgeneration.DungeonGenerator;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.gui.*;
import com.brimstonetower.game.helpers.*;

import java.util.ArrayList;
import java.util.Random;

public class PlayScreen implements Screen, GestureDetector.GestureListener, InputProcessor
{
    private final OrthographicCamera mainCamera;
    private final float minZoom;
    private final float maxZoom;
    private final OrthographicCamera guiCamera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    //Game Info
    private int _depth = 1;
    private DungeonMap _currentDungeonMap;
    private Player _player;
    private Vector2 _previousPlayerPosition;
    private String playerName;
    private GameAction _playerAction;
    private final GameStateUpdater _gameStateUpdater = new GameStateUpdater();
    private Item _itemToThrow;

    //Gui elements
    private Inventory _inventory;
    private Button _waitActionButton;
    private Button _openInventoryButton;
    private Button _searchFloorButton;
    private SelectedItemWindow _selectedItemWindow;
    private PlayerInfoWindowFrame _playerInfoWindowFrame;
    private Window _gameOverWindow;
    private Window _goToMainMenuPrompt;

    private final float levelUpFadeTime = 1f;
    private final float levelUpDisplayTime = 1.5f;
    private float _levelUpTimer = 0;
    private Sprite _levelUpDisplay;

    private enum ScreenState
    {
        InventoryOpen, SelectingTileTarget,SelectingItemTarget, Playing, ShowingItemDescription, GameOver, GameWon
    }

    private ScreenState _currentScreenState = ScreenState.Playing;
    private final BitmapFont _font;

    public PlayScreen(String playerName)
    {
        this.playerName = playerName;
        _playerAction = new GameAction();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        mainCamera = new OrthographicCamera(w, h);
        minZoom = (5*DungeonMap.TileSize)/(w>h?h:w);
        mainCamera.zoom = (10*DungeonMap.TileSize)/(w>h?h:w);
        maxZoom = (15*DungeonMap.TileSize)/(w>h?h:w);

        mainCamera.setToOrtho(true, w, h);
        guiCamera = new OrthographicCamera(w, h);
        guiCamera.setToOrtho(true, w, h);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        _font = AssetManager.getFont("description");
        setupGuiElements();
        repositionGuiElements((int) w, (int) h);
        GameConsole.reset();

        InputMultiplexer im = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        im.addProcessor(gd);
        im.addProcessor(this);
        Gdx.input.setInputProcessor(im);
        gd.setLongPressSeconds(0.4f);

        createNewDungeon();
    }

    private void setupGuiElements()
    {
        _openInventoryButton = new Button(0,0,10,10, "Inventory", new Color(0.6f, 0.2f, 0, 1));
        _waitActionButton = new Button(0,0,10,10, "Wait", new Color(0.5f, 0.5f, 0.5f, 1));
        _searchFloorButton = new Button(0,0,10,10,"Search",new Color(0, 0.6f, 0.2f, 1));

        //Shown when pressing back or escape
        _goToMainMenuPrompt = new Window(0,0,10,10,Color.GRAY,2,Color.BLUE);
        _goToMainMenuPrompt.addButton("Cancel", 0, 0, 0.33f, 0.2f, Color.BLUE);
        _goToMainMenuPrompt.addButton("Go to main menu", 0, 0, 0.33f, 0.2f, Color.RED);

        //Shown when you lose or win
        _gameOverWindow = new Window(0,0,10,10,Color.GRAY, 2, Color.BLACK);
        _gameOverWindow.addButton("Main Menu", 0, 0, 0.33f, 0.2f, Color.RED);
        _gameOverWindow.addButton("High Scores", 0, 0, 0.33f, 0.2f, Color.BLUE);
        _gameOverWindow.addButton("Play Again", 0, 0, 0.33f, 0.2f, Color.GREEN);

        //Shows an item
        _selectedItemWindow = new SelectedItemWindow(0,0,10,10,Color.GRAY, 2, Color.GRAY);

        //Inventory
        _inventory = new Inventory(0,0,10,10, Color.GRAY, 2, Color.BLUE);

        //Info about the player
        _playerInfoWindowFrame = new PlayerInfoWindowFrame(0,0,10,10, new Color(0.3f, 0.3f, 0.3f, 0.5f), 2, new Color(0.4f, 0.4f, 0.4f, 0.5f));
        _playerInfoWindowFrame.show();

        TextureRegion levelUpRegion = new TextureRegion(AssetManager.getGuiTexture("levelUp"),387,124);
        levelUpRegion.flip(false,true);
        _levelUpDisplay = new Sprite(levelUpRegion);
        _levelUpDisplay.setSize(_levelUpDisplay.getWidth()*2,_levelUpDisplay.getHeight()*2);
        _levelUpDisplay.setX(Gdx.graphics.getWidth()/2-(_levelUpDisplay.getWidth()/2));
        _levelUpDisplay.setY(Gdx.graphics.getHeight()/2-(_levelUpDisplay.getHeight()/2));

    }

    private void repositionGuiElements(int width, int height)
    {
        int buttonWidth = (height / 5);
        int buttonHeight = (height / 5);

        _playerInfoWindowFrame.reposition(2, 2, width - 4, (int) (_font.getBounds("Sample").height * 2.5f));

        _inventory.reposition(width/2 - (2 * height / 3)/2, height - (2 * height / 3) - (height / 5) - 4, 2 * height / 3, 2 * height / 3);

        _selectedItemWindow.reposition(width / 2 - width / 4, height / 2 - height / 4, width / 2, height / 2);

        _gameOverWindow.reposition(width / 2 - width / 4, height / 2 - height / 4, width / 2, height / 2);
        _gameOverWindow.arrangeButtons(0.001f, 0.7f, 0.01f, 0, 3);

        _goToMainMenuPrompt.reposition(width / 2 - width / 4, height / 2 - height / 4, width / 2, height / 2);
        _goToMainMenuPrompt.arrangeButtons(0.1f, 0.7f, 0.1f, 0, 2);
        if(width<height)
        {
            GameConsole.setup(4, (height - Gdx.graphics.getHeight() / 8),Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 8);
            int buttonY = (int) (GameConsole.getPosition().y - ((buttonHeight / 2) * 1.1f));
            _waitActionButton.reposition(width / 2 - buttonWidth / 2, buttonY, buttonWidth, buttonHeight / 2);
            _openInventoryButton.reposition(_waitActionButton.getX()+buttonWidth,buttonY, buttonWidth, buttonHeight/2);
            _searchFloorButton.reposition(_waitActionButton.getX() - buttonWidth,buttonY, buttonWidth, buttonHeight / 2);
            _inventory.hideEquippedItems();
        }
        else
        {
            GameConsole.setup(4, (height - buttonHeight),Gdx.graphics.getWidth()*5/8, Gdx.graphics.getHeight() / 5);
            _openInventoryButton.reposition(width - buttonWidth, height - buttonHeight, buttonWidth, buttonHeight);
            _waitActionButton.reposition(_openInventoryButton.getX() - (int)(buttonWidth*1.1f), height - buttonHeight, buttonWidth, buttonHeight);
            _searchFloorButton.reposition(_waitActionButton.getX() - (int)(buttonWidth*1.1f), height - buttonHeight, buttonWidth, buttonHeight);
            _inventory.showEquippedItems();
        }
    }

    //Rendering
    @Override
    public void render(float v)
    {
        update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainCamera.update();
        renderWorld(batch);
        guiCamera.update();
        renderGUI(batch);
    }

    private void renderWorld(SpriteBatch batch)
    {
        batch.setProjectionMatrix(mainCamera.combined);
        batch.begin();
        _gameStateUpdater.drawGameState(batch);
        batch.end();
    }

    private void renderGUI(SpriteBatch batch)
    {
        batch.setProjectionMatrix(guiCamera.combined);
        shapeRenderer.setProjectionMatrix(guiCamera.combined);
        _openInventoryButton.draw(batch, shapeRenderer);
        _waitActionButton.draw(batch, shapeRenderer);
        _searchFloorButton.draw(batch, shapeRenderer);
        _inventory.draw(batch, shapeRenderer);
        _selectedItemWindow.draw(batch, shapeRenderer);
        _goToMainMenuPrompt.draw(batch, shapeRenderer);
        _playerInfoWindowFrame.drawProgressBars(shapeRenderer,_player);
        GameConsole.render(batch, shapeRenderer);
        if (_currentScreenState == ScreenState.GameOver)
        {
            _gameOverWindow.draw(batch, shapeRenderer);
        }


        batch.begin();
        _playerInfoWindowFrame.draw(batch,_player,_depth);
        if(_player.justLeveledUp())
        {

            _levelUpTimer+=Gdx.graphics.getDeltaTime();
            if(_levelUpTimer<levelUpDisplayTime)
            {
                _levelUpDisplay.draw(batch);

            }
            else if(_levelUpTimer<levelUpDisplayTime+levelUpFadeTime)
            {
                _levelUpDisplay.draw(batch,MathUtils.lerp(1,0,_levelUpTimer/(levelUpDisplayTime+levelUpFadeTime)));
            }
            else
            {
                _player.setLevelUpAsOver();
            }

        }

        if (_currentScreenState == ScreenState.GameOver)
        {
            _font.draw(batch, "Game Over", Gdx.graphics.getWidth() / 2 - _font.getBounds("Game Over").width/2, Gdx.graphics.getHeight() / 4 + 10);
            _font.draw(batch, "Score: "+((int) _player.calculateScore() * _depth), Gdx.graphics.getWidth() / 2 - 40, Gdx.graphics.getHeight() / 2);

        }
        else if (_currentScreenState == ScreenState.GameWon)
        {
            final String gameWonText = "Congratulations adventurer! - Your quest is over";
            _font.draw(batch, gameWonText, Gdx.graphics.getWidth() / 2 - (_font.getBounds(gameWonText).width / 2), Gdx.graphics.getHeight() / 4 + 10);
        }
        batch.end();
    }

    //Game state updating, restarting etc
    private void createNewDungeon()
    {
        if (_player == null)
        {
            _player = new Player(playerName);
            _inventory.reset(_player);
            _selectedItemWindow.reset(_player);
        }
        else
        {
            _depth++;
            if (_depth == 22)
            {
                _gameOverWindow.show();
                HighScoreIO.putScore(null, playerName, _depth, (int) _player.calculateScore() * _depth);
                _currentScreenState = ScreenState.GameWon;
                return;
            }
        }
        _currentDungeonMap = DungeonGenerator.GenerateCompleteDungeon(_depth);
        _currentDungeonMap.addPlayer(_player);
        _gameStateUpdater.setGameState( _player, _inventory, _currentDungeonMap);
    }



    private void restartGame()
    {
        _gameOverWindow.hide();
        _depth = 1;
        _player = null;
        createNewDungeon();
        _currentScreenState = ScreenState.Playing;
        GameConsole.reset();
    }

    private void update()
    {
        if (_player.isDead())
        {
            if (_currentScreenState != ScreenState.GameOver)
            {
                _currentScreenState = ScreenState.GameOver;
                _gameOverWindow.show();
                HighScoreIO.putScore(_player.getKilledBy(), playerName, _depth, (int) _player.calculateScore() * _depth);
            }
        }
        if(GameStateUpdater.isSelectingItemForScroll())
        {
            _inventory.show();
            _currentScreenState = ScreenState.SelectingItemTarget;
        }
        else if(GameStateUpdater.isSelectingTileForScroll())
        {
            _currentScreenState = ScreenState.SelectingTileTarget;
        }

        if(_currentScreenState == ScreenState.Playing)
        {
            _gameStateUpdater.updateGameState();
        }

        if(_previousPlayerPosition!=_player.getWorldPosition() && _gameStateUpdater.getCurrentAnimation().getType() != GameAction.Type.Attack)
        {
            _previousPlayerPosition = _player.getWorldPosition();
            mainCamera.position.x=_player.getWorldPosition().x+(DungeonMap.TileSize/2);
            mainCamera.position.y=_player.getWorldPosition().y+(DungeonMap.TileSize/2);
        }

        //Keyboard controls
        if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
        {
            if (_player.isMoving())
            {
                _player.clearNextActions();
            }
            _goToMainMenuPrompt.show();
        }
    }

    @Override
    public boolean keyDown(int keycode)
    {
        Tile newTile = null;
        switch (keycode)
        {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                newTile = _currentDungeonMap.getTouchedTile((int)(_player.getCurrentTile().getTilePosition().x-1),(int)(_player.getCurrentTile().getTilePosition().y));
                break;
            case Input.Keys.UP:
            case Input.Keys.W:
                newTile = _currentDungeonMap.getTouchedTile((int)(_player.getCurrentTile().getTilePosition().x),(int)(_player.getCurrentTile().getTilePosition().y-1));
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                newTile = _currentDungeonMap.getTouchedTile((int)(_player.getCurrentTile().getTilePosition().x+1),(int)(_player.getCurrentTile().getTilePosition().y));
                break;
            case Input.Keys.DOWN:
            case Input.Keys.S:
                newTile = _currentDungeonMap.getTouchedTile((int)(_player.getCurrentTile().getTilePosition().x),(int)(_player.getCurrentTile().getTilePosition().y+1));
                break;
            case Input.Keys.SPACE:
                if(_player.getCurrentTile().containsItem())
                {
                    if (_inventory.isFull())
                    {
                        GameConsole.addMessage("Can not pick up item, inventory is full");
                    }
                    else
                    {
                        _playerAction.setAction(_player, GameAction.Type.PickUp, _player.getCurrentTile(), null);
                        _player.clearQueueAndSetAction(_playerAction);
                    }
                }
                else if (_player.getCurrentTile().getType() == Tile.Types.StairCase)
                {
                    GameConsole.addMessage(playerName + " went down the stairs");
                    createNewDungeon();
                }
                break;
            case Input.Keys.PLUS:
                mainCamera.zoom=Math.max(minZoom, mainCamera.zoom - 0.1f);
                break;
            case Input.Keys.MINUS:
                mainCamera.zoom=Math.min(maxZoom,mainCamera.zoom+0.1f);
                break;
        }
        if(newTile != null && newTile.isWalkable())
        {
            if(newTile.getCharacter() != null)
            {
                if(_player.getEquippedWeapon()!= null && _player.getEquippedWeapon().getRangeType() == Weapon.RangeType.Throwable)
                {
                    _player.setThrowAction(_player.getEquippedWeapon(),newTile);
                }
                else
                {
                    _player.setAttackAction(newTile.getCharacter());
                }

            }
            else
            {
                _playerAction.setAction(_player, GameAction.Type.Move,newTile,null);
                _player.clearQueueAndSetAction(_playerAction);
            }
        }
        return true;
    }


    //Tap handling
    @Override
    public boolean tap(float x, float y, int count, int button)
    {
        if (_goToMainMenuPrompt.isOpen())
        {
            if (_goToMainMenuPrompt.isPressed("Go to main menu", x, y))
            {
                TheBrimstoneTowerGame.getGameInstance().setScreen(new MenuScreen());
            }
            else if (_goToMainMenuPrompt.isPressed("Cancel", x, y))
            {
                _goToMainMenuPrompt.hide();
            }
            return false;
        }

        switch (_currentScreenState)
        {
            case GameOver:
                gameOverStateTap(x, y);
                break;
            case InventoryOpen:
                inventoryOpenStateTap(x, y);
                break;
            case Playing:
                movingStateTap(x, y);
                break;
            case ShowingItemDescription:
                showingItemStateTap(x, y);
                break;
            case SelectingTileTarget:
                selectingTileTarget(x, y);
                break;
            case SelectingItemTarget:
                selectingItemTarget(x,y);
                break;
        }
        return true;
    }

    private void gameOverStateTap(float x, float y)
    {
        if (_gameOverWindow.getButton("Play Again").isTouched(x, y))
            restartGame();
        else if (_gameOverWindow.getButton("High Scores").isTouched(x, y))
            TheBrimstoneTowerGame.getGameInstance().setScreen(new HighScoreScreen());
        else if (_gameOverWindow.getButton("Main Menu").isTouched(x, y))
            TheBrimstoneTowerGame.getGameInstance().setScreen(new MenuScreen());
    }

    private void inventoryOpenStateTap(float x, float y)
    {
        _inventory.tap(x, y);
        if (_inventory.hasItemBeenSelected())
        {
            _inventory.hide();
            _selectedItemWindow.show(_inventory.retrieveItem());
            _currentScreenState = ScreenState.ShowingItemDescription;
        }
        else if (!_inventory.isOpen())
        {
            _currentScreenState = ScreenState.Playing;
        }
    }

    private void movingStateTap(float x, float y)
    {
        if (_openInventoryButton.isTouched(x, y))
        {
            _currentScreenState = ScreenState.InventoryOpen;
            _inventory.show();
        }
        else if (_waitActionButton.isTouched(x, y))
        {
            _playerAction.setAction(_player, GameAction.Type.Wait, _player.getCurrentTile(), null);
            _player.clearQueueAndSetAction(_playerAction);
        }
        else if (_searchFloorButton.isTouched(x, y))
        {
            _playerAction.setAction(_player, GameAction.Type.Search, _player.getCurrentTile(), null);
            _player.clearQueueAndSetAction(_playerAction);
        }
        else
        {
            if (_player.isMoving())
            {
                _player.clearNextActions();
                return;
            }
            Vector3 touchedPosition = new Vector3(x, y, 0);
            mainCamera.unproject(touchedPosition);
            Tile touchedTile = _currentDungeonMap.getTouchedTile(touchedPosition.x, touchedPosition.y);
            if (touchedTile != null)
            {
                if (_itemToThrow != null)
                {
                    _player.setThrowAction(_itemToThrow, touchedTile);
                }
                else if (touchedTile == _player.getCurrentTile())
                {
                    if (touchedTile.containsItem())
                    {
                        if (_inventory.isFull())
                        {
                            GameConsole.addMessage("Can not pick up item, inventory is full");
                        }
                        else
                        {
                            _playerAction.setAction(_player, GameAction.Type.PickUp, touchedTile, null);
                            _player.clearQueueAndSetAction(_playerAction);
                        }
                    }
                    else if (touchedTile.getType() == Tile.Types.StairCase)
                    {
                        GameConsole.addMessage(playerName+" went down the stairs");
                        createNewDungeon();
                    }
                }
                else if (touchedTile.getCharacter() != null)
                {
                    if(touchedTile.isAdjacent(_player.getCurrentTile()))
                    {
                        _player.setAttackAction(touchedTile.getCharacter());

                    }
                    else if(_player.getEquippedWeapon()!= null &&
                            _player.getEquippedWeapon().getRangeType() == Weapon.RangeType.Throwable)
                    {
                        _player.setThrowAction(_player.getEquippedWeapon(),touchedTile);
                    }
                }
                else
                {
                    ArrayList<Tile> playerPath = PathFinder.getPath(_player.getCurrentTile(), touchedTile);

                    if (playerPath != null)
                    {
                        _player.setMovementActions(playerPath);
                    }
                }
            }
        }
    }

    private void showingItemStateTap(float x, float y)
    {
        _selectedItemWindow.tap(x, y);
        if (!_selectedItemWindow.isOpen())
        {
            _currentScreenState = ScreenState.Playing;
        }
        else if (_selectedItemWindow.hasAction())
        {
            GameAction selectedAction = _selectedItemWindow.retrieveAction();

            if (selectedAction.getType() == GameAction.Type.Throw)
            {
                _itemToThrow = selectedAction.getTargetItem();
                _currentScreenState = ScreenState.SelectingTileTarget;
                _player.displayThrowRange();
                _selectedItemWindow.hide();
            }
            else
            {
                _player.clearQueueAndSetAction(selectedAction);
                _selectedItemWindow.hide();
                _currentScreenState = ScreenState.Playing;
            }
        }
    }

    private void selectingTileTarget(float x, float y)
    {
        Vector3 touchedPosition = new Vector3(x, y, 0);
        mainCamera.unproject(touchedPosition);
        Tile touchedTile = _currentDungeonMap.getTouchedTile(touchedPosition.x, touchedPosition.y);
        if (touchedTile != null && touchedTile.getLightAmount() != Tile.LightAmount.Non)
        {
            if(_itemToThrow!=null)
            {
                if(_player.getThrowRange()>=touchedTile.distanceTo(_player.getCurrentTile()))
                {
                    _player.setThrowAction(_itemToThrow, touchedTile);
                    _itemToThrow = null;
                }
                _player.hideThrowRange();
                _currentScreenState = ScreenState.Playing;
            }
            else if(GameStateUpdater.isSelectingTileForScroll())
            {
                Scroll scroll =_gameStateUpdater.getUsedScroll();
                scroll.useOnTile(touchedTile);
                _gameStateUpdater.clearUsedScroll();
                GameStateUpdater.resumeGameStateUpdating();
                _currentScreenState = ScreenState.Playing;
            }
        }
    }

    private void selectingItemTarget(float x, float y)
    {
        _inventory.tap(x, y);
        if (!_inventory.isOpen())
        {
            _player.clearNextActions();
            _gameStateUpdater.clearUsedScroll();
            GameStateUpdater.resumeGameStateUpdating();
            _currentScreenState = ScreenState.Playing;
            //Put warning here
        }
        else
        {
            Item item = _inventory.retrieveItem();
            Scroll usedScroll = _gameStateUpdater.getUsedScroll();
            usedScroll.useOnItem(item);
            if(usedScroll.isUsed())
            {
                GameStateUpdater.inventory.hide();
                _gameStateUpdater.clearUsedScroll();
                GameStateUpdater.resumeGameStateUpdating();
                _currentScreenState = ScreenState.Playing;
            }
        }
    }

    @Override
    public boolean longPress(float x, float y)
    {
        Vector3 unprojectedPos = mainCamera.unproject(new Vector3(x,y,0));
        Tile pressedTile = _currentDungeonMap.getTouchedTile(unprojectedPos.x,unprojectedPos.y);
        if(pressedTile!= null && !pressedTile.isEmpty())
        {
            _gameStateUpdater.hideAttackRanges();
            pressedTile.getCharacter().displayAttackRange();
        }
        return true;
    }


    //Camera control
    @Override
    public boolean pan(float x, float y, float dx, float dy)
    {
        mainCamera.translate(-dx*mainCamera.zoom,-dy*mainCamera.zoom);
        mainCamera.update();
        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance)
    {
        final float zoomAmount = (maxZoom-minZoom)/80;
        float ratio = (initialDistance / distance);
        float newZoom = MathUtils.clamp(mainCamera.zoom * ratio, mainCamera.zoom - zoomAmount, mainCamera.zoom + zoomAmount);
        newZoom = MathUtils.clamp(newZoom, minZoom, maxZoom);

        //Clamp range and set zoom
        mainCamera.zoom =newZoom;

        return true;
    }
    @Override
    public boolean scrolled(int amount)
    {
        float zoomChange = ((float)amount)/10;
        mainCamera.zoom=MathUtils.clamp(mainCamera.zoom + zoomChange,minZoom,maxZoom );
        return true;
    }
    @Override
    public void dispose()
    {
        batch.dispose();
        shapeRenderer.dispose();
        AssetManager.disposeAll();
    }

    @Override
    public void resize(int w, int h)
    {
        repositionGuiElements(w, h);
    }


    //NOT CURRENTLY USED

    @Override
    public boolean touchDown(float x, float y, int i, int i2)
    {
        return false;
    }

    @Override
    public boolean fling(float v, float v2, int i)
    {
        return false;
    }


    @Override
    public boolean panStop(float v, float v2, int i, int i2)
    {
        return true;
    }


    @Override
    public boolean pinch(Vector2 vector2, Vector2 vector22, Vector2 vector23, Vector2 vector24)
    {
        return false;
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
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }


}


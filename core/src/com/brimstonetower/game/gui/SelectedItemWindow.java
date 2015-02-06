package com.brimstonetower.game.gui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.helpers.GameAction;
import com.brimstonetower.game.gameobjects.items.Armor;
import com.brimstonetower.game.gameobjects.items.Item;
import com.brimstonetower.game.gameobjects.items.Weapon;

public class SelectedItemWindow extends Window
{
    private GameAction _selectedAction = new GameAction();

    public boolean hasAction()
    {
        return _selectedAction != null;
    }

    public GameAction retrieveAction()
    {
        return _selectedAction;
    }

    private Item _selectedItem;


    private boolean _isInitialized = false;
    private Player _player;
    private BitmapFont _descriptionFont;

    public SelectedItemWindow(int x, int y, int width, int height, Color color, int frameSize, Color frameColor)
    {
        super(x, y, width, height, color, frameSize, frameColor);
        addButton("Use", 0.10f, 0.85f, 0.25f, 0.10f, Color.BLUE);
        addButton("Equip", 0.10f, 0.85f, 0.25f, 0.10f, Color.BLUE);
        addButton("Throw", 0.40f, 0.85f, 0.25f, 0.10f, Color.BLUE);
        addButton("Drop", 0.70f, 0.85f, 0.25f, 0.10f, Color.BLUE);
        hideButton("Equip");
        _selectedAction = new GameAction();
        _descriptionFont = AssetManager.getFont("description");
        _isInitialized=true;
    }

    public void reset(Player player)
    {
        _player = player;
    }

    @Override
    public void reposition(int x, int y, int width, int height)
    {
        if(_isInitialized)
        {
            super.reposition(x, y, width, height);
            getButton("Use").reposition(x+(int) (width * 0.10f), y+(int) (height * 0.85f), (int) (width * 0.25f), (int) (height * 0.10f));
            getButton("Equip").reposition(x+(int) (width * 0.10f),y+ (int) (height * 0.85f), (int) (width * 0.25f), (int) (height * 0.10f));
            getButton("Throw").reposition(x+(int) (width * 0.40f),y+ (int) (height * 0.85f), (int) (width * 0.25f), (int) (height * 0.10f));
            getButton("Drop").reposition(x+(int) (width * 0.7f),y+ (int) (height * 0.85f), (int) (width * 0.25f), (int) (height * 0.10f));
        }
    }

    public void tap(float x, float y)
    {
        if (_windowRectangle.contains(x, y))
        {
            if (isPressed("Drop", x, y))
            {
                _selectedAction.setAction(_player, GameAction.Type.Drop, _player.getCurrentTile(), _selectedItem);
            }
            else if (isPressed("Throw", x, y))
            {
                _selectedAction.setAction(null, GameAction.Type.Throw, null, _selectedItem);
            }
            else if (_selectedItem instanceof Armor || _selectedItem instanceof Weapon)
            {
                if (isPressed("Equip", x, y))
                {
                    if (_player.isEquipped(_selectedItem))
                    {
                        if (_selectedItem.hasCurse())
                        {
                            GameConsole.addMessage("Some magical energy prevents the item from being unequipped");
                        }
                        else
                        {
                            _selectedAction.setAction(_player, GameAction.Type.Unequip, _player.getCurrentTile(), _selectedItem);
                        }
                    }
                    else
                    {
                        _selectedAction.setAction(_player, GameAction.Type.Equip, _player.getCurrentTile(), _selectedItem);
                    }

                }
            }
            else if (isPressed("Use", x, y))
            {
                _selectedAction.setAction(_player, GameAction.Type.Use, _player.getCurrentTile(), _selectedItem);
            }
        }
        else
        {
            hide();
        }
    }

    public void show(Item item)
    {
        _selectedItem = item;
        if (item instanceof Armor)
        {
            showButton("Equip");
            hideButton("Use");
        }
        else if (item instanceof Weapon)
        {
            if (((Weapon) item).isRanged())
            {
                hideButton("Equip");
            }
            else
            {
                showButton("Equip");
            }
            hideButton("Use");
        }
        else
        {
            showButton("Use");
            hideButton("Equip");
        }
        super.show();
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer)
    {
        if (_isOpen)
        {
            super.draw(batch, shapeRenderer);
            batch.begin();
            _descriptionFont.draw(batch, _selectedItem.getName(), _windowRectangle.x + 5, _windowRectangle.y + 5);
            _selectedItem.draw(batch, _windowRectangle.x + (_windowRectangle.width / 2) - 32, _windowRectangle.y + _descriptionFont.getBounds("Height").height*2f,2);
            _descriptionFont.drawWrapped(batch, _selectedItem.getDescription(), _windowRectangle.x + 5,  _windowRectangle.y + _descriptionFont.getBounds("Height").height*10f, _windowRectangle.width - 10);
            batch.end();
        }
    }
}

package com.brimstonetower.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.gameobjects.items.*;
import com.brimstonetower.game.managers.ItemManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Inventory extends Window
{
    private final ArrayList<Item> _items = new ArrayList<Item>();
    private Item _selectedItem;

    public boolean hasItemBeenSelected()
    {
        return _selectedItem != null;
    }

    public Item retrieveItem()
    {
        Item toReturn = _selectedItem;
        _selectedItem = null;
        return toReturn;
    }

    private boolean _showEquippedItems = false;
    public void showEquippedItems()
    {
        _showEquippedItems=true;
    }
    public void hideEquippedItems()
    {
        _showEquippedItems=false;
    }

    private Player _player;
    private final int maxItems = 16;
    private final BitmapFont _descriptionFont;

    public boolean isFull()
    {
        return _items.size() == maxItems;
    }

    private HashMap<Button, Item> _itemButtonMap = new HashMap<Button, Item>();
    private Armor _equippedArmor;
    private Vector2 _armorPosition = new Vector2();
    private Weapon _equippedWeapon;
    private Vector2 _weaponPosition = new Vector2();

    public Inventory(int x, int y, int width, int height, Color color, int frameSize, Color frameColor)
    {
        super(x, y, width, height, color, frameSize, frameColor);
        _descriptionFont = AssetManager.getFont("description");

    }

    public void reset(Player player)
    {
        super.reset();
        _items.clear();
        _itemButtonMap.clear();
        _player = player;
        _equippedWeapon = null;
        _equippedArmor = null;
        if (_player.getEquippedWeapon() != null)
        {
            addItem(_player.getEquippedWeapon());
            equip(_player.getEquippedWeapon());
        }
        if (_player.getEquippedArmor() != null)
        {
            addItem(_player.getEquippedArmor());
            equip(_player.getEquippedArmor());
        }
    }

    public void addItem(Item item)
    {
        if (ItemManager.isIdentified(item))
        {
            item.identify();
        }

        if (item instanceof Weapon && ((Weapon) item).isRanged())
        {
            for (Item i : _items)
            {
                if (i.isIdentical(item))
                {
                    ((Weapon)i).addAmmo(((Weapon) item).getAmmoCount());
                    return;
                }
            }
        }
        _items.add(item);
        String itemId = String.valueOf(item.getUniqueId());
        Button itemButton = addButton(itemId, item.getTextureRegion());
        _itemButtonMap.put(itemButton, item);
        arrangeButtons(0, 0, 8, 8, 4);
        repositionESigns();
    }

    public void removeItem(Item item)
    {
        _items.remove(item);
        removeButton(String.valueOf(item.getUniqueId()));
        _itemButtonMap.remove(item);
        arrangeButtons(0, 0, 8, 8, 4);
        if (item == _equippedArmor || item == _equippedWeapon)
        {
            unequip(item);
        }
        repositionESigns();
    }

    public void removeThrownItem(Item item)
    {

        if (item instanceof Weapon && ((Weapon) item).isRanged())
        {
            ((Weapon) item).decreaseRangedAmmo();
            if (((Weapon) item).getAmmoCount() == 0)
            {
                removeItem(item);
            }
        }
        else
        {
            removeItem(item);
        }
    }

    public void repositionESigns()
    {
        if (_equippedWeapon != null)
        {
            Button itemButton = getButton(String.valueOf(_equippedWeapon.getUniqueId()));
            _weaponPosition.x=itemButton.getX();
            _weaponPosition.y=itemButton.getY()+itemButton.getHeight()-_descriptionFont.getBounds("E").height;
        }
        if (_equippedArmor != null)
        {
            Button itemButton = getButton(String.valueOf(_equippedArmor.getUniqueId()));
            _armorPosition.x=itemButton.getX();
            _armorPosition.y = itemButton.getY()+itemButton.getHeight()-_descriptionFont.getBounds("E").height;
        }
    }

    public void equip(Item item)
    {
        if (item instanceof Armor)
        {
            if(_equippedArmor == null || (_equippedArmor!=null &&!_equippedArmor.hasCurse()))
            {
                _equippedArmor = (Armor) item;
                if(_equippedArmor.hasCurse())
                {
                    GameConsole.addMessage("The armor tightens uncomfortably to your body");
                    GameConsole.addMessage("The armor is cursed!");
                    _equippedArmor.showCurse();
                    getButton(String.valueOf(item.getUniqueId())).setColor(Color.PINK);
                }
            }
            else
            {
                GameConsole.addMessage("The armor you wear is stuck");
            }
        }
        else
        {
            if(_equippedWeapon == null || (_equippedWeapon!=null &&!_equippedWeapon.hasCurse()))
            {
                _equippedWeapon = (Weapon) item;
                if (_equippedWeapon.hasCurse())
                {
                    GameConsole.addMessage("An invisible force makes you unable let go of the weapon");
                    GameConsole.addMessage("The weapon is cursed!");
                    _equippedWeapon.showCurse();
                    getButton(String.valueOf(item.getUniqueId())).setColor(Color.PINK);
                }
            }
            else
            {
                GameConsole.addMessage("You are unable to let go of your weapon");
            }
        }
        repositionESigns();
    }

    public void unequip(Item item)
    {
        if (_equippedArmor == item)
        {
            _equippedArmor = null;
        }
        else if (_equippedWeapon == item)
        {
            _equippedWeapon = null;
        }
        else
        {
            Gdx.app.log("Inventory", "Item was not equipped");
        }
    }

    public void identifyItems(Item item)
    {
        for (Item i : _items)
        {
            if (i.isIdentical(item))
            {
                i.identify();
            }
        }
        if (!ItemManager.isIdentified(item))
        {
            ItemManager.identifyItem(item);
        }

    }

    public void removeCurses()
    {
        for (Item item : _items)
        {
            if(item.hasCurse())
            {
                item.removeCurse();
                Button itemButton = getButton(String.valueOf(item.getUniqueId()));
                itemButton.setColor(Color.WHITE);
            }
        }
    }

    public void step()
    {
        if (_equippedArmor != null)
        {
            _equippedArmor.step();
        }
        if (_equippedWeapon != null)
        {
            _equippedWeapon.step();
        }
    }

    public void tap(float x, float y)
    {
        if (_isOpen)
        {
            if (!_windowRectangle.contains(x, y))
            {
                _isOpen = false;
            }
            else
            {
                Button clickedButton = getClickedButton(x, y);
                if (clickedButton != null)
                {
                    _selectedItem = _itemButtonMap.get(clickedButton);
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer)
    {
        super.draw(batch, shapeRenderer);
        batch.begin();
        if (_isOpen)
        {
            batch.setColor(Color.GREEN);
            if (_equippedWeapon != null)
            {
                _descriptionFont.draw(batch, "E", _weaponPosition.x, _weaponPosition.y);
            }
            if (_equippedArmor != null)
            {
                _descriptionFont.draw(batch, "E", _armorPosition.x, _armorPosition.y);
            }
            batch.setColor(Color.WHITE);
        }
        if(_showEquippedItems)
        {
            float width = Gdx.graphics.getWidth();
            float height = Gdx.graphics.getHeight();
            if (_equippedWeapon != null)
            {
                _equippedWeapon.draw(batch, width - (height / 4) - 4, 4, height / 400);
            }
            if (_equippedArmor != null)
            {
                _equippedArmor.draw(batch, width - (height / 8) - 4, 4, height / 400);
            }
        }
        batch.end();
    }
}

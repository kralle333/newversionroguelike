package com.brimstonetower.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.gameobjects.equipment.Armor;
import com.brimstonetower.game.gameobjects.equipment.Weapon;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.managers.ItemManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        if (_player.getEquippedWeapon() != null)
        {
            addItem(_player.getEquippedWeapon());
        }
        if (_player.getEquippedArmor() != null)
        {
            addItem(_player.getEquippedArmor());
        }
    }

    public void addItem(Item item)
    {
        if (ItemManager.isIdentified(item))
        {
            item.identify();
        }

        if (item instanceof Weapon && ((Weapon) item).getRangeType() == Weapon.RangeType.Throwable)
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
    }

    public void removeItem(Item item)
    {
        _items.remove(item);
        _itemButtonMap.remove(getButton(String.valueOf(item.getUniqueId())));
        removeButton(String.valueOf(item.getUniqueId()));

        arrangeButtons(0, 0, 8, 8, 4);
        if (item == _player.getEquippedArmor() || item == _player.getEquippedWeapon())
        {
            unequip(item);
        }
    }

    public void removeThrownItem(Item item)
    {

        if (item instanceof Weapon && ((Weapon) item).getRangeType() == Weapon.RangeType.Throwable)
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

    public void equip(Item item)
    {

        if (item instanceof Armor)
        {
            Armor equippedArmor =_player.getEquippedArmor();
            if(equippedArmor == null || (equippedArmor!=null &&!equippedArmor.hasCurse()))
            {
                _player.equip(item);
                if(item.hasCurse())
                {
                    GameConsole.addMessage("The armor tightens uncomfortably to your body");
                    GameConsole.addMessage("The armor is cursed!");
                    item.showCurse();
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
            Weapon equippedWeapon =_player.getEquippedWeapon();
            if(equippedWeapon == null || (equippedWeapon!=null &&!equippedWeapon.hasCurse()))
            {
                _player.equip(item);
                if (item.hasCurse())
                {
                    GameConsole.addMessage("An invisible force makes you unable let go of the weapon");
                    GameConsole.addMessage("The weapon is cursed!");
                    item.showCurse();
                    getButton(String.valueOf(item.getUniqueId())).setColor(Color.PINK);
                }
            }
            else
            {
                GameConsole.addMessage("You are unable to let go of your weapon");
            }
        }
    }

    public void unequip(Item item)
    {
        _player.unequip(item);
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
        if (_player.getEquippedArmor() != null)
        {
            _player.getEquippedArmor().step();
        }
        if (_player.getEquippedWeapon() != null)
        {
            _player.getEquippedWeapon().step();
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

        if (_isOpen)
        {
            super.drawFrame(shapeRenderer);
            for(Map.Entry<Button,Item> buttonItemEntry : _itemButtonMap.entrySet() )
            {
                Button button = buttonItemEntry.getKey();
                button.draw(batch,shapeRenderer);


                batch.begin();
                Item item = buttonItemEntry.getValue();
                if(_player.getEquippedWeapon() == item || _player.getEquippedArmor() == item)
                {
                    batch.setColor(Color.GREEN);
                    _descriptionFont.draw(batch, "E",button.getX(), button.getY()+button.getHeight()-_descriptionFont.getBounds("E").height);
                    batch.setColor(Color.WHITE);
                }
                if(item.isStackable() && item instanceof Weapon)
                {
                    batch.setColor(Color.BLUE);
                    _descriptionFont.draw(batch, String.valueOf(((Weapon) item).getAmmoCount()), button.getX()+button.getWidth()-_descriptionFont.getBounds("10").width, button.getY() + button.getHeight()-_descriptionFont.getBounds("10").height);
                    batch.setColor(Color.WHITE);
                }
                batch.end();
            }
        }
        if(_showEquippedItems)
        {
            batch.begin();
            float width = Gdx.graphics.getWidth();
            float height = Gdx.graphics.getHeight();
            if (_player.getEquippedWeapon() != null)
            {
                _player.getEquippedWeapon().draw(batch, width - (height / 4) - 4, 4, height / 400);
            }
            if (_player.getEquippedArmor() != null)
            {
                _player.getEquippedArmor().draw(batch, width - (height / 8) - 4, 4, height / 400);
            }
            batch.end();
        }

    }
}

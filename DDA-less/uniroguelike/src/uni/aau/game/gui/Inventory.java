package uni.aau.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import uni.aau.game.gameobjects.Player;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.items.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Inventory extends Window
{
    private final ArrayList<Item> _items = new ArrayList<Item>();
    public ArrayList<Item> GetItems(){return _items;}
    private Item _selectedItem;
    public boolean hasItemBeenSelected(){return _selectedItem!= null;}
    public Item retrieveItem()
    {
        Item toReturn = _selectedItem;
        _selectedItem=null;
        return toReturn;
    }

    private Player _player;
    private final int maxItems = 16;
    private final BitmapFont _descriptionFont;
    public boolean isFull(){return _items.size()==maxItems;}
    private HashMap<Button,Item> _itemButtonMap = new HashMap< Button,Item>();
    private Armor _equippedArmor;
    private Vector2 _armorPosition;
    private Weapon _equippedWeapon;
    private Vector2 _weaponPosition;

    public Inventory(int x, int y, int width, int height,Color color, int frameSize,Color frameColor)
    {
        super(x,y,width,height,color,frameSize,frameColor);
        _descriptionFont = AssetManager.getFont("description");

    }
    public void reset(Player player)
    {
        super.reset();
        _player = player;
        _equippedArmor = null;
        _equippedWeapon = null;
        _items.clear();
        _itemButtonMap.clear();
    }
    public float getPotionPotency()
    {
        float averagePotency = 0;
        int count = 0;
        for(Item item : _items)
        {
            if(item instanceof Potion)
            {
                count++;
                averagePotency+=((Potion) item).getPotency();
            }
        }
        if(count == 0)
        {
            return 0;
        }
        return averagePotency/count;
    }
    public int getItemTypeCount(Class<?> i)
    {
        int itemTypeInInventory = 0;
        for(Item item : _items)
        {
            if(i.isInstance(item))
            {
                itemTypeInInventory++;
            }
        }
        return itemTypeInInventory;
    }
    public void addItem(Item item)
    {
        if(ItemManager.isIdentified(item))
        {
            item.identify();
        }
        GameConsole.addMessage("Picked up item " + item.getName());

        if(item instanceof Weapon && ((Weapon) item).isRanged())
        {
            for(Item i : _items)
            {
                if(i instanceof Weapon)
                {
                    Weapon castWeapon = (Weapon)i;
                    if(castWeapon.isRanged() && castWeapon.getName().contains(item.getName().subSequence(0,5)))
                    {
                        castWeapon.addAmmo(((Weapon) item).getAmmoCount());
                        return;
                    }
                }

            }
        }
        _items.add(item);
        String itemId = String.valueOf(item.getId());
        Button itemButton = addButton(itemId,item.getTextureRegion());

        if(item instanceof Potion)
        {
            itemButton.setColor(((Potion) item).getColor());
        }
        _itemButtonMap.put(itemButton,item);
        arrangeButtons(0,0,8,8,4);
        repositionESigns();
    }
    public void removeItem(Item item)
    {
        _items.remove(item);
        //Might make a problem later
        removeButton(String.valueOf(item.getId()));
        _itemButtonMap.remove(item);
        arrangeButtons(0,0,8,8,4);
        if(item == _equippedArmor ||item == _equippedWeapon)
        {
            unequip(item);
        }
        repositionESigns();
    }
    public void repositionESigns()
    {
        if(_equippedWeapon != null)
        {
            equip(_equippedWeapon);
        }
        if(_equippedArmor != null)
        {
            equip(_equippedArmor);
        }
    }
    public void equip(Item item)
    {
        Button itemButton = getButton(String.valueOf(item.getId()));
        Vector2 ePosition = new Vector2(itemButton.getX()+itemButton.getWidth()/2,itemButton.getY()+itemButton.getHeight()/2);
        if(item instanceof Armor)
        {
            _equippedArmor = (Armor) item;
            _armorPosition = ePosition;
        }
        else
        {
            _equippedWeapon = (Weapon)item;
            _weaponPosition = ePosition;
        }

    }
    public void unequip(Item item)
    {
        if(_equippedArmor == item)
        {
            _equippedArmor=null;
        }
        else if(_equippedWeapon == item)
        {
            _equippedWeapon = null;
        }
        else
        {
            Gdx.app.log("Inventory","Item was not equipped");
        }
    }

    public void identifyItems(Item item)
    {
        for(Item i : _items)
        {
            if(i.isIdentical(item))
            {
                i.identify();
            }
        }
        if(!ItemManager.isIdentified(item))
        {
            ItemManager.identifyItem(item);
        }

    }
    public void removeCurses()
    {
        for(Item item :_items)
        {
            item.removeCurse();
        }
    }
    public void step()
    {
        if(_equippedArmor!= null)
        {
            _equippedArmor.step();
        }
        if(_equippedWeapon!=null)
        {
            _equippedWeapon.step();
        }
    }

    public void tap(float x,float y)
    {
        if(_isOpen)
        {
            if(!_windowRectangle.contains(x,y))
            {
                _isOpen = false;
            }
            else
            {
                Button clickedButton = getClickedButton(x,y);
                if(clickedButton != null)
                {
                    _selectedItem=_itemButtonMap.get(clickedButton);
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer)
    {
        super.draw(batch, shapeRenderer);
        if(_isOpen)
        {
            batch.begin();
            if(_equippedWeapon != null)
            {
                _descriptionFont.draw(batch,"E",_weaponPosition.x,_weaponPosition.y);
            }
            if(_equippedArmor != null)
            {
                _descriptionFont.draw(batch,"E",_armorPosition.x,_armorPosition.y);
            }
            batch.end();
        }
        batch.begin();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        if(_equippedWeapon != null)
        {
            _equippedWeapon.draw(batch, width - ( height / 4) - 4, 4,2);
        }
        if(_equippedArmor != null)
        {
            _equippedArmor.draw(batch, width - (height / 8) - 4, 4,2);
        }
        batch.end();
    }
}

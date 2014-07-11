package uni.aau.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import uni.aau.game.gui.GameConsole;
import uni.aau.game.helpers.GameAction;
import uni.aau.game.items.Armor;
import uni.aau.game.items.Item;
import uni.aau.game.items.Weapon;

import java.util.ArrayList;

public class Character
{
    protected float maxHp;
    protected float currentHp;
    public int getMaxHitPoints(){return (int)maxHp;}
    public int getHitpoints(){return (int)currentHp;}
    protected float maxStr;
    protected float currentStr;
    public int getMaxStr(){return (int)maxStr;}
    public int getCurrentStr(){return (int)currentStr;}
    protected int level = 1;
    public int getLevel(){return level;}
    protected int experience = 0;
    protected int experienceToNextLevel = 10;
    public int getExperience(){return experience;}
    public int getExperienceToNextLevel(){return experienceToNextLevel;}
    public enum StatusEffect{Healthy, Poisoned, Paralysed}
    private StatusEffect _currentStatusEffect;
    public StatusEffect getCurrentStatusEffect(){return _currentStatusEffect;}
    private int _statusEffectTimer;

    protected  boolean _isDead = false;
    public boolean isDead(){return _isDead;}

    protected Armor _equippedArmor;
    public Armor getEquippedArmor(){return _equippedArmor;}
    public int getArmorDefense(){return _equippedArmor==null?0:_equippedArmor.getIdentifiedDefense();}
    private Weapon _equippedWeapon;
    public Weapon getEquippedWeapon(){return _equippedWeapon;}
    public int getWeaponAttack(){return _equippedWeapon==null?0:_equippedWeapon.getIdentifiedAttackPower();}

    protected Vector2 _position;
    public Vector2 getPosition(){return _position;}
    protected Tile currentTile;
    public Tile getCurrentTile(){return currentTile;}

    protected ArrayList<GameAction> actionQueue = new ArrayList<GameAction>();
    private String _name;
    public String getName(){return _name;}

    public Character(String name,int str, int hp)
    {
        _name = name;
        currentStr = str;
        maxStr=str;
        currentHp = hp;
        maxHp=hp;
    }


    public void equip(Item item)
    {
        if(item instanceof Armor)
        {
            _equippedArmor = (Armor)item;
        }
        else if(item instanceof Weapon)
        {
            _equippedWeapon = (Weapon)item;
        }
        else
        {
            Gdx.app.log("Character","Can not equip an item which is not armor or weapon");
        }
        GameConsole.addMessage(_name+" equipped "+item.getName());
    }
    public void unequip(Item item)
    {
        if(_equippedArmor == item)
        {
            _equippedArmor = null;
        }
        else if(_equippedWeapon == item)
        {
            _equippedWeapon = null;
        }
        else
        {
            Gdx.app.log("Character","Cannot unequip this item as it is not equipped "+item.getName());
        }
    }
    public boolean isEquipped(Item item)
    {
        if(item == _equippedArmor || item == _equippedWeapon)
        {
            return true;
        }
        return false;
    }
    public void setMovementActions(ArrayList<Tile> path)
    {
        for(Tile tile : path)
        {
            actionQueue.add(new GameAction(this, GameAction.Type.Move,tile,null));
        }
    }
    public void setThrowAction(Item selectedItem, Tile touchedTile)
    {
        actionQueue.clear();
        actionQueue.add(new GameAction(this, GameAction.Type.Throw,touchedTile,selectedItem));
    }
    public void setAttackAction(Tile enemyTile)
    {
        actionQueue.add(new GameAction(this, GameAction.Type.Attack,enemyTile,null));
    }

    public void clearQueueAndSetAction(GameAction action)
    {
        actionQueue.clear();
        actionQueue.add(action);
    }
    public void clearQueue()
    {
        actionQueue.clear();
    }

    public GameAction getNextAction()
    {
        if(actionQueue.size()>0)
        {
            return actionQueue.remove(0);
        }

        return null;
    }

    public int getAttackPower()
    {
        return _equippedWeapon!=null?_equippedWeapon.getIdentifiedAttackPower()+getCurrentStr():getCurrentStr();
    }
    public void giveStatusEffect(StatusEffect effect, int turns)
    {
        _currentStatusEffect = effect;
        _statusEffectTimer = turns;
    }
    public void decreaseStatusEffectTimer()
    {
        _statusEffectTimer--;
        if(_statusEffectTimer<=0)
        {
            _currentStatusEffect=StatusEffect.Healthy;
        }
    }

    public void placeOnTile(Tile tile)
    {
        currentTile = tile;
        currentTile.setCharacter(this);
        _position = new Vector2(currentTile.getX()* DungeonMap.TileSize,currentTile.getY()*DungeonMap.TileSize);
    }

    public void moveTo(Tile tile)
    {
        if(tile.getCharacter() ==null)
        {
            currentTile.removeCharacter();
            placeOnTile(tile);
        }
    }


    public void heal(int hitpoints)
    {
        currentHp+=hitpoints;
        if(currentHp>maxHp)
        {
            currentHp=maxHp;
            GameConsole.addMessage(_name+" was fully healed");
        }
        else
        {
            GameConsole.addMessage(_name+" was healed "+hitpoints+" hitpoints");
        }
    }
    public void damage(int damage)
    {
        //Change into armor def
        int receivedDamage = damage-(_equippedArmor!=null?_equippedArmor.getIdentifiedDefense():0);
        if(receivedDamage<=0)
        {
            receivedDamage=1;
        }
        currentHp-=receivedDamage;
        GameConsole.addMessage(_name + " received " + receivedDamage + " damage");
        if(currentHp<=0)
        {
            kill();
        }
    }


    public void kill()
    {
        GameConsole.addMessage(_name + " was killed");
        _isDead= true;
        currentTile.removeCharacter();
        //Inherit
    }


}

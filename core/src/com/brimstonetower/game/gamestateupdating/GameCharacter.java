package com.brimstonetower.game.gamestateupdating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.BreakableObject;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.gameobjects.equipment.Armor;
import com.brimstonetower.game.gameobjects.equipment.Weapon;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.map.Tile;

import java.util.ArrayList;

public class GameCharacter
{

    private String _name;
    public String getName()
    {
        return _name;
    }

    protected int maxHp;
    protected int currentHp;

    public int getMaxHitPoints()
    {
        return maxHp;
    }
    public int getHitpoints()
    {
        return currentHp;
    }

    protected int maxStr;
    protected int currentStr;

    public int getMaxStr()
    {
        return maxStr;
    }
    public int getCurrentStr()
    {
        return currentStr;
    }

    protected  int maxAgility;
    protected int currentAgility;
    public int getMaxAgility(){return maxAgility;}
    public int getCurrentAgility()
    {
        return currentAgility;
    }

    //Only applied from effects, use armor or weapon otherwise!
    protected int defense = 0;

    protected int level = 1;
    public int getLevel()
    {
        return level;
    }

    protected int experience = 0;
    protected int experienceToNextLevel = 10;
    public int getExperience()
    {
        return experience;
    }
    public int getExperienceToNextLevel()
    {
        return experienceToNextLevel;
    }

    private ArrayList<Effect> _currentEffects = new ArrayList<Effect>();

    protected boolean _isDead = false;
    public boolean isDead()
    {
        return _isDead;
    }

    //Equipment
    protected Armor _equippedArmor;
    public Armor getEquippedArmor()
    {
        return _equippedArmor;
    }
    public int getArmorDefense()
    {
        return _equippedArmor == null ? 0 : _equippedArmor.getIdentifiedDefense();
    }
    protected Weapon _equippedWeapon;
    public Weapon getEquippedWeapon() { return _equippedWeapon;}
    public int getMaxAttackPower()
    {
        return _equippedWeapon != null ? _equippedWeapon.getIdentifiedMaxDamage() + getCurrentStr() : getCurrentStr();
    }

    public int getMinAttackRange(){return _equippedWeapon!=null?_equippedWeapon.getMinRange():1;}
    public int getMaxAttackRange(){return _equippedWeapon!=null?_equippedWeapon.getMaxRange():1;}

    protected Vector2 _worldPosition;
    public Vector2 getWorldPosition()
    {
        return _worldPosition;
    }
    public void setPosition(Vector2 newPosition){
        _worldPosition =newPosition;}

    protected Tile currentTile;
    public Tile getCurrentTile()
    {
        return currentTile;
    }


    public int getCostOfNextAction()
    {
        if (isMoving())
        {
            return movementQueue.get(0).getCost();
        }
        return nextAction == null ? 0 : nextAction.getCost();
    }

    public void clearCurrentAction()
    {
        nextAction.setAsEmpty();
    }
    protected GameAction nextAction = new GameAction();
    protected ArrayList<GameAction> movementQueue = new ArrayList<GameAction>();

    public boolean isMoving()
    {
        return movementQueue.size() > 0 && movementQueue.get(0) != null && movementQueue.get(0).getType() == GameAction.Type.Move;
    }

    protected TextureRegion _texture;
    private int _dealtDamage = 0;
    public int getDealtDamage(){return _dealtDamage;}
    public enum HitState{Miss,Normal,Critical,Blocked};
    private HitState _lastHitState;
    public HitState getLastHitState()
    {
        return _lastHitState;
    }

    protected boolean _displayAttackRange = false;
    public void displayAttackRange(){_displayAttackRange=true;}
    public void hideAttackRange(){_displayAttackRange = false;}

    public GameCharacter(String name, int str, int agility, int hp,TextureRegion texture)
    {
        _name = name;
        currentStr = str;
        maxStr = str;
        currentHp = hp;
        maxHp = hp;
        currentAgility = agility;
        maxAgility = agility;
        _texture= texture;
        nextAction.setAction(this, GameAction.Type.Empty, null, null);
    }


    public void equip(Item item)
    {
        if (item instanceof Armor)
        {
            _equippedArmor = (Armor) item;
        }
        else if (item instanceof Weapon)
        {
            _equippedWeapon = (Weapon) item;
        }
        else
        {
            Gdx.app.log("Character", "Can not equip an item which is not armor or weapon");
        }
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
            Gdx.app.log("Character", "Cannot unequip this item as it is not equipped " + item.getName());
        }
    }

    public boolean isEquipped(Item item)
    {
        if (item == _equippedArmor || item == _equippedWeapon)
        {
            return true;
        }
        return false;
    }

    public void reveal()
    {

    }
    public void setMovementActions(ArrayList<Tile> path)
    {
        int i = 0;
        for (Tile tile : path)
        {
            if (i >= movementQueue.size())
            {
                movementQueue.add(new GameAction());
            }
            movementQueue.get(i).setAction(this, GameAction.Type.Move, tile, null);
            i++;
        }
    }

    public void setThrowAction(Item selectedItem, Tile touchedTile)
    {
        nextAction.setAction(this, GameAction.Type.Throw, touchedTile, selectedItem);
    }

    public void setAttackAction(GameCharacter character)
    {
        nextAction.setAction(this, character, GameAction.Type.Attack, character.currentTile, null);
    }
    public void setDestroyAction(BreakableObject object)
    {
        nextAction.setAction(this, object, GameAction.Type.Destroy, object.getTile(), null);
    }

    public void clearQueueAndSetAction(GameAction action)
    {
        clearNextActions();
        nextAction.setAction(action);
    }

    public void clearNextActions()
    {
        movementQueue.clear();
        nextAction.setAction(this, GameAction.Type.Empty, null, null);
    }

    public GameAction getNextAction()
    {
        if (isMoving())
        {
            GameAction newAction = movementQueue.remove(0);
            if(newAction.getTargetTile().isEmpty())
            {
                return newAction;
            }
            else
            {
                movementQueue.clear();
                if(nextAction!=null && nextAction.getTargetTile() == newAction.getTargetTile() && nextAction.getType() == GameAction.Type.Destroy)
                {
                    return nextAction;
                }
                return null;
            }
        }
        else if (nextAction.isEmpty())
        {
            return null;
        }

        return nextAction;
    }


    public void placeOnTile(Tile tile)
    {
        currentTile = tile;
        currentTile.setCharacter(this);
        _worldPosition = new Vector2(currentTile.getTileX() * DungeonMap.TileSize, currentTile.getTileY() * DungeonMap.TileSize);
    }

    public void moveTo(Tile tile)
    {
        if (tile.isEmpty())
        {
            currentTile.removeCharacter();
            placeOnTile(tile);
        }
    }

    //Effects
    public void giveEffect(Effect effect)
    {
        _currentEffects.add(effect);
    }
    public ArrayList<Effect> getCurrentEffects()
    {
        return _currentEffects;
    }
    public void updateEffects()
    {
        final ArrayList<Effect> effectsToRemove = new ArrayList<Effect>();
        for(Effect effect : _currentEffects)
        {
            if(!effect.isActive())
            {
                applyEffect(effect);
            }
            else
            {
                if(!effect.isThereTurnsLeft())
                {
                    effectsToRemove.add(effect);
                }
                else
                {
                    effect.decreaseTurns();
                }
            }
        }
        for(Effect toRemove : effectsToRemove)
        {
            removeEffect(toRemove);
        }
    }
    protected void applyEffect(Effect effect)
    {
            effect.setACtive();
            //Hp manipulation
            if(effect.getHitPointsChange()>0) {heal(effect.getHitPointsChange());}
            else{damage(-effect.getHitPointsChange());}
            maxHp +=effect.getMaxHitPointsChange();

            //Strength
            currentStr += Math.min(effect.getStrengthChange(),maxStr-currentStr);
            maxStr +=effect.getMaxStrengthChange();
            currentStr+=effect.getMaxStrengthChange();

            //Misc
            currentAgility +=effect.getAgilityChange();
            defense+=effect.getDefenseChange();
    }
    protected void removeEffect(Effect effect)
    {
        if(effect.getType() == Effect.Type.Temporary)
        {

            effect.reverseEffects();
            applyEffect(effect);
        }
        _currentEffects.remove(effect);
    }

    //Combat related methods
    public void heal(int hitpoints)
    {
        currentHp += hitpoints;
        if (currentHp > maxHp)
        {
            currentHp = maxHp;
            GameConsole.addMessage(_name + " was fully healed");
        }
        else
        {
            GameConsole.addMessage(_name + " was healed " + hitpoints + " hitpoints");
        }
    }
    public void damage(int damage)
    {
        currentHp -= damage;
        if (currentHp <= 0)
        {
            kill();
        }
    }

    public void calculateAttackDamage(GameCharacter target)
    {
        int hitChance = _equippedWeapon != null ? _equippedWeapon.getAttackSpeed() : 0;
        int failChance = 5 + target.getCurrentAgility();

        int result = RandomGen.getRandomInt(0, 100);
        if (result >= 98)
        {
            _dealtDamage = getMaxAttackPower() * 2;
            GameConsole.addMessage(_name + " landed a critical hit on " + target.getName() + "!");
            _lastHitState=HitState.Critical;
        }
        else if (result > (failChance - hitChance))
        {
            _dealtDamage = (_equippedWeapon != null ? _equippedWeapon.getRandomDamage() : 0) + getCurrentStr();
            _dealtDamage-=target.getArmorDefense();

            if(_dealtDamage>0)
            {
                GameConsole.addMessage(_name + " attacked " + target.getName() + "-Dealt "+_dealtDamage+" damage.");
                _lastHitState = HitState.Normal;
            }
            else
            {
                if(target.getEquippedArmor()!= null && RandomGen.getRandomInt(0,4)==4)
                {
                    GameConsole.addMessage(target.getName()+"s "+target.getEquippedArmor().getFullName()+" blocked "+_name + "'s attack");
                }
                else
                {
                    GameConsole.addMessage(target.getName()+" blocked "+_name + "'s attack");
                }
                _lastHitState=HitState.Blocked;
            }
        }
        else
        {
            int randomMessage = RandomGen.getRandomInt(1,2);
            switch (randomMessage)
            {
                case 1:GameConsole.addMessage(_name + " tries to attack " + target.getName() + ", but misses!");break;
                case 2:GameConsole.addMessage(target._name+" manage to dodge "+_name+"'s attack");break;
            }
            _lastHitState = HitState.Miss;
            _dealtDamage=0;
        }
    }

    public void dealAttackDamage(GameCharacter target)
    {
        target.damage(_dealtDamage);
        _dealtDamage=0;
    }
    public void kill()
    {
        //Lazy fix
        if(_name.equals("Locked door") || _name.equals("Chest"))
        {
            GameConsole.addMessage(_name + " was smashed");
        }
       else
        {
            GameConsole.addMessage(_name + " was killed");
        }
        _isDead = true;
        currentTile.removeCharacter();
        //Inherit
    }

    //Drawing
    public void draw(SpriteBatch batch)
    {
        batch.draw(_texture, _worldPosition.x, _worldPosition.y);

    }


}

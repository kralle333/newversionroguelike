package com.brimstonetower.game.gamestateupdating;

import com.brimstonetower.game.gameobjects.BreakableObject;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.map.Tile;

import static com.brimstonetower.game.gamestateupdating.GameAction.Type.Empty;

public class GameAction
{
    public enum Type
    {
        Empty, Wait, Move, Throw, Use, Attack,Destroy, Equip, Unequip, PickUp, Drop, Search
    }

    private GameCharacter _owner;
    public GameCharacter getOwner()
    {
        return _owner;
    }

    private Type _type=Empty;
    public Type getType()
    {
        return _type;
    }

    private GameCharacter _targetCharacter;
    public GameCharacter getTargetCharacter()
    {
        return _targetCharacter;
    }

    private Tile _targetTile;
    public Tile getTargetTile()
    {
        return _targetTile;
    }

    private Item _targetItem;
    public Item getTargetItem()
    {
        return _targetItem;
    }

    private BreakableObject _targetObject;
    public BreakableObject getTargetObject(){return _targetObject;}

    public GameAction()
    {

    }

    public boolean isEmpty()
    {
        return _type == Empty;
    }

    public void setAction(GameCharacter owner, Type type, Tile tile, Item item)
    {
        _owner = owner;
        _targetCharacter = null;
        _type = type;
        _targetTile = tile;
        _targetItem = item;
    }

    public void setAsEmpty()
    {
        _type = Empty;
        _owner = null;
        _targetCharacter = null;
        _targetObject=null;
        _targetTile = null;
        _targetItem = null;
    }

    public void setAction(GameCharacter owner, GameCharacter target, Type type, Tile tile, Item item)
    {
        _owner = owner;
        _targetCharacter = target;
        _type = type;
        _targetTile = tile;
        _targetItem = item;
    }
    public void setAction(GameCharacter owner, BreakableObject target, Type type, Tile tile, Item item)
    {
        _owner = owner;
        _targetObject = target;
        _type = type;
        _targetTile = tile;
        _targetItem = item;
    }
    public void setAction(GameAction gameAction)
    {
        _owner = gameAction._owner;
        _type = gameAction._type;
        _targetCharacter = gameAction._targetCharacter;
        _targetObject = gameAction._targetObject;
        _targetTile = gameAction._targetTile;
        _targetItem = gameAction._targetItem;
    }

    public int getCost()
    {
        if (_type == Type.Attack && _owner.getEquippedWeapon() != null )
        {
            return 100-(_owner.getCurrentAgility()+ _owner.getEquippedWeapon().getAttackSpeed());
        }

        return 100-_owner.getCurrentAgility();
    }

}

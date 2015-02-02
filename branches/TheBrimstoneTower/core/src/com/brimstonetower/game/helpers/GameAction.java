package com.brimstonetower.game.helpers;

import com.brimstonetower.game.gameobjects.GameCharacter;
import com.brimstonetower.game.gameobjects.items.Item;
import com.brimstonetower.game.map.Tile;

public class GameAction
{
    public enum Type
    {
        Empty, Wait, Move, Throw, Use, Attack, Equip, Unequip, PickUp, Drop, Search
    }

    private GameCharacter _owner;

    public GameCharacter getOwner()
    {
        return _owner;
    }

    private Type _type;

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

    public GameAction()
    {

    }

    public boolean isEmpty()
    {
        return _type == Type.Empty;
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
        _type = Type.Empty;
        _owner = null;
        _targetCharacter = null;
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

    public void setAction(GameAction gameAction)
    {
        _owner = gameAction._owner;
        _type = gameAction._type;
        _targetTile = gameAction._targetTile;
        _targetItem = gameAction._targetItem;
    }

    public int getCost()
    {
        if (_type == Type.Attack)
        {
            return _owner.getEquippedWeapon() == null ? 10 : _owner.getEquippedWeapon().getAttackSpeed();
        }

        return 10;
    }

}

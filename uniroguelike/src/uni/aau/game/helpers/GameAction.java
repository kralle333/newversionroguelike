package uni.aau.game.helpers;

import uni.aau.game.gameobjects.Character;
import uni.aau.game.mapgeneration.Tile;
import uni.aau.game.items.Item;

public class GameAction
{
    public enum Type{Empty,Wait,Move,Throw,Use,Attack,Equip,Unequip,PickUp,Drop,Search}

    private uni.aau.game.gameobjects.Character _owner;
    public Character getOwner(){return _owner;}
    private Type _type;
    public Type getType(){return _type;}
    private Character _targetCharacter;
    public Character getTargetCharacter(){return _targetCharacter;}
    private Tile _targetTile;
    public Tile getTargetTile(){return _targetTile;}
    private Item _targetItem;
    public Item getTargetItem(){return _targetItem;}

    public GameAction()
    {

    }
    public boolean isEmpty()
    {
        return _type == Type.Empty;
    }
    public void setAction(Character owner, Type type, Tile tile, Item item)
    {
        _owner = owner;
        _targetCharacter=null;
        _type=type;
        _targetTile = tile;
        _targetItem = item;
    }
    public void setAsEmpty()
    {
        _type = Type.Empty;
        _owner = null;
        _targetCharacter =null;
        _targetTile = null;
        _targetItem = null;
    }
    public void setAction(Character owner, Character target, Type type, Tile tile, Item item)
    {
        _owner = owner;
        _targetCharacter =target;
        _type=type;
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
        if(_type == Type.Attack)
        {
            return _owner.getEquippedWeapon()==null?10:_owner.getEquippedWeapon().getAttackSpeed();
        }

        return 10;
    }

}

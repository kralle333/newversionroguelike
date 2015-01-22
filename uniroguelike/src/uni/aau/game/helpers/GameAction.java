package uni.aau.game.helpers;

import uni.aau.game.gameobjects.Character;
import uni.aau.game.mapgeneration.Tile;
import uni.aau.game.items.Item;

public class GameAction
{
    public enum Type{Wait,Move,Throw,Use,Attack,Equip,Unequip,PickUp,Drop,Search}

    private uni.aau.game.gameobjects.Character _owner;
    public Character getOwner(){return _owner;}
    private Type _type;
    public Type getType(){return _type;}
    private Tile _targetTile;
    public Tile getTargetTile(){return _targetTile;}
    private Item _targetItem;
    public Item getTargetItem(){return _targetItem;}

    public GameAction()
    {

    }
    public void setAction(Character owner, Type type, Tile tile, Item item)
    {
        _owner = owner;
        _type=type;
        _targetTile = tile;
        _targetItem = item;
    }

}

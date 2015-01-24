package uni.aau.game.gameobjects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import uni.aau.game.helpers.GameAction;
import uni.aau.game.items.Armor;
import uni.aau.game.items.Item;
import uni.aau.game.mapgeneration.Tile;

import java.util.ArrayList;

public class Monster extends Character
{
    public enum Nature{Aggressive,Passive}
    private Nature _nature;
    private TextureRegion _textureRegion;
    private int _experienceGiven;
    private float _pursueDistance = 4;
    private GameAction _monsterAction;
    private ArrayList<Item> _droppedItems = new ArrayList<Item>();

    public int retrieveExperienceGiven()
    {
        int toReturn = _experienceGiven;
        _experienceGiven = 0;
        return toReturn;
    }

    public Monster(String name, int str, int hp, int def,int dodgeChance,int experienceGiven,Nature nature, TextureRegion textureRegion)
    {
        super(name, str,dodgeChance,hp);
        _monsterAction = new GameAction();
        _equippedArmor = new Armor("MonsterArmor", "Monsters use this", true, null, def, 0);
        _textureRegion = textureRegion;
        _experienceGiven = experienceGiven;
        _nature=nature;
    }
    public void addItemToDrop(Item item)
    {
        _droppedItems.add(item);
    }

    public GameAction createNextAction(Player player)
    {
        if (_isDead)
        {
            return null;
        }
        else if (_nature==Nature.Aggressive && currentTile.distanceTo(player.getCurrentTile()) < _pursueDistance)
        {
            return pursuePlayer(player);
        }
        _monsterAction.setAction(this, GameAction.Type.Wait, null, null);
        return _monsterAction;
    }

    private GameAction pursuePlayer(Player player)
    {
        float smallestDistance = Float.MAX_VALUE;
        Tile nextTile = currentTile;
        float currentDistance;
        for (Tile tile : currentTile.getWalkableNeighbours())
        {
            if (tile.getTrap() == null)
            {
                currentDistance = tile.distanceTo(player.getCurrentTile());
                if (currentDistance < smallestDistance)
                {
                    smallestDistance = currentDistance;
                    nextTile = tile;
                }
            }
        }
        if (nextTile != currentTile)
        {
            if (nextTile.getCharacter() == player)
            {
                _monsterAction.setAction(this, GameAction.Type.Attack, nextTile, null);
            }
            else
            {
                _monsterAction.setAction(this, GameAction.Type.Move, nextTile, null);
            }
        }
        else
        {
            _monsterAction.setAction(this, GameAction.Type.Wait, null, null);
        }

        return _monsterAction;
    }

    public void draw(SpriteBatch batch)
    {
        if (!_isDead && currentTile.getLightAmount() == Tile.LightAmount.Light)
        {
            batch.draw(_textureRegion, _position.x, _position.y);
        }
    }

    @Override
    public void kill()
    {
        super.kill();
        if(_droppedItems.size()>0)
        {
            for(Item item :_droppedItems)
            {
                currentTile.addItem(item);
            }

        }
    }
}

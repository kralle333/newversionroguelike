package uni.aau.game.gameobjects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import uni.aau.game.helpers.GameAction;
import uni.aau.game.helpers.MonsterAttack;
import uni.aau.game.items.Armor;
import uni.aau.game.mapgeneration.Tile;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Monster extends Character
{
    private TextureRegion _textureRegion;
    private int _experienceGiven;
    private float _pursueDistance = 4;
    private GameAction _monsterAction;

    private ArrayList<MonsterAttack> attacks = new ArrayList<MonsterAttack>();

    public int retrieveExperienceGiven()
    {
        int toReturn = _experienceGiven;
        _experienceGiven = 0;
        return toReturn;
    }

    public Monster(String name, int str, int hp, int def,int dodgeChance,int experienceGiven, TextureRegion textureRegion)
    {
        super(name, str,dodgeChance,hp);
        _monsterAction = new GameAction();
        _equippedArmor = new Armor("MonsterArmor", "Monsters use this", true, null, def, 0);
        _textureRegion = textureRegion;
        _experienceGiven = experienceGiven;
    }
    public void copyAttacksToList(ArrayList<MonsterAttack> monsterAttacks)
    {
        for(MonsterAttack monsterAttack : monsterAttacks)
        {
            attacks.add(new MonsterAttack(monsterAttack.name,monsterAttack.minDamage,monsterAttack.maxDamage,monsterAttack.attackSpeed));
        }

    }

    public GameAction createNextAction(Player player)
    {
        if (_isDead)
        {
            return null;
        }
        else if (currentTile.distanceTo(player.getCurrentTile()) < _pursueDistance)
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
    }
}

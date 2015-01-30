package com.brimstonetower.game.gameobjects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.helpers.GameAction;
import com.brimstonetower.game.items.Armor;
import com.brimstonetower.game.items.Item;
import com.brimstonetower.game.mapgeneration.Tile;
import java.util.ArrayList;


public class Monster extends GameCharacter
{
    public enum Nature
    {
        Aggressive, Passive
    }

    private Nature _nature;
    private int _experienceGiven;
    private float _pursueDistance = 4;
    private ArrayList<Item> _droppedItems = new ArrayList<Item>();

    public int retrieveExperienceGiven()
    {
        int toReturn = _experienceGiven;
        _experienceGiven = 0;
        return toReturn;
    }

    public Monster(String name, int str, int hp, int def, int dodgeChance, int experienceGiven, Nature nature, TextureRegion textureRegion)
    {
        super(name, str, dodgeChance, hp,textureRegion);
        nextAction = new GameAction();
        _equippedArmor = new Armor("MonsterArmor", "Monsters use this", true, null, def, 0);
        _experienceGiven = experienceGiven;
        _nature = nature;
    }

    public void addItemToDrop(Item item)
    {
        _droppedItems.add(item);
    }

    public GameAction setNextAction(Player player)
    {
        if (_isDead)
        {
            return null;
        }
        else if (_nature == Nature.Aggressive && currentTile.distanceTo(player.getCurrentTile()) < _pursueDistance)
        {
            return pursuePlayer(player);
        }
        nextAction.setAction(this, GameAction.Type.Wait, null, null);
        return nextAction;
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
                nextAction.setAction(this, player, GameAction.Type.Attack, nextTile, null);
            }
            else
            {
                nextAction.setAction(this, GameAction.Type.Move, nextTile, null);
            }
        }
        else
        {
            nextAction.setAction(this, GameAction.Type.Wait, null, null);
        }

        return nextAction;
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        if (!_isDead && currentTile.getLightAmount() == Tile.LightAmount.Light)
        {
            super.draw(batch);
        }
    }

    @Override
    public void kill()
    {
        super.kill();
        if (_droppedItems.size() > 0)
        {
            for (Item item : _droppedItems)
            {
                currentTile.addItem(item);
            }

        }
    }
}

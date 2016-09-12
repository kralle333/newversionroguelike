package com.brimstonetower.game.gameobjects;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gameobjects.equipment.Weapon;
import com.brimstonetower.game.gamestateupdating.GameCharacter;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.gamestateupdating.GameAction;
import com.brimstonetower.game.gameobjects.equipment.Armor;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;
import java.util.ArrayList;


public class Monster extends GameCharacter
{
    public enum Nature
    {
        Aggressive, Passive
    }

    private Nature _nature;
    private int _experienceGiven;
    private float _pursueDistance = 5;
    private ArrayList<Item> _droppedItems = new ArrayList<Item>();
    private final int turnsToBeHidden = 2;
    private int _turnsNotSeen = 0;
    private boolean _wasSeen = false;
    public boolean wasSeen(){return _wasSeen;}
    private TextureRegion _wasSeenRegion;
    private boolean _wasJustSeen = false;
    private TextureRegion _deadRegion;
    private static Color _attackRangeColor = new Color(1,0,0,0.2f);
    public int retrieveExperienceGiven()
    {
        int toReturn = _experienceGiven;
        _experienceGiven = 0;
        return toReturn;
    }

    public Monster(String name, int str, int hp, int def, int dodgeChance, int experienceGiven, Nature nature, TextureRegion aliveRegion,TextureRegion deadRegion)
    {
        super(name, str, dodgeChance, hp,aliveRegion);
        _equippedArmor = new Armor("MonsterArmor", "Monsters use this", true, null, def, 0);
        _experienceGiven = experienceGiven;
        _nature = nature;
        _wasSeenRegion = AssetManager.getTextureRegion("misc","wasSeen", DungeonMap.TileSize,DungeonMap.TileSize);
        _deadRegion = deadRegion;
    }

    public void addItemToDrop(Item item)
    {
        _droppedItems.add(item);
    }

    @Override
    public void reveal()
    {
        _wasSeen = true;
    }

    public void lookForPlayer(Player player)
    {
        if(!_wasSeen && (getCurrentTile().getLightAmount() == Tile.LightAmount.Light ||
                        getCurrentTile().getLightAmountChangingTo() == Tile.LightAmount.Light))
        {
            GameConsole.addMessage("A "+getName()+" was spotted!");
            AssetManager.getSound("surprise").play();
            player.clearNextActions();
            _wasJustSeen=true;
            _wasSeen=true;
        }
        else if(_wasSeen)
        {
            if(getCurrentTile().getLightAmount() != Tile.LightAmount.Light &&_turnsNotSeen>=turnsToBeHidden)
            {
                _turnsNotSeen=0;
                _wasSeen = false;
            }
            else
            {
                _turnsNotSeen++;
                _wasJustSeen=false;
            }
        }

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
        float bestDistance = Float.MAX_VALUE;
        Tile nextTile = currentTile;
        int currentDistance = (int)currentTile.distanceTo(player.getCurrentTile());

        boolean moveCloser = true;

        if(currentDistance>=getMinAttackRange() && currentDistance<=getMaxAttackRange())
        {
            nextAction.setAction(this, player, GameAction.Type.Attack, nextTile, null);
            return nextAction;
        }
        else if(currentDistance<getMinAttackRange())
        {
            moveCloser=false;
            bestDistance=Float.MIN_VALUE;
        }

            for (Tile tile : currentTile.getWalkableNeighbours())
            {
                if (tile.getTrap() == null && tile.isEmpty())
                {
                    currentDistance = (int)tile.distanceTo(player.getCurrentTile());
                    if (moveCloser && currentDistance < bestDistance)
                    {
                        bestDistance = currentDistance;
                        nextTile = tile;
                    }
                    else if (!moveCloser && currentDistance>bestDistance)
                    {
                        bestDistance = currentDistance;
                        nextTile = tile;
                    }
                }
            }


        if (nextTile != currentTile)
        {
            nextAction.setAction(this, GameAction.Type.Move, nextTile, null);
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
        if (!_isDead)
        {
            if(currentTile.getLightAmount() == Tile.LightAmount.Light ||
                    (currentTile.getLightAmount() == Tile.LightAmount.Shadow && _wasSeen))
            {
                super.draw(batch);
                if(_wasJustSeen)
                {
                    batch.draw(_wasSeenRegion,getWorldPosition().x,getWorldPosition().y,DungeonMap.TileSize,DungeonMap.TileSize);
                }
            }
            if(_displayAttackRange)
            {
                int minRange = getEquippedWeapon()==null?1:getEquippedWeapon().getMinRange();
                int maxRange = getEquippedWeapon()==null?1:getEquippedWeapon().getMaxRange();
                currentTile.drawOverLay(batch,minRange,maxRange,_attackRangeColor);
            }
        }
        else
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
        _texture = _deadRegion;
    }
}

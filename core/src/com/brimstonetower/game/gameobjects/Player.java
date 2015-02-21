package com.brimstonetower.game.gameobjects;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gamestateupdating.GameCharacter;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.managers.ItemManager;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;

public class Player extends GameCharacter
{
    private static int _startHp = 48;
    private static int _startStr = 2;
    private int _lanternStrength = 4;
    public int getLanternStrength()
    {
        return _lanternStrength;
    }
    private int _throwRange = 4;
    public int getThrowRange(){return _throwRange;}
    private boolean _displayThrowRange = false;
    public void displayThrowRange(){_displayThrowRange=true;}
    public void hideThrowRange(){_displayThrowRange=false;}

    private String _killedBy = null;
    public String getKilledBy()
    {
        return _killedBy;
    }
    public void setKilledBy(String killedBy)
    {
        _killedBy = killedBy;
    }
    private static Color _attackRangeColor = new Color(0,1,0,0.2f);
    private TextureRegion _waypointRegion;


    public Player(String name)
    {
        super(name, _startStr, 5, _startHp, AssetManager.getTextureRegion("mainHeroesWithBorder", "playerType2", DungeonMap.TileSize, DungeonMap.TileSize));
        super.equip(ItemManager.getWeapon("Steel Short Sword"));//Player starts with sword
        super.equip(ItemManager.getArmor("Noble Clothes"));//Player starts with some clothes
        _equippedWeapon.identify();
        _equippedArmor.identify();

        _equippedWeapon.identify();
        _waypointRegion = AssetManager.getTextureRegion("misc","wayPoint",DungeonMap.TileSize,DungeonMap.TileSize);
    }

    @Override
    public void equip(Item item)
    {
        super.equip(item);
        if(_equippedArmor == item || _equippedWeapon == item)
        {
            GameConsole.addMessage(getName() + " equipped " + item.getName());
        }
    }

    public void retrieveExperience(Monster monster)
    {
        experience += monster.retrieveExperienceGiven();
        if (experience >= experienceToNextLevel)
        {
            levelUp();
        }
    }

    public float calculateScore()
    {
        return getArmorDefense() + getMaxAttackPower() + level + maxStr + maxHp + experience;
    }


    public void levelUp()
    {
        experience = 0;

        level++;
        experienceToNextLevel *= 2;
        float hpRatio = (float)currentHp/(float)maxHp;
        maxHp = (int) calculateNewStat((float) _startHp, 2000, level, 99);
        maxStr = (int) calculateNewStat((float) _startStr, 100, level, 99);
        currentHp =(int)(hpRatio*maxHp);
        currentStr = maxStr;

        GameConsole.addMessage("Player leveled up! - Welcome to level " + level);
        GameConsole.addMessage("Strength is: " + getMaxStr());
        GameConsole.addMessage("Max hp is: " + getMaxHitPoints());
    }

    private float calculateNewStat(float startStat, float maxStat, float currentLevel, float maxLevel)
    {
        return startStat + (maxStat - startStat) * (float) Math.pow((currentLevel + 10) / (maxLevel + 10), 2);
    }

    @Override
    public void moveTo(Tile tile)
    {
        currentTile.setLight(Tile.LightAmount.Shadow, _lanternStrength, _lanternStrength);
        super.moveTo(tile);
        tile.setLight(Tile.LightAmount.Shadow, _lanternStrength*2, _lanternStrength*2);
        tile.setLight(Tile.LightAmount.Light, _lanternStrength, _lanternStrength);
    }

    @Override
    protected void applyEffect(Effect effect)
    {
        if(!effect.areEffectsReversed())
        {
            GameConsole.addMessage(effect.getEffectDescription());
        }
        super.applyEffect(effect);
        if(_isDead)
        {
            _killedBy = effect.getName()+(effect.isGas()?" gas":"");
        }
    }

    @Override
    protected void removeEffect(Effect effect)
    {
        if(effect.getType() == Effect.Type.Temporary)
        {
            GameConsole.addMessage(effect.getDispelDescription());
            super.removeEffect(effect);
        }
    }

    @Override
    public void damage(int damage)
    {
        super.damage(damage);
        movementQueue.clear();
    }

    @Override
    public void kill()
    {
        super.kill();
    }

    @Override
    public void draw(SpriteBatch batch)
    {
        super.draw(batch);
        if(isMoving())
        {
            Tile lastTile = movementQueue.get(movementQueue.size()-1).getTargetTile();
            batch.draw(_waypointRegion,lastTile.getWorldPosition().x,lastTile.getWorldPosition().y,DungeonMap.TileSize,DungeonMap.TileSize);
        }
        if(_displayAttackRange)
        {
            int minRange = getEquippedWeapon()==null?1:getEquippedWeapon().getMinRange();
            int maxRange = getEquippedWeapon()==null?1:getEquippedWeapon().getMaxRange();
            currentTile.drawOverLay(batch,minRange,maxRange,_attackRangeColor);
        }
        else if(_displayThrowRange)
        {
            currentTile.drawOverLay(batch,1,_throwRange,_attackRangeColor);
        }
    }
}

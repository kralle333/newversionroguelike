package com.brimstonetower.game.gameobjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.helpers.AssetManager;
import com.brimstonetower.game.items.Item;
import com.brimstonetower.game.items.ItemManager;
import com.brimstonetower.game.items.Potion;
import com.brimstonetower.game.mapgeneration.DungeonGenerator;
import com.brimstonetower.game.mapgeneration.DungeonMap;
import com.brimstonetower.game.mapgeneration.Tile;

public class Player extends GameCharacter
{
    private static int _startHp = 30;
    private static int _startStr = 2;
    private int _lanternStrength = 3;

    public int getLanternStrength()
    {
        return _lanternStrength;
    }

    private String _killedBy = null;

    public String getKilledBy()
    {
        return _killedBy;
    }

    public void setKilledBy(String killedBy)
    {
        _killedBy = killedBy;
    }

    public Player(String name)
    {
        super(name, _startStr, 5, _startHp, AssetManager.getTextureRegion("player", "player", DungeonMap.TileSize, DungeonMap.TileSize));
        super.equip(ItemManager.getWeapon(0, 0));//Player starts with sword
        _texture.flip(false, true);
        _equippedWeapon.identify();
    }

    @Override
    public void equip(Item item)
    {
        super.equip(item);
        GameConsole.addMessage(getName() + " equipped " + item.getName());
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

    public void retrieveExperience(Potion potion)
    {
        experience += potion.getPotency();
        if (experience >= experienceToNextLevel)
        {
            levelUp();
        }
    }


    public void levelUp()
    {
        experience = 0;

        level++;
        experienceToNextLevel *= 2;
        maxHp = calculateNewStat(_startHp, 2000, level, 99);
        maxStr = calculateNewStat(_startStr, 100, level, 99);

        currentStr = maxStr;

        GameConsole.addMessage("Player leveled up! - Welcome to level " + level);
        GameConsole.addMessage("Strength is: " + getMaxStr());
        GameConsole.addMessage("Hp is: " + getMaxHitPoints());
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
        tile.setLight(Tile.LightAmount.Shadow, _lanternStrength + 2, _lanternStrength + 2);
        tile.setLight(Tile.LightAmount.Light, _lanternStrength, _lanternStrength);
    }


    @Override
    public void damage(int damage)
    {
        super.damage(damage);
        if (getCurrentStatusEffect() == StatusEffect.Poisoned)
        {
            _killedBy = "poison";
        }
        movementQueue.clear();
    }

    @Override
    public void kill()
    {
        super.kill();
    }
}

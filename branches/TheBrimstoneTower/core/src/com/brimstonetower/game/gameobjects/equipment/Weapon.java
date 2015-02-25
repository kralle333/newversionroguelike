package com.brimstonetower.game.gameobjects.equipment;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.helpers.RandomGen;

public class Weapon extends Item
{
    public enum RangeType{Melee,Throwable,AmmoThrower}
    private RangeType _rangeType;
    public RangeType getRangeType(){return _rangeType;}

    private int _ammoCount = 0;
    public int getAmmoCount()
    {
        return _ammoCount;
    }

    private int _minDamage;
    public int getMinDamage()
    {
        return _minDamage;
    }
    private int _maxDamage;
    public int getMaxDamage()
    {
        return _maxDamage;
    }
    private int _bonusDamage;
    public int getBonusDamage(){return _bonusDamage;}
    public int getIdentifiedMaxDamage()
    {
        return _maxDamage + _bonusDamage;
    }
    public int getExpectedMaxDamage()
    {
        return _maxDamage;
    }

    private int _attackSpeed;
    public int getAttackSpeed()
    {
        return _attackSpeed;
    }

    private int _minRange;
    public int getMinRange(){return _minRange;}
    private int _maxRange;
    public int getMaxRange(){return _maxRange;}

    private int _stepsTillIdentified;
    private static int weaponId=1;


    public Weapon(String name, String description, boolean isIdentified, TextureRegion textureRegion, int minDamage,
                  int maxDamage, int bonusDamage,int minRange, int maxRange, int attackSpeed, RangeType rangeType)
    {
        super(name, description, isIdentified, textureRegion, rangeType==RangeType.Throwable ,weaponId++);
        _minDamage = minDamage;
        _maxDamage = maxDamage;
        _bonusDamage = bonusDamage;
        _minRange = minRange;
        _maxRange = maxRange;
        _rangeType = rangeType;
        _attackSpeed = attackSpeed;
        if (_rangeType==RangeType.Throwable)
        {
            _ammoCount = RandomGen.getRandomInt(3, 5) + _bonusDamage * 2;
            _bonusDamage = 0;
            _isIdentified = true;
        }
        else if (!isIdentified)
        {
            _stepsTillIdentified = maxDamage * RandomGen.getRandomInt(30, 100);
            if (bonusDamage > 0)
            {
                _stepsTillIdentified *= bonusDamage;
            }
        }
    }

    public Weapon(Weapon toCopy, int bonusAttack)
    {
        this(toCopy.getNameWithoutBonus(), toCopy.getIdentifiedDescription(), toCopy.isIdentified(), toCopy.getTextureRegion(), toCopy.getMinDamage(),
                toCopy.getMaxDamage(), bonusAttack,toCopy.getMinRange(),toCopy.getMaxRange(), toCopy.getAttackSpeed(), toCopy.getRangeType());
    }

    public int getRandomDamage()
    {
        return RandomGen.getRandomInt(_minDamage + _bonusDamage, _maxDamage + _bonusDamage);
    }

    public void step()
    {
        if (!_isIdentified)
        {
            _stepsTillIdentified--;
            if (_stepsTillIdentified <= 0)
            {
                GameConsole.addMessage(super.getName() + " [?] was identified to be " + super.getName() + " [" + _bonusDamage + "]");
                identify();
            }
        }
    }

    public String getNameWithoutBonus()
    {
        return _name;
    }


    @Override
    public String getName()
    {
        if (_isIdentified)
        {
            if (_rangeType == RangeType.Throwable)
            {
                return _name + "(" + _ammoCount + ")";
            }
            return _name + " [" + _bonusDamage + "]";
        }
        else
        {
            return _name + " [?]";
        }
    }

    public String getDescription()
    {
        if (_isIdentified)
        {
            if (_rangeType==RangeType.Throwable)
            {
                return super.getDescription() + "- There are " + _ammoCount + " of them";
            }
            return super.getDescription() + "- Can deal " +_minDamage+"-"+(_maxDamage + _bonusDamage) + " damage";
        }
        else
        {
            return super.getDescription() + " - Its effects are not known, but normally this type of weapon deal "+_minDamage+"-" + _maxDamage + " damage - Its secrets will be revealed in " + _stepsTillIdentified + " steps";
        }
    }

    public void addAmmo(int ammoToAdd)
    {
        _ammoCount += ammoToAdd;
    }

    public void decreaseRangedAmmo()
    {
        if (_rangeType!=RangeType.Throwable)
        {
            Gdx.app.log("Weapon", "Decreasing ammo on a non ranged weapon!");
        }
        _ammoCount--;
    }
    public void setAmmoCount(int newCount)
    {
        _ammoCount = newCount;
    }

    @Override
    public void draw(SpriteBatch batch, float x, float y)
    {
        if (_isCurseKnown && hasCurse())
        {
            batch.setColor(Color.PINK);
        }
        super.draw(batch, x, y);
    }

    @Override
    public void draw(SpriteBatch batch, float x, float y, float scale)
    {
        if (_isCurseKnown && hasCurse())
        {
            batch.setColor(Color.PINK);
        }
        super.draw(batch, x, y, scale);
    }


}

package com.brimstonetower.game.items;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.mapgeneration.RandomGen;

public class Armor extends Item
{
    private int _defense;

    public int getDefense()
    {
        return _defense;
    }

    private int _bonusDef;

    public int getIdentifiedDefense()
    {
        return _defense + _bonusDef;
    }

    public int getExpectedDefense()
    {
        return _defense;
    }

    private int _stepsTillIdentified;

    public int getStepsRemaining()
    {
        return _stepsTillIdentified;
    }

    public Armor(String name, String description, boolean isIdentified, TextureRegion textureRegion, int defense, int bonusDef)
    {
        super(name, description, isIdentified, textureRegion, false, RandomGen.getRandomInt(0, 10) > 8);
        _defense = defense;
        _bonusDef = bonusDef;
        if (!_isIdentified)
        {
            _stepsTillIdentified = _defense * RandomGen.getRandomInt(30, 100);
            if (_bonusDef > 0)
            {
                _stepsTillIdentified *= _bonusDef;
            }
        }
    }

    public Armor(Armor toCopy, int bonusDef)
    {
        this(toCopy.getIdentifiedName(), toCopy.getIdentifiedDescription(), toCopy.isIdentified(), toCopy.getTextureRegion(), toCopy.getDefense(), bonusDef);
    }

    public void step()
    {
        if (!_isIdentified)
        {
            _stepsTillIdentified--;
            if (_stepsTillIdentified <= 0)
            {
                GameConsole.addMessage(super.getName() + " [?] was identified to be " + super.getName() + " [" + _bonusDef + "]");
                identify();
            }
        }
    }

    public String getName()
    {
        if (_isIdentified)
        {
            return super.getName() + " [" + _bonusDef + "]";
        }
        else
        {
            return super.getName() + " [?]";
        }
    }

    public String getDescription()
    {
        if (_isIdentified)
        {
            return super.getDescription() + "- Gives " + (_defense + _bonusDef) + " armor";
        }
        else
        {
            return super.getDescription() + " - Its effects are not known, but normally this type of armor gives " + _defense + " armor - Its secrets will be revealed in " + _stepsTillIdentified + " steps";
        }
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

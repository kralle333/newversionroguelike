package com.brimstonetower.game.helpers;


import com.badlogic.gdx.graphics.Color;
import com.brimstonetower.game.gui.GameConsole;

public class Effect
{
    public enum Type{Gas,OverMultipleTurns,Instant}
    private Type _type;

    //Part of all types
    private String _name;
    private String _effectDescription;
    private int _hitPointsChange;
    private int _maxHitPointsChange;
    private int _strengthChange;
    private int _maxStrengthChange;
    private int _defenseChange;
    private int _attackSpeedChange;
    private int _dodgeRateChange;
    private boolean _isPermanent;
    private boolean _isEffectsReversed = false;

    //Temporary effect
    private boolean _isActive = false;
    public boolean isActive(){return _isActive;}
    public void activate(){_isActive =true;}
    private int _turnsLeft;
    public void decreaseTurns()
    {
        _turnsLeft--;
    }
    public boolean isThereTurnsLeft(){return _turnsLeft>0;}

    //Gas type
    private Color _color;
    public Color getColor(){return _color;}

    //Getters
    public Type getType()
    {
        return _type;
    }
    public String getName()
    {
        return _name;
    }
    public String getEffectDescription()
    {
        return _effectDescription;
    }
    public int getHitPointsChange()
    {
        return _hitPointsChange;
    }
    public int getMaxHitPointsChange(){return _maxHitPointsChange;}
    public int getStrengthChange()
    {
        return _strengthChange;
    }
    public int getMaxStrengthChange(){return _maxStrengthChange;}
    public int getDefenseChange()
    {
        return _defenseChange;
    }
    public int getAttackSpeedChange()
    {
        return _attackSpeedChange;
    }
    public int getDodgeRateChange()
    {
        return _dodgeRateChange;
    }
    public boolean isPermanent()
    {
        return _isPermanent;
    }


    //Builder pattern to create effects as requested
    private Effect()
    {

    }


    public static Effect createGasEffect(String name, String description,
                                         int hitPointsChange,int strengthChange,
                                         int defenseChange,int attackSpeedChange,int dodgeRateChange,
                                         Color color, boolean isPermanent)
    {
        Effect gasEffect = new Effect();
        gasEffect._name=name;
        gasEffect._effectDescription=description;
        gasEffect._hitPointsChange=hitPointsChange;
        gasEffect._strengthChange=strengthChange;
        gasEffect._defenseChange=defenseChange;
        gasEffect._attackSpeedChange=attackSpeedChange;
        gasEffect._dodgeRateChange=dodgeRateChange;
        gasEffect._color=color;
        gasEffect._isPermanent=isPermanent;
        gasEffect._isActive = isPermanent;
        gasEffect._type = Type.Gas;

        return gasEffect;
    }

    public static Effect createInstantEffect(String name, String description,
                                             int hitPointsChange,int strengthChange,
                                             int defenseChange,int attackSpeedChange,
                                             int dodgeRateChange, int turnsActive, boolean isPermanent)
    {
        Effect instantEffect = new Effect();
        instantEffect._name = name;
        instantEffect._effectDescription = description;
        instantEffect._hitPointsChange = hitPointsChange;
        instantEffect._hitPointsChange = hitPointsChange;
        instantEffect._strengthChange = strengthChange;
        instantEffect._defenseChange = defenseChange;
        instantEffect._attackSpeedChange = attackSpeedChange;
        instantEffect._dodgeRateChange = dodgeRateChange;
        instantEffect._type = Type.Instant;
        instantEffect._turnsLeft = turnsActive;
        instantEffect._isPermanent = isPermanent;
        instantEffect._isActive = isPermanent;

        if (turnsActive > 0 && isPermanent)
        {
            GameConsole.addMessage("Mix of temporary and permanent stats for effect: " + name);
        }

        return instantEffect;
    }


    //Used for removing temporary  effects
    public void reverseEffects()
    {
        _hitPointsChange =-_hitPointsChange;
        _maxHitPointsChange=-_maxHitPointsChange;
        _strengthChange=-_strengthChange;
        _maxStrengthChange=-_maxStrengthChange;
        _defenseChange=-_defenseChange;
         _attackSpeedChange=-_attackSpeedChange;
        _dodgeRateChange =-_dodgeRateChange;

        _isEffectsReversed=!_isEffectsReversed;
    }
}

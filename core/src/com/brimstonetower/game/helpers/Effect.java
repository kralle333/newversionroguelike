package com.brimstonetower.game.helpers;


import com.badlogic.gdx.graphics.Color;
import com.brimstonetower.game.gui.GameConsole;

public class Effect
{
    public enum Type{Temporary, Permanent}
    private Type _type;

    //Properties
    private String _name;
    private String _effectDescription;
    private String _dispelDescription;
    private int _hitPointsChange;
    private int _maxHitPointsChange;
    private int _strengthChange;
    private int _maxStrengthChange;
    private int _defenseChange;
    private int _attackSpeedChange;
    private int _dodgeRateChange;

    //Used for keeping track of the effects
    private boolean _isEffectsReversed = false;
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
    private boolean _isGas = false;
    private Color _color;
    public Color getColor(){return _color;}

    //Getters
    public boolean isGas(){return _isGas;}
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
    public String getDispelDescription(){return _dispelDescription;}
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

    //Builder pattern to create effects as requested
    private Effect(String name, String description,
                   int hitPointsChange,int maxHitPointsChange,
                   int strengthChange,int maxStrengthChange,
                   int defenseChange,int attackSpeedChange,
                   int dodgeRateChange, int turnsActive)
    {
        _name = name;
        _effectDescription = description;
        _hitPointsChange = hitPointsChange;
        _maxHitPointsChange = maxHitPointsChange;
        _strengthChange = strengthChange;
        _maxStrengthChange = maxStrengthChange;
        _defenseChange = defenseChange;
        _attackSpeedChange = attackSpeedChange;
        _dodgeRateChange = dodgeRateChange;
        _turnsLeft = turnsActive;
    }




    public static Effect createPermanentEffect(String name, String description,
                                                     int hitPointsChange, int maxHitPointsChange,
                                                     int strengthChange, int maxStrengthChange,
                                                     int defenseChange, int attackSpeedChange,
                                                     int dodgeRateChange, int turnsActive,
                                                     boolean isGas, Color color)
    {
        Effect permanentEffect = new Effect(
                name,description,hitPointsChange,maxHitPointsChange,
                strengthChange,maxStrengthChange,defenseChange,
                attackSpeedChange,dodgeRateChange,turnsActive);

        permanentEffect._isGas = isGas;
        permanentEffect._type = Type.Permanent;
        permanentEffect._color = isGas?color:Color.WHITE;

        return permanentEffect;
    }

    public static Effect createTemporaryEffect(String name, String description,String dispelDescription,
                                               int hitPointsChange, int maxHitPointsChange,
                                               int strengthChange, int maxStrengthChange,
                                               int defenseChange, int attackSpeedChange,
                                               int dodgeRateChange, int turnsActive,
                                               boolean isGas, Color color)
    {
        Effect temporaryEffect = new Effect(
                name,description,hitPointsChange,maxHitPointsChange,
                strengthChange,maxStrengthChange,defenseChange,
                attackSpeedChange,dodgeRateChange,turnsActive);
        temporaryEffect._dispelDescription = dispelDescription;
        temporaryEffect._isGas = isGas;
        temporaryEffect._type = Type.Temporary;
        temporaryEffect._color = isGas?color:Color.WHITE;

        return temporaryEffect;
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

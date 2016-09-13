package com.brimstonetower.game.helpers;


import com.badlogic.gdx.Gdx;
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
    private int _agilityChange;
    private int _maxAgilityChange;
    private int _viewDistanceChange;

    //Used for keeping track of the effects
    private boolean _areEffectsReversed = false;
    public boolean areEffectsReversed(){return _areEffectsReversed;}
    private boolean _isActive = false;
    public boolean isActive(){return _isActive;}
    public void setACtive(){_isActive =true;}
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
    public int getAgilityChange()
    {
        return _agilityChange;
    }
    public int getMaxAgilityChange(){return _maxAgilityChange;}
    public int getViewDistanceChange(){return _viewDistanceChange;}

    //Builder pattern to create effects as requested
    private Effect(String name, String description,
                   int hitPointsChange,int maxHitPointsChange,
                   int strengthChange,int maxStrengthChange,
                   int defenseChange,int agilityChange,int maxAgilityChange, int viewDistanceChange, int turnsActive)
    {
        _name = name;
        _effectDescription = description;
        _hitPointsChange = hitPointsChange;
        _maxHitPointsChange = maxHitPointsChange;
        _strengthChange = strengthChange;

        _maxStrengthChange = maxStrengthChange;
        _defenseChange = defenseChange;
        _agilityChange = agilityChange;
        _maxAgilityChange = maxAgilityChange;
        _viewDistanceChange = viewDistanceChange;
        _turnsLeft = turnsActive;
    }

    public Effect(Effect toCopy)
    {
        this(toCopy._name,toCopy._effectDescription,
                toCopy._hitPointsChange,toCopy._maxHitPointsChange,
                toCopy._strengthChange,toCopy._maxStrengthChange,
                toCopy._defenseChange,toCopy.getAgilityChange(),toCopy.getMaxAgilityChange(),
                toCopy.getViewDistanceChange(),toCopy._turnsLeft);
        _isGas = toCopy._isGas;
        _dispelDescription = toCopy._dispelDescription;
        _color = toCopy._color;
        _type = toCopy.getType();
    }


    public static Effect createPermanentEffect(String name, String description,
                                                     int hitPointsChange, int maxHitPointsChange,
                                                     int strengthChange, int maxStrengthChange,
                                                     int defenseChange, int agilityChange,int maxAgilityChange,
                                                     int viewDistanceChange,int turnsActive,
                                                     boolean isGas, Color color)
    {
        Effect permanentEffect = new Effect(
                name,description,hitPointsChange,maxHitPointsChange,
                strengthChange,maxStrengthChange,defenseChange,
                agilityChange,maxAgilityChange,viewDistanceChange,turnsActive);

        permanentEffect._isGas = isGas;
        permanentEffect._type = Type.Permanent;
        permanentEffect._color = isGas?color:Color.WHITE;

        return permanentEffect;
    }

    public static Effect createTemporaryEffect(String name, String description,String dispelDescription,
                                               int hitPointsChange, int maxHitPointsChange,
                                               int strengthChange, int maxStrengthChange,
                                               int defenseChange, int agilityChange,int maxAgilityChange,int viewDistanceChange, int turnsActive,
                                               boolean isGas, Color color)
    {
        Effect temporaryEffect = new Effect(
                name,description,hitPointsChange,maxHitPointsChange,
                strengthChange,maxStrengthChange,defenseChange,
                agilityChange,maxAgilityChange, viewDistanceChange,turnsActive);
        temporaryEffect._dispelDescription = dispelDescription;
        temporaryEffect._isGas = isGas;
        temporaryEffect._type = Type.Temporary;
        temporaryEffect._color = isGas?color:Color.WHITE;

        return temporaryEffect;
    }


    //Used for removing temporary  effects
    public void reverseEffects()
    {
        _hitPointsChange *=-1;
        _maxHitPointsChange*=-1;
        _strengthChange*=-1;
        _maxStrengthChange*=-1;
        _defenseChange*=-1;
         _agilityChange*=-1;
        _maxAgilityChange*=-1;
        _viewDistanceChange*=-1;
        _areEffectsReversed=!_areEffectsReversed;
    }
}

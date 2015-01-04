package uni.aau.game.items;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import uni.aau.game.gui.GameConsole;
import uni.aau.game.mapgeneration.MapGenerator;
import uni.aau.game.mapgeneration.RandomGen;

public class Weapon extends Item
{
    private boolean _isRanged = false;
    public boolean isRanged(){return _isRanged;}
    private int _ammoCount = 0;
    public int getAmmoCount(){return _ammoCount;}
    private int _attack;
    public int getAttack(){return _attack;}
    private int _bonusAttack;
    public int getIdentifiedAttackPower(){return _attack+_bonusAttack;}
    public int getExpectedAttackPower(){return _attack;}
    private float _attackSpeed;
    public float getAttackSpeed(){return  _attackSpeed;}
    private int _stepsTillIdentified;

    public Weapon(String name, String description,boolean isIdentified,TextureRegion textureRegion,
                  int attack, int bonusAttack,float attackSpeed, boolean isRanged)
    {
        super(name,description,isIdentified,textureRegion,isRanged, RandomGen.getRandomInt(0, 10)>8);
        _attack = attack;
        _bonusAttack = bonusAttack;
        _isRanged = isRanged;
        _attackSpeed = attackSpeed;
        if(_isRanged)
        {
            _ammoCount = 1+_bonusAttack*2;
            _bonusAttack = 0;
            _isIdentified=true;
        }
        else if(!isIdentified)
        {
            _stepsTillIdentified = attack* RandomGen.getRandomInt(30, 100);
            if(bonusAttack>0)
            {
                _stepsTillIdentified *=bonusAttack;
            }
        }
    }
    public Weapon(Weapon toCopy, int bonusAttack)
    {
        this(toCopy.getIdentifiedName(),toCopy.getIdentifiedDescription(),toCopy.isIdentified(),toCopy.getTextureRegion(),
                toCopy.getAttack(),bonusAttack,toCopy.getAttackSpeed(),toCopy.isRanged());
    }

    public void step()
    {
        if(!_isIdentified)
        {
            _stepsTillIdentified--;
            if(_stepsTillIdentified<=0)
            {
                GameConsole.addMessage(super.getName()+" [?] was identified to be "+ super.getName()+" ["+_bonusAttack+"]");
                identify();
            }
        }
    }
    public String getName()
    {
        if(_isIdentified)
        {
            if(_isRanged)
            {
                return super.getName() + "("+ _ammoCount +")";
            }
            return super.getName()+" ["+_bonusAttack+"]";
        }
        else
        {
            return super.getName()+" [?]";
        }
    }
    public String getDescription()
    {
        if(_isIdentified)
        {
            if(_isRanged)
            {
                return super.getDescription()+"- There are "+_ammoCount+" of them";
            }
            return super.getDescription()+"- Gives "+(_attack+_bonusAttack)+" attack";
        }
        else
        {
            return super.getDescription()+" - Its effects are not known, but normally this type of weapon gives "+_attack+" attack - Its secrets will be revealed in "+_stepsTillIdentified+" steps";
        }
    }
    public void addAmmo(int ammoToAdd)
    {
        _ammoCount+=ammoToAdd;
    }
    public void decreaseRangedAmmo()
    {
        if(!_isRanged)
        {
            Gdx.app.log("Weapon","Decreasing ammo on a non ranged weapon!");
        }
        _ammoCount--;
    }
    @Override
    public void draw(SpriteBatch batch, float x, float y)
    {
        if(_isCurseKnown && hasCurse())
        {
            batch.setColor(Color.PINK);
        }
        super.draw(batch, x, y);
    }

    @Override
    public void draw(SpriteBatch batch, float x, float y, float scale)
    {
        if(_isCurseKnown && hasCurse())
        {
            batch.setColor(Color.PINK);
        }
        super.draw(batch, x, y, scale);
    }


}

package uni.aau.game.gameobjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import uni.aau.game.gui.GameConsole;
import uni.aau.game.helpers.GameAction;
import uni.aau.game.items.Potion;

public class Player extends Character
{
    private Texture _texture;
    private final String _texturePath = "data/player.png";
    private float _startStr;
    private float _startHp;
    private String _killedBy = null;
    public String getKilledBy(){return _killedBy;}
    public void setKilledBy(String killedBy){_killedBy = killedBy;}

    public boolean isMoving()
    {
        return actionQueue.size()>0 && actionQueue.get(0) != null && actionQueue.get(0).getType() == GameAction.Type.Move;
    }

    public Player(int startStr, int startHp,String name)
    {
        super(name,startStr,startHp);
        _texture = new Texture(Gdx.files.internal(_texturePath));
        _startStr = startStr;
        _startHp = startHp;
    }
    public void retrieveExperience(Monster monster)
    {
        experience+=monster.retrieveExperienceGiven();
        if(experience>=experienceToNextLevel)
        {
            levelUp();
        }
    }
    public float calculateScore()
    {
        return getArmorDefense()+getAttackPower()+level+maxStr+maxHp+experience;
    }
    public void retrieveExperience(Potion potion)
    {
        experience+=potion.getPotency();
        if(experience>=experienceToNextLevel)
        {
            levelUp();
        }
    }


    public void levelUp()
    {
        experience=0;


        level++;
        experienceToNextLevel*=2;
        maxHp=calculateNewStat(_startHp,2000,level,99);
        maxStr =calculateNewStat(_startStr,100,level,99);

        currentStr=maxStr;

        GameConsole.addMessage("Player leveled up! - Welcome to level "+level);
        GameConsole.addMessage("Strength is: "+getMaxStr());
        GameConsole.addMessage("Hp is: "+getMaxHitPoints());
    }
    private float calculateNewStat(float startStat, float maxStat, float currentLevel, float maxLevel)
    {
        return startStat+(maxStat-startStat)*(float)Math.pow((currentLevel+10) / (maxLevel +10),2);
    }

    public void draw(SpriteBatch batch)
    {
        batch.draw(_texture, _position.x, _position.y,_texture.getWidth(),_texture.getHeight(),0,0,_texture.getWidth(),_texture.getHeight(),false,true);
    }

    @Override
    public void damage(int damage)
    {
        super.damage(damage);
        if(getCurrentStatusEffect() == StatusEffect.Poisoned)
        {
            _killedBy = "poison";
        }
    }

    @Override
    public void kill()
    {
        super.kill();
    }
}

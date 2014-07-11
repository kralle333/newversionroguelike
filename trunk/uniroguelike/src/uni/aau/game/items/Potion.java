package uni.aau.game.items;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class Potion extends Item
{
    private String _stringColor;
    private Color _color;
    private int _potency;
    public int getPotency(){return _potency;}
    public Color getColor(){return _color;}
    public String getStringColor(){return _stringColor;}
    public enum PotionType{Unidentified, Healing, Experience, PoisonGas, ParaGas};
    private PotionType _potionType;
    public PotionType getType(){return _isIdentified?_potionType:PotionType.Unidentified;}

    public Potion(String name, String description,boolean isIdentified, TextureRegion textureRegion,int potency, Color color)
    {
        super(name,description,isIdentified,textureRegion,true,false);
        _potionType = getPotionType(name);
        _potency = potency;
        if(_potency>10)
        {
            _name = "Greater "+_name;
        }
        else if(_potency>20)
        {
            _name = "Superior "+_name;
        }
        _color = color;
        _stringColor= convertColorToString(color);
    }
    public Potion(Potion toCopy,int potency)
    {
        this(toCopy.getIdentifiedName(),toCopy.getIdentifiedDescription(),
                toCopy.isIdentified(),toCopy.getTextureRegion(),
                potency,toCopy.getColor());
    }

    public String getName()
    {
        if(_isIdentified)
        {
            return super.getName();
        }
        else
        {
            return _stringColor+" potion";
        }
    }
    public String getDescription()
    {
        if(_isIdentified)
        {
            return super.getDescription();
        }
        else
        {
            return "The effect of this potion is not known";
        }
    }
    private PotionType getPotionType(String name)
    {
        if(name.equals("Potion of Healing"))
        {
            return PotionType.Healing;
        }
        else if(name.equals("Potion of Experience"))
        {
            return PotionType.Experience;
        }
        else if(name.equals("Potion of Poison Gas"))
        {
            return PotionType.PoisonGas;
        }
        else if(name.equals("Potion of Paralysis Gas"))
        {
            return PotionType.ParaGas;
        }
        else
        {
            throw new IllegalArgumentException(name+" does not name a potion type");
        }
    }
    private String convertColorToString(Color color)
    {
        if(color == Color.RED)
        {
            return "red";
        }
        else if(color == Color.BLUE)
        {
            return "blue";
        }
        else if(color == Color.BLACK)
        {
            return "black";
        }
        else if(color == Color.CYAN)
        {
            return "cyan";
        }
        else if(color == Color.GREEN)
        {
            return "green";
        }
        return "unknown";
    }

    @Override
    public void draw(SpriteBatch batch, float x,float y)
    {
        batch.setColor(_color);
        super.draw(batch, x,y);
        batch.setColor(Color.WHITE);
    }
    public void draw(SpriteBatch batch,float x, float y, float scale)
    {
        batch.setColor(_color);
        super.draw(batch, x,y,scale);
        batch.setColor(Color.WHITE);
    }
}

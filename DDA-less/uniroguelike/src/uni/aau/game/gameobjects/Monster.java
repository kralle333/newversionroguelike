package uni.aau.game.gameobjects;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import uni.aau.game.helpers.GameAction;
import uni.aau.game.items.Armor;

public class Monster extends Character
{
    private TextureRegion _textureRegion;
    private int _experienceGiven;
    public int retrieveExperienceGiven()
    {
        int toReturn = _experienceGiven;
        _experienceGiven = 0;
        return toReturn;
    }

    public Monster(String name,int str, int hp,int def,TextureRegion textureRegion,int experienceGiven)
    {
        super(name,str,hp);
        _equippedArmor = new Armor("MonsterArmor","Monsters use this",true,null,def,0);
        _textureRegion=textureRegion;
        _textureRegion.flip(false,true);
        _experienceGiven = experienceGiven;
    }


    public GameAction createNextAction(Player player)
    {
       if(_isDead)
       {
           return null;
       }
        float smallestDistance = Float.MAX_VALUE;
        Tile nextTile = currentTile;
        float currentDistance;

        for(Tile tile : currentTile.getNeighbours())
        {
            if(tile.getTrap() == null)
            {
                currentDistance = tile.distanceTo(player.getCurrentTile());
                if (currentDistance < smallestDistance) {
                    smallestDistance = currentDistance;
                    nextTile = tile;
                }
            }
        }
        if(nextTile != currentTile)
        {
            if(nextTile.getCharacter() == player)
            {
                return new GameAction(this, GameAction.Type.Attack,nextTile,null);
            }
            else
            {
                return new GameAction(this, GameAction.Type.Move,nextTile,null);
            }
        }

        return new GameAction(this, GameAction.Type.Wait,null,null);
    }

    public void draw(SpriteBatch batch)
    {
        if(!_isDead && currentTile.getLightAmount() == Tile.LightAmount.Light)
        {
            batch.draw(_textureRegion,_position.x,_position.y);
        }
    }

    @Override
    public void kill()
    {
        super.kill();
    }
}

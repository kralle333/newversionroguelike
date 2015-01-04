package uni.aau.game.mapgeneration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import uni.aau.game.gameobjects.DungeonMap;
import uni.aau.game.gameobjects.Monster;

public class MonsterGenerator
{

    private static final float percentRange = 0.1f;
    private static final String _monsterTileSetPath = "data/monsterTileSet.png";
    private static final Texture _monsterTileSet=new Texture(Gdx.files.internal(_monsterTileSetPath));

    public static enum MonsterType{BeaverRat,Bat, Troll, DirtGolem,Vampire, SwampMan,Missing}


    public static Monster createMonster(int averageStr, int averageHP,int averageDef)
    {
        int str = (int)RandomGen.getRandomFloat(averageStr*(1-percentRange),averageStr*(1+percentRange));
        int hp = (int)RandomGen.getRandomFloat(averageHP*(1-percentRange),averageHP*(1+percentRange));
        int def = (int)RandomGen.getRandomFloat(averageDef*(1-percentRange),averageDef*(1+percentRange));
        if(str<=0){str=1;}
        if(hp<=0){hp=1;}
        if(def<0){def=0;}

        int exp = hp/15+(str+def)/2;
        if(exp<=0){exp = 1;}

        MonsterType type = getType(str,hp);
        return new Monster(type.toString(),str,hp,def,getTextureRegion(type),exp);
    }

    private static TextureRegion getTextureRegion(MonsterType type)
    {
        return new TextureRegion(_monsterTileSet,type.ordinal()* DungeonMap.TileSize,0,DungeonMap.TileSize,DungeonMap.TileSize);
    }

    //Change to texture later
    private static MonsterType getType(int str,int hp)
    {
        if(str*hp<=50)
        {
            return MonsterType.BeaverRat;
        }
        else if(str*hp<=100)
        {
            return MonsterType.Bat;
        }
        else if(str*hp<=200)
        {
            return MonsterType.Troll;
        }
        else if(str*hp<=400)
        {
            return MonsterType.DirtGolem;
        }
        else if(str*hp<=800)
        {
            return MonsterType.Vampire;
        }
        else if(str*hp<1600)
        {
            return MonsterType.SwampMan;
        }

        return MonsterType.Missing;
    }
}

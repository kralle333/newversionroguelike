package uni.aau.game.mapgeneration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import uni.aau.game.gameobjects.Monster;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.helpers.MonsterAttack;

import java.util.ArrayList;
import java.util.HashMap;

public class MonsterGenerator
{

    private static final float percentRange = 0.1f;
    private static final String _monsterTileSetPath = "data/monsterTileSet.png";
    private static final Texture _monsterTileSet=new Texture(Gdx.files.internal(_monsterTileSetPath));

    public static enum MonsterType{BeaverRat,Bat, Troll, DirtGolem,Vampire, SwampMan,Missing}

    private static ArrayList<MonsterPrototype> monsterPrototypes = new ArrayList<MonsterPrototype>();
    private static int numberOfPrototypes;
    private static int[] maxMonsters = new int[]{3,4,5,6,7,8};

    private static class MonsterPrototype
    {
        public ArrayList<Vector2> probabilityOfAppearance;
        public TextureRegion texture;
        public String name;
        public int minHp;
        public int maxHp;
        public int defense;
        public int dodgeChance;
        public int experience;
        public ArrayList<MonsterAttack> attacks = new ArrayList<MonsterAttack>();

        public MonsterPrototype(String name, int minHp, int maxHp,int defense,int dodgeChance,int experience,Vector2 textureRegionPosition)
        {
            this.name = name;
            this.minHp = minHp;
            this.maxHp = maxHp;
            this.defense = defense;
            this.dodgeChance = dodgeChance;
            this.experience = experience;
            probabilityOfAppearance = new ArrayList<Vector2>();
            this.texture = AssetManager.getTextureRegion("monster", (int)textureRegionPosition.x, (int)textureRegionPosition.y, DungeonMap.TileSize, DungeonMap.TileSize);
            this.texture.flip(false,true);
        }
        public void addProbabilityOfAppearing(float probability, int depth)
        {
            probabilityOfAppearance.add(new Vector2(probability,depth));
        }
    }

    private static boolean prototypesInitialized = false;
    public static void Initialize()
    {
        if(!prototypesInitialized)
        {
            MonsterPrototype beaverRat = new MonsterPrototype("BeaverRat",2,12,1,5,2,new Vector2(0,0));
            beaverRat.addProbabilityOfAppearing(1, 1);
            beaverRat.attacks.add(new MonsterAttack("Bite",1,4,10));

            MonsterPrototype bat = new MonsterPrototype("Bat", 4, 4, 1,15,1, new Vector2(1, 0));
            beaverRat.attacks.add(new MonsterAttack("Bite",1,4,10));
            bat.addProbabilityOfAppearing(0.2f, 1);
            bat.addProbabilityOfAppearing(0.5f, 3);
            bat.addProbabilityOfAppearing(1, 5);

            MonsterPrototype troll = new MonsterPrototype("Troll",10,25, 3, 3,10, new Vector2(2, 0));
            troll.addProbabilityOfAppearing(0.2f, 2);
            troll.addProbabilityOfAppearing(0.4f, 4);
            troll.addProbabilityOfAppearing(0.6f, 6);
            troll.addProbabilityOfAppearing(0.8f, 8);
            troll.addProbabilityOfAppearing(1, 10);

            monsterPrototypes.add(beaverRat);
            monsterPrototypes.add(bat);
            monsterPrototypes.add(troll);
            numberOfPrototypes = monsterPrototypes.size();
            prototypesInitialized=true;
        }
    }

    public static ArrayList<Monster> createMonsters(int depth)
    {
        int numberOfMonsters = maxMonsters[depth];
        int monstersAdded = 0;
        ArrayList<Monster> returnedMonsters = new ArrayList<Monster>();

        while(numberOfMonsters>monstersAdded)
        {
            MonsterPrototype randomMonsterType = monsterPrototypes.get(RandomGen.getRandomInt(0,numberOfPrototypes-1));
            float probabilityOfAdding = 0;
            for(int i = randomMonsterType.probabilityOfAppearance.size()-1;i>=0;i--)
            {
                if(depth>=randomMonsterType.probabilityOfAppearance.get(i).y)
                {
                    probabilityOfAdding = randomMonsterType.probabilityOfAppearance.get(i).x;
                    break;
                }
            }
            float probabilityToBeat=RandomGen.getRandomFloat(0,1);
            if(probabilityOfAdding>probabilityToBeat)
            {
                returnedMonsters.add(createMonsterFromPrototype(randomMonsterType));
                monstersAdded++;
            }

        }
        return returnedMonsters;
    }


    private static Monster createMonsterFromPrototype(MonsterPrototype prototype)
    {
        int str = prototype.attacks.size()>0?RandomGen.getRandomInt(1,prototype.attacks.get(0).minDamage):0;
        int hp = RandomGen.getRandomInt(prototype.minHp,prototype.maxHp);
        Monster monsterToReturn = new Monster(prototype.name,str,hp,prototype.defense,prototype.dodgeChance,prototype.experience,prototype.texture);
        monsterToReturn.copyAttacksToList(prototype.attacks);
        return monsterToReturn;
    }
    public static Monster createMonsterForDDA(int averageStr, int averageHP, int averageDef)
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
        return new Monster(type.toString(),str,hp,def,5,exp,getTextureRegion(type));
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

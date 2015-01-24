package uni.aau.game.mapgeneration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.w3c.dom.Text;
import uni.aau.game.gameobjects.Monster;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.helpers.TileSetCoordinate;
import uni.aau.game.items.Item;
import uni.aau.game.items.ItemManager;
import uni.aau.game.items.Weapon;

import java.util.*;

public class MonsterGenerator
{
    private static ArrayList<MonsterPrototype> monsterPrototypes = new ArrayList<MonsterPrototype>();
    private static int numberOfPrototypes;
    private static int[] maxMonsters = new int[]{3,4,5,6,7,8};

    private static class MonsterPrototype
    {
        public ArrayList<Vector2> probabilityOfAppearance;
        public TextureRegion texture;
        public Monster.Nature nature;
        public String name;
        public int minHp;
        public int maxHp;
        public int defense;
        public int dodgeChance;
        public int experience;
        public Weapon attack;
        public HashMap<Item,Float> droppedItemsAndProbability = new HashMap<Item,Float>();

        public MonsterPrototype(String name, int minHp, int maxHp,int defense,int dodgeChance,int experience,Monster.Nature nature,TileSetCoordinate textureRegionPosition)
        {
            this.name = name;
            this.minHp = minHp;
            this.maxHp = maxHp;
            this.defense = defense;
            this.dodgeChance = dodgeChance;
            this.experience = experience;
            probabilityOfAppearance = new ArrayList<Vector2>();
            this.nature = nature;
            this.texture = AssetManager.getTextureRegion("monster", textureRegionPosition.x,textureRegionPosition.y, DungeonMap.TileSize, DungeonMap.TileSize);
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
            MonsterPrototype beaverRat = new MonsterPrototype("BeaverRat",2,10,1,5,2, Monster.Nature.Aggressive,new TileSetCoordinate(0,0));
            beaverRat.addProbabilityOfAppearing(1, 1);
            beaverRat.attack = new Weapon("Bite","It's a bite",true,null,1,4,0,10,false);

            MonsterPrototype bat = new MonsterPrototype("Bat", 4, 4, 1,15,1,Monster.Nature.Aggressive, new TileSetCoordinate(1, 0));
            bat.attack = new Weapon("Bite","It's a bite",true,null,1,4,0,10,false);
            bat.addProbabilityOfAppearing(0.2f, 1);
            bat.addProbabilityOfAppearing(0.5f, 3);
            bat.addProbabilityOfAppearing(1, 5);

            MonsterPrototype troll = new MonsterPrototype("Troll",10,25, 3, 3,10,Monster.Nature.Aggressive, new TileSetCoordinate(2, 0));
            troll.attack = new Weapon("Club","Big club",true,null,1,10,0,5,false);
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

    public static Monster generateChest(int depth)
    {
        TextureRegion chestRegion = AssetManager.getTextureRegion("tile","chest",DungeonMap.TileSize,DungeonMap.TileSize);
        chestRegion.flip(false,true);
        Monster chest = new Monster("Chest",0,1,0,1,0, Monster.Nature.Passive,chestRegion);
        int itemType = RandomGen.getRandomInt(0,5);//Chests more likely to spawn scrolls and potions
        switch (itemType)
        {
            case 0:chest.addItemToDrop(ItemManager.getRandomPotion(depth)); break;
            case 1:chest.addItemToDrop(ItemManager.getRandomScroll());break;
            case 2:chest.addItemToDrop(ItemManager.getRandomPotion(depth));break;
            case 3:chest.addItemToDrop(ItemManager.getRandomScroll());break;
            case 4:chest.addItemToDrop(ItemManager.getRandomWeapon(depth));break;
            case 5:chest.addItemToDrop(ItemManager.getRandomArmor(depth));break;
        }

        return chest;
    }
    public static ArrayList<Monster> generateMonsters(int depth)
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
        int str = RandomGen.getRandomInt(0, prototype.attack.getMinDamage());
        int hp = RandomGen.getRandomInt(prototype.minHp,prototype.maxHp);
        ArrayList<Item> droppedItems = new ArrayList<Item>();
        for(Map.Entry<Item,Float> item : prototype.droppedItemsAndProbability.entrySet())
        {
            float probabilityToBeat=RandomGen.getRandomFloat(0,1);
            if(item.getValue()>=probabilityToBeat)
            {
                droppedItems.add(item.getKey());
            }
        }
        Monster monsterToReturn = new Monster(prototype.name,str,hp,prototype.defense,prototype.dodgeChance,prototype.experience,prototype.nature,prototype.texture);
        for(Item item : droppedItems)
        {
            monsterToReturn.addItemToDrop(item);
        }
        monsterToReturn.equip(new Weapon(prototype.attack,0));

        return monsterToReturn;
    }

}

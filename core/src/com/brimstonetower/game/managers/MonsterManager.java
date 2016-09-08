package com.brimstonetower.game.managers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.gameobjects.Monster;
import com.brimstonetower.game.gameobjects.equipment.Weapon;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.helpers.TileSetCoordinate;
import com.brimstonetower.game.map.DungeonMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MonsterManager
{
    private static ArrayList<MonsterPrototype> monsterPrototypes = new ArrayList<MonsterPrototype>();
    private static int numberOfPrototypes;

    private static class MonsterPrototype
    {
        public ArrayList<Vector2> probabilityOfAppearance;
        public TextureRegion aliveTexture;
        public TextureRegion deadTexture;
        public Monster.Nature nature;
        public String name;
        public int minHp;
        public int maxHp;
        public int defense;
        public int dodgeChance;
        public int experience;
        public Weapon attack;
        public HashMap<Item, Float> droppedItemsAndProbability = new HashMap<Item, Float>();

        public MonsterPrototype(String name, int minHp, int maxHp, int defense, int dodgeChance, int experience, Monster.Nature nature, TileSetCoordinate textureRegionPositionAlive,TileSetCoordinate textureRegionPositionDead)
        {
            this.name = name;
            this.minHp = minHp;
            this.maxHp = maxHp;
            this.defense = defense;
            this.dodgeChance = dodgeChance;
            this.experience = experience;
            probabilityOfAppearance = new ArrayList<Vector2>();
            this.nature = nature;
            this.aliveTexture = AssetManager.getTextureRegion("monster", textureRegionPositionAlive.x, textureRegionPositionAlive.y, DungeonMap.TileSize, DungeonMap.TileSize);
            this.deadTexture = AssetManager.getTextureRegion("monster", textureRegionPositionDead.x, textureRegionPositionDead.y, DungeonMap.TileSize, DungeonMap.TileSize);
        }

        public void addProbabilityOfAppearing(float probability, int depth)
        {
            probabilityOfAppearance.add(new Vector2(depth,probability));
        }
    }

    private static boolean prototypesInitialized = false;

    public static void Initialize()
    {
        if (!prototypesInitialized)
        {
            MonsterPrototype rat = new MonsterPrototype("Rat", 5, 10, 0, 5, 2, Monster.Nature.Aggressive, new TileSetCoordinate(0, 0),new TileSetCoordinate(0,1));
            rat.addProbabilityOfAppearing(1, 1);
            rat.addProbabilityOfAppearing(0, 5);
            rat.attack = new Weapon("Bite", "It's a bite", true, null, 1, 4, 0, 1,1,10, Weapon.RangeType.Melee);

            MonsterPrototype skeleton = new MonsterPrototype("Skeleton Soldier", 10, 20, 2, 10, 10, Monster.Nature.Aggressive, new TileSetCoordinate(1, 0),new TileSetCoordinate(1, 1));
            skeleton.attack = new Weapon("Sword", "Sword", true, null, 10, 10, 0, 1,1, 10, Weapon.RangeType.Melee);
            skeleton.addProbabilityOfAppearing(0.33f, 3);
            skeleton.addProbabilityOfAppearing(0.66f, 5);
            skeleton.addProbabilityOfAppearing(0.99f, 7);
            skeleton.addProbabilityOfAppearing(0, 10);

            MonsterPrototype skeletonArcher = new MonsterPrototype("Skeleton Archer", 10, 10, 0, 10, 10, Monster.Nature.Aggressive, new TileSetCoordinate(2, 0),new TileSetCoordinate(2, 1));
            skeletonArcher.attack = new Weapon("Bow", "Bow", true, null, 4, 7, 0,2,4,  10,Weapon.RangeType.AmmoThrower);
            skeletonArcher.addProbabilityOfAppearing(0.33f, 4);
            skeletonArcher.addProbabilityOfAppearing(0.66f, 6);
            skeletonArcher.addProbabilityOfAppearing(0.99f, 8);
            skeletonArcher.addProbabilityOfAppearing(0, 10);

            monsterPrototypes.add(rat);
            monsterPrototypes.add(skeleton);
            monsterPrototypes.add(skeletonArcher);
            numberOfPrototypes = monsterPrototypes.size();
            prototypesInitialized = true;
        }
    }


    public static ArrayList<Monster> generateMonsters(int numberOfMonsters,int depth)
    {
        int monstersAdded = 0;
        ArrayList<Monster> returnedMonsters = new ArrayList<Monster>();

        while (numberOfMonsters > monstersAdded)
        {
            MonsterPrototype randomMonsterType = monsterPrototypes.get(RandomGen.getRandomInt(0, numberOfPrototypes - 1));
            float probabilityOfAdding = 0;
            for (int i = randomMonsterType.probabilityOfAppearance.size() - 1; i >= 0; i--)
            {
                if (depth >= randomMonsterType.probabilityOfAppearance.get(i).x)
                {
                    probabilityOfAdding = randomMonsterType.probabilityOfAppearance.get(i).y;
                    break;
                }
            }
            float probabilityToBeat = RandomGen.getRandomFloat(0, 1);
            if (probabilityOfAdding > probabilityToBeat)
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
        int hp = RandomGen.getRandomInt(prototype.minHp, prototype.maxHp);
        ArrayList<Item> droppedItems = new ArrayList<Item>();
        for (Map.Entry<Item, Float> item : prototype.droppedItemsAndProbability.entrySet())
        {
            float probabilityToBeat = RandomGen.getRandomFloat(0, 1);
            if (item.getValue() >= probabilityToBeat)
            {
                droppedItems.add(item.getKey());
            }
        }
        Monster monsterToReturn = new Monster(prototype.name, str, hp, prototype.defense, prototype.dodgeChance, prototype.experience, prototype.nature, prototype.aliveTexture,prototype.deadTexture);
        for (Item item : droppedItems)
        {
            monsterToReturn.addItemToDrop(item);
        }
        monsterToReturn.equip(new Weapon(prototype.attack, 0));

        return monsterToReturn;
    }

}

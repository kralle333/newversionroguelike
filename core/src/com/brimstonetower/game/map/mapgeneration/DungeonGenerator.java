package com.brimstonetower.game.map.mapgeneration;


import com.brimstonetower.game.gameobjects.Chest;
import com.brimstonetower.game.gameobjects.Trap;
import com.brimstonetower.game.gameobjects.equipment.Armor;
import com.brimstonetower.game.gameobjects.equipment.Weapon;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.ItemManager;
import com.brimstonetower.game.managers.MonsterManager;
import com.brimstonetower.game.map.DungeonMap;

import java.util.ArrayList;

public class DungeonGenerator
{
    public static DungeonMap GenerateCompleteDungeon(int depth)
    {
        MonsterManager.Initialize();
        int width = 30 + (int) (depth * RandomGen.getRandomFloat(1, 3));
        int height = 30 + (int) (depth * RandomGen.getRandomFloat(1, 3));
        BSPMapNode bspMap = MapGenerator.generateMap(width,height);

        DungeonMap newDungeon = new DungeonMap("tile",bspMap);
        newDungeon.createStairs();

        //Add chests
        ArrayList<Chest> chests = new ArrayList<Chest>();
        int numberOfItems = RandomGen.getRandomInt(width/6,width/3);
        for (int i = 0; i < numberOfItems; i++)
        {
            chests.add(generateChest(depth));
        }
        newDungeon.addChests(chests);

        //Add monsters
        newDungeon.addMonsters(MonsterManager.generateMonsters(depth));

        //Traps
        int trapCount = RandomGen.getRandomInt(width/12,width/6);
        ArrayList<Trap> traps = new ArrayList<Trap>();
        for(int i = 0;i<trapCount;i++)
        {
            traps.add(TrapGenerator.generateTrap(depth));
        }
        newDungeon.addTraps(traps);

        return newDungeon;
    }

    private static Chest generateChest(int depth)
    {
        final int equipmentCurseRate = 33;
        int itemType = RandomGen.getRandomInt(0, 8);//Chests more likely to spawn scrolls and potions
        Chest chest = new Chest(itemType>6?2:1);

        switch (itemType)
        {
            case 0:
            case 1:
            case 2:
            case 3:
                chest.addItemToDrop(ItemManager.getRandomPotion());
                break;
            case 4:
            case 5:
            case 6:
                chest.addItemToDrop(ItemManager.getRandomScroll());
                break;
            case 7:
                Weapon weapon =ItemManager.getRandomWeapon(depth);
                if(weapon.getRangeType() == Weapon.RangeType.Melee && weapon.getIdentifiedMaxDamage()<weapon.getExpectedMaxDamage())
                {
                    if(RandomGen.getRandomInt(1,100)<=equipmentCurseRate)
                    {
                        weapon.curse();
                    }
                }
                chest.addItemToDrop(weapon);
                break;
            case 8:
                Armor armor =ItemManager.getRandomArmor(depth);
                if(armor.getIdentifiedDefense()<armor.getExpectedDefense())
                {
                    if(RandomGen.getRandomInt(1,100)<=equipmentCurseRate)
                    {
                        armor.curse();
                    }
                }
                chest.addItemToDrop(armor);
                break;
        }

        return chest;
    }

}

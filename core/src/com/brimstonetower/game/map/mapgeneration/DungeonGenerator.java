package com.brimstonetower.game.map.mapgeneration;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.brimstonetower.game.gameobjects.Chest;
import com.brimstonetower.game.gameobjects.Monster;
import com.brimstonetower.game.gameobjects.items.Armor;
import com.brimstonetower.game.gameobjects.items.Weapon;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
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
        DungeonMap newDungeon = MapGenerator.generateMap(width, height, "tile");
        newDungeon.createStairs();

        //Add chests
        ArrayList<Chest> chests = new ArrayList<Chest>();
        int numberOfItems = 5;
        for (int i = 0; i < numberOfItems; i++)
        {
            chests.add(generateChest(depth));
        }
        newDungeon.addChests(chests);

        //Add monsters
        newDungeon.addMonsters(MonsterManager.generateMonsters(depth));

        return newDungeon;
    }

    private static Chest generateChest(int depth)
    {
        final int equipmentCurseRate = 50;

        TextureRegion chestRegion = AssetManager.getTextureRegion("tile", "chest", DungeonMap.TileSize, DungeonMap.TileSize);
        chestRegion.flip(false, true);
        Chest chest = new Chest(0);
        int itemType = RandomGen.getRandomInt(0, 7);//Chests more likely to spawn scrolls and potions
        switch (itemType)
        {
            case 0:
            case 1:
            case 2:
                chest.addItemToDrop(ItemManager.getRandomPotion());
                break;
            case 3:
            case 4:
            case 5:
                chest.addItemToDrop(ItemManager.getRandomScroll());
                break;
            case 6:
                Weapon weapon =ItemManager.getRandomWeapon(depth);
                if(!weapon.isRanged() && weapon.getIdentifiedMaxDamage()<weapon.getExpectedMaxDamage())
                {
                    if(RandomGen.getRandomInt(1,100)<=equipmentCurseRate)
                    {
                        weapon.curse();
                    }
                }
                chest.addItemToDrop(weapon);
                break;
            case 7:
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

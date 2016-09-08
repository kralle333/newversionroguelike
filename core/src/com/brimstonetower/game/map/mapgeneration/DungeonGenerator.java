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

        DungeonMap newDungeon = new DungeonMap("tile",width,height);
        newDungeon.createStairs();

        //Add chests
        newDungeon.addChests(depth);

        //Add monsters
        newDungeon.addMonsters(depth);

        //Traps
        newDungeon.addTraps(depth);

        return newDungeon;
    }



}

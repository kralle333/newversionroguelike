package com.brimstonetower.game.map.mapgeneration;


import com.brimstonetower.game.gameobjects.Monster;
import com.brimstonetower.game.helpers.RandomGen;
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
        ArrayList<Monster> chests = new ArrayList<Monster>();
        int numberOfItems = 5;
        for (int i = 0; i < numberOfItems; i++)
        {
            chests.add(MonsterManager.generateChest(depth));
        }
        newDungeon.addMonsters(chests);

        //Add monsters
        newDungeon.addMonsters(MonsterManager.generateMonsters(depth));

        return newDungeon;
    }
}

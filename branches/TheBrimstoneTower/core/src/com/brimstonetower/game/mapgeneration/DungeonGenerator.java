package com.brimstonetower.game.mapgeneration;


import com.brimstonetower.game.gameobjects.Monster;

import java.util.ArrayList;

public class DungeonGenerator
{
    public static DungeonMap GenerateCompleteDungeon(int depth)
    {
        MonsterGenerator.Initialize();
        int width = 30 + (int) (depth * RandomGen.getRandomFloat(1, 3));
        int height = 30 + (int) (depth * RandomGen.getRandomFloat(1, 3));
        DungeonMap newDungeon = MapGenerator.generateMap(width, height, "tile");
        newDungeon.createStairs();

        //Add chests
        ArrayList<Monster> chests = new ArrayList<Monster>();
        int numberOfItems = 5;
        for (int i = 0; i < numberOfItems; i++)
        {
            chests.add(MonsterGenerator.generateChest(depth));
        }
        newDungeon.addMonsters(chests);

        //Add monsters
        newDungeon.addMonsters(MonsterGenerator.generateMonsters(depth));

        return newDungeon;
    }
}

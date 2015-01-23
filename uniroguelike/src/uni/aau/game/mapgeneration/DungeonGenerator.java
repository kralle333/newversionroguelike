package uni.aau.game.mapgeneration;


public class DungeonGenerator
{
    public static DungeonMap GenerateCompleteDungeon(int depth)
    {
        int width = 30+(int)(depth*RandomGen.getRandomFloat(1,3));
        int height = 30+(int)(depth*RandomGen.getRandomFloat(1,3));
        DungeonMap newDungeon = MapGenerator.generateMap(width,height,"tile");
        newDungeon.createStairs();
        MonsterGenerator.Initialize();
        newDungeon.addMonsters(MonsterGenerator.createMonsters(depth));


        return newDungeon;
    }
}

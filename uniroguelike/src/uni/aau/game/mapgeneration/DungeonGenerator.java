package uni.aau.game.mapgeneration;


public class DungeonGenerator
{
    public static DungeonMap GenerateCompleteDungeon(int depth)
    {
        int width = 25+(int)(depth*RandomGen.getRandomFloat(0.5f,1.5f));
        int height = 20+(int)(depth*RandomGen.getRandomFloat(0.5f,1.5f));
        DungeonMap newDungeon = MapGenerator.generateMap(width,height,"tile");
        newDungeon.createStairs();
        MonsterGenerator.Initialize();
        newDungeon.addMonsters(MonsterGenerator.createMonsters(depth));


        return newDungeon;
    }
}

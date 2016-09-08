package com.brimstonetower.game.map.mapgeneration.rooms;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.BreakableObject;
import com.brimstonetower.game.gameobjects.Chest;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.Tile;
import com.brimstonetower.game.map.mapgeneration.ChestGenerator;
public class DoomRoom extends Room
{
    private enum DoomType
    {
        None, Monsters, Traps
    }
    ;
    private DoomType _type = DoomType.None;
    private Chest _chest;

    public DoomRoom(int x, int y, int width, int height)
    {
        super(x, y, width, height);
        if(RandomGen.getRandomInt(1, 100) >= 75)
        {
            _type = RandomGen.getRandomInt(1, 100) >= 50 ? DoomType.Monsters : DoomType.Traps;
            _chest = ChestGenerator.generateChests(5);

        }
    }

    @Override
    protected void finalize()
    {
        int numberOfFloorTiles = getNumberOfFloorTiles();
        if(numberOfFloorTiles >= 6)
        {
            int numberOfBreakables = RandomGen.getRandomInt(1, (int) ((double) numberOfFloorTiles / 2.0));
            float[][] tileScores = new float[getWidth()-1][];
            for(int i = 0;i<getWidth()-1;i++)
            {
                tileScores[i] = new float[getHeight()-1];
            }
            //Create weighted random by calculating the distance to the center
            Vector2 center = getCenterPos();
            for(int x = 1; x < getWidth() - 1; x++)
            {
                for(int y = 1; y < getHeight() - 1; y++)
                {
                    tileScores[x][y] = Vector2.dst(x,y,center.x,center.y);
                }
            }
            float currentScore =0;
            float bestScore=0;
            Tile bestCandidate = null;
            //Insert a random number of breakables
            while(numberOfBreakables > 0)
            {
                bestCandidate = null;
                bestScore = 0;
                //The best scored empty tile gets a breakable
                for(int x = 1; x < getWidth() - 1; x++)
                {
                    for(int y = 1; y < getHeight() - 1; y++)
                    {
                        currentScore = RandomGen.getRandomFloat(1,100)*tileScores[x][y];
                        if(currentScore>bestScore && _tiles[x][y].isEmpty())
                        {
                            bestCandidate = _tiles[x][y];
                            bestScore = currentScore;
                        }
                    }
                }
                //TODO: Find proper selection of boxes
                BreakableObject object = new BreakableObject("Box", AssetManager.getTileTextureRegion("interior",RandomGen.getRandomInt(0,4),0));
                object.placeOnTile(bestCandidate);
                _breakableObjects.add(object);
                numberOfBreakables--;
            }
        }

    }

    public void activate()
    {

    }
}

package uni.aau.game.helpers;

import com.badlogic.gdx.Gdx;
import uni.aau.game.mapgeneration.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class PathFinder
{
    private static final ArrayList<Tile> closedSet = new ArrayList<Tile>();
    private static final ArrayList<Tile> openSet = new ArrayList<Tile>();
    private static final HashMap<Tile,Tile> cameFrom = new HashMap<Tile,Tile>();

    private static final HashMap<Tile,Float> fScores = new HashMap<Tile,Float>();
    private static final HashMap<Tile,Float> gScores = new HashMap<Tile, Float>();
    private static final boolean isDebugging = false;

    ///A* algorithm
    public static ArrayList<Tile> getPath(Tile from,Tile to)
    {
        closedSet.clear();
        openSet.clear();
        cameFrom.clear();
        fScores.clear();
        gScores.clear();
        float tentative_g_score;

        openSet.add(from);
        gScores.put(from,0.0f);
        fScores.put(from,gScores.get(from)+getManhattanDistance(from,to));

        Tile currentNode = from;
        float smallestFScore;

        while(!openSet.isEmpty())
        {
            smallestFScore = Float.MAX_VALUE;
            for(Tile tile : openSet)
            {
                if(isDebugging) Gdx.app.log("Pathfinder","Node"+tile.getTilePosition().toString()+" from openset - has fscore: "+String.valueOf(fScores.get(tile)));

                if(fScores.get(tile)<smallestFScore)
                {
                    smallestFScore = fScores.get(tile);
                    currentNode = tile;
                }
            }
            if(currentNode == to)
            {
                if(isDebugging)
                    Gdx.app.log("Pathfinder","Returning path");

                return reconstructPath(to);
            }
            openSet.remove(currentNode);
            closedSet.add(currentNode);
            if(isDebugging)
                Gdx.app.log("Pathfinder","Current tile is at: "+currentNode.getTilePosition().toString()+"-fScore is: "+fScores.get(currentNode)+"This tile has neighbours: "+currentNode.getWalkableNeighbours().size());
            for(Tile neighbour : currentNode.getWalkableNeighbours())
            {
                if(closedSet.contains(neighbour))
                {
                    continue;
                }
                //Always a distance of 1 between all neighbours
                tentative_g_score = gScores.get(currentNode) + 1;


                float gScoreNeighbour = 0;
                if(gScores.get(neighbour)!= null)
                {
                    gScoreNeighbour = gScores.get(neighbour);
                }

                if(tentative_g_score < gScoreNeighbour ||!openSet.contains(neighbour))
                {
                    cameFrom.put(neighbour,currentNode);
                    gScores.put(neighbour, tentative_g_score);
                    fScores.put(neighbour,gScores.get(neighbour)+getManhattanDistance(neighbour,to));
                    if(!openSet.contains(neighbour))
                    {
                        openSet.add(neighbour);
                    }
                }
            }
        }
        if(isDebugging)
            Gdx.app.log("Pathfinder","Failure!");
        return null;
    }

    private static ArrayList<Tile> reconstructPath(Tile goal)
    {
        final ArrayList<Tile> constructedPath = new ArrayList<Tile>();
        constructedPath.clear();
        Tile currentNode = goal;
        while(cameFrom.get(currentNode) != null)
        {
            constructedPath.add(currentNode);
            currentNode = cameFrom.get(currentNode);
        }
        Collections.reverse(constructedPath);
        return constructedPath;
    }

    private static float getManhattanDistance(Tile from, Tile to)
    {
        return Math.abs(to.getX()-from.getX())+Math.abs(to.getY()-from.getY());
    }
    public static float getEuclideanDistance(Tile from,Tile to)
    {
        return (float)Math.sqrt(Math.pow(from.getX()-to.getX(),2)+Math.pow(from.getY()-to.getY(),2));
    }

}

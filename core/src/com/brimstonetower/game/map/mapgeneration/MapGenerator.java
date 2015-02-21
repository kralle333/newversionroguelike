package com.brimstonetower.game.map.mapgeneration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.map.DungeonMap;

import java.util.ArrayList;

public class MapGenerator
{
    public static final int minWidth = 8;
    public static final int minHeight = 8;
    private static BSPMapNode rootNode;

    public static BSPMapNode generateMap(int width, int height)
    {

        rootNode = new BSPMapNode(0,0,width, height,0,null);

        //Creates the BSP tree and saves the leaf nodes
        ArrayList<BSPMapNode> leaves = createBSPAndGetLeaves(rootNode);

        //Create rooms
        for (BSPMapNode leaf : leaves)
        {
            leaf.createRoom();
        }

        rootNode.connectChildren();

        Gdx.app.log("MapGen", "Created " + leaves.size() + " rooms");

        return rootNode;
    }

    private static ArrayList<BSPMapNode> createBSPAndGetLeaves(BSPMapNode root)
    {

        final ArrayList<BSPMapNode> nodesLeft = new ArrayList<BSPMapNode>();
        final ArrayList<BSPMapNode> leaves = new ArrayList<BSPMapNode>();
        nodesLeft.clear();
        leaves.clear();
        BSPMapNode currentNode;

        nodesLeft.add(root);

        while (!nodesLeft.isEmpty())
        {
            currentNode = nodesLeft.remove(0);
            if (currentNode.canSplit())
            {
                currentNode.split();
                nodesLeft.add(currentNode.getLeftNode());
                nodesLeft.add(currentNode.getRightNode());
            }
            else
            {
                leaves.add(currentNode);
            }
        }
        return leaves;
    }


    public static void draw(SpriteBatch batch)
    {
        if (rootNode != null)
        {
            rootNode.draw(batch);
        }
    }
}

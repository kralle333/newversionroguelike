package com.brimstonetower.game.map.mapgeneration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.map.Tile;
import com.brimstonetower.game.map.mapgeneration.rooms.DoomRoom;
import com.brimstonetower.game.map.mapgeneration.rooms.Room;
import com.brimstonetower.game.map.mapgeneration.rooms.SuiteRoom;

import java.util.ArrayList;

public class MapGenerator
{
    public static final int minWidth = 10;
    public static final int minHeight = 10;
    public static final int maxWidth = 20;
    public static final int maxHeight = 20;
    private static Tile[][] map;
    private static BSPMapNode rootNode;

    private static ArrayList<Room> _rooms = new ArrayList<>();
    public static ArrayList<Room> getRooms(){return _rooms;}

    public static Tile[][] generateMap(int width, int height)
    {
        map = new Tile[width][height];
        _rooms.clear();
        rootNode = new BSPMapNode(0,0,width, height,0,null);

        //Creates the BSP tree and saves the leaf nodes
        ArrayList<BSPMapNode> leaves = createBSPAndGetLeaves(rootNode);

        //Create rooms
        for (BSPMapNode leaf : leaves)
        {
            Room room = createRoom(leaf.getX(),leaf.getY(),leaf.getWidth(),leaf.getHeight());
            leaf.room = room;
        }

        rootNode.connectChildren(map);

        //Try fitting in extra rooms
        int numberOfExtraRooms = RandomGen.getRandomInt(1,(int)(_rooms.size()/3f));
        int tries = 0;
        while(numberOfExtraRooms>0 && tries <1000)
        {

            int roomWidth = RandomGen.getRandomInt(6,minWidth);
            int roomHeight = RandomGen.getRandomInt(6,minHeight);
            int xRoom = RandomGen.getRandomInt(0,width-1-roomWidth);
            int yRoom =RandomGen.getRandomInt(0,height-1-roomHeight);
            if(canPlaceRoom(xRoom,yRoom,roomWidth,roomHeight))
            {
                Room room =getRandomRoomType(xRoom,yRoom,roomWidth,roomHeight);
                room.setWallsAndFloorTiles();

                Tile[][] roomTiles = room.getTiles();
                for(int xx = 0;xx<room.getWidth();xx++)
                {
                    for(int yy =0;yy<room.getHeight();yy++)
                    {
                        int tileX = roomTiles[xx][yy].getTileX();
                        int tileY = roomTiles[xx][yy].getTileY();
                        map[tileX][tileY] = roomTiles[xx][yy];
                    }
                }

                _rooms.add(room);
                Gdx.app.log("MapGen","Put a small room at: "+xRoom+","+yRoom);
                //Find closest room
                float shortestDistance = Float.MAX_VALUE;
                Vector2 newRoomCenter = room.getCenterWorldPos();
                Room closestRoom =null;
                for(Room placedRoom : _rooms)
                {
                    if(room==placedRoom)continue;

                    float distance = newRoomCenter.dst2(placedRoom.getCenterWorldPos());
                    if(distance<shortestDistance)
                    {
                        shortestDistance = distance;
                        closestRoom=placedRoom;
                    }
                }
                Corridor c = new Corridor(closestRoom,room);
                c.generate(map);

                _rooms.add(room);
            }
            tries++;
        }

        Room room1 = null;
        Room room2 = null;
        double shortestDist = Double.MAX_VALUE;
        double thisDist = 0;
        for(int i = 0;i<_rooms.size()-1;i++)
        {
            for(int j = i+1;j<_rooms.size();j++)
            {
                thisDist=_rooms.get(i).getCenterWorldPos().dst(_rooms.get(j).getCenterWorldPos());
                if(thisDist<shortestDist && !_rooms.get(i).isConnected(_rooms.get(j)))
                {
                    room1 = _rooms.get(i);
                    room2 = _rooms.get(j);
                }
            }
        }
        if(room1!= null && room2!=null && false)
        {
            Corridor c = new Corridor(room1,room2);
            c.generate(map);
            Gdx.app.log("MapGen","Created a new corridor");
        }
        Gdx.app.log("MapGen", "Created " + leaves.size() + " rooms");

        return map;
    }

    private static boolean canPlaceRoom(int xRoom, int yRoom, int roomWidth, int roomHeight)
    {

        for (int xx = xRoom; xx < xRoom+roomWidth+1; xx++)
        {
            for (int yy = yRoom; yy < yRoom+roomHeight+1; yy++)
            {
                if (map[xx][yy]!= null)
                {
                    return false;
                }
            }
        }
        return true;
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

    private static Room createRoom(int x, int y, int width, int height)
    {
        int roomWidth = RandomGen.getRandomInt(MapGenerator.minWidth, width);
        int roomHeight = RandomGen.getRandomInt(MapGenerator.minHeight, height);
        int roomX = x + RandomGen.getRandomInt(0, width - roomWidth);
        int roomY = y + RandomGen.getRandomInt(0, height - roomHeight);

        Room room = getRandomRoomType(roomX,roomY,roomWidth,roomHeight);
        room.setWallsAndFloorTiles();

        Tile[][] roomTiles = room.getTiles();
        for(int xx = 0;xx<room.getWidth();xx++)
        {
            for(int yy =0;yy<room.getHeight();yy++)
            {
                int tileX = roomTiles[xx][yy].getTileX();
                int tileY = roomTiles[xx][yy].getTileY();
                map[tileX][tileY] = roomTiles[xx][yy];
            }
        }

        _rooms.add(room);
        return room;
    }

    private static Room getRandomRoomType(int x, int y, int width, int height)
    {
        Room selectedRoom = null;
        int randIndex = RandomGen.getRandomInt(0,1);
        switch(randIndex)
        {
            case 0:selectedRoom = new DoomRoom(x,y,width,height);break;
            case 1:selectedRoom = new SuiteRoom(x,y,width,height);break;
        }
        return selectedRoom;
    }

    public static void draw(SpriteBatch batch)
    {
        if (rootNode != null)
        {
            rootNode.draw(batch);
        }
    }
}

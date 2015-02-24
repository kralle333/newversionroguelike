package com.brimstonetower.game.map.mapgeneration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.map.Tile;

import java.util.ArrayList;
import java.util.HashSet;

public class BSPMapNode
{
    private final boolean drawSplits = true;

    private int _x;

    public int getX()
    {
        return _x;
    }

    private int _y;

    public int getY()
    {
        return _y;
    }

    private int _width;

    public int getWidth()
    {
        return _width;
    }

    private int _height;

    public int getHeight()
    {
        return _height;
    }

    public String regionToString()
    {
        return _x + "," + _y + "," + _width + "," + _height;
    }

    private Room _room;

    public Room getRoom()
    {
        return _room;
    }

    private ArrayList<Corridor> _corridors = new ArrayList<Corridor>();

    public ArrayList<Corridor> getCorridors()
    {
        return _corridors;
    }

    private BSPMapNode _parent;

    public BSPMapNode getParent()
    {
        return _parent;
    }

    private boolean _isLeaf = true;

    public boolean isLeaf()
    {
        return _isLeaf;
    }

    private BSPMapNode _leftNode;

    public BSPMapNode getLeftNode()
    {
        return _leftNode;
    }

    private BSPMapNode _rightNode;

    public BSPMapNode getRightNode()
    {
        return _rightNode;
    }

    private boolean doVerticalSplit = false;

    public boolean wasVerticallySplit()
    {
        return doVerticalSplit;
    }

    private int split;
    private int _level = -1;
    private boolean isDebugging = false;
    private boolean _hasBeenConnected = true;

    public boolean hasBeenConnected()
    {
        return _hasBeenConnected;
    }

    public BSPMapNode(int x, int y, int width, int height, int level, BSPMapNode parent)
    {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _parent = parent;
        _level = level;
    }

    public Tile[][] convertToDoubleArray()
    {
        Tile[][] tiles = new Tile[_width][_height];

        HashSet<Corridor> checkedCorridors = new HashSet<Corridor>();
        ArrayList<BSPMapNode> nodesLeft = new ArrayList<BSPMapNode>();
        nodesLeft.add(this);
        while(!nodesLeft.isEmpty())
        {
            BSPMapNode currentNode = nodesLeft.remove(0);
            if(currentNode.getRoom()!=null)
            {
                Tile[][] roomTiles = currentNode.getRoom().getTiles();
                for(int x = 0;x<currentNode.getRoom().getWidth();x++)
                {
                    for(int y =0;y<currentNode.getRoom().getHeight();y++)
                    {
                        int tileX = roomTiles[x][y].getTileX();
                        int tileY = roomTiles[x][y].getTileY();
                        tiles[tileX][tileY] = roomTiles[x][y];
                    }
                }
            }
            ArrayList<Corridor> corridors = currentNode.getCorridors();
            for(Corridor corridor : corridors)
            {
                if(!checkedCorridors.contains(corridor))
                {
                    ArrayList<Tile> corridorTiles = corridor.getTiles();
                    for(Tile tile : corridorTiles)
                    {
                        int tileX = tile.getTileX();
                        int tileY = tile.getTileY();
                        tiles[tileX][tileY] = tile;
                    }
                    checkedCorridors.add(corridor);
                }
            }
            if(currentNode._leftNode!=null){nodesLeft.add(currentNode._leftNode);}
            if(currentNode._rightNode!=null){nodesLeft.add(currentNode._rightNode);}
        }

        return tiles;
    }

    public boolean canSplit()
    {
        int minSplitX = _x + MapGenerator.minWidth;
        int maxSplitX = _x + _width - MapGenerator.minWidth;
        int minSplitY = _y + MapGenerator.minHeight;
        int maxSplitY = _y + _height - MapGenerator.minHeight;
        return minSplitX<maxSplitX  ||
                minSplitY <maxSplitY;
    }

    public void split()
    {
        _isLeaf = false;
        _hasBeenConnected = false;
        int minSplitX = _x + MapGenerator.minWidth;
        int maxSplitX = _x + _width - MapGenerator.minWidth;
        int minSplitY = _y + MapGenerator.minHeight;
        int maxSplitY = _y + _height - MapGenerator.minHeight;

        if (minSplitX < maxSplitX)
        {
            doVerticalSplit = true;
        }
        else if (minSplitY < maxSplitY)
        {
            doVerticalSplit = false;
        }
        else
        {
            doVerticalSplit = RandomGen.getRandomInt(0, 1) == 1;
        }

        if (doVerticalSplit)
        {
            split = RandomGen.getRandomInt(_x + MapGenerator.minWidth, _x + _width - MapGenerator.minWidth - 1);
            _leftNode = new BSPMapNode(_x, _y, split - _x, _height, _level + 1, this);
            _rightNode = new BSPMapNode(split + 1, _y, _width - (split - _x) - 1, _height, _level + 1, this);
        }
        else
        {
            split = RandomGen.getRandomInt(_y + MapGenerator.minHeight, _y + _height - MapGenerator.minHeight - 1);
            _leftNode = new BSPMapNode(_x, _y, _width, split - _y, _level + 1, this);
            _rightNode = new BSPMapNode(_x, split + 1, _width, _height - (split - _y) - 1, _level + 1, this);
        }

    }

    public void createRoom()
    {
        int roomWidth = RandomGen.getRandomInt(MapGenerator.minWidth, _width);
        int roomHeight = RandomGen.getRandomInt(MapGenerator.minHeight, _height);
        int roomX = _x + RandomGen.getRandomInt(0, _width - roomWidth);
        int roomY = _y + RandomGen.getRandomInt(0, _height - roomHeight);

        _room = new Room(roomX, roomY, roomWidth, roomHeight);
        _room.setWallsAndFloor();
    }

    public void connectChildren()
    {
        if (isDebugging)
        {
            Gdx.app.log("BSPMapNode", "Connecting region " + getLeftNode().regionToString() + " and " + getRightNode().regionToString());
        }
        if (!getLeftNode().hasBeenConnected())
        {
            getLeftNode().connectChildren();
        }
        if (!getRightNode().hasBeenConnected())
        {
            getRightNode().connectChildren();
        }


        Room room1 = getLeftNode().getRoom();
        Room room2 = getRightNode().getRoom();

        if (room1 == null || room2 == null)
        {
            //Split variable tells where the children are positioned
            int x = doVerticalSplit ? split : _width / 2;
            int y = doVerticalSplit ? _height / 2 : split;
            boolean xIsPriority = x == split;

            if (room1 == null)
            {
                room1 = getRoomClosestTo(_leftNode, x, y, xIsPriority);
            }
            if (room2 == null)
            {
                room2 = getRoomClosestTo(_rightNode, x, y, xIsPriority);
            }
        }

        Corridor corridor = new Corridor(room1, room2);

        if (doVerticalSplit)
        {
            corridor.connectRoomsHorizontal(split);
        }
        else
        {
            corridor.connectRoomsVertical(split);
        }
        _corridors.add(corridor);
    }

    private Room getRoomClosestTo(BSPMapNode node, int x, int y, boolean isXPriority)
    {
        final Vector2 leftRoomCenter = new Vector2();
        final Vector2 rightRoomCenter = new Vector2();
        if (node.isLeaf())
        {
            return node.getRoom();
        }
        Room leftNodeRoom = getRoomClosestTo(node.getLeftNode(), x, y, isXPriority);
        leftRoomCenter.x =leftNodeRoom.getX()+leftNodeRoom.getWidth()/2;
        leftRoomCenter.y =leftNodeRoom.getY()+leftNodeRoom.getHeight()/2;

        Room rightNodeRoom = getRoomClosestTo(node.getRightNode(), x, y, isXPriority);
        rightRoomCenter.x =rightNodeRoom.getX()+rightNodeRoom.getWidth()/2;
        rightRoomCenter.y =rightNodeRoom.getY()+rightNodeRoom.getHeight()/2;

        if (isXPriority)//Are we using x or y to find the closest? this case is using x
        {
            int dxLeft = Math.abs((int)leftRoomCenter.x - x);
            int dxRight = Math.abs((int)rightRoomCenter.x - x);
            if (dxLeft == dxRight)
            {
                if (Math.abs((int)leftRoomCenter.y - y) < Math.abs((int)rightRoomCenter.y - y))
                {
                    return leftNodeRoom;
                }
                else
                {
                    return rightNodeRoom;
                }
            }
            else if (dxLeft < dxRight)
            {
                return leftNodeRoom;
            }
            else
            {
                return rightNodeRoom;
            }
        }
        else//Using y to find closest
        {
            int dyLeft = Math.abs((int)leftRoomCenter.y - y);
            int dyRight = Math.abs((int)rightRoomCenter.y - y);
            if (dyLeft == dyRight)
            {
                if (Math.abs((int)leftRoomCenter.x - x) < Math.abs((int)rightRoomCenter.x - x))
                {
                    return leftNodeRoom;
                }
                else
                {
                    return rightNodeRoom;
                }
            }
            else if (dyLeft < dyRight)
            {
                return leftNodeRoom;
            }
            else
            {
                return rightNodeRoom;
            }
        }
    }


    public void draw(SpriteBatch batch)
    {
        if (_isLeaf)
        {
            //Gdx.app.log("Draw", "Rect: "+_roomDimensions.toString());
            _room.draw(batch);
        }
        else
        {
            if (drawSplits)
            {
                /* Create Shaperenderer to show
                if(doVerticalSplit)
                {

                    batch.drawLine(split * DungeonMap.TileSize + DungeonMap.TileSize / 2, _y * DungeonMap.TileSize, split * DungeonMap.TileSize + DungeonMap.TileSize / 2, (_y + _height) * DungeonMap.TileSize, p);
                }
                else
                {
                    batch.drawLine(_x * DungeonMap.TileSize, split * DungeonMap.TileSize + DungeonMap.TileSize / 2, (_x + _width) * DungeonMap.TileSize, split * DungeonMap.TileSize + DungeonMap.TileSize / 2, p);
                }
                */
            }
            for (Corridor c : _corridors)
            {
                c.draw(batch);
            }
            _leftNode.draw(batch);
            _rightNode.draw(batch);
        }
    }
}

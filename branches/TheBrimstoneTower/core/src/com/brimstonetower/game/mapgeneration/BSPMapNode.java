package com.brimstonetower.game.mapgeneration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

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

    public boolean canSplit()
    {
        return _width > MapGenerator.minWidth * 2 + 1 + RandomGen.getRandomInt(0, MapGenerator.minWidth) ||
                _height > MapGenerator.minHeight * 2 + 1 + RandomGen.getRandomInt(0, MapGenerator.minWidth);
    }

    public void split()
    {
        _isLeaf = false;
        _hasBeenConnected = false;
        int minSplitX = _x + MapGenerator.minWidth;
        int maxSplitX = _x + _width - MapGenerator.minWidth;
        int minSplitY = _y + MapGenerator.minHeight;
        int maxSplitY = _y + _height - MapGenerator.minHeight;

        if (minSplitX >= maxSplitX)
        {
            doVerticalSplit = false;
        }
        else if (minSplitY >= maxSplitY)
        {
            doVerticalSplit = true;
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
            corridor.connectRoomsHorizontal();
        }
        else
        {
            corridor.connectRoomsVertical();
        }
        _corridors.add(corridor);
    }

    private Room getRoomClosestTo(BSPMapNode node, int x, int y, boolean isXPriority)
    {
        if (node.isLeaf())
        {
            return node.getRoom();
        }
        Room leftNodeRoom = getRoomClosestTo(node.getLeftNode(), x, y, isXPriority);
        Room rightNodeRoom = getRoomClosestTo(node.getRightNode(), x, y, isXPriority);

        if (isXPriority)//Are we using x or y to find the closest? this case is using x
        {
            int dxLeft = Math.abs(leftNodeRoom.getX() - x);
            int dxRight = Math.abs(rightNodeRoom.getX() - x);
            if (dxLeft == dxRight)
            {
                if (Math.abs(leftNodeRoom.getY() - y) < Math.abs(rightNodeRoom.getY() - y))
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
            int dyLeft = Math.abs(leftNodeRoom.getY() - y);
            int dyRight = Math.abs(rightNodeRoom.getY() - y);
            if (dyLeft == dyRight)
            {
                if (Math.abs(leftNodeRoom.getX() - x) < Math.abs(rightNodeRoom.getX() - x))
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

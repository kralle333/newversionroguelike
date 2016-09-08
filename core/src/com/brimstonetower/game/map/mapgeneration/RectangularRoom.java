package com.brimstonetower.game.map.mapgeneration;

import com.badlogic.gdx.Gdx;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.Tile;


public class RectangularRoom extends Room
{

    public RectangularRoom(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public void setWallsAndFloor()
    {
        //Top wall
        _tiles[0][0] = new Tile(Tile.Types.Wall, _x, _y, AssetManager.getTileSetPosition("nwWall"));
        _tiles[1][0] = new Tile(Tile.Types.Wall, 1+_x, _y, AssetManager.getTileSetPosition("nwRoomWall"));
        _tiles[_width - 1][0] = new Tile(Tile.Types.Wall,_width - 1+ _x, _y, AssetManager.getTileSetPosition("neWall"));
        _tiles[_width - 2][0] = new Tile(Tile.Types.Wall, _width - 2+ _x, _y, AssetManager.getTileSetPosition("neRoomWall"));
        for(int x = 2;x<_width-2;x++)
        {
            _tiles[x][0] = new Tile(Tile.Types.Wall, x + _x,  _y, AssetManager.getTileSetPosition("nRoomWall"));
        }

        //Bottom wall
        _tiles[0][_height - 1] = new Tile(Tile.Types.Wall, _x, _height - 1+ _y, AssetManager.getTileSetPosition("swWall"));
        _tiles[_width - 1][_height - 1] = new Tile(Tile.Types.Wall, _width - 1 + _x, _height - 1 + _y, AssetManager.getTileSetPosition("seWall"));
        for(int x = 1;x<_width-1;x++)
        {
            _tiles[x][_height - 1] = new Tile(Tile.Types.Wall, x + _x, _height - 1 + _y, AssetManager.getTileSetPosition("sWall"));
        }

        //West Wall
        for(int y = 1;y<_height-1;y++)
        {
            _tiles[0][y] = new Tile(Tile.Types.Wall, _x, y + _y, AssetManager.getTileSetPosition("wWall"));
        }

        //East wall
        for(int y = 1;y<_height-1;y++)
        {
            _tiles[_width-1][y] = new Tile(Tile.Types.Wall, _width-1+_x, y + _y, AssetManager.getTileSetPosition("eWall"));
        }

        //Floor
        String floorType = "shiny";
        for(int x =1;x<_width-1;x++)
        {
            for(int y=1;y<_height-1;y++)
            {
                //Random floor
                _tiles[x][y] = new Tile(Tile.Types.Floor, x + _x, y + _y, AssetManager.getTileSetPosition("floor-"+floorType+"-"+String.valueOf(RandomGen.getRandomInt(1, 2))));
            }
        }

        makeSubRooms(0,0,_width,_height,_width/2,false);

        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                setNeighbours(_tiles[x][y]);
            }
        }



    }

    private void makeSubRooms(int x,int y,int width, int height, int previousDoorPos,boolean previousWasHor)
    {
        //Make some smaller rooms:
        int minSplitX = x + Room.minSubRoomWidth;
        int maxSplitX = x + width - Room.minSubRoomWidth;
        int minSplitY = y + Room.minSubRoomHeight;
        int maxSplitY = y + height - Room.minSubRoomHeight;

        int split;
        int door;
        if(maxSplitY-minSplitY>3 && !previousWasHor)//Horizontal
        {
            do
            {
                split = RandomGen.getRandomInt(minSplitY, maxSplitY - 1);
            }while(split==previousDoorPos);

            door =RandomGen.getRandomInt(x+1,x+width-2);


            for(int wX = 1;wX<width-1;wX++)
            {
                if(x+wX==door-1)
                {
                    _tiles[wX+x][split].setType(Tile.Types.SubWall);
                    _tiles[wX+x][split].setTextureRegion(AssetManager.getTileSetPosition("nRoomWall"));
                }
                else if(x+wX==door+1)
                {
                    _tiles[wX+x][split].setTextureRegion(AssetManager.getTileSetPosition("nRoomWall"));
                    _tiles[wX+x][split].setType(Tile.Types.SubWall);
                }
                else if(x+wX==door)
                {
                    _tiles[door][split].placeDoor("nDoor-1");
                }
                else
                {
                    _tiles[wX+x][split].setTextureRegion(AssetManager.getTileSetPosition("nRoomWall"));
                    _tiles[wX+x][split].setType(Tile.Types.SubWall);
                }
            }

            makeSubRooms(x, y, width, split - y+1,door,true);
            makeSubRooms(x, split, width, height- (split - y),door,true);
            _usedWallsY.add(split);
        }
        else if(maxSplitX-minSplitX>3&& previousWasHor)//Vertical
        {
            do
            {
                split = RandomGen.getRandomInt(minSplitX,maxSplitX-1);
            }while(split==previousDoorPos);

            door =RandomGen.getRandomInt(y+1,y+height-2);

            //Make the wall
            for(int wY = 1;wY<height-1;wY++)
            {
                if(y+wY==door-1)
                {
                    _tiles[split][y+wY].setType(Tile.Types.SubWall);
                    _tiles[split][y+wY].setTextureRegion(AssetManager.getTileSetPosition("verticalWallBottom"));
                }
                else if(y+wY==door+1)
                {
                    _tiles[split][y+wY].setType(Tile.Types.SubWall);
                    _tiles[split][y+wY].setTextureRegion(AssetManager.getTileSetPosition("verticalWallTop"));
                }
                else if(y+wY==door)
                {
                    //Place a door in the wall
                    _tiles[split][door].placeDoor("wDoor");
                }
                else
                {
                    _tiles[split][y+wY].setType(Tile.Types.SubWall);
                    _tiles[split][y+wY].setTextureRegion(AssetManager.getTileSetPosition("verticalWall"));
                }
            }



            //Determine whether or not to put a top tile or a normal one, depends on if the wall intersects with another
            if(_tiles[split][y].getType() != Tile.Types.SubWall)
            {
                _tiles[split][y].setType(Tile.Types.SubWall);
                _tiles[split][y].setTextureRegion(AssetManager.getTileSetPosition("verticalWallTop"));
            }
            else
            {
                _tiles[split][y].setTextureRegion(AssetManager.getTileSetPosition("verticalWall"));
            }

            //Recursively make smaller subrooms
            makeSubRooms(x, y, split - x+1, height,door,false);
            makeSubRooms(split, y, width - (split - x), height,door,false);
            _usedWallsX.add(split);
        }
        else
        {
            decorateSubRoom(x,y,width,height);
        }



    }
    public Tile getRandomEmptyWall(WallSide side)
    {
        Tile returnedTile = null;
        int randomTile=0;
        switch (side)
        {
            case West:
                do
                {
                    randomTile = RandomGen.getRandomInt(1, _height - 2);
                    returnedTile = _tiles[0][randomTile];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall || _usedWallsY.contains(randomTile));
                break;
            case East:
                do
                {
                    randomTile=RandomGen.getRandomInt(1, _height - 2);
                    returnedTile = _tiles[_width - 1][randomTile];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall|| _usedWallsY.contains(randomTile));
                break;
            case South:
                do
                {
                    randomTile=RandomGen.getRandomInt(1, _width - 2);
                    returnedTile = _tiles[randomTile][_height - 1];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall|| _usedWallsX.contains(randomTile));
                break;
            case North:
                do
                {
                    randomTile=RandomGen.getRandomInt(1, _width - 2);
                    returnedTile = _tiles[randomTile][0];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall|| _usedWallsX.contains(randomTile));
                break;
        }
        return returnedTile;
    }


    enum SubRoomSize {Small,Medium,Large};
    private void decorateSubRoom(int x,int y,int width, int height)
    {
        SubRoomSize size;
        if((width+height/2)<=minSubRoomWidth*3/2){size = SubRoomSize.Small;}
        else if((width+height/2)<=minSubRoomWidth*3){size = SubRoomSize.Medium;}
        else{size = SubRoomSize.Large;}

        switch(size)
        {
            case Small:break;
            case Medium:break;
            case Large:break;
        }
    }

}

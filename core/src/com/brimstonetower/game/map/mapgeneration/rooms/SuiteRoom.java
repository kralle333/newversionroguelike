package com.brimstonetower.game.map.mapgeneration.rooms;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.Tile;
public class SuiteRoom extends Room
{

    public SuiteRoom(int x, int y, int width, int height)
    {
        super(x,y,width,height);
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

    @Override
    protected void finalize()
    {
        makeSubRooms(0,0,getWidth(),getHeight(),getWidth()/2,false);
    }
}

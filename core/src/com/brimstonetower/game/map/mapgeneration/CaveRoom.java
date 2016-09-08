package com.brimstonetower.game.map.mapgeneration;


import com.badlogic.gdx.Gdx;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.helpers.TileSetCoordinate;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.Tile;

public class CaveRoom extends Room
{
    public CaveRoom(int x, int y, int width, int height)
    {
        super(x, y, width, height);
        InsertOvalRoom(width/2,height/2,width-1,height-1);
    }

    private void InsertOvalRoom(int startX, int startY, int width, int height)
    {
        _tiles[startX][startY] = new Tile(Tile.Types.Floor,startX,startY, new TileSetCoordinate(startX,startY));
        float step = (float)Math.PI/100;
        float fWidth = (float)width;
        float fHeight = (float)height;
        for (float theta = 0; theta < 2 * Math.PI; theta += step)
        {
            int x = startX + (int)(fWidth * Math.cos(theta));
            int y = startY + (int)(fHeight * Math.sin(theta));
            x = Math.min(x, _width - 2);
            y = Math.min(y, height - 2);
            x = Math.max(x, 0);
            y = Math.max(y, 0);
            InsertLine(startX,startY,startX, startY, x, y);
        }
        Gdx.app.log("OvalRoom","Created room");
    }

    private void InsertLine(int startX,int startY,int fromX, int fromY, int toX, int toY)
    {
        int xDiff;
        int yDiff;
        while (fromX != toX || fromY != toY)
        {
            xDiff = fromX - toX;
            yDiff = fromY - toY;

            if (Math.abs(xDiff) > Math.abs(yDiff))
            {
                fromX += xDiff > 0 ? -1 : 1;
            }
            else
            {
                fromY += yDiff > 0 ? -1 : 1;
            }
            if(fromX<0 || fromY<0)
            {
                return;
            }
            if(_tiles[fromX][fromY] == null)
            {
                _tiles[fromX][fromY] = new Tile(Tile.Types.Empty,startX+fromX,startY+fromY, AssetManager.getTileSetPosition("floor-shiny-1"));
            }
            //Gdx.app.log("Create Line","x,y:" + fromX + "," + fromY);
            if (_tiles[fromX][fromY].getType() == Tile.Types.Empty || _tiles[fromX][ fromY].getType() == Tile.Types.Wall)
            {
                if ((fromX == toX && fromY == toY) ||
                        (fromY == toY && (_tiles[fromX][ fromY].getType() == Tile.Types.Wall ||_tiles[fromX][fromY].getType() == Tile.Types.Empty )))
                {
                    _tiles[fromX][ fromY].setType( Tile.Types.Wall);
                    _tiles[fromX][fromY].setTextureRegion(AssetManager.getTileSetPosition("nwWall"));
                }
                else
                {
                    _tiles[fromX][ fromY].setType( Tile.Types.Floor);
                }
            }
        }
    }

    public Tile getRandomEmptyWall(WallSide side)
    {
        Tile returnedTile = null;
        int randomTileX,randomTileY;
        switch (side)
        {
            case West:
                do
                {
                    randomTileX = RandomGen.getRandomInt(0,_width/4);
                    randomTileY = RandomGen.getRandomInt(1, _height - 2);
                    returnedTile = _tiles[randomTileX][randomTileY];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall || _usedWallsY.contains(randomTileY));
                break;
            case East:
                do
                {
                    randomTileX = RandomGen.getRandomInt(_width*3/4,_width-1);
                    randomTileY=RandomGen.getRandomInt(1, _height - 2);
                    returnedTile = _tiles[randomTileX][randomTileY];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall|| _usedWallsY.contains(randomTileY));
                break;
            case South:
                do
                {
                    randomTileX = RandomGen.getRandomInt(1, _width - 2);
                    randomTileY=RandomGen.getRandomInt(_height*3/4, _height-1);
                    returnedTile = _tiles[randomTileX][randomTileY];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall|| _usedWallsX.contains(randomTileY));
                break;
            case North:
                do
                {
                    randomTileX=RandomGen.getRandomInt(1, _width - 2);
                    randomTileY=RandomGen.getRandomInt(0, _height/4);
                    returnedTile = _tiles[randomTileX][0];
                } while (returnedTile == null ||returnedTile.getType() != Tile.Types.Wall|| _usedWallsX.contains(randomTileY));
                break;
        }
        return returnedTile;
    }
}



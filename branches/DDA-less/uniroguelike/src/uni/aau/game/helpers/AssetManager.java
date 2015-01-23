package uni.aau.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class AssetManager
{
    private static HashMap<String,Texture> _assets = new HashMap<String, Texture>();
    private static HashMap<String,TileSetCoordinate> tileSetCoordinateMap = new HashMap<String, TileSetCoordinate>();
    private static HashMap<String,BitmapFont> _fonts = new HashMap<String, BitmapFont>();
    private static boolean _isInitialized = false;



    public static void initialize()
    {
        if(!_isInitialized)
        {
            _assets.put("player", new Texture(Gdx.files.internal("data/player.png")));
            _assets.put("monster", new Texture(Gdx.files.internal("data/monsterTileSet.png")));
            _assets.put("tile", new Texture(Gdx.files.internal("data/tileset.png")));
            _assets.put("armor", new Texture(Gdx.files.internal("data/armorTileSet.png")));
            _assets.put("weapon", new Texture(Gdx.files.internal("data/weaponTileSet.png")));
            _assets.put("potion", new Texture(Gdx.files.internal("data/potionTileSet.png")));
            _assets.put("scroll", new Texture(Gdx.files.internal("data/scrollTileSet.png")));
            _assets.put("gas", new Texture(Gdx.files.internal("data/gasCloud.png")));
            _assets.put("trap", new Texture(Gdx.files.internal("data/trap.png")));
            _fonts.put("description", new BitmapFont(Gdx.files.internal("data/DungeonFont.fnt"), Gdx.files.internal("data/DungeonFont.png"), true));
            for(Texture t : _assets.values())
            {
                t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
            initializeTileSetCoordinateMap();

        }
    }

    private static void initializeTileSetCoordinateMap()
    {
        //Floor tiles
        tileSetCoordinateMap.put("floor1",new TileSetCoordinate(3,3));
        tileSetCoordinateMap.put("floor2",new TileSetCoordinate(4,3));
        tileSetCoordinateMap.put("floor3",new TileSetCoordinate(5,3));

        //Corridors - n=north,w=west,s=south,e=east
        tileSetCoordinateMap.put("nwCorridor",new TileSetCoordinate(0,0));
        tileSetCoordinateMap.put("nCorridor",new TileSetCoordinate(1,0));
        tileSetCoordinateMap.put("neCorridor",new TileSetCoordinate(2,0));
        tileSetCoordinateMap.put("wCorridor",new TileSetCoordinate(0,1));
        tileSetCoordinateMap.put("eCorridor",new TileSetCoordinate(2,1));
        tileSetCoordinateMap.put("swCorridor",new TileSetCoordinate(0,2));
        tileSetCoordinateMap.put("sCorridor",new TileSetCoordinate(0,2));
        tileSetCoordinateMap.put("seCorridor",new TileSetCoordinate(2,2));

        //Rooms - n=north,w=west,s=south,e=east
        tileSetCoordinateMap.put("nwWall",new TileSetCoordinate(3,0));
        tileSetCoordinateMap.put("nwRoomWall",new TileSetCoordinate(4,0));
        tileSetCoordinateMap.put("nRoomWall",new TileSetCoordinate(5,0));
        tileSetCoordinateMap.put("neRoomWall",new TileSetCoordinate(6,0));
        tileSetCoordinateMap.put("neWall",new TileSetCoordinate(7,0));
        tileSetCoordinateMap.put("wWall",new TileSetCoordinate(3,1));
        tileSetCoordinateMap.put("eWall",new TileSetCoordinate(7,1));
        tileSetCoordinateMap.put("swWall",new TileSetCoordinate(3,2));
        tileSetCoordinateMap.put("sWall",new TileSetCoordinate(6,2));
        tileSetCoordinateMap.put("seWall",new TileSetCoordinate(7,2));

        //Doors - n=north,w=west,s=south,e=east
        tileSetCoordinateMap.put("nCorridor",new TileSetCoordinate(0,3));
        tileSetCoordinateMap.put("wCorridor",new TileSetCoordinate(1,3));
        tileSetCoordinateMap.put("sCorridor",new TileSetCoordinate(0,4));
        tileSetCoordinateMap.put("eCorridor",new TileSetCoordinate(1,4));

        //Misc
        tileSetCoordinateMap.put("stairs",new TileSetCoordinate(2,3));
        tileSetCoordinateMap.put("chest",new TileSetCoordinate(2,4));
    }
    public static BitmapFont getFont(String string)
    {
        return _fonts.get(string);
    }
    public static Texture getTexture(String string)
    {
        return _assets.get(string);
    }
    public static TextureRegion getTextureRegion(String path,String tileSetPositionKey,int width, int height)
    {
        TileSetCoordinate tile = getTileSetPosition(tileSetPositionKey);
        return new TextureRegion(_assets.get(path),tile.x*width,tile.y*height,width,height);
    }
    public static TextureRegion getTextureRegion(String path,int xIndex, int yIndex,int width, int height)
    {
        return new TextureRegion(_assets.get(path),xIndex*width,yIndex*height,width,height);
    }
    public static TileSetCoordinate getTileSetPosition(String key)
    {
        return tileSetCoordinateMap.get(key);
    }
    public static void disposeAll()
    {
        _isInitialized=false;
        for(Texture t : _assets.values())
        {
            t.dispose();
        }
        for(BitmapFont f: _fonts.values())
        {
            f.dispose();
        }
    }
}

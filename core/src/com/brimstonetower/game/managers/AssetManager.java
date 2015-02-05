package com.brimstonetower.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.brimstonetower.game.helpers.TileSetCoordinate;
import com.brimstonetower.game.map.Tile;

import java.util.HashMap;

public class AssetManager
{
    private static HashMap<String, Texture> _assets = new HashMap<String, Texture>();
    private static HashMap<String, TileSetCoordinate> tileSetCoordinateMap = new HashMap<String, TileSetCoordinate>();
    private static HashMap<String, BitmapFont> _fonts = new HashMap<String, BitmapFont>();
    private static boolean _isInitialized = false;


    public static void initialize()
    {
        if (!_isInitialized)
        {
            initializeTextures();
            initializeTileSetCoordinateMap();
            initializeFonts();
        }
    }

    private static void initializeTextures()
    {
        _assets.put("player", new Texture(Gdx.files.internal("art/player.png")));
        _assets.put("mainHeroes",new Texture(Gdx.files.internal("art/mainHeroes.png")));
        _assets.put("mainHeroesWithBorder",new Texture(Gdx.files.internal("art/mainHeroesWithBorder.png")));
        _assets.put("searchEye",new Texture(Gdx.files.internal("art/searchEye.png")));
        _assets.put("monster", new Texture(Gdx.files.internal("art/monsterTileSet.png")));
        _assets.put("tile", new Texture(Gdx.files.internal("art/tilesetNew.png")));
        _assets.put("armors", new Texture(Gdx.files.internal("art/armorTileSet.png")));
        _assets.put("weapons", new Texture(Gdx.files.internal("art/weaponTileSet.png")));
        _assets.put("potion", new Texture(Gdx.files.internal("art/potionTileSet.png")));
        _assets.put("scroll", new Texture(Gdx.files.internal("art/scrollTileSet.png")));
        _assets.put("gas", new Texture(Gdx.files.internal("art/gasCloud.png")));
        _assets.put("trap", new Texture(Gdx.files.internal("art/trap.png")));
        for (Texture t : _assets.values())
        {
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    private static void initializeTileSetCoordinateMap()
    {
        //Player
        tileSetCoordinateMap.put("player",new TileSetCoordinate(0,0));
        tileSetCoordinateMap.put("playerType1",new TileSetCoordinate(0,0));
        tileSetCoordinateMap.put("playerType2",new TileSetCoordinate(1,0));
        tileSetCoordinateMap.put("playerType3",new TileSetCoordinate(0,1));
        tileSetCoordinateMap.put("playerType4",new TileSetCoordinate(1,1));

        //Floor tiles
        tileSetCoordinateMap.put("floor-1", new TileSetCoordinate(3, 3));
        tileSetCoordinateMap.put("floor-2", new TileSetCoordinate(4, 3));

        //Corridors - n=north,w=west,s=south,e=east
        tileSetCoordinateMap.put("nwCorridor", new TileSetCoordinate(0, 0));
        tileSetCoordinateMap.put("nCorridor", new TileSetCoordinate(1, 0));
        tileSetCoordinateMap.put("neCorridor", new TileSetCoordinate(2, 0));
        tileSetCoordinateMap.put("wCorridor", new TileSetCoordinate(0, 1));
        tileSetCoordinateMap.put("eCorridor", new TileSetCoordinate(2, 1));
        tileSetCoordinateMap.put("swCorridor", new TileSetCoordinate(0, 2));
        tileSetCoordinateMap.put("sCorridor", new TileSetCoordinate(0, 2));
        tileSetCoordinateMap.put("seCorridor", new TileSetCoordinate(2, 2));

        //Rooms - n=north,w=west,s=south,e=east
        tileSetCoordinateMap.put("nwWall", new TileSetCoordinate(3, 0));
        tileSetCoordinateMap.put("nwRoomWall", new TileSetCoordinate(4, 0));
        tileSetCoordinateMap.put("nRoomWall", new TileSetCoordinate(5, 0));
        tileSetCoordinateMap.put("neRoomWall", new TileSetCoordinate(6, 0));
        tileSetCoordinateMap.put("neWall", new TileSetCoordinate(7, 0));
        tileSetCoordinateMap.put("wWall", new TileSetCoordinate(3, 1));
        tileSetCoordinateMap.put("eWall", new TileSetCoordinate(7, 1));
        tileSetCoordinateMap.put("swWall", new TileSetCoordinate(3, 2));
        tileSetCoordinateMap.put("sWall", new TileSetCoordinate(6, 2));
        tileSetCoordinateMap.put("seWall", new TileSetCoordinate(7, 2));

        //Doors - n=north,w=west,s=south,e=east
        tileSetCoordinateMap.put("nDoor-1", new TileSetCoordinate(0, 3));
        tileSetCoordinateMap.put("nDoor-2", new TileSetCoordinate(1, 3));
        tileSetCoordinateMap.put("eDoor", new TileSetCoordinate(1, 4));
        tileSetCoordinateMap.put("sDoor", new TileSetCoordinate(0, 5));
        tileSetCoordinateMap.put("wDoor", new TileSetCoordinate(1, 5));

        //Misc
        tileSetCoordinateMap.put("stairs", new TileSetCoordinate(2, 3));
        tileSetCoordinateMap.put("chest-1", new TileSetCoordinate(2, 5));
        tileSetCoordinateMap.put("chest-2", new TileSetCoordinate(3, 5));
        tileSetCoordinateMap.put("chest-3", new TileSetCoordinate(4, 5));
        tileSetCoordinateMap.put("searchEye",new TileSetCoordinate(0,0));

        //Weapons
        tileSetCoordinateMap.put("steelSword",new TileSetCoordinate(0,0));
        tileSetCoordinateMap.put("steelAxe",new TileSetCoordinate(1,0));
        tileSetCoordinateMap.put("steelDagger",new TileSetCoordinate(2,0));
        tileSetCoordinateMap.put("steelThrow",new TileSetCoordinate(3,0));
        tileSetCoordinateMap.put("crystalSword",new TileSetCoordinate(0,1));
        tileSetCoordinateMap.put("crystalAxe",new TileSetCoordinate(1,1));
        tileSetCoordinateMap.put("crystalDagger",new TileSetCoordinate(2,1));
        tileSetCoordinateMap.put("crystalThrow",new TileSetCoordinate(3,1));
        tileSetCoordinateMap.put("unholySword",new TileSetCoordinate(0,2));
        tileSetCoordinateMap.put("unholyCudgel",new TileSetCoordinate(1,2));
        tileSetCoordinateMap.put("unholyDagger",new TileSetCoordinate(2,2));
        tileSetCoordinateMap.put("unholyThrow",new TileSetCoordinate(3,2));
        tileSetCoordinateMap.put("demonicSword",new TileSetCoordinate(0,3));
        tileSetCoordinateMap.put("demonicMace",new TileSetCoordinate(1,3));
        tileSetCoordinateMap.put("demonicDagger",new TileSetCoordinate(2,3));
        tileSetCoordinateMap.put("demonicThrow",new TileSetCoordinate(3,3));

        //Armors
        tileSetCoordinateMap.put("rags",new TileSetCoordinate(0,0));
        tileSetCoordinateMap.put("nobleClothes",new TileSetCoordinate(1,0));
        tileSetCoordinateMap.put("furArmor",new TileSetCoordinate(2,0));
        tileSetCoordinateMap.put("leatherArmor",new TileSetCoordinate(3,0));
        tileSetCoordinateMap.put("sturdyLeather",new TileSetCoordinate(0,1));
        tileSetCoordinateMap.put("chainMail",new TileSetCoordinate(1,1));
        tileSetCoordinateMap.put("breastPlate",new TileSetCoordinate(2,1));
        tileSetCoordinateMap.put("scaleMail",new TileSetCoordinate(3,1));

        //Gas levels
        tileSetCoordinateMap.put("type1.1Gas", new TileSetCoordinate(0, 0));
        tileSetCoordinateMap.put("type1.2Gas", new TileSetCoordinate(1, 0));
        tileSetCoordinateMap.put("type1.3Gas", new TileSetCoordinate(2, 0));
        tileSetCoordinateMap.put("type2.1Gas", new TileSetCoordinate(0, 1));
        tileSetCoordinateMap.put("type2.2Gas", new TileSetCoordinate(1, 1));
        tileSetCoordinateMap.put("type2.3Gas", new TileSetCoordinate(2, 1));
        tileSetCoordinateMap.put("type3.1Gas", new TileSetCoordinate(0, 2));
        tileSetCoordinateMap.put("type3.2Gas", new TileSetCoordinate(1, 2));
        tileSetCoordinateMap.put("type3.3Gas", new TileSetCoordinate(2, 2));
        tileSetCoordinateMap.put("type4.1Gas", new TileSetCoordinate(0, 3));
        tileSetCoordinateMap.put("type4.2Gas", new TileSetCoordinate(1, 3));
        tileSetCoordinateMap.put("type4.3Gas", new TileSetCoordinate(2, 3));
    }

    private static void initializeFonts()
    {

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/mono.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameters.flip = true;
        parameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        parameters.minFilter= Texture.TextureFilter.Nearest;
        parameters.magFilter= Texture.TextureFilter.Nearest;
        int maxSize = 200;
        BitmapFont font;
        int widthToFit = Gdx.graphics.getWidth()*5/8-(Gdx.graphics.getWidth()/128);
        int heightToFit = Gdx.graphics.getHeight()/20;
        String testString = "Your sight is blocked by gray blobs moving on your eyes";

        do
        {
            font=generator.generateFont(parameters);
            BitmapFont.TextBounds textDimensions = font.getBounds(testString);
            if(textDimensions.width>widthToFit || textDimensions.height>heightToFit)
            {
                break;
            }
            parameters.size++;
        }while(parameters.size<maxSize);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        _fonts.put("description",font);
        generator.dispose();

    }

    public static BitmapFont getFont(String string)
    {
        return _fonts.get(string);
    }

    public static TextureRegion getTextureRegion(String path, String tileSetPositionKey, int width, int height)
    {
        TileSetCoordinate tile = getTileSetPosition(tileSetPositionKey);
        return new TextureRegion(_assets.get(path), tile.x * width, tile.y * height, width, height);
    }

    public static TextureRegion getTextureRegion(String path, TileSetCoordinate tileSetCoordinate, int width, int height)
    {
        return getTextureRegion(path, tileSetCoordinate.x, tileSetCoordinate.y, width, height);
    }

    public static TextureRegion getTextureRegion(String path, int xIndex, int yIndex, int width, int height)
    {
        return new TextureRegion(_assets.get(path), xIndex * width, yIndex * height, width, height);
    }

    public static TileSetCoordinate getTileSetPosition(String key)
    {
        return tileSetCoordinateMap.get(key);
    }

    public static void disposeAll()
    {
        _isInitialized = false;
        for (Texture t : _assets.values())
        {
            t.dispose();
        }
        for (BitmapFont f : _fonts.values())
        {
            f.dispose();
        }
    }
}

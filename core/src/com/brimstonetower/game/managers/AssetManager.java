package com.brimstonetower.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.brimstonetower.game.helpers.TileSetCoordinate;
import com.brimstonetower.game.map.DungeonMap;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class AssetManager
{

    private static HashMap<String, Texture> _guiAssets = new HashMap<String,Texture>();
    private static HashMap<String, Texture> _gameAssets = new HashMap<String, Texture>();
    private static HashMap<String, TileSetCoordinate> tileSetCoordinateMap = new HashMap<String, TileSetCoordinate>();
    private static HashMap<String, BitmapFont> _fonts = new HashMap<String, BitmapFont>();
    private static HashMap<String, Sound> _soundEffects = new HashMap<String,Sound>();
    private static boolean _isInitialized = false;


    public static void initialize()
    {
        if (!_isInitialized)
        {
            initializeTextures();
            initializeTileSetCoordinateMap();
            initializeFonts();

            loadSound("hit");
            loadSound("miss");
            loadSound("critical");
            loadSound("block");
            loadSound("pickup");
            loadSound("effect");
            loadSound("throw");
            loadSound("gas");
            loadSound("search");
            loadSound("surprise");
            loadSound("levelup");
        }
    }
    private static void loadSound(String name)
    {
        _soundEffects.put(name,Gdx.audio.newSound(Gdx.files.internal("sounds/"+name+".wav")));
    }
    private static void initializeTextures()
    {
        _guiAssets.put("levelUp",new Texture(Gdx.files.internal("art/levelUp.png")));
        _guiAssets.put("background",new Texture(Gdx.files.internal("art/art_big_workInProgress.png")));

        _gameAssets.put("player", new Texture(Gdx.files.internal("art/player.png")));
        _gameAssets.put("mainHeroes", new Texture(Gdx.files.internal("art/mainHeroes.png")));
        _gameAssets.put("mainHeroesWithBorder", new Texture(Gdx.files.internal("art/mainHeroesWithBorder.png")));
        _gameAssets.put("misc", new Texture(Gdx.files.internal("art/misc.png")));
        _gameAssets.put("monster", new Texture(Gdx.files.internal("art/monsterTileSet.png")));
        _gameAssets.put("tile", new Texture(Gdx.files.internal("art/tilesetBrown.png")));
        _gameAssets.put("armors", new Texture(Gdx.files.internal("art/armorTileSet.png")));
        _gameAssets.put("weapons", new Texture(Gdx.files.internal("art/weaponTileSet.png")));
        _gameAssets.put("potion", new Texture(Gdx.files.internal("art/potionTileSet.png")));
        _gameAssets.put("scroll", new Texture(Gdx.files.internal("art/scrollTileSet.png")));
        _gameAssets.put("gas", new Texture(Gdx.files.internal("art/gasCloud.png")));
        _gameAssets.put("interior", new Texture(Gdx.files.internal("art/interior.png")));
        for (Texture t : _gameAssets.values())
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
        tileSetCoordinateMap.put("floor-shiny-1", new TileSetCoordinate(3, 3));
        tileSetCoordinateMap.put("floor-shiny-2", new TileSetCoordinate(4, 3));
        tileSetCoordinateMap.put("floor-shiny-3", new TileSetCoordinate(3, 4));
        tileSetCoordinateMap.put("floor-shiny-4", new TileSetCoordinate(4, 4));


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

        tileSetCoordinateMap.put("verticalWallTop", new TileSetCoordinate(6, 3));
        tileSetCoordinateMap.put("verticalWall", new TileSetCoordinate(5, 3));
        tileSetCoordinateMap.put("verticalWallBottom",new TileSetCoordinate(6,5));
        tileSetCoordinateMap.put("horizontalWallEast", new TileSetCoordinate(7, 4));
        tileSetCoordinateMap.put("horizontalWall", new TileSetCoordinate(5, 5));
        tileSetCoordinateMap.put("horizontalWallWest", new TileSetCoordinate(5, 4));
        tileSetCoordinateMap.put("centerWall", new TileSetCoordinate(6, 4));


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

        //Misc file
        tileSetCoordinateMap.put("searchEye",new TileSetCoordinate(1,0));
        tileSetCoordinateMap.put("wayPoint",new TileSetCoordinate(0,0));
        tileSetCoordinateMap.put("wasSeen",new TileSetCoordinate(0,1));
        tileSetCoordinateMap.put("trap",new TileSetCoordinate(1,1));

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

        tileSetCoordinateMap.put("barrel1",new TileSetCoordinate(0,0));
        tileSetCoordinateMap.put("barrel2",new TileSetCoordinate(1,0));
        tileSetCoordinateMap.put("box1",new TileSetCoordinate(2,0));
        tileSetCoordinateMap.put("box2",new TileSetCoordinate(3,0));
        tileSetCoordinateMap.put("box3",new TileSetCoordinate(4,0));
        tileSetCoordinateMap.put("bucket",new TileSetCoordinate(0,1));
    }

    private static void initializeFonts()
    {
        final int devWidth = 960;
        final int devHeight = 540;
        final int devFontSize = 21;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/mono.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameters.flip = true;
        parameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
        parameters.minFilter= Texture.TextureFilter.Nearest;
        parameters.magFilter= Texture.TextureFilter.Nearest;
        parameters.size=devFontSize*Gdx.graphics.getWidth()/devWidth;

        BitmapFont font=generator.generateFont(parameters);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        _fonts.put("description",font);
        generator.dispose();

    }
    public static Sound getSound(String soundName)
    {
        return _soundEffects.get(soundName);
    }

    public static BitmapFont getFont(String string)
    {
        return _fonts.get(string);
    }

    public static Texture getGuiTexture(String name)
    {
        return _guiAssets.get(name);
    }

    /**
     * For variable size texture region
     */
    public static TextureRegion getTextureRegion(String imageHandle, int xIndex, int yIndex, int width, int height)
    {
        try
        {
            TextureRegion newRegion = new TextureRegion(_gameAssets.get(imageHandle), xIndex * width, yIndex * height, width, height);
            newRegion.flip(false,true);
            return newRegion;
        }
        catch(Exception e)
        {
            System.out.println("Image handle:"+imageHandle+" not found in _gameAssets - Add before use");
            throw e;
        }
    }
    /**
     * For variable size texture region
     */
    public static TextureRegion getTextureRegion(String imageHandle, String tileSetPositionKey, int width, int height)
    {
        TileSetCoordinate tile = getTileSetPosition(tileSetPositionKey);
        return getTextureRegion(imageHandle, tile.x , tile.y , width, height);
    }
    /**
     * For variable size texture region
     */
    public static TextureRegion getTextureRegion(String imageHandle, TileSetCoordinate tileSetCoordinate, int width, int height)
    {
        return getTextureRegion(imageHandle, tileSetCoordinate.x, tileSetCoordinate.y, width, height);
    }

    /**
     * For tile sized textures
     */
    public static TextureRegion getTileTextureRegion(String imageHandle, int xIndex, int yIndex)
    {
        return getTextureRegion(imageHandle,xIndex,yIndex, DungeonMap.TileSize,DungeonMap.TileSize);
    }
    /**
     * For tile sized textures
     */
    public static TextureRegion getTileTextureRegion(String imageHandle, String tileSetPositionKey)
    {
        TileSetCoordinate tile = getTileSetPosition(tileSetPositionKey);
        return getTextureRegion(imageHandle, tile.x , tile.y , DungeonMap.TileSize,DungeonMap.TileSize);
    }
    /**
     * For tile sized textures
     */
    public static TextureRegion getTileTextureRegion(String imageHandle, TileSetCoordinate tileSetCoordinate)
    {
        return getTextureRegion(imageHandle, tileSetCoordinate.x, tileSetCoordinate.y, DungeonMap.TileSize,DungeonMap.TileSize);
    }

    public static TileSetCoordinate getTileSetPosition(String key)
    {
        return tileSetCoordinateMap.get(key);
    }

    public static void disposeAll()
    {
        _isInitialized = false;
        for (Texture t : _gameAssets.values())
        {
            t.dispose();
        }
        for (BitmapFont f : _fonts.values())
        {
            f.dispose();
        }
    }
}

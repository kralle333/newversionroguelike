package uni.aau.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class AssetManager
{
    private static HashMap<String,Texture> _assets = new HashMap<String, Texture>();
    private static HashMap<String,BitmapFont> _fonts = new HashMap<String, BitmapFont>();
    private static boolean _isInitialized = false;
    public class TileSetCoordinate
    {
        public int x;
        public int y;
        public TileSetCoordinate(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }


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
            _assets.put("stairs", new Texture(Gdx.files.internal("data/stairs.png")));
            _fonts.put("description", new BitmapFont(Gdx.files.internal("data/DungeonFont.fnt"), Gdx.files.internal("data/DungeonFont.png"), true));
            //Find a good formula
            //_fonts.get("description").scale(1);
            for(Texture t : _assets.values())
            {
                t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
        }
    }
    public static BitmapFont getFont(String string)
    {
        return _fonts.get(string);
    }
    public static Texture getTexture(String string)
    {
        return _assets.get(string);
    }
    public static TextureRegion getTextureRegion(String string,int xIndex, int yIndex,int width, int height)
    {
        return new TextureRegion(_assets.get(string),xIndex*width,yIndex*height,width,height);
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

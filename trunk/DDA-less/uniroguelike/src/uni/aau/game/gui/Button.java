package uni.aau.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import uni.aau.game.helpers.AssetManager;

import javax.microedition.khronos.opengles.GL10;

public class Button
{
    private Color _color;
    private float _x;
    public float getX(){return _x;}
    private float _y;
    public float getY(){return _y;}
    private float _width;
    public float getWidth(){return _width;}
    private float _height;
    public float getHeight(){return _height;}
    private String _text;
    public String getText(){return _text;}
    private BitmapFont _font;
    private TextureRegion _textureRegion = null;
    private boolean _isHidden = false;
    private Rectangle _hitRectangle;
    private float _scale=1;
    public float getScale(){return _scale;}
    public void setScale(float newScale){_scale = newScale;}

    public Button(float x, float y,float width, float height,String text,Color color)
    {
        _x=x;
        _y=y;
        _width=width;
        _height=height;
        _text=text;
        _font = AssetManager.getFont("description");
        _color=color;
        _hitRectangle = new Rectangle(x,y,width,height);
    }
    public Button(float x, float y, TextureRegion region, String text)
    {
        _x=x;
        _y=y;
        _width=region.getRegionWidth();
        _height=region.getRegionHeight();
        _textureRegion = region;
        _text=text;
        _font = AssetManager.getFont("description");
        _hitRectangle = new Rectangle(x,y,_width,_height);
    }
    public void hide()
    {
        _isHidden=true;
    }
    public void show()
    {
        _isHidden = false;
    }
    public void setColor(Color newColor)
    {
        _color = newColor;
    }
    public void reposition(float x, float y)
    {
        _x=x;
        _y=y;
        _hitRectangle.x = x;
        _hitRectangle.y = y;
        if(_textureRegion != null)
        {
            _hitRectangle.width = _textureRegion.getRegionWidth() * _scale;
            _hitRectangle.height = _textureRegion.getRegionHeight() * _scale;
        }
    }

    public boolean isTouched(float x, float y)
    {
        return _hitRectangle.contains(x,y);
    }

    public void draw(SpriteBatch batch,ShapeRenderer shapeRenderer)
    {
        if(!_isHidden)
        {
            if(_textureRegion ==null)
            {
                Gdx.gl.glEnable(GL10.GL_BLEND);
                shapeRenderer.setColor(_color);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.rect(_x,_y,_width,_height);
                shapeRenderer.end();
                Gdx.gl.glDisable(GL10.GL_BLEND);

                batch.begin();
                _font.draw(batch,_text,_x+(_width/2)-(_font.getBounds(_text).width/2),_y+(_height/2)-(_font.getBounds(_text).height/2));
                batch.end();
            }
            else
            {

                batch.begin();
                if(_color != null)
                {
                    batch.setColor(_color);
                }
                batch.draw(_textureRegion,_x,_y,0,0,_width,_height,_scale,_scale,0);
                batch.setColor(Color.WHITE);
                batch.end();
            }
        }
    }
}

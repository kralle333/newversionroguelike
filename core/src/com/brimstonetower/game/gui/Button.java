package com.brimstonetower.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.brimstonetower.game.managers.AssetManager;


public class Button
{
    private Color _color;
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
    public void setWidth(int width)
    {
        _width=width;
    }
    public int getWidth()
    {
        return _width;
    }

    private int _height;
    public void setHeight(int height)
    {
        _height = height;
    }
    public int getHeight()
    {
        return _height;
    }

    private String _text;

    public String getText()
    {
        return _text;
    }

    public void setText(String newText)
    {
        _text = newText;
    }

    private BitmapFont _font;
    private TextureRegion _textureRegion = null;
    private boolean _isHidden = false;
    private Rectangle _hitRectangle;
    private float _scaleX = 1;
    private float _scaleY = 1;

    public float getScale()
    {
        return _scaleX;
    }

    public void setScale(float newScale)
    {
        _scaleX = newScale;
    }

    public Button(int x,int y,int width,int height, String text, Color color)
    {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _text = text;
        _font = AssetManager.getFont("description");
        _color = color;
        _hitRectangle = new Rectangle(x, y, width, height);
    }

    public Button(int x,int y, TextureRegion region, String text)
    {
        _x = x;
        _y = y;
        _width = region.getRegionWidth();
        _height = region.getRegionHeight();
        _textureRegion = region;
        _text = text;
        _font = AssetManager.getFont("description");
        _hitRectangle = new Rectangle(x, y, _width, _height);
    }
    public Button(int x,int y, TextureRegion region,int width,int height,String text)
    {
        _x = x;
        _y = y;
        _width =width;
        _height = height;
        _textureRegion = region;
        _text = text;
        _font = AssetManager.getFont("description");
        _hitRectangle = new Rectangle(x, y, _width, _height);
    }
    public void reposition(int x, int y,int width, int height)
    {
        _x=x;
        _y=y;
        _width=width;
        _height=height;
        _hitRectangle = new Rectangle(x, y, _width, _height);
    }
    public void hide()
    {
        _isHidden = true;
    }

    public void show()
    {
        _isHidden = false;
    }

    public void setColor(Color newColor)
    {
        _color = newColor;
    }

    public void reposition(int x, int y)
    {
        _x = x;
        _y = y;
        _hitRectangle.x = x;
        _hitRectangle.y = y;
        if (_textureRegion != null)
        {
            _hitRectangle.width = _textureRegion.getRegionWidth() * _scaleX;
            _hitRectangle.height = _textureRegion.getRegionHeight() * _scaleX;
        }
    }

    public boolean isTouched(float x, float y)
    {
        return _hitRectangle.contains(x, y);
    }

    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer)
    {
        if (!_isHidden)
        {
            if (_textureRegion == null)
            {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                shapeRenderer.setColor(_color);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.rect(_x, _y, _width, _height);
                shapeRenderer.end();
                Gdx.gl.glDisable(GL20.GL_BLEND);

                GlyphLayout layout = new GlyphLayout();
                layout.setText(_font,_text);
                batch.begin();
                _font.draw(batch,layout, _x + (_width / 2) - (layout.width / 2), _y + (_height / 2) - (layout.height / 2));
                batch.end();
            }
            else
            {

                batch.begin();
                if (_color != null)
                {
                    batch.setColor(_color);
                }
                GlyphLayout layout = new GlyphLayout();
                layout.setText(_font,_text);
                batch.draw(_textureRegion, _x, _y, _width/2, _height/2, _width, _height, _scaleX, _scaleY, 0);
                batch.setColor(Color.WHITE);
                _font.draw(batch,layout, _x + (_width / 2) - (layout.width / 2), _y + (_height / 2) );// (layout.height / 2) );
                batch.end();
            }
        }
    }
}

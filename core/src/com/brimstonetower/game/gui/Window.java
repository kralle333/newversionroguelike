package com.brimstonetower.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.brimstonetower.game.managers.AssetManager;

import java.util.HashMap;


public class Window
{
    protected Rectangle _windowRectangle;
    public int getWidth(){return (int)_windowRectangle.getWidth();}
    public int getHeight(){return (int)_windowRectangle.getHeight();}
    protected Color _windowColor;

    protected int _frameSize;
    protected Color _frameColor;

    protected HashMap<String, Button> _nameButtonMap = new HashMap<String, Button>();
    protected boolean _isOpen;
    public boolean isOpen()
    {
        return _isOpen;
    }

    public void show()
    {
        _isOpen = true;
    }
    public void hide()
    {
        _isOpen = false;
    }

    private TextureRegion _buttonRegion;


    public Window(int x, int y, int width, int height, Color color, int frameSize, Color frameColor)
    {
        _windowRectangle = new Rectangle();
        _windowColor = color;
        _frameSize = frameSize;
        _frameColor = frameColor;
        reposition(x,y,width,height);
        _buttonRegion=new TextureRegion(AssetManager.getGuiTexture("menuButton"),0,48,128,64);
    }

    public void reposition(int x, int y, int width, int height)
    {
        _windowRectangle.set(x,y,width,height);
    }

    public void reset()
    {
        _nameButtonMap.clear();
    }

    public void arrangeButtons(float percentageX, float percentageY, float percentageXGap, float percentageYGap, int buttonsPerRow)
    {
        arrangeButtons((int) (_windowRectangle.getWidth() * percentageX), (int) (_windowRectangle.getHeight() * percentageY), (int) (_windowRectangle.getWidth() * percentageX), (int) (_windowRectangle.getHeight() * percentageY), buttonsPerRow);
    }

    public void arrangeButtons(int startXLocal, int startYLocal, int xGap, int yGap, int buttonsPerRow)
    {
        if (_nameButtonMap.values().size() == 0)
        {
            return;
        }

        int x = (int)_windowRectangle.x + startXLocal;
        int y = (int)_windowRectangle.y + startYLocal;
        int widthForButtons = (int)(_windowRectangle.width-((buttonsPerRow-1)*xGap));
        widthForButtons-=startXLocal*2<widthForButtons?(2*startXLocal):widthForButtons/10;
        int buttonWidth =  widthForButtons/ buttonsPerRow;

        int heightForButtons = (int)(_windowRectangle.width-((buttonsPerRow-1)*xGap));
        heightForButtons-=startYLocal*2<heightForButtons?(2*startYLocal):heightForButtons/10;
        int buttonHeight =  heightForButtons/ buttonsPerRow;
        for (Button button : _nameButtonMap.values())
        {
            button.reposition(x,y,buttonWidth,buttonHeight);
            x += buttonWidth+xGap;
            if (x + button.getWidth() > _windowRectangle.x + _windowRectangle.width)
            {
                x = (int)_windowRectangle.x + startXLocal;
                y += buttonHeight+yGap;
            }
            if (y > _windowRectangle.y + _windowRectangle.getHeight())
            {
                Gdx.app.log("Window", "Buttons repositioned outside window frame");
            }
        }
    }

    public void arrangeButtons(int startXLocal, int startYLocal, int xGap, int yGap)
    {
        if (_nameButtonMap.values().size() == 0)
        {
            return;
        }

        int x = (int)_windowRectangle.x + startXLocal;
        int y = (int)_windowRectangle.y + startYLocal;
        for (Button button : _nameButtonMap.values())
        {

            button.setScale(_windowRectangle.width / button.getWidth());
            button.reposition(x + xGap, y + yGap);
            x += button.getWidth();
            if (x + button.getWidth() > _windowRectangle.x + _windowRectangle.width)
            {
                x = (int)_windowRectangle.x + startXLocal;
                y += button.getHeight();
            }
            if (y > _windowRectangle.y + _windowRectangle.getHeight())
            {
                Gdx.app.log("Window", "Buttons repositioned outside window frame");
            }
        }
    }



    public Button addButton(String buttonKey, String buttonText, TextureRegion region)
    {
        _nameButtonMap.put(buttonKey, new Button(0, 0, region,region.getRegionWidth(),region.getRegionHeight(),buttonText));
        return _nameButtonMap.get(buttonKey);
    }

    public Button addButton(String buttonText, float percentX, float percentY, float percentWidth, float percentHeight)
    {
        int buttonX = (int)(_windowRectangle.x + (_windowRectangle.width * percentX));
        int buttonY = (int)(_windowRectangle.y + (_windowRectangle.height * percentY));
        int width = (int)(_windowRectangle.width * percentWidth);
        int height= (int)( _windowRectangle.height * percentHeight);

        _nameButtonMap.put(buttonText, new Button(buttonX, buttonY,_buttonRegion,width, height, buttonText));
        return _nameButtonMap.get(buttonText);
    }

    public void removeButton(String buttonName)
    {
        _nameButtonMap.remove(buttonName);
    }

    public void hideButton(String buttonName)
    {
        _nameButtonMap.get(buttonName).hide();
    }

    public void showButton(String buttonName)
    {
        _nameButtonMap.get(buttonName).show();
    }

    public Button getClickedButton(float x, float y)
    {
        for (Button button : _nameButtonMap.values())
        {
            if (button.isTouched(x, y))
            {
                return button;
            }
        }
        return null;
    }

    public Button getButton(String name)
    {
        return _nameButtonMap.get(name);
    }

    public boolean isPressed(String buttonName, float x, float y)
    {
        return _isOpen && _nameButtonMap.get(buttonName).isTouched(x, y);
    }

    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer)
    {
        if (_isOpen)
        {
            drawFrame(shapeRenderer);
            drawButtons(batch,shapeRenderer);
        }
    }
    protected void drawFrame(ShapeRenderer shapeRenderer)
    {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(_frameColor);
        shapeRenderer.rect(_windowRectangle.x - _frameSize, _windowRectangle.y - _frameSize, _windowRectangle.width + (_frameSize * 2), _windowRectangle.height + (_frameSize * 2));
        shapeRenderer.setColor(_windowColor);
        shapeRenderer.rect(_windowRectangle.x, _windowRectangle.y, _windowRectangle.width, _windowRectangle.height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    protected void drawButtons(SpriteBatch batch,ShapeRenderer shapeRenderer)
    {
        for (Button button : _nameButtonMap.values())
        {
            button.draw(batch, shapeRenderer);
        }
    }
}

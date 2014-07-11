package uni.aau.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.HashMap;


public class Window
{
    protected Rectangle _windowRectangle;
    protected Color _windowColor;

    protected int _frameSize;
    protected Color _frameColor;

    public enum ButtonStyle{FreeForm,Sorted}
    private ButtonStyle _buttonStyle;
    private HashMap<String,Button> _nameButtonMap = new HashMap<String, Button>();
    protected boolean _isOpen;
    public boolean isOpen(){return _isOpen;}

    public void show(){_isOpen=true;}
    public void hide(){_isOpen=false;}


    public Window(int x, int y, int width, int height,Color color, int frameSize,Color frameColor)
    {
        _windowRectangle = new Rectangle(x,y,width,height);
        _windowColor=color;
        _frameSize = frameSize;
        _frameColor=frameColor;
    }
    public void reset()
    {
        _nameButtonMap.clear();
    }

    public void arrangeButtons(float percentageX, float percentageY, float percentageXGap, float percentageYGap, int buttonsPerRow)
    {
        arrangeButtons((int)(_windowRectangle.getWidth()*percentageX),(int)(_windowRectangle.getHeight()*percentageY),(int)(_windowRectangle.getWidth()*percentageX),(int)(_windowRectangle.getHeight()*percentageY),buttonsPerRow);
    }
    public void arrangeButtons(int startXLocal, int startYLocal,int xGap, int yGap, int buttonsPerRow)
    {
        if(_nameButtonMap.values().size()==0)
        {
            return;
        }

        float x=_windowRectangle.x+startXLocal-xGap;
        float y=_windowRectangle.y+startYLocal-yGap;
        float buttonWidth = _windowRectangle.width/buttonsPerRow;
        for(Button button : _nameButtonMap.values())
        {
            button.setScale(buttonWidth/button.getWidth());
            button.reposition(x+xGap,y+yGap);
            x+=buttonWidth;
            if(x+button.getWidth()>_windowRectangle.x+_windowRectangle.width)
            {
                x=_windowRectangle.x+startXLocal;
                y+=buttonWidth;
            }
            if(y>_windowRectangle.y+_windowRectangle.getHeight())
            {
                Gdx.app.log("Window","Buttons repositioned outside window frame");
            }
        }
    }
    public void arrangeButtons(int startXLocal,int startYLocal,int xGap, int yGap)
    {
        if(_nameButtonMap.values().size()==0)
        {
            return;
        }

        float x=_windowRectangle.x+startXLocal;
        float y=_windowRectangle.y+startYLocal;
        for(Button button : _nameButtonMap.values())
        {

            button.setScale(_windowRectangle.width/button.getWidth());
            button.reposition(x+xGap,y+yGap);
            x+=button.getWidth();
            if(x+button.getWidth()>_windowRectangle.x+_windowRectangle.width)
            {
                x=_windowRectangle.x+startXLocal;
                y+=button.getHeight();
            }
            if(y>_windowRectangle.y+_windowRectangle.getHeight())
            {
                Gdx.app.log("Window","Buttons repositioned outside window frame");
            }
        }
    }
    public Button addButton(String buttonText,Color color)
    {
        return addButton(buttonText,0,0,0,0, color);
    }
    public Button addButton(String buttonText,TextureRegion region)
    {
        _nameButtonMap.put(buttonText,new Button(0,0,region,buttonText));
        return _nameButtonMap.get(buttonText);
    }
    public Button addButton(String buttonText,float percentX,float percentY,float percentWidth, float percentHeight,Color color)
    {
        float buttonX = _windowRectangle.x+(_windowRectangle.width*percentX);
        float buttonY = _windowRectangle.y+(_windowRectangle.height*percentY);

        _nameButtonMap.put(buttonText,new Button(buttonX,buttonY,_windowRectangle.width*percentWidth,_windowRectangle.height*percentHeight,buttonText,color));
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
        for(Button button : _nameButtonMap.values())
        {
            if(button.isTouched(x,y))
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
        return _isOpen && _nameButtonMap.get(buttonName).isTouched(x,y);
    }

    public void draw(SpriteBatch batch,ShapeRenderer shapeRenderer)
    {
        if(_isOpen)
        {
            Gdx.gl.glEnable(GL10.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(_frameColor);
            shapeRenderer.rect(_windowRectangle.x - _frameSize, _windowRectangle.y - _frameSize, _windowRectangle.width + (_frameSize * 2), _windowRectangle.height + (_frameSize * 2));
            shapeRenderer.setColor(_windowColor);
            shapeRenderer.rect(_windowRectangle.x, _windowRectangle.y, _windowRectangle.width, _windowRectangle.height);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL10.GL_BLEND);
            for(Button button : _nameButtonMap.values())
            {
                button.draw(batch,shapeRenderer);
            }

        }
    }
}

package com.brimstonetower.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.managers.AssetManager;

public class PlayerInfoWindowFrame extends Window
{
    private BitmapFont _font;
    private Vector2 namePos;
    private Vector2 levelPos;
    private Vector2 hpPos;
    private Vector2 strPos;
    private Vector2 expPos;
    private boolean _isInitialized = false;
    private Rectangle hpRect = new Rectangle();
    private Rectangle strRect = new Rectangle();
    private Rectangle expRect = new Rectangle();

    private Color hpColor = new Color(0.7f,0.21f,0.21f,1);
    private Color expColor = new Color(0.21f,0.7f,0.21f,1);


    public PlayerInfoWindowFrame(int x, int y, int width, int height,Color color,int frameSize, Color frameColor)
    {
        super(x,y,width,height,color,frameSize,frameColor);
        _font = AssetManager.getFont("description");
        _isInitialized=true;
    }

    @Override
    public void reposition(int x, int y, int width, int height)
    {
        if(_isInitialized)
        {
            super.reposition(x, y, width, height);
            namePos = new Vector2(x + (Gdx.graphics.getWidth() / 128), y + (Gdx.graphics.getWidth() / 128));
            levelPos = new Vector2(namePos.x+_font.getBounds("Name: VeryLongName").width, namePos.y);
            hpPos = new Vector2(namePos.x, namePos.y + height*1.2f - _font.getBounds("Height").height);
            expPos = new Vector2(hpPos.x + _font.getBounds("Hp: 1000/1000   ").width, hpPos.y);

            strPos = new Vector2(hpPos.x + _font.getBounds("Hp: 1000/1000   ").width, hpPos.y);

            hpRect.x = hpPos.x+_font.getBounds("Hp: ").width;
            hpRect.y= hpPos.y*0.9f;
            hpRect.width =Gdx.graphics.getWidth()*0.1f;
            hpRect.height= _font.getBounds("height").height*1.2f;

            strRect.x = strPos.x+_font.getBounds("Str: ").width;
            strRect.y= strPos.y*0.9f;
            strRect.width =Gdx.graphics.getWidth()*0.1f;
            strRect.height= hpRect.height;

            expRect.x = expPos.x+_font.getBounds("Exp: ").width;
            expRect.y= expPos.y*0.9f;
            expRect.width =Gdx.graphics.getWidth()*0.1f;
            expRect.height=hpRect.height;
        }
    }

    public void draw(SpriteBatch batch,Player player, int depth)
    {
        _font.draw(batch, "Name: " + player.getName(), namePos.x, namePos.y);
        _font.draw(batch, "Lvl: "+player.getLevel(),levelPos.x,levelPos.y);
        _font.draw(batch, "Hp: "+player.getHitpoints()+"/"+player.getMaxHitPoints(),hpPos.x, hpPos.y);
        //_font.draw(batch, "Str: ", strPos.x,strPos.y);
        _font.draw(batch, "Exp: "+player.getExperience()+"/"+player.getExperienceToNextLevel(),expPos.x, expPos.y);
        _font.draw(batch, "Floor: " + depth, Gdx.graphics.getWidth()/2, namePos.y);
    }
    public void drawProgressBars(ShapeRenderer shapeRenderer,Player player)
    {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawProgress(shapeRenderer, hpRect, ((float) player.getHitpoints()) / ((float) player.getMaxHitPoints()), hpColor);
        //drawProgress(shapeRenderer, strRect, ((float) player.getCurrentStr()) / ((float) player.getMaxStr()), Color.BLUE);
        drawProgress(shapeRenderer, expRect, ((float) player.getExperience()) / ((float) player.getExperienceToNextLevel()), expColor);
        shapeRenderer.end();
    }

    private void drawProgress(ShapeRenderer shapeRenderer, Rectangle positionRectangle,float progress,Color color)
    {
        shapeRenderer.rect(positionRectangle.x,positionRectangle.y,positionRectangle.width,positionRectangle.height,Color.GRAY,Color.GRAY,Color.GRAY,Color.GRAY);
        shapeRenderer.rect(positionRectangle.x,positionRectangle.y,progress*(positionRectangle.width),positionRectangle.height,color,color,color,color);
    }
}

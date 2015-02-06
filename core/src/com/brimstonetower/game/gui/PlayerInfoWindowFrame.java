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
            hpPos = new Vector2(namePos.x, namePos.y + height - _font.getBounds("Height").height);
            strPos = new Vector2(hpPos.x + _font.getBounds("Hp: 1000/1000   ").width, hpPos.y);
            expPos = new Vector2(strPos.x + _font.getBounds("Str: 100/100    ").width, hpPos.y);

            hpRect.x = hpPos.x+_font.getBounds("Hp: ").width;
            hpRect.y= hpPos.y;
            hpRect.width =Gdx.graphics.getWidth()*0.1f;
            hpRect.height= _font.getBounds("height").height;

            strRect.x = strPos.x+_font.getBounds("Str: ").width;
            strRect.y= strPos.y;
            strRect.width =Gdx.graphics.getWidth()*0.1f;
            strRect.height= hpRect.height;

            expRect.x = expPos.x+_font.getBounds("Exp: ").width;
            expRect.y= expPos.y;
            expRect.width =Gdx.graphics.getWidth()*0.1f;
            expRect.height=hpRect.height;
        }
    }

    public void draw(SpriteBatch batch,Player player, int depth)
    {
        _font.draw(batch, "Name: " + player.getName(), namePos.x, namePos.y);
        _font.draw(batch, "Lvl: "+player.getLevel(),levelPos.x,levelPos.y);
        _font.draw(batch, "Hp: ",hpPos.x, hpPos.y);
        _font.draw(batch, "Str: ", strPos.x,strPos.y);
        _font.draw(batch, "Exp: ",expPos.x, expPos.y);
        _font.draw(batch, "Floor: " + depth, Gdx.graphics.getWidth()/2, namePos.y);
    }
    public void drawProgressBars(ShapeRenderer shapeRenderer,Player player)
    {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawProgress(shapeRenderer, hpRect, ((float) player.getHitpoints()) / ((float) player.getMaxHitPoints()), Color.RED);
        drawProgress(shapeRenderer, strRect, ((float) player.getCurrentStr()) / ((float) player.getMaxStr()), Color.BLUE);
        drawProgress(shapeRenderer, expRect, ((float) player.getExperience()) / ((float) player.getExperienceToNextLevel()), Color.GREEN);
        shapeRenderer.end();
    }

    private void drawProgress(ShapeRenderer shapeRenderer, Rectangle positionRectangle,float progress,Color color)
    {
        shapeRenderer.rect(positionRectangle.x,positionRectangle.y,positionRectangle.width,positionRectangle.height,Color.GRAY,Color.GRAY,Color.GRAY,Color.GRAY);
        shapeRenderer.rect(positionRectangle.x,positionRectangle.y,progress*(positionRectangle.width),positionRectangle.height,color,color,color,color);
    }
}

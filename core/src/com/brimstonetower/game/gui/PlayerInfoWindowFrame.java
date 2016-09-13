package com.brimstonetower.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.managers.AssetManager;

import java.util.ArrayList;

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
            GlyphLayout layout = new GlyphLayout();
            layout.setText(_font,"Name: VeryLongName");

            super.reposition(x, y, width, height);
            namePos = new Vector2(x + (Gdx.graphics.getWidth() / 128), y + (Gdx.graphics.getWidth() / 128));
            levelPos = new Vector2(namePos.x+layout.width, namePos.y);
            layout.setText(_font,"Height");
            hpPos = new Vector2(namePos.x, namePos.y + height*1.3f +layout.height);
            layout.setText(_font,"Hp: 1000/1000   ");
            expPos = new Vector2(levelPos.x, hpPos.y);
            strPos = new Vector2(hpPos.x + layout.width, hpPos.y);
            layout.setText(_font,"Hp: ");
            hpRect.x = hpPos.x+layout.width;
            hpRect.y= hpPos.y*0.9f;
            hpRect.width =Gdx.graphics.getWidth()*0.1f;
            layout.setText(_font,"height");
            hpRect.height= layout.height*1.4f;

            layout.setText(_font,"Str: ");
            strRect.x = strPos.x+layout.width;
            strRect.y= strPos.y*0.9f;
            strRect.width =Gdx.graphics.getWidth()*0.1f;
            strRect.height= hpRect.height;

            layout.setText(_font,"Exp: ");
            expRect.x = expPos.x+layout.width;
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
        //_font.drawAnimation(batch, "Str: ", strPos.x,strPos.y);
        _font.draw(batch, "Exp: "+player.getExperience()+"/"+player.getExperienceToNextLevel(),expPos.x, expPos.y);
        _font.draw(batch, "Floor: " + depth, Gdx.graphics.getWidth()/2, namePos.y);

        Vector2 effectPos = new Vector2(Gdx.graphics.getWidth()/3,namePos.y);
        ArrayList<Effect> currentEffects = player.getCurrentEffects();
        for (Effect e : currentEffects)
        {
            switch (e.getName())
            {
                case "Swiftness":batch.draw(AssetManager.getTextureRegion("statusIcons","swift",32,32),effectPos.x+=32,effectPos.y);break;
                case "Blindness":batch.draw(AssetManager.getTextureRegion("statusIcons","blind",32,32),effectPos.x+=32,effectPos.y);break;
                case "Poison":batch.draw(AssetManager.getTextureRegion("statusIcons","poison",32,32),effectPos.x+=32,effectPos.y);break;
                case "Regeneration":batch.draw(AssetManager.getTextureRegion("statusIcons","regeneration",32,32),effectPos.x+=32,effectPos.y);break;
            }
        }
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

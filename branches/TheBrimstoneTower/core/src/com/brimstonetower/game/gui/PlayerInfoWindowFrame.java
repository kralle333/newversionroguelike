package com.brimstonetower.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
            levelPos = new Vector2(namePos.x, namePos.y + height - _font.getBounds("Height").height);
            hpPos = new Vector2(levelPos.x + _font.getBounds("Lvl: 100     ").width, levelPos.y);
            strPos = new Vector2(hpPos.x + _font.getBounds("Hp: 1000/1000     ").width, levelPos.y);
            expPos = new Vector2(strPos.x + _font.getBounds("Str: 100/100     ").width, levelPos.y);
        }
    }

    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer,Player player, int depth)
    {
        _font.draw(batch, "Name: " + player.getName(), namePos.x, namePos.y);
        _font.draw(batch, "Lvl: "+player.getLevel(),levelPos.x,levelPos.y);
        _font.draw(batch, "Hp: " + player.getHitpoints() + "/" + player.getMaxHitPoints(),hpPos.x, hpPos.y);
        _font.draw(batch, "Str: " + player.getCurrentStr() + "/" + player.getMaxStr(), strPos.x,strPos.y);
        _font.draw(batch, "Exp: " + player.getExperience() + "/" + player.getExperienceToNextLevel(),expPos.x, expPos.y);
        _font.draw(batch, "Floor: " + depth, Gdx.graphics.getWidth()/2, namePos.y);
    }
}

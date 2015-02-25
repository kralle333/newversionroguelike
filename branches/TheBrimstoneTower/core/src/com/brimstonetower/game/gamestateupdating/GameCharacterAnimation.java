package com.brimstonetower.game.gamestateupdating;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.BreakableObject;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;

import java.util.ArrayList;
import java.util.HashMap;

public class GameCharacterAnimation
{
    private class DamageIndicator
    {
        public float fallSpeedX;
        public float fallSpeedY;
        public Vector2 position;
        public String textToShow;
        public Color color;
        public float secondsActive;
        public float timeLeft;
        public boolean isVisible(){return timeLeft>0;}

        public DamageIndicator(float fallSpeedX, float fallSpeedY,Vector2 position, String textToShow, Color color, float secondsActive)
        {
            this.textToShow = textToShow;
            this.fallSpeedX = fallSpeedX;
            this.fallSpeedY = fallSpeedY;
            this.position =position;
            this.color = color;
            this.secondsActive = secondsActive;
            this.timeLeft = secondsActive;
        }

        public void update()
        {
            position.x+=fallSpeedX;
            position.y+=fallSpeedY;
            timeLeft-=Gdx.app.getGraphics().getDeltaTime();
        }
        public void draw(BitmapFont font, SpriteBatch batch)
        {
            font.setColor(color.r, color.g, color.b, timeLeft / secondsActive);
            font.draw(batch, textToShow, (int) position.x, (int) position.y);
            font.setColor(Color.WHITE);
        }
    }

    private ArrayList<DamageIndicator> _damageIndicators = new ArrayList<DamageIndicator>();
    private boolean _isShowingDamageIndicator = false;
    public boolean isShowingDamageIndicator(){return _isShowingDamageIndicator;}
    private float _timer;
    private float _playTime;
    public boolean isPlaying(){return _timer <_playTime;}
    private GameAction _gameActionToPlay;
    public GameAction getPlayedAction(){return _gameActionToPlay;}
    public GameAction.Type getType(){return _gameActionToPlay.getType();}

    //Attack variables
    private float lungeTime;
    private float retractTime;
    private boolean isLunging=true;
    private GameCharacter defender;
    private GameCharacter attacker;
    private Vector2 attackedPosition = new Vector2();

    //Destroy
    private BreakableObject targetObject;

    //Keeping track of thrown items
    private Item _thrownItem;
    private Vector2 _thrownItemFromPosition;
    private Vector2 _thrownItemCurrentPosition;
    private Vector2 _thrownItemTarget;

    //Search variables
    private HashMap<Tile,Float> scaleOfSearchIcons = new HashMap<Tile,Float>();
    private TextureRegion _searchIconRegion;

    public static boolean typeIsAnimated(GameAction.Type type)
    {
        return type== GameAction.Type.Attack ||
                type== GameAction.Type.Destroy ||
                type== GameAction.Type.Move ||
                type== GameAction.Type.Search ||
                type == GameAction.Type.Throw;
    }


    public GameCharacterAnimation()
    {
        _gameActionToPlay = new GameAction();
        _gameActionToPlay.setAsEmpty();
    }

    public void emptyGameAction()
    {
        _gameActionToPlay.setAsEmpty();
    }
    public void playGameAction(GameAction gameAction, float playTime)
    {
        _gameActionToPlay.setAction(gameAction);
        _timer = 0f;
        _playTime = playTime;

        if(gameAction.getType() == GameAction.Type.Attack)
        {
            _isShowingDamageIndicator=true;
            lungeTime = _playTime/3;
            retractTime = _playTime*2/3;
            defender = _gameActionToPlay.getTargetCharacter();
            attacker = _gameActionToPlay.getOwner();
            //Done here such that we can retrieve the damage total
            attacker.calculateAttackDamage(defender);

            attackedPosition.x=(attacker.getWorldPosition().x+defender.getWorldPosition().x)/2;
            attackedPosition.y=(attacker.getWorldPosition().y+defender.getWorldPosition().y)/2;

            Color color = attacker instanceof Player?Color.GREEN:Color.RED;
            Vector2 indicatorPosition = new Vector2(defender.getWorldPosition().x+(DungeonMap.TileSize/2),defender.getWorldPosition().y);

            String damageToShow = "Magenta";
            switch (attacker.getLastHitState())
            {
                case Normal:
                    damageToShow =String.valueOf(attacker.getDealtDamage());
                    AssetManager.getSound("hit").play();
                    break;
                case Critical:
                    damageToShow =String.valueOf(attacker.getDealtDamage())+"!";
                    AssetManager.getSound("critical").play();
                    break;
                case Blocked:
                    damageToShow ="Blocked";
                    AssetManager.getSound("block").play();
                    break;
                case Miss:
                    damageToShow = "Miss";
                    AssetManager.getSound("miss").play();
                    break;
            }

            _damageIndicators.add(new DamageIndicator(0,0.8f,indicatorPosition,damageToShow, color, 0.5f));
            isLunging=true;


        }
        else if(gameAction.getType() == GameAction.Type.Destroy)
        {
            lungeTime = _playTime/3;
            retractTime = _playTime*2/3;
            attacker = gameAction.getOwner();
            targetObject = gameAction.getTargetObject();
            attackedPosition.x=(attacker.getWorldPosition().x+targetObject.getWorldPosition().x)/2;
            attackedPosition.y=(attacker.getWorldPosition().y+targetObject.getWorldPosition().y)/2;
            isLunging=true;
            AssetManager.getSound("hit").play();
        }
        else if(gameAction.getType() == GameAction.Type.Throw)
        {
            _thrownItem = gameAction.getTargetItem();
            _thrownItemCurrentPosition = gameAction.getOwner().getWorldPosition();
            _thrownItemFromPosition=_thrownItemCurrentPosition;
            _thrownItemTarget = gameAction.getTargetTile().getWorldPosition();
            AssetManager.getSound("throw").play();
        }
        else if(gameAction.getType() == GameAction.Type.Search)
        {
            scaleOfSearchIcons.clear();
            for(Tile tile : gameAction.getTargetTile().getWalkableNeighbours())
            {
                scaleOfSearchIcons.put(tile, RandomGen.getRandomFloat(0.45f, 0.5f));
            }
            _searchIconRegion =AssetManager.getTextureRegion("misc", "searchEye", DungeonMap.TileSize, DungeonMap.TileSize);
            AssetManager.getSound("search").play();
        }
    }
    public void playDamageIndication(int damage, Vector2 position, Color color)
    {
        _damageIndicators.add(new DamageIndicator(0,0.8f,new Vector2(position.x,position.y),String.valueOf(damage),color,0.5f));
        _isShowingDamageIndicator=true;
    }

    final ArrayList<DamageIndicator> damageIndicatorsToRemove = new ArrayList<DamageIndicator>();
    public void drawAnimation(SpriteBatch batch)
    {
            switch(_gameActionToPlay.getType())
            {
                case Attack:
                    if(isLunging)
                    {
                        attacker.setPosition(
                                moveTowards(attacker.getCurrentTile().getWorldPosition(),
                                        attackedPosition,
                                        (_timer - lungeTime) / retractTime));
                        if(_timer >= lungeTime)
                        {
                            isLunging = false;
                        }
                    }
                    else
                    {
                        attacker.setPosition(
                                moveTowards(attackedPosition,
                                        attacker.getCurrentTile().getWorldPosition(), (_timer - lungeTime) / retractTime));
                    }
                    break;
                case Destroy:
                    if(isLunging)
                    {
                        attacker.setPosition(
                                moveTowards(attacker.getCurrentTile().getWorldPosition(),
                                        attackedPosition,
                                        (_timer - lungeTime) / retractTime));
                        if(_timer >= lungeTime)
                        {
                            isLunging = false;
                        }
                    }
                    else
                    {
                        attacker.setPosition(
                                moveTowards(attackedPosition,
                                        attacker.getCurrentTile().getWorldPosition(), (_timer - lungeTime) / retractTime));
                    }
                    break;
                case Move:
                    Vector2 oldPosition = _gameActionToPlay.getOwner().getCurrentTile().getWorldPosition();
                    Vector2 goalPosition = _gameActionToPlay.getTargetTile().getWorldPosition();
                    Vector2 newPosition = moveTowards(oldPosition, goalPosition, _timer / _playTime);

                    _gameActionToPlay.getOwner().setPosition(newPosition);
                    break;
                case Search:
                    batch.setColor(1, 1, 1, 1 - (_timer / _playTime));
                    for(Tile neighbour : _gameActionToPlay.getTargetTile().getWalkableNeighbours())
                    {
                        float x = neighbour.getWorldPosition().x;
                        float y = neighbour.getWorldPosition().y;
                        float scale = scaleOfSearchIcons.get(neighbour);
                        batch.draw(
                                _searchIconRegion, x, y, DungeonMap.TileSize / 2, DungeonMap.TileSize / 2, //Position
                                DungeonMap.TileSize, DungeonMap.TileSize,    //Dimensions
                                scale, scale, //Scale
                                0);//Rotation

                        scaleOfSearchIcons.put(neighbour, scale - 0.01f);
                    }
                    batch.setColor(Color.WHITE);

                    break;
                case Throw:
                    _thrownItemCurrentPosition = moveTowards(_thrownItemFromPosition, _thrownItemTarget, _timer / _playTime);
                    _thrownItem.draw(batch, _thrownItemCurrentPosition.x, _thrownItemCurrentPosition.y);
                    break;
            }
        _timer += Gdx.graphics.getDeltaTime();
    }
    public void drawDamageIndicators(SpriteBatch batch)
    {
        if(_isShowingDamageIndicator)
        {
            damageIndicatorsToRemove.clear();
            for (DamageIndicator damageIndicator : _damageIndicators)
            {
                damageIndicator.update();
                if (damageIndicator.isVisible())
                {
                    damageIndicator.draw(AssetManager.getFont("description"),batch);
                }
                else
                {
                    damageIndicatorsToRemove.add(damageIndicator);
                }
            }
            for(DamageIndicator toRemove : damageIndicatorsToRemove)
            {
                _damageIndicators.remove(toRemove);
            }
            if(_damageIndicators.isEmpty())
            {
                _isShowingDamageIndicator=false;
            }
        }
    }


    private Vector2 moveTowards(Vector2 oldPosition, Vector2 newPosition, float progress)
    {
        float newX = MathUtils.lerp(oldPosition.x,newPosition.x,progress);
        float newY = MathUtils.lerp(oldPosition.y,newPosition.y,progress);

        return new Vector2(newX,newY);
    }
}

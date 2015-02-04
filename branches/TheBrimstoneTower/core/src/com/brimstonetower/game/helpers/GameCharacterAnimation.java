package com.brimstonetower.game.helpers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.GameCharacter;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.gameobjects.items.Item;
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
    GameCharacter defender;
    GameCharacter attacker;

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
        return type== GameAction.Type.Attack||
                type== GameAction.Type.Move||
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
        _damageIndicators.clear();
        _gameActionToPlay.setAction(gameAction);
        _timer = 0f;
        _playTime = playTime;

        if(gameAction.getType() == GameAction.Type.Attack)
        {
            lungeTime = _playTime/3;
            retractTime = _playTime*2/3;
            defender = _gameActionToPlay.getTargetCharacter();
            attacker = _gameActionToPlay.getOwner();
            //Done here such that we can retrieve the damage total
            attacker.attack(defender);

            Color color = attacker instanceof Player?Color.GREEN:Color.RED;
            Vector2 indicatorPosition = new Vector2(defender.getWorldPosition().x+(DungeonMap.TileSize/2),defender.getWorldPosition().y);

            _damageIndicators.add(new DamageIndicator(0,0.8f,indicatorPosition,String.valueOf(attacker.getDealtDamage()), color, 0.5f));
        }
        else if(gameAction.getType() == GameAction.Type.Throw)
        {
            _thrownItem = gameAction.getTargetItem();
            _thrownItemCurrentPosition = gameAction.getOwner().getWorldPosition();
            _thrownItemFromPosition=_thrownItemCurrentPosition;
            _thrownItemTarget = gameAction.getTargetTile().getWorldPosition();
        }
        else if(gameAction.getType() == GameAction.Type.Search)
        {
            scaleOfSearchIcons.clear();
            for(Tile tile : gameAction.getTargetTile().getWalkableNeighbours())
            {
                scaleOfSearchIcons.put(tile,RandomGen.getRandomFloat(0.45f,0.5f));
            }
            _searchIconRegion =AssetManager.getTextureRegion("searchEye","searchEye",DungeonMap.TileSize,DungeonMap.TileSize);
        }
    }
    public void playDamageIndication(int damage, Vector2 position, Color color,float playTime)
    {
        _damageIndicators.clear();
        _timer = 0;
        _damageIndicators.add(new DamageIndicator(2,0,position,String.valueOf(damage),color,playTime));
    }


    public void draw(SpriteBatch batch)
    {
        switch (_gameActionToPlay.getType())
        {
            case Attack:
                final ArrayList<DamageIndicator> damageIndicatorsToRemove = new ArrayList<DamageIndicator>();
                damageIndicatorsToRemove.clear();
                if(isLunging)
                {
                    attacker.setPosition(
                            moveTowards(attacker.getCurrentTile().getWorldPosition(),
                                    defender.getWorldPosition(),
                                    (_timer-lungeTime)/retractTime));
                    if(_timer>=lungeTime)
                    {
                        isLunging = false;
                    }
                }
                else
                {
                    attacker.setPosition(
                            moveTowards(defender.getWorldPosition(),
                                    attacker.getCurrentTile().getWorldPosition(),(_timer-lungeTime)/retractTime));
                }
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
                break;
            case Move:
                Vector2 oldPosition = _gameActionToPlay.getOwner().getCurrentTile().getWorldPosition();
                Vector2 goalPosition = _gameActionToPlay.getTargetTile().getWorldPosition();
                Vector2 newPosition = moveTowards(oldPosition, goalPosition, _timer / _playTime);

                _gameActionToPlay.getOwner().setPosition(newPosition);
                break;
            case Search:
                batch.setColor(1,1,1,1-(_timer/_playTime));
                for(Tile neighbour : _gameActionToPlay.getTargetTile().getWalkableNeighbours())
                {
                    float x = neighbour.getWorldPosition().x;
                    float y = neighbour.getWorldPosition().y;
                    float scale =  scaleOfSearchIcons.get(neighbour);
                    batch.draw(
                            _searchIconRegion,x,y,DungeonMap.TileSize/2,DungeonMap.TileSize/2, //Position
                            DungeonMap.TileSize,DungeonMap.TileSize,    //Dimensions
                            scale,scale, //Scale
                            0);//Rotation

                    scaleOfSearchIcons.put(neighbour,scale-0.01f);
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


    private Vector2 moveTowards(Vector2 oldPosition, Vector2 newPosition, float progress)
    {
        float newX = MathUtils.lerp(oldPosition.x,newPosition.x,progress);
        float newY = MathUtils.lerp(oldPosition.y,newPosition.y,progress);

        return new Vector2(newX,newY);
    }
}

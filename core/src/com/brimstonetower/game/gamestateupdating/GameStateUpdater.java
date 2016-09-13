package com.brimstonetower.game.gamestateupdating;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.*;
import com.brimstonetower.game.gameobjects.equipment.Weapon;
import com.brimstonetower.game.gameobjects.scrolls.Scroll;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.gui.Inventory;
import com.brimstonetower.game.helpers.ColorHelper;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.AssetManager;
import com.brimstonetower.game.managers.ItemManager;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class GameStateUpdater
{

    public static Player player;
    public static Inventory inventory;
    public static  DungeonMap playedMap;
    private ArrayList<Monster> _monsters;
    private ArrayList<Gas> _gasClouds = new ArrayList<Gas>();
    private ArrayList<Chest> _chests = new ArrayList<Chest>();
    private ArrayList<Trap> _traps = new ArrayList<Trap>();


    //Turn handling
    private HashMap<Monster, Integer> _monsterTime = new HashMap<Monster, Integer>();
    private PriorityQueue<Monster> _monsterTurns;

    public class CharacterSpeedComparator implements Comparator<GameCharacter>
    {
        @Override
        public int compare(GameCharacter lhs, GameCharacter rhs)
        {
            return lhs.getCostOfNextAction() - rhs.getCostOfNextAction();
        }
    }
    private static int _turn = 0;

    //Animation
    private GameCharacterAnimation _currentAnimation = new GameCharacterAnimation();
    public GameCharacterAnimation getCurrentAnimation(){return _currentAnimation;}
    private final float timePerAnimation = 0.20f;

    //Select item dialog
    private static boolean _selectItemDialog = false;
    public static boolean isSelectingItemForScroll()
    {
        return _selectItemDialog;
    }
    private static boolean _selectTileDialog = false;
    public static boolean isSelectingTileForScroll(){return _selectTileDialog;}

    private Scroll _usedScroll;
    public Scroll getUsedScroll()
    {
        return _usedScroll;
    }
    public void clearUsedScroll(){_usedScroll=null;}

    public void setGameState(Player player, Inventory inventory, DungeonMap playedMap)
    {
        _turn = 1;
        GameStateUpdater.player = player;
        _monsters = playedMap.getMonsters();

        GameStateUpdater.inventory = inventory;
        GameStateUpdater.playedMap = playedMap;
        _chests = playedMap.getChests();
        _traps = playedMap.getTraps();
        _gasClouds.clear();

        GameStateUpdater.player.getCurrentTile().setLight(Tile.LightAmount.Shadow, GameStateUpdater.player.getViewDistance()*2, GameStateUpdater.player.getCurrentTile());
        GameStateUpdater.player.getCurrentTile().setLight(Tile.LightAmount.Light, GameStateUpdater.player.getViewDistance(), GameStateUpdater.player.getCurrentTile());
        _monsterTime.clear();
        for (Monster monster : _monsters)
        {
            _monsterTime.put(monster, 0);
            monster.lookForPlayer(GameStateUpdater.player);
        }
        _monsterTurns = new PriorityQueue<Monster>(_monsters.size() + 1, new CharacterSpeedComparator());

    }

    public static void resumeGameStateUpdating()
    {
        _selectItemDialog = false;
        _selectTileDialog=false;
    }

    public void hideAttackRanges()
    {
        player.hideAttackRange();
        for(Monster monster : _monsters)
        {
            monster.hideAttackRange();
        }
    }

    //Update:
    private int turnState = 0;

    public void updateGameState()
    {
        if(!_currentAnimation.isPlaying())
        {
            if(_currentAnimation.getType() != GameAction.Type.Empty)
            {
                executeAction(_currentAnimation.getPlayedAction());
                _currentAnimation.emptyGameAction();
            }
            else
            {
                switch (turnState)
                {
                    case 0:startTurn();break;
                    case 1:updateMonsterActions();break;
                    case 2:updateEndOfTurn();break;
                }
            }
        }
    }

    //
    // ACTION EXECUTION
    //

    private void startTurn()
    {
        GameAction playerAction= player.getNextAction();
        if (playerAction != null)
        {
            _turn++;
            player.hideAttackRange();
            //Record the time of the next action of characters
            int playerActionCost = playerAction.getCost();
            for (Monster monster : _monsters)
            {
                monster.hideAttackRange();
                if(!monster.isDead() && monster.wasSeen())
                {
                    //Let the time a monster has to act be the time the player's action take
                    _monsterTime.put(monster, _monsterTime.get(monster) + playerActionCost);
                    _monsterTurns.add(monster);
                }
            }
            if(GameCharacterAnimation.typeIsAnimated(playerAction.getType()))
            {
                _currentAnimation.playGameAction(playerAction, timePerAnimation);
            }
            else
            {
                executeAction(playerAction);
            }
            turnState = _monsterTurns.isEmpty()?2:1;
        }
    }

    private void updateMonsterActions()
    {
        //Execute actions, starting with the quickest ones, but only if the character has enough time
        if(_monsterTurns.size()!=0)
        {
            Monster monster = _monsterTurns.peek();
            monster.setNextAction(player);
            GameAction nextAction = monster.getNextAction();
            if ( _monsterTime.get(monster) - monster.getCostOfNextAction() >=0&& !monster.isDead())
            {
                _monsterTime.put(monster, _monsterTime.get(monster) - monster.getCostOfNextAction());
                if(GameCharacterAnimation.typeIsAnimated(nextAction.getType()))
                {
                    _currentAnimation.playGameAction(nextAction, timePerAnimation);
                    return;
                }
                else
                {
                    executeAction(nextAction);
                }
            }
            else
            {
                _monsterTurns.poll();
            }
        }
        if(_monsterTurns.size()==0)
        {
            turnState=2;
        }
    }
    private void updateEndOfTurn()
    {

        //Update clouds and remove ones that are not yet active
        final ArrayList<Gas> _gassesToRemove = new ArrayList<Gas>();
        for (Gas gas : _gasClouds)
        {
            gas.update();
            if (gas.hasDisappeared())
            {
                _gassesToRemove.add(gas);
            }
        }
        for (Gas gas : _gassesToRemove)
        {
            _gasClouds.remove(gas);
        }

            player.updateEffects();
            for(Monster monster :_monsters)
            {
                if(!monster.isDead())
                {
                    monster.lookForPlayer(player);
                    monster.updateEffects();
                }
            }
            for(Chest chest : _chests)
            {
                chest.update(player);
            }
        turnState=0;
    }

    private void executeAction(GameAction action)
    {
        switch (action.getType())
        {
            case Move:
                executeMoveAction(action);
                break;
            case Attack:
                executeAttack(action);
                break;
            case Destroy:
                action.getOwner().setPosition(action.getOwner().getCurrentTile().getWorldPosition());
                action.getTargetObject().destroy();
                player.getCurrentTile().updateLight(player);
                break;
            case Equip:
                Item equipment = action.getTargetItem();
                inventory.equip(equipment);
                break;
            case Unequip:
                Item unequipment = action.getTargetItem();
                action.getOwner().unequip(unequipment);
                inventory.unequip(unequipment);
                break;
            case Wait:
                break;
            case PickUp:
                Item item = action.getTargetTile().pickupItem();
                inventory.addItem(item);
                GameConsole.addMessage("Picked up item " + item.getName());
                AssetManager.getSound("pickup").play();
                break;
            case Drop:
                Item droppedItem = action.getTargetItem();
                inventory.removeItem(droppedItem);
                action.getOwner().currentTile.addItem(droppedItem);
                break;
            case Throw:
                //The animation for throwing has just ended, resolve the result of the throw:
                executeThrowResults(action);
                break;
            case Use:
                executeUseAction(action);
                break;
            case Search:
                GameConsole.addMessage(action.getOwner().getName() + " searches the floor");
                for (Tile tile : action.getTargetTile().getWalkableNeighbours())
                {
                    Trap trap = tile.getTrap();
                    if (trap != null && !trap.hasBeenActivated())
                    {
                        trap.reveal();
                        GameConsole.addMessage("A trap was spotted");
                    }
                }
                break;
        }
        if(action.getType()!= GameAction.Type.Move)
        {
            //Remove the action as being the next action
            action.getOwner().clearCurrentAction();
        }

    }

    private void executeAttack(GameAction action)
    {
        GameCharacter defender = action.getTargetCharacter();
        GameCharacter attacker = action.getOwner();

        //Animation can leave the attacker a bit outside of the tile
        attacker.setPosition(attacker.getCurrentTile().getWorldPosition());
        attacker.dealAttackDamage(defender);
        if (defender instanceof Monster)
        {
            if (defender.isDead())
            {
                player.retrieveExperience((Monster) (defender));
                player.getCurrentTile().updateLight(player);
            }
        }
        else if (defender instanceof Player)
        {
            if (defender.isDead())
            {
                player.setKilledBy(attacker.getName());
            }
        }
        else
        {
            player.getCurrentTile().updateLight(player);
        }

    }

    private void executeMoveAction(GameAction action)
    {
        Tile newTile = action.getTargetTile();
        GameCharacter character = action.getOwner();
        if (character == player)
        {
            Trap trapOnTile = newTile.getTrap();
            if (trapOnTile != null && !trapOnTile.hasBeenActivated())
            {
                int chanceToBeat = RandomGen.getRandomInt(1, 100);
                if (5>=chanceToBeat)
                {
                    AssetManager.getSound("surprise").play();
                    GameConsole.addMessage(player.getName() + " almost stepped on a trap!");
                    trapOnTile.reveal();
                    player.clearNextActions();
                    return;
                }
                else
                {
                    String trapMessage = player.getName() + " stepped on a trap";

                    chanceToBeat = RandomGen.getRandomInt(0, 100);
                    if (player.getCurrentAgility() >= chanceToBeat)
                    {
                        trapMessage += ", but didn't activate it";
                        trapOnTile.reveal();
                    }
                    else
                    {
                        trapMessage += " and activated it";
                        trapOnTile.activate();
                        if (trapOnTile.hasCreatedGas())
                        {
                            AssetManager.getSound("gas").play();
                            _gasClouds.add(trapOnTile.retrieveCreatedGas());
                        }
                        else
                        {
                            AssetManager.getSound("critical").play();
                        }
                        //Player was hurt by trap
                        if (player.isDead())
                        {
                            player.setKilledBy("trap");
                        }
                    }
                    GameConsole.addMessage(trapMessage);
                }
            }

            inventory.step();
            playedMap.setLighting(player.getCurrentTile(),player.getViewDistance(), Tile.LightAmount.DarkShadow);
            player.moveTo(newTile);
            player.getCurrentTile().updateLight(player);

        }
        else
        {
            character.moveTo(newTile);
        }

    }

    private void executeUseAction(GameAction action)
    {
        if (action.getOwner() == player)
        {
            //Remove the item from the inventory
            inventory.removeItem(action.getTargetItem());

            //Identify items similar to the used one
            if (!action.getTargetItem().isIdentified())
            {
                //Identify the item as it has been used
                action.getTargetItem().identify();

                inventory.identifyItems(action.getTargetItem());
                ItemManager.identifyItem(action.getTargetItem());
            }

            GameConsole.addMessage("Player used " + action.getTargetItem().getName());
        }
        if (action.getTargetItem() instanceof Scroll)
        {
            useScroll((Scroll) action.getTargetItem());
        }
        else if (action.getTargetItem() instanceof Potion)
        {
            Potion potion = (Potion) action.getTargetItem();
            usePotion(potion, action.getOwner(), action.getTargetTile());
        }
    }
    private void executeThrowResults(GameAction throwAction)
    {

        Item thrownObject = throwAction.getTargetItem();
        Tile targetTile = throwAction.getTargetTile();

        if (thrownObject instanceof Potion)//Use the potion on the tile
        {
            usePotion((Potion) thrownObject, targetTile.getCharacter(), targetTile);
            if(targetTile.getCharacter()!=null)
            {
                GameConsole.addMessage(targetTile.getCharacter().getName() + " was hit by " + thrownObject.getName());
            }
        }
        else if (thrownObject instanceof Weapon)
        {
            Weapon thrownWeapon = (Weapon) thrownObject;
            int damage = 0;
            if (!targetTile.isEmpty() && thrownWeapon.getRangeType() == Weapon.RangeType.Throwable && RandomGen.getRandomInt(1, 100) > 20 + targetTile.getCharacter().getCurrentAgility())//Get ranged damage
            {
                damage = thrownWeapon.getRandomDamage();
            }
            else if (!targetTile.isEmpty() && RandomGen.getRandomInt(1, 100) > 50 + targetTile.getCharacter().getCurrentAgility())//Get damage from a sword etc being thrown
            {
                damage = thrownWeapon.getRandomDamage() / 2;
            }

            if (damage > 0)
            {
                GameConsole.addMessage(targetTile.getCharacter().getName() + " got " + damage + " damage from thrown " + thrownWeapon.getNameWithoutBonus());
                targetTile.getCharacter().damage(damage);
                Vector2 shownPosition = targetTile.getWorldPosition();
                shownPosition.add(DungeonMap.TileSize/2,-DungeonMap.TileSize/2);
                _currentAnimation.playDamageIndication(damage,shownPosition, Color.GREEN);

            }
            else
            {
                if(thrownWeapon.getRangeType() == Weapon.RangeType.Throwable)
                {
                    Weapon tileWeapon = new Weapon(thrownWeapon,thrownWeapon.getBonusDamage());
                    tileWeapon.setAmmoCount(1);
                    targetTile.addItem(tileWeapon);
                }
                else
                {
                    targetTile.addItem(thrownWeapon);
                }

                GameConsole.addMessage(thrownWeapon.getNameWithoutBonus() + " landed on the floor");
            }
        }
        else //Otherwise the item lands on the tile
        {
            targetTile.addItem(thrownObject);
            GameConsole.addMessage(thrownObject.getName() + " landed on the floor");
        }
        inventory.removeThrownItem(thrownObject);
    }

    //
    // Scroll and potion activation
    //
    private void useScroll(Scroll scroll)
    {
        if(!scroll.canBeUsed())
        {
            Gdx.app.log("GSU-useScroll","Trying to use scroll which cant be used!!");
            return;
        }
        switch (scroll.getType())
        {
            case Instant:scroll.use();break;
            case OnItem:
                _usedScroll = scroll;
                _selectItemDialog = true;
                break;
            case OnTile:
                _usedScroll = scroll;
                _selectTileDialog = true;
                break;
        }
        AssetManager.getSound("effect").play();
    }

    private void usePotion(Potion potion, GameCharacter target, Tile tile)
    {
        String potionColor = ColorHelper.convertColorToString(potion.getColor());
        if (potion.getEffect().isGas())
        {
            AssetManager.getSound("gas").play();
            GameConsole.addMessage("A " + ColorHelper.convertColorToString(potion.getEffect().getColor()) + " gas spreads from the bottle");
            _gasClouds.add(new Gas(tile,new Effect( potion.getEffect())));
        }
        else if (target != null)
        {
            if (target==player)
            {
                AssetManager.getSound("effect").play();
                GameConsole.addMessage(target.getName() + " drank the " +potionColor+ " potion");
            }
            target.giveEffect(new Effect(potion.getEffect()));
        }
        else
        {
            GameConsole.addMessage(potionColor + " colored liquid runs out of the vial");
        }

    }


    //
    //Drawing
    //
    public void drawGameState(SpriteBatch batch)
    {
        playedMap.draw(batch);

        for (Trap trap : _traps)
        {
            trap.draw(batch);
        }
        for(Chest chest : _chests)
        {
            chest.draw(batch);
        }
        for (Gas gas : _gasClouds)
        {
            gas.draw(batch);
        }
        for (Monster monster : _monsters)
        {
            monster.draw(batch);
        }
        player.draw(batch);
        if(_currentAnimation.isPlaying() )
        {
            _currentAnimation.drawAnimation(batch);
        }
        if(_currentAnimation.isShowingDamageIndicator())
        {
            _currentAnimation.drawDamageIndicators(batch);
        }
    }

}

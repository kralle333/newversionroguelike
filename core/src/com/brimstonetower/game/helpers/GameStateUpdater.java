package com.brimstonetower.game.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.brimstonetower.game.gameobjects.*;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.gui.Inventory;
import com.brimstonetower.game.gameobjects.items.*;
import com.brimstonetower.game.managers.ItemManager;
import com.brimstonetower.game.map.DungeonMap;
import com.brimstonetower.game.map.Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class GameStateUpdater
{



    private Player _player;
    private ArrayList<Monster> _monsters;
    private ArrayList<Gas> _gasClouds = new ArrayList<Gas>();
    private ArrayList<Chest> _chests = new ArrayList<Chest>();
    private ArrayList<Trap> _traps = new ArrayList<Trap>();
    private Inventory _inventory;
    private DungeonMap _playedMap;

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
    private boolean _showingThrowingAnimation = false;
    private final float timePerAnimation = 0.25f;


    //Select item dialog
    private boolean _selectItemDialog = false;
    public boolean isSelectingItemToIdentify()
    {
        return _selectItemDialog;
    }

    public void setGameState(Player player, Inventory inventory, DungeonMap playedMap)
    {
        _turn = 1;
        _player = player;
        _monsters = playedMap.getMonsters();

        _inventory = inventory;
        _playedMap = playedMap;
        _chests = playedMap.getChests();
        _traps = playedMap.getTraps();
        _gasClouds.clear();
        _player.getCurrentTile().setLight(Tile.LightAmount.Shadow, _player.getLanternStrength()*2, _player.getLanternStrength()*2);
        _player.getCurrentTile().setLight(Tile.LightAmount.Light, _player.getLanternStrength(), _player.getLanternStrength());
        _monsterTime.clear();
        for (Monster monster : _monsters)
        {
            _monsterTime.put(monster, 0);
            monster.lookForPlayer(player);
        }
        _monsterTurns = new PriorityQueue<Monster>(_monsters.size() + 1, new CharacterSpeedComparator());

    }

    public void resumeGameStateUpdating()
    {
        if (_selectItemDialog)
        {
            _selectItemDialog = false;
        }
        else if (_showingThrowingAnimation)
        {
            _showingThrowingAnimation = false;
        }
    }

    //Update:
    public void updateGameState()
    {
        if(!_currentAnimation.isPlaying())
        {
            if(_currentAnimation.getType() != GameAction.Type.Empty)
            {
                executeAction(_currentAnimation.getPlayedAction());
                _currentAnimation.emptyGameAction();
            }
            else if(isTurnOver())
            {
                startTurn();
            }
            else
            {
                updateTurn();
            }
        }
    }

    //
    // ACTION EXECUTION
    //

    public boolean isTurnOver(){return _monsterTurns.isEmpty();}
    private void startTurn()
    {
        GameAction playerAction;
        if ((playerAction = _player.getNextAction()) != null)
        {

            //Record the time of the next action of characters
            int playerActionCost = playerAction.getCost();
            for (Monster monster : _monsters)
            {
                monster.setNextAction(_player);
                //Let the time a monster has to act be the time the player's action take
                _monsterTime.put(monster, _monsterTime.get(monster) + playerActionCost);
                _monsterTurns.add(monster);
            }
            if(GameCharacterAnimation.typeIsAnimated(playerAction.getType()))
            {
                _currentAnimation.playGameAction(playerAction,timePerAnimation);
                return;
            }
            else
            {
                executeAction(playerAction);
            }
        }
    }

    private void updateTurn()
    {
        //Execute actions, starting with the quickest ones, but only if the character has enough time
        if(!_monsterTurns.isEmpty())
        {
            Monster monster = _monsterTurns.peek();
            if (_monsterTime.get(monster) > 0 && !monster.isDead())
            {
                monster.setNextAction(_player);
                _monsterTime.put(monster, _monsterTime.get(monster) - monster.getCostOfNextAction());
                GameAction nextAction = monster.getNextAction();
                if(GameCharacterAnimation.typeIsAnimated(nextAction.getType()))
                {
                    _currentAnimation.playGameAction(nextAction,timePerAnimation);
                    return;
                }
                else
                {
                    executeAction(nextAction);
                }
            }

            if (_monsterTime.get(monster) <= 0 || monster.isDead())
            {
                _monsterTurns.poll();
            }
        }
        if(_monsterTurns.isEmpty())
        {
            _turn++;

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
            for(Chest chest : _chests)
            {
                chest.update(_player);
            }

            //Apply effects
            _player.updateEffects();
            for (Monster monster : _monsters)
            {
                monster.updateEffects();
                monster.lookForPlayer(_player);
            }
        }
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
            case Equip:
                Item equipment = action.getTargetItem();
                action.getOwner().equip(equipment);
                _inventory.equip(equipment);
                break;
            case Unequip:
                Item unequipment = action.getTargetItem();
                action.getOwner().unequip(unequipment);
                _inventory.unequip(unequipment);
                break;
            case Wait:
                break;
            case PickUp:
                Item item = action.getTargetTile().pickupItem();
                _inventory.addItem(item);
                GameConsole.addMessage("Picked up item " + item.getName());
                break;
            case Drop:
                Item droppedItem = action.getTargetItem();
                _inventory.removeItem(droppedItem);
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

        //Remove the action as being the next action
        action.getOwner().clearCurrentAction();
    }

    private void executeAttack(GameAction action)
    {
        GameCharacter defender = action.getTargetCharacter();
        GameCharacter attacker = action.getOwner();

        //Animation can leave the attacker a bit outside of the tile
        attacker.setPosition(attacker.getCurrentTile().getWorldPosition());
        attacker.attack(defender);

        if (defender instanceof Monster)
        {
            if (defender.isDead())
            {
                _player.retrieveExperience((Monster) (defender));
            }
        }
        else if (defender instanceof Player)
        {
            if (defender.isDead())
            {
                _player.setKilledBy(attacker.getName());
            }
        }

    }

    private void executeMoveAction(GameAction action)
    {
        Tile newTile = action.getTargetTile();
        GameCharacter character = action.getOwner();
        if (character == _player)
        {
            Trap trapOnTile = newTile.getTrap();
            if (trapOnTile != null && !trapOnTile.hasBeenActivated())
            {
                int chanceToBeat = RandomGen.getRandomInt(1, 100);
                if (chanceToBeat >= 5)
                {
                    GameConsole.addMessage(_player.getName() + " spotted a trap");
                    _player.clearNextActions();
                    return;
                }
                else
                {
                    String trapMessage = _player.getName() + " stepped on a trap ";

                    chanceToBeat = RandomGen.getRandomInt(0, 100);
                    if (_player.getDodgeRate() >= chanceToBeat)
                    {
                        trapMessage += ", but didn't activate it";
                    }
                    else
                    {
                        trapMessage += "and activated it";
                        trapOnTile.activate();
                        if (trapOnTile.hasCreatedGas())
                        {
                            _gasClouds.add(trapOnTile.retrieveCreatedGas());
                        }
                        //Player was hurt by trap
                        if (_player.isDead())
                        {
                            _player.setKilledBy("trap");
                        }
                    }
                    GameConsole.addMessage(trapMessage);
                }
            }
            _inventory.step();
            _player.moveTo(newTile);
        }
        else
        {
            character.moveTo(newTile);
        }

    }

    private void executeUseAction(GameAction action)
    {
        if (action.getOwner() == _player)
        {
            //Remove the item from the inventory
            _inventory.removeItem(action.getTargetItem());

            //Identify items similar to the used one
            if (!action.getTargetItem().isIdentified())
            {
                //Identify the item as it has been used
                action.getTargetItem().identify();

                _inventory.identifyItems(action.getTargetItem());
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
            usePotion((Potion) action.getTargetItem(), action.getOwner(), action.getTargetTile());
        }
    }
    private void executeThrowResults(GameAction throwAction)
    {

        Item thrownObject = throwAction.getTargetItem();
        Tile targetTile = throwAction.getTargetTile();

        if (thrownObject instanceof Potion)//Use the potion on the tile
        {
            usePotion((Potion) thrownObject, targetTile.getCharacter(), targetTile);
        }
        else if (targetTile.getCharacter() != null && thrownObject instanceof Weapon)
        {
            Weapon thrownWeapon = (Weapon) thrownObject;
            int damage = 0;
            if (thrownWeapon.isRanged() && RandomGen.getRandomInt(1, 100) > 20 + targetTile.getCharacter().getDodgeRate())//Get ranged damage
            {
                damage = thrownWeapon.getRandomDamage();
            }
            else if (RandomGen.getRandomInt(1, 100) > 50 + targetTile.getCharacter().getDodgeRate())//Get damage from a sword etc being thrown
            {
                damage = thrownWeapon.getRandomDamage() / 2;
            }

            if (damage > 0)
            {
                GameConsole.addMessage(targetTile.getCharacter().getName() + " got " + damage + " from thrown " + thrownWeapon.getFullName());
                targetTile.getCharacter().damage(damage);
                _currentAnimation.playDamageIndication(damage,targetTile.getWorldPosition(), Color.GREEN,timePerAnimation);

            }
            else
            {
                targetTile.addItem(thrownWeapon);
                GameConsole.addMessage(thrownWeapon.getFullName() + " landed on the floor");
            }
        }
        else //Otherwise the item lands on the tile
        {
            targetTile.addItem(thrownObject);
            GameConsole.addMessage(thrownObject.getName() + " landed on the floor");
        }
    }

    //
    // Scroll and potion activation
    //
    private void useScroll(Scroll scroll)
    {
        switch (scroll.getType())
        {
            case Identify:
                _selectItemDialog = true;
                _inventory.show();
                break;
            case Teleport:
                _player.moveTo(_playedMap.getRandomEmptyTile());
                GameConsole.addMessage("Player was teleported to a random tile");
                break;
            case RemoveCurse:
                _inventory.removeCurses();
                GameConsole.addMessage("A magical light cleanses your backpack");
                break;
            case Mapping:
                _playedMap.revealAll();
                for (Trap trap : _traps)
                {
                    trap.reveal();
                }
                GameConsole.addMessage("Map has been revealed");
                break;
            default:
                throw new IllegalArgumentException("Invalid scroll type: " + scroll.getName());
        }
    }

    private void usePotion(Potion potion, GameCharacter target, Tile tile)
    {
        String potionColor = ColorHelper.convertColorToString(potion.getColor());
        if (potion.getEffect().isGas())
        {
            GameConsole.addMessage("A " + ColorHelper.convertColorToString(potion.getEffect().getColor()) + " gas spreads from the bottle");
            _gasClouds.add(new Gas(tile, potion.getEffect()));
        }
        else if (target != null)
        {
            if (tile == null)
            {
                GameConsole.addMessage(target.getName() + " drank the " +potionColor+ " potion");
            }
            target.giveEffect(potion.getEffect());
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
        _playedMap.draw(batch);

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
        _player.draw(batch);
        for (Monster monster : _monsters)
        {
            monster.draw(batch);
        }
        if(_currentAnimation.isPlaying())
        {
            _currentAnimation.draw(batch);
        }

    }

}

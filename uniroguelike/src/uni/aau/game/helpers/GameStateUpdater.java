package uni.aau.game.helpers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import uni.aau.game.gameobjects.Character;
import uni.aau.game.gameobjects.*;
import uni.aau.game.gui.GameConsole;
import uni.aau.game.gui.Inventory;
import uni.aau.game.items.*;
import uni.aau.game.mapgeneration.DungeonMap;
import uni.aau.game.mapgeneration.RandomGen;
import uni.aau.game.mapgeneration.Tile;

import java.util.*;

public class GameStateUpdater
{
    private Player _player;
    private ArrayList<Monster> _monsters;
    private ArrayList<Gas> _gasClouds = new ArrayList<Gas>();
    private ArrayList<Trap> _traps = new ArrayList<Trap>();
    private Inventory _inventory;
    private DungeonMap _playedMap;

    private HashMap<Character,Integer> _characterTime = new HashMap<Character, Integer>();
    private PriorityQueue<Character> _characterTurn;
    public class CharacterSpeedComparator implements Comparator<Character>
    {
        @Override
        public int compare(Character lhs, Character rhs)
        {
            return lhs.getCostOfNextAction()-rhs.getCostOfNextAction();
        }
    }

    private static int _turn = 0;
    public static int getTurn()
    {
        return _turn;
    }
    private boolean _showingThrowingAnimation = false;

    public boolean isShowingAnimation()
    {
        return _showingThrowingAnimation;
    }

    //Animation
    private final float _throwAnimationSpeed = 16;
    private float _thrownObjectX;
    private float _thrownObjectY;
    private Tile _targetTile;
    private Item _thrownWeapon;
    private float _angleToTarget;

    //Select item dialog
    private boolean _selectItemDialog = false;

    public boolean isSelectingItemToIdentify()
    {
        return _selectItemDialog;
    }

    public void setGameState(ArrayList<Monster> monsters, Player player, Inventory inventory, DungeonMap playedMap, ArrayList<Trap> traps)
    {
        _turn = 1;
        _player = player;
        _monsters = monsters;

        _characterTime.clear();
        _characterTime.put(_player,0);
        for(Monster monster : _monsters)
        {
            _characterTime.put(monster, 0);
        }
        _characterTurn = new PriorityQueue<Character>(_monsters.size()+1,new CharacterSpeedComparator());

        _inventory = inventory;
        _playedMap = playedMap;
        _traps = traps;
        _gasClouds.clear();
        _player.getCurrentTile().setLight(Tile.LightAmount.Shadow, _player.getLanternStrength() + 2, _player.getLanternStrength() + 2);
        _player.getCurrentTile().setLight(Tile.LightAmount.Light,_player.getLanternStrength(), _player.getLanternStrength());
    }

    public void resumeGameStateUpdating()
    {
        if(_selectItemDialog)
        {
            _selectItemDialog = false;
        }
        else if(_showingThrowingAnimation)
        {
            _showingThrowingAnimation = false;
        }
    }

    //Update:
    public void updateGameState()
    {
        if (!_selectItemDialog && !_showingThrowingAnimation)
        {
            handleExecutionOfActions();
        }
        else if (_showingThrowingAnimation)
        {
            //Move the object closer to the target
            _thrownObjectX += (float) Math.cos(_angleToTarget) * _throwAnimationSpeed;
            _thrownObjectY += (float) Math.sin(_angleToTarget) * _throwAnimationSpeed;

            //If the object is close enough-> do effects
            if (Math.abs(_thrownObjectX - (_targetTile.getX() * 32)) <= _throwAnimationSpeed &&
                    Math.abs(_thrownObjectY - (_targetTile.getY() * 32)) <= _throwAnimationSpeed)
            {

                if (_thrownWeapon instanceof Potion)//Use the potion on the tile
                {
                    usePotion((Potion) _thrownWeapon, _targetTile.getCharacter(), _targetTile);
                }
                else if (_targetTile.getCharacter() != null && //Damage if ranged weapon
                        _thrownWeapon instanceof Weapon &&
                        (((Weapon) _thrownWeapon).isRanged()))
                {
                    _targetTile.getCharacter().damage(((Weapon) _thrownWeapon).getIdentifiedMaxDamage());
                }
                else //Otherwise the item lands on the tile
                {
                    _targetTile.addItem(_thrownWeapon);

                }
                _showingThrowingAnimation = false;
                _targetTile = null;
                _thrownWeapon = null;
                _thrownObjectX = 0;
                _thrownObjectY = 0;
            }
        }
    }

    //
    // ACTION EXECUTION
    //
    private void handleExecutionOfActions()
    {
        GameAction playerAction = _player.getNextAction();
        if(playerAction != null)
        {
            //Record the time of the next action of characters
            int playerActionCost = playerAction.getCost();
            executeAction(playerAction);
            for(Monster monster : _monsters)
            {
                    monster.setNextAction(_player);
                    //Let the time a monster has to act be the time the player's action take
                    _characterTime.put(monster, _characterTime.get(monster) + playerActionCost);
                    _characterTurn.add(monster);
            }

            //Execute actions, starting with the quickest ones, but only if the character has enough time
            while(!_characterTurn.isEmpty())
            {
                Character character = _characterTurn.peek();
                if(_characterTime.get(character)>0 && !character.isDead())
                {

                    _characterTime.put(character, _characterTime.get(character) - character.getCostOfNextAction());
                    executeAction(character.getNextAction());
                    //Can only be a monster if there is time left,
                    // because the cost of the player's action = the time given to characters
                    if(_characterTime.get(character)>0)
                    {
                        ((Monster)character).setNextAction(_player);
                    }
                }

                if(_characterTime.get(character)<=0 ||character.isDead())
                {
                    _characterTurn.poll();
                }
            }
            _turn++;
            for (Gas gas : _gasClouds)
            {
                gas.update();
            }
        }
    }

    private void executeAction(GameAction action)
    {

        //Check status effects
        if (action.getOwner() != null && action.getOwner().getCurrentStatusEffect() != null)
        {
            switch (action.getOwner().getCurrentStatusEffect())
            {
                case Paralysed:
                    if (RandomGen.getRandomInt(0, 100) > 50)
                    {
                        GameConsole.addMessage(action.getOwner().getName() + " is paralyzed and cannot act");
                        action.getOwner().decreaseStatusEffectTimer();
                        action.getOwner().clearCurrentAction();
                        return;
                    }
                    else
                    {
                        action.getOwner().decreaseStatusEffectTimer();
                        break;
                    }
                case Poisoned:
                    GameConsole.addMessage(action.getOwner().getName() + " is poisoned");
                    action.getOwner().damage(RandomGen.getRandomInt(action.getOwner().getHitpoints() / 10,action.getOwner().getHitpoints() / 8));
                    action.getOwner().decreaseStatusEffectTimer();
                    break;
            }
        }

        switch (action.getType())
        {
            case Move:
                executeMoveAction(action);
                break;
            case Attack:
                executeAttack(action);
                break;
            case Equip:
                action.getOwner().equip(action.getTargetItem());
                break;
            case Unequip:
                action.getOwner().unequip(action.getTargetItem());
                break;
            case Wait:
                break;
            case PickUp:
                Item item =action.getTargetTile().pickupItem();
                _inventory.addItem(item);
                GameConsole.addMessage("Picked up item " + item.getName());
                break;
            case Drop:
                _player.getCurrentTile().addItem(action.getTargetItem());
                break;
            case Throw:
                _thrownWeapon = action.getTargetItem();
                _targetTile = action.getTargetTile();
                _showingThrowingAnimation = true;
                _thrownObjectX = action.getOwner().getCurrentTile().getX() * 32;
                _thrownObjectY = action.getOwner().getCurrentTile().getY() * 32;
                _angleToTarget = (float) Math.atan2(action.getTargetTile().getY() * 32 - _thrownObjectY, action.getTargetTile().getX() * 32 - _thrownObjectX);
                if (_angleToTarget < 0)
                {
                    _angleToTarget += 2 * Math.PI;
                }
                if (_thrownWeapon instanceof Weapon && ((Weapon) _thrownWeapon).isRanged())
                {
                    ((Weapon) _thrownWeapon).decreaseRangedAmmo();
                    if (((Weapon) _thrownWeapon).getAmmoCount() == 0)
                    {
                        _inventory.removeItem(_thrownWeapon);
                    }
                }
                else
                {
                    _inventory.removeItem(_thrownWeapon);
                }
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
        Character defender = action.getTargetCharacter();
        Character attacker = action.getOwner();
        attacker.attack(defender);
        if (defender.isDead())
        {
            if (defender instanceof Monster)
            {
                _player.retrieveExperience((Monster) (defender));
                _monsters.remove(defender);
            }
            else if (defender instanceof Player)
            {
                _player.setKilledBy(attacker.getName());
            }
        }
    }

    private void executeMoveAction(GameAction action)
    {
        Tile newTile = action.getTargetTile();
        Character character = action.getOwner();
        if (character == _player)
        {
            _inventory.step();
            Trap trapOnTile = newTile.getTrap();
            if (trapOnTile != null && !trapOnTile.hasBeenActivated())
            {
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

    private void usePotion(Potion potion, Character target, Tile tile)
    {
        if (target != null && potion.getType() == Potion.PotionType.Healing)
        {
            target.heal(potion.getPotency());
        }
        else if (target != null && potion.getType() == Potion.PotionType.Experience)
        {
            if (target == _player)
            {
                _player.retrieveExperience(potion);

            }
        }
        else if (potion.getType() == Potion.PotionType.PoisonGas)
        {
            GameConsole.addMessage("A poisonous gas spreads from the bottle");
            _gasClouds.add(new Gas(tile, Character.StatusEffect.Poisoned, potion.getPotency()));
        }
        else if (potion.getType() == Potion.PotionType.ParaGas)
        {
            GameConsole.addMessage("A paralyzing gas spreads from the bottle");
            _gasClouds.add(new Gas(tile, Character.StatusEffect.Paralysed, potion.getPotency()));
        }
        else
        {
            GameConsole.addMessage(potion.getStringColor() + " colored liquid runs out of the vial");
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

        for (Gas gas : _gasClouds)
        {
            gas.draw(batch);
        }
        _player.draw(batch);
        for (Monster monster : _monsters)
        {
            monster.draw(batch);
        }
        if (_showingThrowingAnimation)
        {
            _thrownWeapon.draw(batch, _thrownObjectX, _thrownObjectY);
        }
    }

}

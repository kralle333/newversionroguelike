package com.brimstonetower.game.dda;

import com.badlogic.gdx.Gdx;
import com.brimstonetower.game.gameobjects.Monster;
import com.brimstonetower.game.gameobjects.Player;
import com.brimstonetower.game.gui.GameConsole;
import com.brimstonetower.game.gui.Inventory;
import com.brimstonetower.game.helpers.GameAction;
import com.brimstonetower.game.helpers.PathFinder;
import com.brimstonetower.game.items.*;
import com.brimstonetower.game.mapgeneration.Corridor;
import com.brimstonetower.game.mapgeneration.Room;
import com.brimstonetower.game.mapgeneration.Tile;

import java.util.ArrayList;

public class Bot extends Player
{

    private ArrayList<Room> _exploredRooms = new ArrayList<Room>();
    private ArrayList<Room> _roomsToExplore = new ArrayList<Room>();
    private ArrayList<Tile> _currentPath = new ArrayList<Tile>();
    private Tile _stairCaseTile;
    private boolean _isLookingForExit = false;

    public boolean isLookingForExit()
    {
        return _isLookingForExit;
    }

    public boolean isStandingOnExit()
    {
        return currentTile == _stairCaseTile;
    }

    private Room _previousRoom;
    private Room _currentRoom;
    private Room _nextRoom;
    private Corridor _currentCorridor;
    private Tile _currentGoalItemTile;
    private Monster _currentGoalMonster;
    private float slowDownTime = 0f;
    private float timer = 0;

    private int _monstersInCurrentRoom = 0;
    private Inventory _inventory;
    private GameAction _currentAction = new GameAction();
    private GameAction _previousAction = new GameAction();
    private ArrayList<Item> _droppedItems = new ArrayList<Item>();
    private final boolean isDebugging = false;

    public Bot(String name)
    {
        super(name);
    }

    public void setInventory(Inventory inventory)
    {
        _inventory = inventory;
    }

    public void resetVariables()
    {
        _droppedItems.clear();
        _exploredRooms.clear();
        _stairCaseTile = null;
        _currentPath.clear();
        _roomsToExplore.clear();
        _isLookingForExit = false;
        _currentRoom = null;
        _nextRoom = null;
        _currentGoalItemTile = null;
        _currentGoalMonster = null;
    }

    @Override
    public void placeOnTile(Tile tile)
    {
        super.placeOnTile(tile);
        if (_exploredRooms.size() == 0 && _currentRoom == null)
        {
            _currentRoom = currentTile.getRoom();
        }
    }

    @Override
    public GameAction getNextAction()
    {
        timer += Gdx.graphics.getDeltaTime();

        if (timer > slowDownTime)
        {
            timer = 0;

            if (_roomsToExplore.size() == 0)
            {
                int i = 0;
            }

            if (_currentRoom == null)
            {
                if (currentTile.getRoom() == _nextRoom)
                {
                    _currentRoom = _nextRoom;
                    _nextRoom = null;
                }
            }
            if (getCurrentStatusEffect() == StatusEffect.Paralysed)
            {
                _currentAction.setAction(this, GameAction.Type.Wait, currentTile, null);
                return _currentAction;
            }

            GameAction actionToReturn = null;
            if ((actionToReturn = getUseItemAction()) != null) return actionToReturn;
            if ((actionToReturn = getEquipAction()) != null) return actionToReturn;
            if ((actionToReturn = getMonsterAction()) != null) return actionToReturn;
            //There was no monsters to fight, which means that no monsters are in the room
            _monstersInCurrentRoom = 0;
            if ((actionToReturn = getPickUpAction()) != null) return actionToReturn;
            if (_isLookingForExit)
            {
                if ((actionToReturn = getDropAction()) != null) return actionToReturn;
                if ((actionToReturn = getExperimentAction()) != null) return actionToReturn;
                if (_currentPath == null)
                {
                    _currentPath = PathFinder.getPath(currentTile, _stairCaseTile);
                }
            }
            if ((actionToReturn = getMoveAction()) != null) return actionToReturn;

            if (isDebugging)
            {
                Gdx.app.log("Bot", "Unable to get a action!");
            }
        }
        return super.getNextAction();
    }

    private GameAction getMonsterAction()
    {
        //Is there a monster next to us
        for (Tile t : currentTile.getWalkableNeighbours())
        {
            if (t.getCharacter() instanceof Monster)
            {
                _currentGoalMonster = (Monster) t.getCharacter();
                _previousAction.setAction(this, GameAction.Type.Attack, _currentGoalMonster.getCurrentTile(), null);
                return _previousAction;
            }
        }

        //Make sure that we dont fight a monster that is dead
        if (_currentGoalMonster != null && _currentGoalMonster.isDead()) _currentGoalMonster = null;

        //See if there is a monster to fight
        if (_currentGoalMonster == null) _currentGoalMonster = getMonsterToFight();

        //There is a monster to fight
        if (_currentGoalMonster != null)
        {
            //Do we stand next to the monster:
            if (_currentGoalMonster.getCurrentTile().distanceTo(currentTile) == 1)
            {
                _previousAction.setAction(this, GameAction.Type.Attack, _currentGoalMonster.getCurrentTile(), null);
                return _previousAction;
            }

            //Find the tile that get us the closest to the monster
            Tile nextTile = getClosestTile(_currentGoalMonster.getCurrentTile(), currentTile.getWalkableNeighbours());
            _previousAction.setAction(this, GameAction.Type.Move, nextTile, null);
            return _previousAction;
        }
        return null;
    }

    private Monster getMonsterToFight()
    {
        final ArrayList<Monster> monstersInRoom = new ArrayList<Monster>();

        monstersInRoom.clear();

        if (_currentRoom != null) monstersInRoom.addAll(_currentRoom.getMonsters());
        else if (_currentCorridor != null) monstersInRoom.addAll(_currentCorridor.getMonsters());

        Monster selectedMonster = null;
        if (monstersInRoom != null && monstersInRoom.size() > 0)
        {
            _monstersInCurrentRoom = monstersInRoom.size();
            float distanceToClosestMonster = Float.MAX_VALUE;
            float currentDistance = 0;
            for (Monster monster : monstersInRoom)
            {
                currentDistance = currentTile.distanceTo(monster.getCurrentTile());
                if (currentDistance < distanceToClosestMonster)
                {
                    selectedMonster = monster;
                    distanceToClosestMonster = currentDistance;
                }
            }
        }
        return selectedMonster;
    }

    private GameAction getUseItemAction()
    {
        for (Item i : _inventory.GetItems())
        {
            Boolean useItem = false;
            if (i instanceof Potion)
            {
                switch (((Potion) i).getType())
                {
                    case Experience:
                        useItem = true;
                        break;
                    case Healing:
                        if (currentHp < maxHp / 3)
                        {
                            useItem = true;
                        }
                        break;
                }
            }
            else if (i instanceof Scroll)
            {
                switch (((Scroll) i).getType())
                {
                    case RemoveCurse:
                        if ((getEquippedArmor() != null && getEquippedArmor().hasCurse()) ||
                                (getEquippedWeapon() != null && getEquippedWeapon().hasCurse()))
                        {
                            useItem = true;
                        }
                        break;
                    case Teleport:
                        if (_monstersInCurrentRoom >= 1 && currentHp < maxHp / 2)
                        {
                            useItem = true;
                        }
                        break;
                    case Identify:
                        for (Item item : _inventory.GetItems())
                        {
                            if (!item.isIdentified())
                            {
                                useItem = true;
                                break;
                            }
                        }
                        break;
                }
            }
            if (useItem)
            {
                _inventory.removeItem(i);
                _previousAction.setAction(this, GameAction.Type.Use, currentTile, i);
                return _previousAction;
            }
        }

        return null;
    }

    private GameAction getUseItemAction(Scroll.ScrollType scrollType)
    {
        if (_monstersInCurrentRoom >= 1)
        {
            return null;
        }
        for (Item i : _inventory.GetItems())
        {
            if (i instanceof Scroll && ((Scroll) i).getType() == scrollType)
            {
                _inventory.removeItem(i);
                _previousAction.setAction(this, GameAction.Type.Use, currentTile, i);
                return _previousAction;
            }
        }
        return null;
    }


    public void usedTeleport()
    {
        if (_isLookingForExit)
        {
            _currentPath = PathFinder.getPath(currentTile, _stairCaseTile);
        }
        else if (_currentRoom != null && _currentRoom != currentTile.getRoom())
        {
            _exploredRooms.remove(_currentRoom);
            _roomsToExplore.add(_currentRoom);
            _previousRoom = _currentRoom;
            _currentRoom = currentTile.getRoom();
            _currentPath.clear();
        }
        else
        {
            _currentPath.clear();
        }
        _currentGoalItemTile = null;
        _currentGoalMonster = null;
    }

    public void selectItemToIdentify()
    {
        for (Item i : _inventory.GetItems())
        {
            if (!i.isIdentified())
            {
                String oldName = i.getName();
                i.identify();
                GameConsole.addMessage(oldName + " was identified to be " + i.getName());
                _inventory.identifyItems(i);
                ItemManager.identifyItem(i);
                break;
            }
        }
    }

    private GameAction getEquipAction()
    {
        for (Item i : _inventory.GetItems())
        {
            if (i instanceof Weapon && !((Weapon) i).isRanged())
            {

                if (i == getEquippedWeapon())
                {
                    continue;
                }
                //Calculate the attackpower of the equipped weapon and the current weapon looked at
                //Use your expectation of attack power when unidentified otherwise use actual attack power
                int equippedAttackPower = 0;
                int otherAttackPower = i.isIdentified() ? ((Weapon) i).getIdentifiedMaxDamage() : ((Weapon) i).getExpectedMaxDamage();
                if (getEquippedWeapon() != null)
                {
                    equippedAttackPower = getEquippedWeapon().isIdentified() ? getEquippedWeapon().getIdentifiedMaxDamage() : getEquippedWeapon().getExpectedMaxDamage();
                }

                if (otherAttackPower > equippedAttackPower)
                {
                    if (getEquippedWeapon() != null && getEquippedWeapon().hasCurse())
                    {
                        return getUseItemAction(Scroll.ScrollType.RemoveCurse);
                    }
                    _inventory.equip(i);
                    _currentAction.setAction(this, GameAction.Type.Equip, currentTile, i);
                    return _currentAction;
                }
            }
            else if (i instanceof Armor)
            {
                if (i == getEquippedArmor())
                {
                    continue;
                }
                //Calculate the defense of the equipped armor and the current armor looked at
                //Use your expectation of defense when unidentified otherwise use actual defense
                int currentDefense = 0;
                int otherDefense = i.isIdentified() ? ((Armor) i).getIdentifiedDefense() : ((Armor) i).getExpectedDefense();
                if (getEquippedArmor() != null)
                {
                    currentDefense = getEquippedArmor().isIdentified() ? getEquippedArmor().getIdentifiedDefense() : getEquippedArmor().getExpectedDefense();
                }

                if (otherDefense > currentDefense)
                {
                    if (getEquippedArmor() != null && getEquippedArmor().hasCurse())
                    {
                        return getUseItemAction(Scroll.ScrollType.RemoveCurse);
                    }
                    _inventory.equip(i);
                    _currentAction.setAction(this, GameAction.Type.Equip, currentTile, i);
                    return _currentAction;
                }
            }
        }
        return null;
    }

    private GameAction getDropAction()
    {
        Item itemToDrop = null;
        int removeCurseScrolls = 0;
        int teleportationScrolls = 0;

        if (getEquippedArmor() != null && getEquippedArmor().isIdentified() && getEquippedArmor().getIdentifiedDefense() == 0)
        {
            _previousAction.setAction(this, GameAction.Type.Drop, currentTile, getEquippedArmor());
            _inventory.removeItem(getEquippedArmor());
            return _previousAction;
        }
        else if (getEquippedWeapon() != null && getEquippedWeapon().isIdentified() && getEquippedWeapon().getIdentifiedMaxDamage() == 0)
        {
            _previousAction.setAction(this, GameAction.Type.Drop, currentTile, getEquippedWeapon());
            _inventory.removeItem(getEquippedWeapon());
            return _previousAction;
        }
        for (Item i : _inventory.GetItems())
        {
            if (i instanceof Weapon && !((Weapon) i).isRanged())
            {
                if (getEquippedWeapon() != null && i != getEquippedWeapon() && ((Weapon) i).getIdentifiedMaxDamage() <= getEquippedWeapon().getIdentifiedMaxDamage())
                {
                    itemToDrop = i;
                    break;
                }
            }
            else if (i instanceof Armor && getEquippedArmor() != null && i != _equippedArmor && ((Armor) i).getIdentifiedDefense() <= getEquippedArmor().getIdentifiedDefense())
            {
                itemToDrop = i;
                break;
            }
            else if (i instanceof Potion)
            {
                if (((Potion) i).getType() == Potion.PotionType.ParaGas || ((Potion) i).getType() == Potion.PotionType.PoisonGas)
                {
                    itemToDrop = i;
                    break;
                }
            }
            else if (i instanceof Scroll)
            {
                if (((Scroll) i).getType() == Scroll.ScrollType.Mapping)
                {
                    itemToDrop = i;
                    break;
                }
                else if (((Scroll) i).getType() == Scroll.ScrollType.RemoveCurse)
                {
                    removeCurseScrolls++;
                    if (removeCurseScrolls >= 2)
                    {
                        itemToDrop = i;
                        break;
                    }
                }
                else if (((Scroll) i).getType() == Scroll.ScrollType.Teleport)
                {
                    teleportationScrolls++;
                    if (teleportationScrolls >= 2)
                    {
                        itemToDrop = i;
                        break;
                    }
                }

            }
        }
        if (itemToDrop != null)
        {
            _previousAction.setAction(this, GameAction.Type.Drop, currentTile, itemToDrop);
            _inventory.removeItem(itemToDrop);
            _droppedItems.add(itemToDrop);
            return _previousAction;
        }
        //_inventory
        return null;
    }

    private GameAction getPickUpAction()
    {
        if (_inventory.isFull())
        {
            //Try to drop an item
            return getDropAction();
        }
        //Bot is standing on top of item
        if (currentTile == _currentGoalItemTile)
        {
            _currentGoalItemTile = null;
            _previousAction.setAction(this, GameAction.Type.PickUp, currentTile, null);
            return _previousAction;
        }
        //Find an item close-by
        if (_currentGoalItemTile == null) _currentGoalItemTile = getTileWithItem();

        if (_currentGoalItemTile != null)
        {
            Tile nextTile = getClosestTile(_currentGoalItemTile, currentTile.getWalkableNeighbours());
            _previousAction.setAction(this, GameAction.Type.Move, nextTile, null);
            return _previousAction;
        }

        return null;
    }

    private Tile getTileWithItem()
    {
        final ArrayList<Tile> itemsInRoom = new ArrayList<Tile>();

        if (_currentRoom != null) itemsInRoom.addAll(_currentRoom.getTilesWithItems());
        else if (_currentCorridor != null) itemsInRoom.addAll(_currentCorridor.getTilesWithItems());

        if (itemsInRoom != null)
        {
            ArrayList<Tile> itemsNotDropped = new ArrayList<Tile>();

            for (Tile t : itemsInRoom)
            {
                if (!_droppedItems.contains(t.getItems().get(t.getItems().size() - 1)))
                {
                    itemsNotDropped.add(t);
                }
            }
            return getClosestTile(currentTile, itemsNotDropped);
        }

        return null;
    }

    private Tile getClosestTile(Tile fromOrTo, ArrayList<Tile> tiles)
    {
        Tile closestTile = null;
        float currentDistance;
        float closestDistance = Float.MAX_VALUE;
        for (Tile t : tiles)
        {
            currentDistance = fromOrTo.distanceTo(t);
            if (currentDistance < closestDistance)
            {
                closestDistance = currentDistance;
                closestTile = t;
            }
        }
        return closestTile;
    }

    private GameAction getExperimentAction()
    {
        for (Item i : _inventory.GetItems())
        {
            if (!i.isIdentified() && (i instanceof Potion || i instanceof Scroll))
            {
                _previousAction.setAction(this, GameAction.Type.Use, currentTile, i);
                return _previousAction;
            }
        }

        return null;
    }

    private GameAction getMoveAction()
    {
        if (_currentPath != null && _currentPath.size() > 0)
        {
            if (!_currentPath.get(0).isEmpty())
            {
                _currentPath.clear();
                return null;
            }
            _previousAction.setAction(this, GameAction.Type.Move, _currentPath.remove(0), null);
            return _previousAction;
        }
        else if (!_isLookingForExit) //Nothing left to explore, leave room
        {
            if (_currentRoom != null)
            {

                if (isDebugging)
                {
                    Gdx.app.log("BotMoveAction", "Nothing left to explore leaving room " + _currentRoom);
                    Gdx.app.log("BotMoveAction", "Saving the neighbours of current room");
                }
                //Save neighbours and add current room to rooms we have already seen
                saveNeighbourRooms(_currentRoom);
                _previousRoom = _currentRoom;
                _currentRoom = null;
                return getMoveAction();
            }
            else
            {
                //Get a future room to explore

                if (_nextRoom != null || (_roomsToExplore.size() > 0 && (_nextRoom = _roomsToExplore.remove(0)) != null))
                {
                    if (isDebugging)
                    {
                        Gdx.app.log("BotMoveAction", "Trying to find a path to the next room " + _nextRoom);
                    }
                    _currentPath = getNextPath(_nextRoom);
                    if (_currentPath == null)
                    {
                        if (isDebugging)
                        {
                            Gdx.app.log("BotMoveAction", "Couldn't find path, saves the next dungeon for later and waits for now");
                        }

                        _roomsToExplore.add(_nextRoom);
                        _nextRoom = null;

                        return null;
                    }
                    else
                    {
                        return getMoveAction();
                    }
                }
                else
                {
                    if (isDebugging)
                    {
                        Gdx.app.log("BotMoveAction", "Dungeon fully explored, going to exit");
                    }

                    _currentPath = PathFinder.getPath(currentTile, _stairCaseTile);
                    if (_currentPath == null)
                    {
                        Gdx.app.log("BotMoveAction", "Cannot go to exit");
                    }
                    _isLookingForExit = true;
                    return getMoveAction();
                }

            }
        }

        return null;
    }

    private void saveNeighbourRooms(Room room)
    {
        if (!_exploredRooms.contains(room))
        {
            //Lets save the rooms this room connects to
            Room otherRoom = null;
            for (Corridor corridor : room.getCorridors())
            {
                otherRoom = corridor.getRoom1() == room ? corridor.getRoom2() : corridor.getRoom1();
                if (!_exploredRooms.contains(otherRoom) && !_roomsToExplore.contains(otherRoom))
                {
                    _roomsToExplore.add(otherRoom);
                }
            }
            _exploredRooms.add(room);
            if (_stairCaseTile == null)
            {
                _stairCaseTile = room.getStairCase();
            }
        }
    }

    private ArrayList<Tile> getNextPath(Room toRoom)
    {
        ArrayList<Tile> pathToReturn;

        //Try to reach the room
        int tries = 1;
        Tile nextTile = null;
        Corridor corridorToNextRoom = null;
        Tile doorToCorridor = null;
        while (tries < 3)
        {
            switch (tries)
            {
                case 1:
                    //Can we reach the next room by getting a random tile from there?
                    nextTile = toRoom.getRandomTile();
                    break;
                case 2:
                    //Can we reach the corridor going to the next room by getting a random tile form there?
                    for (Corridor corridor : toRoom.getCorridors())
                    {
                        //Find the corridor that connects to the previous room
                        if (corridor.getRoom1() == _previousRoom || corridor.getRoom2() == _previousRoom)
                        {
                            corridorToNextRoom = corridor;
                            nextTile = corridorToNextRoom.getRandomTile();
                            break;
                        }
                    }
                    break;
                case 3:
                    //Can we reach the door going to the corridor which goes to the next room
                    for (Tile door : _previousRoom.getDoors())
                    {
                        if (corridorToNextRoom == door.getCorridor())
                        {
                            nextTile = door;
                            doorToCorridor = door;
                            break;
                        }
                    }
                    break;
                case 4:
                    //If we are standing in the corridor
                    if (currentTile == doorToCorridor)
                    {
                        //Try finding a path to any other tile in the corridor
                        for (Tile cTile : corridorToNextRoom.getTiles())
                        {
                            if ((pathToReturn = PathFinder.getPath(currentTile, cTile)) != null)
                            {
                                return pathToReturn;
                            }
                        }
                    }
                    break;
            }
            if ((pathToReturn = PathFinder.getPath(currentTile, nextTile)) != null)
            {
                return pathToReturn;
            }
            tries++;
        }

        return null;
    }
}

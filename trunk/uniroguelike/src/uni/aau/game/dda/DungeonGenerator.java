package uni.aau.game.dda;


import com.badlogic.gdx.Gdx;
import uni.aau.game.gameobjects.DungeonMap;
import uni.aau.game.gameobjects.Monster;
import uni.aau.game.gameobjects.Player;
import uni.aau.game.gameobjects.Trap;
import uni.aau.game.gui.Inventory;
import uni.aau.game.helpers.GameAction;
import uni.aau.game.items.Item;
import uni.aau.game.items.ItemManager;
import uni.aau.game.items.Potion;
import uni.aau.game.items.Scroll;
import uni.aau.game.mapgeneration.*;

import java.io.*;
import java.util.*;

public class DungeonGenerator
{

    private static final int generationsOfEvolution = 100;
    private static final int savedForNextGen = 1000;
    private static final int startPoolSize = 10000;
    private static final boolean isBreeding = false;
    private static final boolean isNormalizing = false;

    private static ArrayList<DungeonCandidate> currentCandidates = new ArrayList<DungeonCandidate>();
    private static DungeonCandidate _currentBestCandidate;
    public static DungeonCandidate getCurrentBestCandidate(){return _currentBestCandidate;}
    private static DungeonCandidate _minValues = new DungeonCandidate();
    private static DungeonCandidate _maxValues = new DungeonCandidate();

    private static ArrayList<DungeonCandidate> breedNewCandidates(ArrayList<DungeonCandidate> candidates, float maxUtility)
    {
        if(maxUtility<0)
        {
            throw new IllegalArgumentException("Max utility cannot be below 0");
        }
        int candidatesSelected =0;
        ArrayList<DungeonCandidate> nextGeneration = new ArrayList<DungeonCandidate>();
        DungeonCandidate father = null;
        DungeonCandidate mother = null;
        boolean isFatherVerified = false;
        boolean isMotherVerified = false;
        while(candidatesSelected<savedForNextGen)
        {
            if(father == null){father = candidates.get(RandomGen.getRandomInt(0, candidates.size() - 1));}
            if(mother == null){mother = candidates.get(RandomGen.getRandomInt(0, candidates.size() - 1));}

            if(!isFatherVerified)
            {
                if(RandomGen.getRandomFloat(0.0f,1.0f)<Math.max(father.getCurrentCalculatedUtility(),0)/maxUtility)
                {
                    isFatherVerified = true;
                }
                else
                {
                    father = null;
                }
            }
            if(!isMotherVerified)
            {
                if(RandomGen.getRandomFloat(0.0f,1.0f)<Math.max(mother.getCurrentCalculatedUtility(),0)/maxUtility)
                {
                    isMotherVerified = true;
                }
                else
                {
                    mother = null;
                }
            }

            if(isFatherVerified && isMotherVerified)
            {
                nextGeneration.add(crossCandidates(father,mother));
                candidatesSelected++;
                isMotherVerified = false;
                isFatherVerified = false;
                mother = null;
                father = null;
            }
        }

        return nextGeneration;
    }

    private static float getArithmeticCrossover(float fatherValue, float motherValue)
    {
        return (fatherValue+motherValue)/2;
    }

    private static DungeonCandidate crossCandidates(DungeonCandidate father, DungeonCandidate mother)
    {
        DungeonCandidate baby = new DungeonCandidate();
        //Arithmetic crossover performed

        //Items
        baby.potionCount = (int)getArithmeticCrossover((float)father.potionCount,(float)mother.potionCount);
        baby.potionAveragePotency = (int)getArithmeticCrossover((float)father.potionAveragePotency,(float)mother.potionAveragePotency);
        baby.scrollCount =(int)getArithmeticCrossover((float)father.scrollCount,(float)mother.scrollCount);
        baby.weaponAttack =(int)getArithmeticCrossover((float)father.weaponAttack,(float)mother.weaponAttack);
        baby.weaponCount = (int)getArithmeticCrossover((float)father.weaponCount,(float)mother.weaponCount);
        baby.armorCount = (int)getArithmeticCrossover((float)father.armorCount,(float)mother.armorCount);
        baby.armorDefense =(int)getArithmeticCrossover((float)father.armorDefense,(float)mother.armorDefense);

        //Monsters
        baby.monsterAvgDef=(int)getArithmeticCrossover((float)father.monsterAvgDef,(float)mother.monsterAvgDef);
        baby.monsterAvgHp=(int)getArithmeticCrossover((float)father.monsterAvgHp,(float)mother.monsterAvgHp);
        baby.monsterAvgStr=(int)getArithmeticCrossover((float)father.monsterAvgStr,(float)mother.monsterAvgStr);
        baby.monsterCount = (int)getArithmeticCrossover((float)father.monsterCount,(float)mother.monsterCount);

        //Traps
        baby.trapCount = (int)getArithmeticCrossover((float)father.trapCount,(float)mother.trapCount);
        baby.trapPotency=(int)getArithmeticCrossover((float)father.trapPotency,(float)mother.trapPotency);

        return baby;
    }



    public static DungeonMap generateDungeonFromCandidate(DungeonCandidate candidate)
    {
        int dungeonSize = 15+candidate.monsterCount+candidate.trapCount+(candidate.armorCount+candidate.weaponCount+candidate.potionCount+candidate.scrollCount)/4;
        Gdx.app.log("DungeonGenerator","About to create a map of size: "+dungeonSize);
        DungeonMap returnedMap = MapGenerator.generateMap(dungeonSize, dungeonSize, "tile");

        //Create monsters
        ArrayList<Monster> monstersInMap = new ArrayList<Monster>();
        for(int i = 0;i<candidate.monsterCount;i++)
        {
            monstersInMap.add(MonsterGenerator.createMonster(candidate.monsterAvgStr, candidate.monsterAvgHp, candidate.monsterAvgDef));
        }
        returnedMap.addMonsters(monstersInMap);

        //Create traps
        ArrayList<Trap> trapsInMap = new ArrayList<Trap>();
        for(int i = 0;i<candidate.trapCount;i++)
        {
            trapsInMap.add(TrapGenerator.createTrap(candidate.trapPotency));
        }
        returnedMap.addTraps(trapsInMap);

        //Create items
        ArrayList<Item> itemsInMap = new ArrayList<Item>();
        for(int i = 0 ;i<candidate.scrollCount;i++)
        {
            itemsInMap.add(ItemManager.getRandomScroll());
        }
        for(int i = 0;i<candidate.potionCount;i++)
        {
            itemsInMap.add(ItemManager.getRandomPotion(candidate.potionAveragePotency));
        }
        //Equipment
        for(int i = 0;i<candidate.armorCount;i++)
        {
            itemsInMap.add(ItemManager.getRandomArmor(candidate.armorDefense));
        }
        for(int i = 0;i<candidate.weaponCount;i++)
        {
            itemsInMap.add(ItemManager.getRandomWeapon(candidate.weaponAttack));
        }
        returnedMap.addItems(itemsInMap);
        returnedMap.createStairs();
        return returnedMap;
    }

    private static void recalculateMinMax(Player player)
    {
        _minValues.monsterCount = 1;
        _minValues.monsterAvgDef = 1;
        _minValues.monsterAvgHp = 5;
        _minValues.monsterAvgStr = player.getLevel();
        _minValues.potionCount = 1;
        _minValues.potionAveragePotency = player.getMaxHitPoints()/5;
        _minValues.scrollCount =1;
        _minValues.trapCount = 2;
        _minValues.trapPotency = player.getMaxHitPoints()/10;
        _minValues.weaponCount = 1;
        _minValues.weaponAttack = 1;
        _minValues.armorCount = 1;
        _minValues.armorDefense = 1;

        _maxValues.monsterCount = 10;
        _maxValues.monsterAvgDef = player.getArmorDefense();
        _maxValues.monsterAvgHp = player.getMaxHitPoints()/5;
        _maxValues.monsterAvgStr = player.getMaxStr();
        _maxValues.potionCount = 5;
        _maxValues.potionAveragePotency = player.getMaxHitPoints()/2;
        _maxValues.scrollCount =5;
        _maxValues.trapCount = 10;
        _maxValues.trapPotency = player.getMaxHitPoints()/3;
        _maxValues.weaponCount = 2;
        _maxValues.weaponAttack = player.getLevel()+1;
        _maxValues.armorCount = 2;
        _maxValues.armorDefense = player.getLevel()+1;

    }

    private static DungeonCandidate getLimitedRandomDungeon(Player player)
    {
        final   DungeonCandidate limitedCandidate= new DungeonCandidate();
        limitedCandidate.potionCount = RandomGen.getRandomInt(_minValues.potionCount, _maxValues.potionCount);
        limitedCandidate.potionAveragePotency =  RandomGen.getRandomInt(_minValues.potionAveragePotency, _maxValues.potionAveragePotency);
        limitedCandidate.scrollCount = RandomGen.getRandomInt(_minValues.scrollCount, _maxValues.scrollCount);
        limitedCandidate.trapCount = RandomGen.getRandomInt(_minValues.trapCount, _maxValues.trapCount);
        limitedCandidate.trapPotency = RandomGen.getRandomInt(_minValues.trapPotency, _maxValues.trapPotency);
        limitedCandidate.weaponAttack = RandomGen.getRandomInt(_minValues.weaponAttack, _maxValues.weaponAttack);
        limitedCandidate.weaponCount = RandomGen.getRandomInt(_minValues.weaponCount, _maxValues.weaponCount);
        limitedCandidate.armorDefense = RandomGen.getRandomInt(_minValues.armorDefense, _maxValues.armorDefense);
        limitedCandidate.armorCount = RandomGen.getRandomInt(_minValues.armorCount, _maxValues.armorCount);
        limitedCandidate.monsterCount = RandomGen.getRandomInt(_minValues.monsterCount, _maxValues.monsterCount);
        limitedCandidate.monsterAvgStr = RandomGen.getRandomInt(_minValues.monsterAvgStr, _maxValues.monsterAvgStr);
        limitedCandidate.monsterAvgDef = RandomGen.getRandomInt(_minValues.monsterAvgDef, _maxValues.monsterAvgDef);
        limitedCandidate.monsterAvgHp = RandomGen.getRandomInt(_minValues.monsterAvgHp, _maxValues.monsterAvgHp);

        return limitedCandidate;
    }

    public static DungeonCandidate getBestNewDungeonCandidate(Player player, Inventory inventory)
    {
        float highestUtility = -1;
        float currentUtility = -1;
        DungeonCandidate bestCandidate = null;
        DungeonCandidate currentCandidate = null;

        int potionCount = inventory.getItemTypeCount(Potion.class);
        float potionPotency = inventory.getPotionPotency();
        int scrollCount = inventory.getItemTypeCount(Scroll.class);

        recalculateMinMax(player);
        /*
        DungeonCandidate normalizingMin = new DungeonCandidate();

        normalizingMin.monsterCount = _minValues.monsterCount;
        normalizingMin.monsterAvgHp = _minValues.monsterAvgHp;
        normalizingMin.monsterAvgDef = _minValues.monsterAvgDef;
        normalizingMin.monsterAvgStr = _minValues.monsterAvgStr;
        normalizingMin.trapCount = _minValues.trapCount;
        normalizingMin.trapPotency = _minValues.trapPotency;
        normalizingMin.potionAveragePotency = _maxValues.potionAveragePotency;
        normalizingMin.potionCount = _maxValues.potionCount;
        normalizingMin.armorDefense = _maxValues.armorDefense;
        normalizingMin.armorCount = _maxValues.armorCount;
        normalizingMin.weaponAttack = _maxValues.weaponAttack;
        normalizingMin.weaponCount = _maxValues.weaponCount;
        normalizingMin.scrollCount = _maxValues.scrollCount;

        DungeonCandidate normalizingMax = new DungeonCandidate();

        normalizingMax.monsterCount = _maxValues.monsterCount;
        normalizingMax.monsterAvgHp = _maxValues.monsterAvgHp;
        normalizingMax.monsterAvgDef = _maxValues.monsterAvgDef;
        normalizingMax.monsterAvgStr = _maxValues.monsterAvgStr;
        normalizingMax.trapCount = _maxValues.trapCount;
        normalizingMax.trapPotency = _maxValues.trapPotency;
        normalizingMax.potionAveragePotency = _minValues.potionAveragePotency;
        normalizingMax.potionCount = _minValues.potionCount;
        normalizingMax.armorCount = _minValues.armorCount;
        normalizingMax.armorDefense = _minValues.armorDefense;
        normalizingMax.weaponCount = _minValues.weaponCount;
        normalizingMax.weaponAttack = _minValues.weaponAttack;
        normalizingMax.scrollCount = _minValues.scrollCount;


        normalizingMin.calculateAndSetImbalanceScores(player.getMaxHitPoints(),player.getMaxStr(),player.getWeaponAttack(),player.getArmorDefense(),potionCount,potionPotency,scrollCount);
        normalizingMax.calculateAndSetImbalanceScores(player.getMaxHitPoints(),player.getMaxStr(),player.getWeaponAttack(),player.getArmorDefense(),potionCount,potionPotency,scrollCount);

        */

        for(int i = 0;i<startPoolSize;i++)
        {
            currentCandidate = getLimitedRandomDungeon(player);
            currentCandidate.calculateAndSetImbalanceScores(player.getMaxHitPoints(),player.getMaxStr(),player.getWeaponAttack(),player.getArmorDefense(),potionCount,potionPotency,scrollCount);
            if(isNormalizing)
            {
                //currentCandidate.normalizeImbalanceScores(normalizingMin,normalizingMax);
            }
            currentUtility = currentCandidate.calculateAndGetUtility(FitnessCalculator.getCurrentWeights());
            if(currentUtility>highestUtility)
            {
                highestUtility = currentUtility;
                bestCandidate = currentCandidate;
            }
        }
        _currentBestCandidate = bestCandidate;

        return _currentBestCandidate;
    }

    //Used earlier for breeding
    public static DungeonCandidate breedBestCandidate(Player player, Inventory inventory)
    {
        //The player is dead, no candidate will be good - Just return some empty one
        if(player.getHitpoints() <= 0)
        {
            return new DungeonCandidate();
        }

        //Create some currentCandidates if we don't have any
        currentCandidates.clear();
        for(int i = 0;i<startPoolSize;i++)
        {
            DungeonCandidate candidate = new DungeonCandidate(RandomGen.getRandomInt(1, 5), RandomGen.getRandomInt(2, player.getMaxHitPoints() / 2),RandomGen.getRandomInt(1, 5),RandomGen.getRandomInt(1, 10),RandomGen.getRandomInt(2, player.getMaxHitPoints()/2),RandomGen.getRandomInt(1, 10),RandomGen.getRandomInt(1, 5),RandomGen.getRandomInt(1, 10),RandomGen.getRandomInt(1, 5),RandomGen.getRandomInt(1, 20),RandomGen.getRandomInt(1, player.getAttackPower()),RandomGen.getRandomInt(1, player.getArmorDefense()),RandomGen.getRandomInt(2, player.getMaxHitPoints()));
            currentCandidates.add(candidate);
        }

        float currentHighestFitnessValue = -1;
        if(isBreeding)
        {
            //Go through generationsOfEvolution rounds of evolution
            for(int i = 0; i < generationsOfEvolution; i++)
            {
                //currentHighestFitnessValue = scoreCandidatesAndGetHighestFitnessValue(currentCandidates, player, inventory);
                currentCandidates.clear();
                currentCandidates.addAll(breedNewCandidates(currentCandidates, currentHighestFitnessValue));
            }
        }

        //Find the one with highest utility and generate a map from it
        //currentHighestFitnessValue =scoreCandidatesAndGetHighestFitnessValue(currentCandidates,player,inventory);
        for(DungeonCandidate candidate : currentCandidates)
        {
            if(candidate.getCurrentCalculatedUtility() == currentHighestFitnessValue)
            {
                _currentBestCandidate = candidate;
                return _currentBestCandidate;
            }
        }
        throw new IllegalStateException("No dungeon was found, is something wrong here?");
    }

}

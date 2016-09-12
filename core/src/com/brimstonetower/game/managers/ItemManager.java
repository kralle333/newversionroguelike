package com.brimstonetower.game.managers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.gameobjects.Item;
import com.brimstonetower.game.gameobjects.Potion;
import com.brimstonetower.game.gameobjects.equipment.Armor;
import com.brimstonetower.game.gameobjects.equipment.Weapon;
import com.brimstonetower.game.gameobjects.scrolls.*;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.helpers.TileSetCoordinate;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.map.DungeonMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ItemManager
{

    private static final ArrayList<Weapon> _weaponPrototypes = new ArrayList<Weapon>();
    private static final HashMap<Weapon,ArrayList<Vector2>> _weaponSpawnProbabilities = new HashMap<Weapon,ArrayList<Vector2>>();

    private static final ArrayList<Armor> _armorPrototypes = new ArrayList<Armor>();
    private static final HashMap<Armor,ArrayList<Vector2>> _armorSpawnProbabilities = new HashMap<Armor,ArrayList<Vector2>>();

    private static final ArrayList<Scroll> _scrollPrototypes = new ArrayList<Scroll>();
    private static final ArrayList<Potion> _potionPrototypes = new ArrayList<Potion>();

    public static boolean isIdentified(Item item)
    {
        if (item instanceof Scroll)
        {
            for (int i = 0; i < _scrollPrototypes.size(); i++)
            {
                if (_scrollPrototypes.get(i).isIdentified() && _scrollPrototypes.get(i).isIdentical(item))
                {
                    return true;
                }
            }
        }
        else if (item instanceof Potion)
        {
            for (int i = 0; i < _potionPrototypes.size(); i++)
            {
                if (_potionPrototypes.get(i).isIdentified() &&
                        _potionPrototypes.get(i).isIdentical(item))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void identifyItem(Item item)
    {
        if (item instanceof Scroll)
        {
            Scroll scroll = (Scroll)item;
            for (int i = 0; i < _scrollPrototypes.size(); i++)
            {
                if (_scrollPrototypes.get(i).isIdentical(scroll))
                {
                    _scrollPrototypes.get(i).identify();
                    return;
                }
            }
        }
        else if (item instanceof Potion)
        {
            Potion potion = (Potion) item;
            for (int i = 0; i < _potionPrototypes.size(); i++)
            {
                if (_potionPrototypes.get(i).isIdentical(potion))
                {
                    _potionPrototypes.get(i).identify();
                    return;
                }
            }
        }
    }

    public static Weapon getWeapon(String name)
    {
        for(Weapon weapon : _weaponPrototypes)
        {
            if(weapon.getNameWithoutBonus() == name)
            {
                return new Weapon(weapon,0);
            }
        }
        return null;
    }
    public static Weapon getRandomWeapon(int depth)
    {
        Weapon toReturn = null;
        int numberOfPrototypes = _weaponPrototypes.size();
        while(toReturn==null)
        {
            Weapon prototype = _weaponPrototypes.get(RandomGen.getRandomInt(0,numberOfPrototypes-1));
            ArrayList<Vector2> probabilities = _weaponSpawnProbabilities.get(prototype);
            float probabilityOfPicking = 0;
            for(int i=probabilities.size()-1;i>=0;i--)
            {
                if (depth >= probabilities.get(i).x)
                {
                    probabilityOfPicking=probabilities.get(i).y;
                    break;
                }
            }
            float probabilityToBeat = RandomGen.getRandomFloat(0, 1);
            if (probabilityOfPicking > probabilityToBeat)
            {
                int bonusDamage = RandomGen.getRandomInt(-2, 2);
                toReturn= new Weapon(prototype, bonusDamage);
            }
        }

        return toReturn;
    }

    public static Armor getArmor(String name)
    {
        for(Armor armor : _armorPrototypes)
        {
            if(armor.getNameWithoutBonus() == name)
            {
                return new Armor(armor,0);
            }
        }
        return null;
    }
    public static Armor getRandomArmor(int depth)
    {
        Armor toReturn = null;
        int numberOfPrototypes = _armorPrototypes.size();
        while(toReturn==null)
        {
            Armor prototype = _armorPrototypes.get(RandomGen.getRandomInt(0,numberOfPrototypes-1));
            ArrayList<Vector2> probabilities = _armorSpawnProbabilities.get(prototype);
            float probabilityOfPicking = 0;
            for(int i=probabilities.size()-1;i>=0;i--)
            {
                if (depth >= probabilities.get(i).x)
                {
                    probabilityOfPicking=probabilities.get(i).y;
                    break;
                }
            }
            float probabilityToBeat = RandomGen.getRandomFloat(0, 1);
            if (probabilityOfPicking > probabilityToBeat)
            {
                int bonusDefense = RandomGen.getRandomInt(-2, 2);
                toReturn= new Armor(prototype, bonusDefense);
            }
        }

        return toReturn;
    }

    public static Potion getRandomPotion()
    {
        Potion toCopy = _potionPrototypes.get(RandomGen.getRandomInt(0, _potionPrototypes.size() - 1));
        return new Potion(toCopy);
    }

    public static Scroll getRandomScroll()
    {
        int randomType = RandomGen.getRandomInt(0, _scrollPrototypes.size() - 1);
        Scroll prototype = _scrollPrototypes.get(randomType);
        switch (randomType)
        {
            case 0: return new IdentifyScroll(prototype.getTextureRegion(),prototype.getUnidentifiedName());
            case 1: return new MappingScroll(prototype.getTextureRegion(),prototype.getUnidentifiedName());
            case 2: return new TeleportScroll(prototype.getTextureRegion(),prototype.getUnidentifiedName());
            case 3: return new RemoveCurseScroll(prototype.getTextureRegion(),prototype.getUnidentifiedName());
        }
        return null;
    }

    public static void initialize()
    {
        initializeArmors();
        initializeWeapons();
        initializePotions();
        initializeScrolls();
    }

    private static void initializeWeapons()
    {
        _weaponPrototypes.clear();
        //Steel
        TextureRegion region=AssetManager.getTextureRegion("weapons","steelSword", DungeonMap.TileSize,DungeonMap.TileSize);
        Weapon newWeapon = new Weapon("Steel Short Sword", "A standard short sword found in most armies and militias",
                false,region , 1, 4, 0, 1,1,10,5, Weapon.RangeType.Melee);

        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(1,1));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(3,0.3f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(5,0));

        region=AssetManager.getTextureRegion("weapons","steelAxe", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Steel Great Axe", "A 2-handed great axe made of steel, slow to use but powerful",
                false, region, 1, 10, 0, 1,1,12,10, Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(1,1));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(3,0.3f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(5,0));

        region=AssetManager.getTextureRegion("weapons","steelDagger", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Steel Dagger", "A steel dagger, its small size makes it possible getting multiple hits on foes.",
                false,region , 1, 3, 0,1,1, 3,8, Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(1,1));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(3,0.3f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(5,0));

        region=AssetManager.getTextureRegion("weapons","steelThrow", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon= new Weapon("Steel Throwing axe", "Flimsy throwing axes made of steel",
                false, region, 1, 2, 0,1,4, 10,3, Weapon.RangeType.Throwable);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(1,1));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(3,0.3f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(5,0));

        //Crystal
        region=AssetManager.getTextureRegion("weapons","crystalSword", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Crystal Sword", "A sword so clear you can see your own reflection in it",
                false, region,  1, 9, 0, 1,1,1,5, Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(3,0.5f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(5,0.7f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(10,0));

        region=AssetManager.getTextureRegion("weapons","crystalAxe", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Crystal Great Axe", "A massive great axe made in clear blue crystal",
                false,region,1, 18, 0, 1,1,1,15,Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(3,0.5f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(5,0.7f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(10,0));

        region=AssetManager.getTextureRegion("weapons","crystalDagger", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Crystal Dagger", "A small dagger made of pure crystal, it is as sharp as sharpened broken glass",
                false, region, 2, 3, 0,1,1, 2,8,  Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(3,0.5f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(5,0.7f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(10,0));

        region=AssetManager.getTextureRegion("weapons","crystalThrow", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Crystal throwing scythe", "A scythe meant for throwing at your foes, has a crystal blue color",
                false, region, 2, 2, 0,1,4, 10,5,  Weapon.RangeType.Throwable);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(3,0.5f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(5,0.7f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(10,0));

        //Unholy
        region=AssetManager.getTextureRegion("weapons","unholySword", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Unholy Sword", "Holding the sword in the right angle you can see you yourself as if you were dead in the reflection of the sword.",
                false, region, 3, 10, 0,1,1, 10,10, Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(8,0.2f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(10,0.6f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(18,0));

        region=AssetManager.getTextureRegion("weapons","unholyCudgel", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Unholy Cudgel", "A gigantic cudgel with a razor sharp green spikes",
                false, region, 1, 12, 0,  1,1,1,15,Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(8,0.2f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(10,0.6f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(18,0));

        region=AssetManager.getTextureRegion("weapons","unholyDagger", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Unholy Ceremonial dagger", "A menacing dagger, that looks like its used for ceremonial sacrifices",
                false, region, 2, 8, 0, 1,1,5,5, Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(8,0.2f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(10,0.6f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(18,0));

        region= AssetManager.getTextureRegion("weapons","unholyThrow", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Unholy throwing axe", "A throwing axe embedded with a large emerald. In it haunted souls can be seen crying for peace",
                false,region, 1, 5, 0, 1,4,10,5, Weapon.RangeType.Throwable);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(8,0.2f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(10,0.6f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(18,0));
        //Demonic
        region=AssetManager.getTextureRegion("weapons","demonicSword", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Demonic Sword", "Holding this sword fills your head with noise and your hands with pure energy",
                false,region , 4, 12, 0, 1,1,10,5, Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(15,0.3f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(20,0.6f));

        region= AssetManager.getTextureRegion("weapons","demonicMace", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Demonic Mace", "A mace with red sparks flying out of it",
                false,region, 1, 14, 0, 1,1,15,5, Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(15,0.5f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(20,0.3f));

        region=AssetManager.getTextureRegion("weapons","demonicDagger", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Demonic dagger", "A large dagger inscribed with demonic lettering",
                false, region, 2, 10, 0, 1,1,5,5, Weapon.RangeType.Melee);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(15,0.2f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(20,0.7f));

        region=AssetManager.getTextureRegion("weapons","demonicThrow", DungeonMap.TileSize,DungeonMap.TileSize);
        newWeapon=new Weapon("Brimstone throwing axe", "A throwing axe made of brimstone. Overwhelming heat comes out of it",
                false,region , 5, 7, 0, 1,4,1,5, Weapon.RangeType.Throwable);
        _weaponPrototypes.add(newWeapon);
        _weaponSpawnProbabilities.put(newWeapon,new ArrayList<Vector2>());
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(10,0.1f));
        _weaponSpawnProbabilities.get(newWeapon).add(new Vector2(20,0.7f));
    }

    private static void initializeArmors()
    {
        _armorPrototypes.clear();

        TextureRegion armorRegion = AssetManager.getTextureRegion("armors","rags",DungeonMap.TileSize,DungeonMap.TileSize);
        Armor newArmor = new Armor("Rags", "Barely clothes. Definitely not protective", false, armorRegion, 0, 0);
        _armorPrototypes.add(newArmor);
        _armorSpawnProbabilities.put(newArmor,new ArrayList<Vector2>());

        armorRegion = AssetManager.getTextureRegion("armors","nobleClothes",DungeonMap.TileSize,DungeonMap.TileSize);
        newArmor = new Armor("Noble Clothes","Finely crafted clothes, although it gives no protection", false, armorRegion, 0, 0);
        _armorPrototypes.add(newArmor);
        _armorSpawnProbabilities.put(newArmor,new ArrayList<Vector2>());

        armorRegion = AssetManager.getTextureRegion("armors","furArmor",DungeonMap.TileSize,DungeonMap.TileSize);
        newArmor = new Armor("Fur Armor","The wool covered armor give the wearer both warmth and a bit of protection", false, armorRegion, 1, 0);
        _armorPrototypes.add(newArmor);
        _armorSpawnProbabilities.put(newArmor,new ArrayList<Vector2>());
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(1,1));
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(2,0.5f));

        armorRegion = AssetManager.getTextureRegion("armors","leatherArmor",DungeonMap.TileSize,DungeonMap.TileSize);
        newArmor = new Armor("Leather Armor","The layers of leather gives the bearer increased mobility and some protection.", false, armorRegion, 2, 0);
        _armorPrototypes.add(newArmor);
        _armorSpawnProbabilities.put(newArmor,new ArrayList<Vector2>());
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(2, 1));
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(4,0));


        armorRegion = AssetManager.getTextureRegion("armors","sturdyLeather",DungeonMap.TileSize,DungeonMap.TileSize);
        newArmor = new Armor("Sturdy Leather Armor","The boiling of the armor have left it very hard, thereby protecting the user better than normal leather armors", false, armorRegion, 4, 0);
        _armorPrototypes.add(newArmor);
        _armorSpawnProbabilities.put(newArmor,new ArrayList<Vector2>());
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(3,0.3f));
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(5,0.6f));
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(8,0f));

        armorRegion = AssetManager.getTextureRegion("armors","chainMail",DungeonMap.TileSize,DungeonMap.TileSize);
        newArmor = new Armor("Chainmail","A heavy armor made of hundreds of chains. For those frequently against perilous foes.", false, armorRegion, 6, 0);
        _armorPrototypes.add(newArmor);
        _armorSpawnProbabilities.put(newArmor,new ArrayList<Vector2>());
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(6,0.2f));
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(10,0));

        armorRegion = AssetManager.getTextureRegion("armors","breastPlate",DungeonMap.TileSize,DungeonMap.TileSize);
        newArmor = new Armor("Breastplate","The shape of the plate of the armor protects the wearer from most direct hits.", false, armorRegion, 8, 0);
        _armorPrototypes.add(newArmor);
        _armorSpawnProbabilities.put(newArmor,new ArrayList<Vector2>());
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(8,0.3f));
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(13,0));

        armorRegion = AssetManager.getTextureRegion("armors","scaleMail",DungeonMap.TileSize,DungeonMap.TileSize);
        newArmor = new Armor("Scalemail","A master-worked piece of armor that only an expert armorsmith could have produced.", false, armorRegion, 12, 0);
        _armorSpawnProbabilities.put(newArmor,new ArrayList<Vector2>());
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(15,0.1f));
        _armorSpawnProbabilities.get(newArmor).add(new Vector2(20,1));
    }

    private static Color convertPotionTypeIndexToColor(TileSetCoordinate textureRegionPosition)
    {
        //The x position in the tilemap represents the different colors
        switch (textureRegionPosition.x)
        {
            case 0:
                return Color.WHITE;
            case 1:
                return Color.RED;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.GREEN;
        }
        return Color.PINK;
    }
    //Remember to set it when adding new ones
    private static int potionId = 1;
    private static Potion createPotion(String description,String identifiedName,Effect effect,ArrayList<TileSetCoordinate> availablePotionTypes)
    {
        if(availablePotionTypes.size()>0)
        {
            TileSetCoordinate randomType = availablePotionTypes.remove(RandomGen.getRandomInt(0, availablePotionTypes.size() - 1));
            TextureRegion potionRegion = AssetManager.getTextureRegion("potion",  randomType.x,  randomType.y, 32, 32);
            return new Potion(description,identifiedName,effect,false,potionRegion,convertPotionTypeIndexToColor(randomType),potionId++);
        }
        Gdx.app.log("Item Manager","Cant create potion, no unused type left!");
        return null;
    }
    private static void initializePotions()
    {
        _potionPrototypes.clear();
        potionId=1;
        final ArrayList<TileSetCoordinate> availablePotionTypes = new ArrayList<TileSetCoordinate>();
        availablePotionTypes.clear();
        //There are 16 different types of potion types in the potion.png tileset
        for (int x = 0; x < 4; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                availablePotionTypes.add(new TileSetCoordinate(x, y));
            }
        }

        _potionPrototypes.add(createPotion("Potion of Healing","Heals 20 hitpoints", Effect.createPermanentEffect("Healing", "You feel refreshed", 20, 0, 0, 0, 0, 0,  0,0, false, null),availablePotionTypes));
        _potionPrototypes.add(createPotion("Potion of Harmful Gas", "Releases a cloud of harmful gas",Effect.createPermanentEffect("Death", "Breathing the gas hurts your lungs", -5, 0, 0, 0, 0, 0,0,0, true, Color.DARK_GRAY),availablePotionTypes));
        _potionPrototypes.add(createPotion("Potion of Swiftness", "Move with increased speed for 5 turns", Effect.createTemporaryEffect("Swiftness", "You feel like time has slowed down", "Time feels normal again", 0, 0, 0, 0, 0,5,0,20, false, null),availablePotionTypes));
        _potionPrototypes.add(createPotion("Potion of Blindness", "Decreases attack and movement speed for 5 turns", Effect.createTemporaryEffect("Blindness", "Your vision is blurred", "Everything becomes clear again", 0, 0, 0, 0, 0, -5,-3, 5, false, null),availablePotionTypes));

    }

    private static String getRandomScrollName(ArrayList<String> availableWords)
    {
        String randomName = "";
        int wordCount = RandomGen.getRandomInt(2, 4);
        for (int i = 0; i < wordCount; i++)
        {
            randomName += availableWords.remove(RandomGen.getRandomInt(0, availableWords.size() - 1)) + " ";
        }
        return randomName;
    }

    private static void initializeScrolls()
    {
        _scrollPrototypes.clear();
        final ArrayList<String> availableWords = new ArrayList<String>(Arrays.asList(new String[]{"shia", "ach", "vosom", "xam", "xhamet", "lok", "sqace", "thunwen", "wex", "natas", "qientis", "commodo", "porta", "vella", "lorem", "consequat", "fringilla"}));
        final ArrayList<TileSetCoordinate> availableScrollTypes = new ArrayList<TileSetCoordinate>();
        availableScrollTypes.clear();
        for(int i = 0;i<4;i++)
        {
            availableScrollTypes.add(new TileSetCoordinate(i,0));
        }

        TileSetCoordinate randomType = availableScrollTypes.remove(RandomGen.getRandomInt(0, availableScrollTypes.size() - 1));
        TextureRegion scrollRegion = AssetManager.getTextureRegion("scroll",  randomType.x,  randomType.y, 32, 32);
        _scrollPrototypes.add(new IdentifyScroll(scrollRegion, getRandomScrollName(availableWords)));

        randomType = availableScrollTypes.remove(RandomGen.getRandomInt(0, availableScrollTypes.size() - 1));
        scrollRegion = AssetManager.getTextureRegion("scroll",  randomType.x,  randomType.y, 32, 32);
        _scrollPrototypes.add(new MappingScroll(scrollRegion, getRandomScrollName(availableWords)));

        randomType = availableScrollTypes.remove(RandomGen.getRandomInt(0, availableScrollTypes.size() - 1));
        scrollRegion = AssetManager.getTextureRegion("scroll",  randomType.x,  randomType.y, 32, 32);
        _scrollPrototypes.add(new TeleportScroll(scrollRegion, getRandomScrollName(availableWords)));

        randomType = availableScrollTypes.remove(RandomGen.getRandomInt(0, availableScrollTypes.size() - 1));
        scrollRegion = AssetManager.getTextureRegion("scroll",  randomType.x,  randomType.y, 32, 32);
        _scrollPrototypes.add(new RemoveCurseScroll(scrollRegion, getRandomScrollName(availableWords)));
    }

}

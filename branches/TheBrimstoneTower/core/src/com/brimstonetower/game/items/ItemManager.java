package com.brimstonetower.game.items;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.brimstonetower.game.helpers.AssetManager;
import com.brimstonetower.game.helpers.Effect;
import com.brimstonetower.game.mapgeneration.RandomGen;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemManager
{
    private final static int itemSize = 32;

    private static final ArrayList<Weapon> _availableWeapons = new ArrayList<Weapon>();
    private static final ArrayList<Armor> _availableArmors = new ArrayList<Armor>();
    private static final ArrayList<Potion> _availablePotions = new ArrayList<Potion>();
    private static final ArrayList<Scroll> _availableScrolls = new ArrayList<Scroll>();

    private static ArrayList<String> _availableWords = new ArrayList<String>();
    private static ArrayList<Vector2> _availablePotionTypes = new ArrayList<Vector2>();

    public static boolean isIdentified(Item item)
    {
        if (item instanceof Scroll)
        {
            for (int i = 0; i < _availableScrolls.size(); i++)
            {
                if (_availableScrolls.get(i).isIdentified() &&
                        _availableScrolls.get(i).getIdentifiedName() == item.getIdentifiedName())
                {
                    return true;
                }
            }
        }
        else if (item instanceof Potion)
        {
            for (int i = 0; i < _availablePotions.size(); i++)
            {
                if (_availablePotions.get(i).isIdentified() &&
                        _availablePotions.get(i).getIdentifiedName() == item.getIdentifiedName())
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
            identifyScroll((Scroll) item);
        }
        else if (item instanceof Potion)
        {
            identifyPotion((Potion) item);
        }
    }

    private static void identifyScroll(Scroll scroll)
    {
        for (int i = 0; i < _availableScrolls.size(); i++)
        {
            if (_availableScrolls.get(i).getUnidentifiedName() == scroll.getUnidentifiedName())
            {
                _availableScrolls.get(i).identify();
                return;
            }
        }
    }

    private static void identifyPotion(Potion potion)
    {
        for (int i = 0; i < _availablePotions.size(); i++)
        {
            if (_availablePotions.get(i)._textureRegion.getRegionX() == potion._textureRegion.getRegionX() &&
                    _availablePotions.get(i)._textureRegion.getRegionY() == potion._textureRegion.getRegionY())
            {
                _availablePotions.get(i).identify();
                return;
            }
        }
    }

    public static Weapon getWeapon(int type, int power)
    {
        return new Weapon(_availableWeapons.get(power * 4 + type),0);
    }

    public static Weapon getRandomWeapon(int depth)
    {
        //Ma
        int weaponType = depth / 5;
        Weapon toCopy = _availableWeapons.get(RandomGen.getRandomInt(0, 3) + weaponType);

        int bonusDamage = RandomGen.getRandomInt(-2, 2);
        return new Weapon(toCopy, bonusDamage);
    }

    public static Armor getRandomArmor(int armorDef)
    {
        Armor toCopy = null;
        for (Armor armor : _availableArmors)
        {
            if (armor.getDefense() <= armorDef)
            {
                toCopy = armor;
            }
            else
            {
                //Random chance of spawning a strong armor
                if (RandomGen.getRandomInt(0, 4) == 0)
                {
                    toCopy = armor;
                }
                break;
            }
        }
        if (toCopy == null)
        {
            toCopy = _availableArmors.get(_availableArmors.size() - 1);
        }

        int bonusArmor = armorDef - toCopy.getDefense();

        return new Armor(toCopy, bonusArmor);
    }

    public static Potion getRandomPotion()
    {
        Potion toCopy = _availablePotions.get(RandomGen.getRandomInt(0, _availablePotions.size() - 1));
        return new Potion(toCopy);
    }

    public static Scroll getRandomScroll()
    {
        Scroll toCopy = _availableScrolls.get(RandomGen.getRandomInt(0, _availableScrolls.size() - 1));
        return new Scroll(toCopy);
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
        TextureRegion swordRegion;
        TextureRegion axeRegion;
        TextureRegion daggerRegion;
        TextureRegion throwingAxeRegion;
        String prefix = "";
        for (int i = 0; i < 4; i++)
        {
            swordRegion = AssetManager.getTextureRegion("weapon", 0, i, itemSize, itemSize);
            axeRegion = AssetManager.getTextureRegion("weapon", 1, i, itemSize, itemSize);
            daggerRegion = AssetManager.getTextureRegion("weapon", 2, i, itemSize, itemSize);
            throwingAxeRegion = AssetManager.getTextureRegion("weapon", 3, i, itemSize, itemSize);
            swordRegion.flip(false, true);
            axeRegion.flip(false, true);
            daggerRegion.flip(false, true);
            throwingAxeRegion.flip(false, true);

            switch (i)
            {
                case 0:
                    prefix = "Iron";
                    break;
                case 1:
                    prefix = "Mythril";
                    break;
                case 2:
                    prefix = "Adamantite";
                    break;
                case 3:
                    prefix = "Ruby";
                    break;
            }
            _availableWeapons.add(new Weapon(prefix + " Sword", "A" + prefix + " sword. Attacks are average speed.", false, swordRegion, i + 1, 6 + (i * 2), 0, 10, false));
            _availableWeapons.add(new Weapon(prefix + " Axe", "A 2-handed " + prefix + " axe. Attacks are slow.", false, axeRegion, 1, 8 + (i * 2), 0, 15, false));
            _availableWeapons.add(new Weapon(prefix + " Dagger", "A " + prefix + " dagger. Its attacks are quick.", false, daggerRegion, 2, 4 + (i * 2), 0, 5, false));
            _availableWeapons.add(new Weapon(prefix + " Throwing axe", "Ranged throwing axe made of " + prefix, false, throwingAxeRegion, 1, 1 + (i * 2), 0, 10, true));
        }
    }

    private static void initializeArmors()
    {
        TextureRegion leatherArmorRegion = AssetManager.getTextureRegion("armor", 0, 0, itemSize, itemSize);
        TextureRegion hardLeatherArmorRegion = AssetManager.getTextureRegion("armor", 1, 0, itemSize, itemSize);
        TextureRegion chainMailRegion = AssetManager.getTextureRegion("armor", 2, 0, itemSize, itemSize);
        TextureRegion scaleArmor = AssetManager.getTextureRegion("armor", 3, 0, itemSize, itemSize);
        leatherArmorRegion.flip(false, true);
        hardLeatherArmorRegion.flip(false, true);
        chainMailRegion.flip(false, true);
        scaleArmor.flip(false, true);

        _availableArmors.add(new Armor("Leather Armor", "An armor made of leather", false, leatherArmorRegion, 1, 0));
        _availableArmors.add(new Armor("Hardened Leather Armor", "An armor that protects better than normal leather armor", false, hardLeatherArmorRegion, 3, 0));
        _availableArmors.add(new Armor("Chain Mail", "A very protective iron chain mail", false, chainMailRegion, 5, 0));
        _availableArmors.add(new Armor("Scale armor", "Considered to be the most protective armor", false, scaleArmor, 7, 0));
    }

    private static Color convertPotionTypeIndexToColor(Vector2 textureRegionPosition)
    {
        //The x position in the tilemap represents the different colors
        switch ((int) textureRegionPosition.x)
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

    private static void initializePotions()
    {

        //There are 16 different types of potion types in the potion.png tileset
        for (int x = 0; x < 4; x++)
        {
            for (int y = 0; y < 4; y++)
            {
                _availablePotionTypes.add(new Vector2(x, y));
            }
        }

        //1. Figure out the type of the potion
        //2. Get the corresponding texture region of that type
        //3. Flip the texture so its not upside down
        //4. Add the potion to the list of available potions
        Vector2 randomType = _availablePotionTypes.remove(RandomGen.getRandomInt(0, _availablePotionTypes.size() - 1));
        TextureRegion potionRegion = AssetManager.getTextureRegion("potion", (int) randomType.x, (int) randomType.y, 32, 32);
        potionRegion.flip(false, true);
        _availablePotions.add(new Potion(Effect.createInstantEffect("Healing","heals you",10,0,0,0,0,0,true),false,potionRegion,convertPotionTypeIndexToColor(randomType)));

        randomType = _availablePotionTypes.remove(RandomGen.getRandomInt(0, _availablePotionTypes.size() - 1));
        potionRegion = AssetManager.getTextureRegion("potion", (int) randomType.x, (int) randomType.y, 32, 32);
        potionRegion.flip(false, true);
        _availablePotions.add(new Potion(Effect.createGasEffect("Death","haunting cries rip your soul apart",-5,0,0,0,0,Color.DARK_GRAY,true), false, potionRegion,convertPotionTypeIndexToColor(randomType)));
    }

    private static String getRandomScrollName()
    {
        String randomName = "";
        int wordCount = RandomGen.getRandomInt(2, 4);
        for (int i = 0; i < wordCount; i++)
        {
            randomName += _availableWords.remove(RandomGen.getRandomInt(0, _availableWords.size() - 1)) + " ";
        }
        return randomName;
    }

    private static void initializeScrolls()
    {
        TextureRegion scrollRegion = AssetManager.getTextureRegion("scroll", 0, 0, 32, 32);
        scrollRegion.flip(false, true);
        _availableWords = new ArrayList<String>(Arrays.asList(new String[]{"donec", "ach", "nisi", "sit", "amet", "sem", "dapibus", "semper", "praesent", "tincidunt", "ultricies", "commodo", "porta", "velit", "lorem", "consequat", "fringilla"}));
        _availableScrolls.add(new Scroll("Scroll of Identify", "Use to identify an item", false, scrollRegion, getRandomScrollName()));
        _availableScrolls.add(new Scroll("Scroll of Mapping", "Reveals the layout of the dungeon", false, scrollRegion, getRandomScrollName()));
        _availableScrolls.add(new Scroll("Scroll of Teleport", "Use to teleport you to a random location", false, scrollRegion, getRandomScrollName()));
        _availableScrolls.add(new Scroll("Scroll of Remove Curse", "Use to remove curses from your bag", false, scrollRegion, getRandomScrollName()));
    }

}

package com.brimstonetower.game.map.mapgeneration;
import com.brimstonetower.game.gameobjects.Chest;
import com.brimstonetower.game.gameobjects.equipment.Armor;
import com.brimstonetower.game.gameobjects.equipment.Weapon;
import com.brimstonetower.game.helpers.RandomGen;
import com.brimstonetower.game.managers.ItemManager;
public class ChestGenerator
{
    public static Chest generateChests(int depth)
    {
        final int equipmentCurseRate = 50;

        Chest chest = new Chest(0);
        int itemType = RandomGen.getRandomInt(0, 7);//Chests more likely to spawn scrolls and potions
        switch (itemType)
        {
            case 0:
            case 1:
            case 2:
                chest.addItemToDrop(ItemManager.getRandomPotion());
                break;
            case 3:
            case 4:
            case 5:
                chest.addItemToDrop(ItemManager.getRandomScroll());
                break;
            case 6:
                Weapon weapon =ItemManager.getRandomWeapon(depth);
                if(weapon.getRangeType() == Weapon.RangeType.Melee && weapon.getIdentifiedMaxDamage()<weapon.getExpectedMaxDamage())
                {
                    if(RandomGen.getRandomInt(1,100)<=equipmentCurseRate)
                    {
                        weapon.curse();
                    }
                }
                chest.addItemToDrop(weapon);
                break;
            case 7:
                Armor armor =ItemManager.getRandomArmor(depth);
                if(armor.getIdentifiedDefense()<armor.getExpectedDefense())
                {
                    if(RandomGen.getRandomInt(1,100)<=equipmentCurseRate)
                    {
                        armor.curse();
                    }
                }
                chest.addItemToDrop(armor);
                break;
        }

        return chest;
    }

}

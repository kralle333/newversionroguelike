package uni.aau.game.dda;

import com.badlogic.gdx.Gdx;
import uni.aau.game.gameobjects.Player;
import uni.aau.game.gui.Inventory;
import uni.aau.game.helpers.GameAction;
import uni.aau.game.items.Potion;
import uni.aau.game.items.Scroll;

import java.io.*;

public class FitnessCalculator
{
    public static String weightsPath = "";
    public enum RewardType{Balancing, Adjusting, NotTooEasy, NotTooHard}
    private static RewardType _usedRewardType = RewardType.Balancing;

    public static String getWeightsPath()
    {
        String path = "/featureWeights/";
        switch(_usedRewardType)
        {
            case Adjusting:path+="reward=Adjusting";break;
            case Balancing: path+="reward=Balancing";break;
            case NotTooEasy:path+="reward=NotTooEasy"; break;
            case NotTooHard:path+="reward=NotTooHard";break;
        }
        path+="/"+DungeonCandidate.GetInversionFunction();
        if(DungeonCandidate.GetInversionFunction() == DungeonCandidate.InversionFunction.Gaussian)
        {
            path+=" c="+String.valueOf(DungeonCandidate.getC());
        }


        path+="/";
        return path;
    }
    private static float alpha = 1f;
    public static float getAlpha(){return alpha;}
    private static boolean _isDecreasingAlpha = false;
    private static float stepSize = 0.001f;
    private static final float gamma = 0.2f;
    private static final boolean isDebugging = false;
    private static final boolean saveWeights = false;
    private static final boolean isUpdatingWeights =true;

    //State +
    private static float playerDamage;
    private static int hitsOnPlayer;
    private static float monsterDamage;
    private static int hitsOnMonsters;
    private static int playerHp =0;
    private static int potionsUsed;
    private static int potionsWasAvailable;
    private static int scrollsUsed;
    private static int scrollsWasAvailable;

    private static int playerDamageTooEasy;
    private static int playerDamageTooHard;
    private static final int folderIncrements = 100;

    private static FeatureWeights _currentWeights;
    public static FeatureWeights getCurrentWeights(){return _currentWeights;}

    public static void initialize()
    {
        if(!isDebugging)
        {
            _currentWeights = new FeatureWeights(1000,7.610813f,0.5869262f,0.023130855f,3.047652E-17f);
            return;
        }
        weightsPath = getWeightsPath();
        File newestWeightFile = getNewestWeightFile();
        if(newestWeightFile == null)
        {
            _currentWeights = new FeatureWeights(1,10,10,10,10);
            saveWeights(_currentWeights);
        }
        else
        {
            _currentWeights = new FeatureWeights(convertFileToString(newestWeightFile));
        }
        if(_isDecreasingAlpha)
        {
            alpha = 1-((_currentWeights.cycles-1)*stepSize);
            if(alpha<0)
            {
                throw new ExceptionInInitializerError("Alpha is below 0");
            }
        }
    }

    public static FeatureWeights getFeatureWeights(int number)
    {
        File weightFolders = new File(System.getProperty("user.dir")+weightsPath);
        int currentValue;
        String correctFolderName = "";

        //Find the directory that fits for the weights
        for(String handle : weightFolders.list())
        {
            currentValue = Integer.parseInt(handle);
            if(number<=currentValue && number+folderIncrements > currentValue)
            {
                correctFolderName = handle;
                break;
            }
        }
        File correctFolder = null;
        if(correctFolderName == "")//No folder matches input
        {
            return null;
        }
        else
        {
            correctFolder = new File(weightFolders,correctFolderName);
        }
        //Find newest weights in directory

        for(String weightHandle : correctFolder.list())
        {
            currentValue = Integer.parseInt(weightHandle);
            if(currentValue == number)
            {

                return new FeatureWeights(convertFileToString(new File(correctFolder.getPath()+"\\"+weightHandle)));
            }
        }
        return null;
    }

    private static String convertFileToString(File file)
    {
        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream in = null;
        try
        {
            in = new FileInputStream(file);
        }
        catch(FileNotFoundException e)
        {
            Gdx.app.log("ConvertString","File not found");
        }
        try
        {
            in.read(bytes);
        }
        catch(IOException e)
        {
            Gdx.app.log("ConvertString","IO exception of some sorts");
        }
        try
        {
            in.close();
        }
        catch(IOException e)
        {
            Gdx.app.log("ConvertString","IO exception of some other sorts");
        }

        return new String(bytes);

    }

    private static String getNewestWeightDirectory()
    {
        File weightFolders = new File(System.getProperty("user.dir")+weightsPath);
        if(!weightFolders.exists())
        {
            weightFolders.mkdir();
        }
        int newestValue = -1;
        int currentValue = 0;
        String newestDirectoryName = "";

        //Find the directory containing newest weights
        for(String handle : weightFolders.list())
        {
            currentValue = Integer.parseInt(handle);
            if(currentValue>newestValue)
            {
                newestDirectoryName = handle;
                newestValue = currentValue;
            }
        }
        return newestDirectoryName;
    }
    private static File getNewestWeightFile()
    {

        String newestDirectoryName = getNewestWeightDirectory();

        if(newestDirectoryName != "")
        {
            File newestDirectory = new File(System.getProperty("user.dir")+weightsPath+"\\"+newestDirectoryName+"\\");
            String newestWeightsName = null;
            int currentValue = 0;
            int newestValue = -1;

            //Find newest weights in directory
            for(String weightHandle : newestDirectory.list())
            {
                currentValue = Integer.parseInt(weightHandle);
                if(currentValue>newestValue)
                {
                    newestWeightsName = weightHandle;
                    newestValue = currentValue;
                }
            }
            if(newestWeightsName != "")
            {
                return new File(System.getProperty("user.dir")+weightsPath+"\\"+newestDirectoryName+"\\"+newestWeightsName);
            }
        }


        return null;
    }

    private static void saveWeights(FeatureWeights featureWeights)
    {
        File weightFolders = new File(System.getProperty("user.dir")+weightsPath);
        int currentValue = 0;
        String correctFolderName = "";

        //Find the directory that fits for the weights
        for(String handle : weightFolders.list())
        {
            currentValue = Integer.parseInt(handle);
            if(featureWeights.cycles<=currentValue && featureWeights.cycles+folderIncrements >= currentValue)
            {
                correctFolderName = handle;
                break;
            }
        }
        File correctFolder = null;
        if(correctFolderName == "")//No folder fits - Create new one
        {
            int folderValueName = ((int)featureWeights.cycles/folderIncrements)*folderIncrements+folderIncrements;
            File newFolder = new File(weightFolders,String.valueOf(folderValueName));
            newFolder.mkdirs();
            correctFolder = newFolder;
        }
        else
        {
            correctFolder = new File(weightFolders,correctFolderName);
        }

        //Creating a new weight file
        File newWeights = new File(correctFolder,String.valueOf(featureWeights.cycles));
        try
        {
            newWeights.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(newWeights));
            output.write(featureWeights.toString());
            output.close();
        }
        catch(IOException e)
        {
            Gdx.app.log("SaveWeights","Caught an IOException when trying to create new weight file");
        }

    }

    //Updating weights:
    public static void updateWeights(DungeonCandidate currentCandidate, DungeonCandidate futurePotentialCandidate)
    {
        if(!isUpdatingWeights)
        {
            return;
        }
        //Update feature weights
        FeatureWeights oldWeights = _currentWeights;
        FeatureWeights newWeights = new FeatureWeights(_currentWeights.cycles+1,0,0,0,0);

        DungeonCandidate oldCandidate = currentCandidate;
        DungeonCandidate newCandidate = futurePotentialCandidate;

        float currentQ =  oldCandidate.calculateAndGetUtility(oldWeights);
        float futureQ = newCandidate.calculateAndGetUtility(oldWeights);
        float reward = getReward();

        newWeights.monsterWeight = Math.max(0,calculateUpdatedWeight(oldWeights.monsterWeight,oldCandidate.getMonsterUtility(),reward,alpha,gamma,futureQ,currentQ));
        newWeights.itemWeight = Math.max(0,calculateUpdatedWeight(oldWeights.itemWeight,oldCandidate.getItemUtility(),reward,alpha,gamma,futureQ,currentQ));
        newWeights.trapWeight = Math.max(0,calculateUpdatedWeight(oldWeights.trapWeight,oldCandidate.getTrapUtility(),reward,alpha,gamma,futureQ,currentQ));
        newWeights.equipWeight = Math.max(0,calculateUpdatedWeight(oldWeights.equipWeight,oldCandidate.getEquipmentUtility(),reward,alpha,gamma,futureQ,currentQ));

        if(saveWeights)
        {
            saveWeights(newWeights);
        }
        if(_isDecreasingAlpha)
        {
            alpha = 1-((_currentWeights.cycles-1)*stepSize);
        }
        _currentWeights = newWeights;
    }
    public static void resetStatePlus(Player player, Inventory inventory)
    {
        playerDamage = 0;
        hitsOnPlayer = 0;
        monsterDamage = 0;
        hitsOnMonsters = 0;
        playerHp =player.getMaxHitPoints();
        potionsUsed = 0;
        potionsWasAvailable = inventory.getItemTypeCount(Potion.class);
        scrollsUsed = 0;
        scrollsWasAvailable = inventory.getItemTypeCount(Scroll.class);
        playerDamageTooEasy = playerHp/10;
        playerDamageTooHard = playerHp/2;
    }
    public static void saveInformation(GameAction gameAction)
    {
        boolean actionOwnerIsPlayer = gameAction.getOwner() instanceof Player;
        switch(gameAction.getType())
        {
            case Attack:
                float damage = gameAction.getOwner().getMaxAttackPower()-gameAction.getTargetTile().getCharacter().getArmorDefense();
                if(damage<0)
                {
                    damage = 1;
                }
                if(actionOwnerIsPlayer)
                {
                    hitsOnMonsters++;
                    monsterDamage +=damage;
                }
                else
                {
                    hitsOnPlayer++;
                    playerDamage +=damage;
                }
                break;
            case Use:
                if(gameAction.getTargetItem() instanceof Potion)
                {
                    potionsUsed++;
                }
                else if(gameAction.getTargetItem() instanceof Scroll)
                {
                    scrollsUsed++;
                }
                break;
        }
    }

    private static float getNotToEasyReward()
    {
        float reward = 0;
        if(playerDamage< playerHp /10)
        {
            reward-=1;
        }
        else
        {
            reward+=1;
        }
        return reward;
    }

    private static float getNotToHardReward()
    {
        float reward = 0;
        if(playerDamage> playerHp /2)
        {
            reward-=1;
        }
        else
        {
            reward+=1;
        }
        return reward;
    }
    private static float getBalanceReward()
    {
        float reward = 0;

        if(playerDamage> playerDamageTooHard)
        {
            reward-=1;
        }
        else if(playerDamage < playerDamageTooEasy)
        {
            reward-=1;
        }
        else
        {
            reward+=1;
        }

        return reward;
    }
    private static float getDifficultyAdjustingReward()
    {
        float reward = 0;

        if(playerDamage> playerHp /2)
        {
            reward+=2;
        }
        else if(playerDamage < playerHp /5)
        {
            reward-=2;
        }

        if(playerDamage<monsterDamage*3 && (hitsOnPlayer-hitsOnMonsters)<5)
        {
            reward+=2;
        }
        else if(playerDamage*3 >monsterDamage && (hitsOnPlayer-hitsOnMonsters)<5)
        {
            reward-=2;
        }

        if(potionsWasAvailable != 0)
        {
            if((potionsUsed > 4 ||potionsUsed == 0))
            {
                reward += 2;
            }
            else
            {
                reward -= 2;
            }
        }

        return reward;
    }
    private static float getReward()
    {
        switch(_usedRewardType)
        {
            case Balancing:return getBalanceReward();
            case Adjusting:return getDifficultyAdjustingReward();
            case NotTooEasy:return getNotToEasyReward();
            case NotTooHard:return getNotToHardReward();
        }

        return -1;
    }
    private static float calculateUpdatedWeight(float oldWeight, float featureValue,float reward, float alpha, float gamma, float futureMaxUtility,float currentUtility)
    {
        return oldWeight+ alpha*(reward+gamma*(futureMaxUtility-currentUtility))*featureValue;
    }

}

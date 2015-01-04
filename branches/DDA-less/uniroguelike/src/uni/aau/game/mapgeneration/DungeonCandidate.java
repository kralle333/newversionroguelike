package uni.aau.game.mapgeneration;

import com.badlogic.gdx.Gdx;
import uni.aau.game.dda.FeatureWeights;

public class DungeonCandidate
{
    public int potionCount;
    public int potionAveragePotency;
    public int scrollCount;
    public int trapCount;
    public int trapPotency;
    public int weaponAttack;
    public int weaponCount;
    public int armorDefense;
    public int armorCount;
    public int monsterCount;
    public int monsterAvgStr;
    public int monsterAvgDef;
    public int monsterAvgHp;


    private float weaponImbalanceScore;
    private float armorImbalanceScore;
    private float potionImbalanceScore;
    private float scrollImbalanceScore;
    private float trapImbalanceScore;
    private float monsterImbalanceScore;


    private float currentCalculatedUtility = -1;
    public enum InversionFunction{Linear, Gaussian, Polynomial}
    private static final InversionFunction setFunctionType = InversionFunction.Gaussian;
    public static InversionFunction GetInversionFunction(){return setFunctionType;}

    public DungeonCandidate()
    {

    }
    public DungeonCandidate(int potionCount, int potionAveragePotency,
                            int scrollCount, int trapCount, int trapPotency,
                            int weaponAttack, int weaponCount, int armorDefense, int armorCount,
                            int monsterCount, int monsterAvgStr, int monsterAvgDef, int monsterAvgHp)
    {
        this.potionCount = potionCount;
        this.potionAveragePotency = potionAveragePotency;
        this.scrollCount = scrollCount;
        this.trapCount = trapCount;
        this.trapPotency = trapPotency;
        this.weaponAttack = weaponAttack;
        this.weaponCount = weaponCount;
        this.armorDefense = armorDefense;
        this.armorCount = armorCount;
        this.monsterCount = monsterCount;
        this.monsterAvgStr = monsterAvgStr;
        this.monsterAvgDef = monsterAvgDef;
        this.monsterAvgHp = monsterAvgHp;
    }
    public DungeonCandidate(float weaponImbalanceScore,float armorImbalanceScore, float potionImbalanceScore, float scrollImbalanceScore, float trapImbalanceScore, float monsterImbalanceScore)
    {
        this.weaponImbalanceScore = weaponImbalanceScore;
        this.armorImbalanceScore = armorImbalanceScore;
        this.potionImbalanceScore = potionImbalanceScore;
        this.scrollImbalanceScore = scrollImbalanceScore;
        this.trapImbalanceScore = trapImbalanceScore;
        this.monsterImbalanceScore = monsterImbalanceScore;
    }


    public void calculateAndSetImbalanceScores(float playerMaxHp, float playerStr,float playerWeaponAtk,
                                               float playerArmorDef,int playerPotionCount,
                                               float playerPotionAvgPotency,int playerScrollCount)
    {
        //Negative number means too easy
        //Positive number means too hard

        float dungeonPotionTotalPotency = (potionAveragePotency*potionCount);
        float expectedPotionTotalPotency = ((monsterCount*1/3)*(monsterAvgHp+(playerMaxHp/4))/2);

        potionImbalanceScore= expectedPotionTotalPotency-dungeonPotionTotalPotency;
        scrollImbalanceScore = 2-(scrollCount+playerScrollCount);

        float dungeonPotentialTrapDamage = trapPotency;
        trapImbalanceScore = dungeonPotentialTrapDamage-(playerMaxHp/4);

        weaponImbalanceScore = ((playerStr/2)+1)-weaponAttack;
        armorImbalanceScore = ((playerArmorDef/2)+1)-armorDefense;

        float hitsToKillAMonster = (monsterAvgHp)/Math.max(1,(playerStr+playerWeaponAtk-monsterAvgDef));
        float hitsToKillPlayer = (playerMaxHp)/(Math.max(1,monsterAvgStr-playerArmorDef));

        float lengthOfBattles = Math.min(hitsToKillAMonster,hitsToKillPlayer);

        float damageGivenByMonsters = monsterCount*(lengthOfBattles*(Math.max(1,monsterAvgStr-playerArmorDef)));
        float damageGivenByPlayer = monsterCount*(lengthOfBattles*(Math.max(1,playerStr+playerWeaponAtk-monsterAvgDef)));

        monsterImbalanceScore = hitsToKillAMonster-(2*hitsToKillPlayer);
        monsterImbalanceScore += (2*damageGivenByMonsters)-damageGivenByPlayer;
        monsterImbalanceScore = monsterImbalanceScore*1;
    }

    public void normalizeImbalanceScores(DungeonCandidate minScores, DungeonCandidate maxScores)
    {
        weaponImbalanceScore = getNormalizedScore(minScores.weaponImbalanceScore,maxScores.weaponImbalanceScore,weaponImbalanceScore);
        armorImbalanceScore = getNormalizedScore(minScores.armorImbalanceScore,maxScores.armorImbalanceScore,armorImbalanceScore);
        trapImbalanceScore = getNormalizedScore(minScores.trapImbalanceScore,maxScores.trapImbalanceScore,trapImbalanceScore);
        potionImbalanceScore = getNormalizedScore(minScores.potionImbalanceScore,maxScores.potionImbalanceScore,potionImbalanceScore);
        scrollImbalanceScore = getNormalizedScore(minScores.scrollImbalanceScore,maxScores.scrollImbalanceScore,scrollImbalanceScore);
        monsterImbalanceScore = getNormalizedScore(minScores.monsterImbalanceScore,maxScores.monsterImbalanceScore,monsterImbalanceScore);
    }
    private float getNormalizedScore(float min, float max, float current)
    {
        return (current-min)*(1/(max-min));
    }

    private static final float c = 0.5f;
    public static float getC(){return c;}

    private float getGaussianUtilityOfScore(float imbalanceScore,float c)
    {
        float currentUtility = (float)Math.exp(-Math.pow(imbalanceScore,2)/(2*Math.pow(c,2)));
        return currentUtility;
    }
    private float getLinearUtilityOfScore(float imbalanceScore)
    {
        return imbalanceScore;
    }
    private float getPolynomialUtilityScore(float imbalanceScore,float width,float height)
    {
        float currentUtility = (float)(-Math.pow(imbalanceScore,2))/width+height;
        return currentUtility;
    }

    public float getMonsterUtility()
    {
        switch(setFunctionType)
        {
            case Linear:return getLinearUtilityOfScore(monsterImbalanceScore);
            case Gaussian:return getGaussianUtilityOfScore(monsterImbalanceScore,c);
            case Polynomial:return getPolynomialUtilityScore(monsterImbalanceScore,1,1);
        }
        return -1;
    }
    public float getTrapUtility()
    {
        switch(setFunctionType)
        {
            case Linear:return getLinearUtilityOfScore(trapImbalanceScore);
            case Gaussian:return getGaussianUtilityOfScore(trapImbalanceScore,c);
            case Polynomial:return getPolynomialUtilityScore(trapImbalanceScore,1,1);
        }
        return -1;
    }
    public float getEquipmentUtility()
    {
        switch(setFunctionType)
        {
            case Linear:return getLinearUtilityOfScore(armorImbalanceScore+weaponImbalanceScore);
            case Gaussian:return getGaussianUtilityOfScore(armorImbalanceScore+weaponImbalanceScore,c);
            case Polynomial:return getPolynomialUtilityScore(armorImbalanceScore+weaponImbalanceScore,1,1);
        }
        return -1;
    }
    public float getItemUtility()
    {
        switch(setFunctionType)
        {
            case Linear:return getLinearUtilityOfScore(potionImbalanceScore+scrollImbalanceScore);
            case Gaussian:return getGaussianUtilityOfScore(potionImbalanceScore+scrollImbalanceScore,c);
            case Polynomial:return getPolynomialUtilityScore(potionImbalanceScore+scrollImbalanceScore,1,1);
        }
        return -1;
    }

    public float getCurrentCalculatedUtility()
    {
        return currentCalculatedUtility;
    }
    public float calculateAndGetUtility(FeatureWeights weights)
    {
        float monsterUtility = weights.monsterWeight*getMonsterUtility();
        float trapUtility = weights.trapWeight*getTrapUtility();
        float equipUtility= weights.equipWeight*getEquipmentUtility();
        float itemUtility = weights.itemWeight*getItemUtility();
        currentCalculatedUtility = monsterUtility+trapUtility+equipUtility+itemUtility;
        return currentCalculatedUtility;
    }

}

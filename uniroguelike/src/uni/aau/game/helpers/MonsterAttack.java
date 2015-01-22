package uni.aau.game.helpers;

public class MonsterAttack
{
    public String name;
    public int minDamage;
    public int maxDamage;
    public int attackSpeed;
    public MonsterAttack(String name,int minDamage, int maxDamage,int attackSpeed)
    {
        this.name = name;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.attackSpeed = attackSpeed;
    }
}
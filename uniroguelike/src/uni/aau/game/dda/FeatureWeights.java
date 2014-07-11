package uni.aau.game.dda;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.IllegalFormatException;

public class FeatureWeights
{
    public int cycles;
    private final String cycleText = "Cycle";
    public float monsterWeight;
    private final String monsterWeightText = "MonsterWeight";
    public float itemWeight;
    private final String itemWeightText = "ItemWeight";
    public float equipWeight;
    private final String equipWeightText = "EquipWeight";
    public float trapWeight;
    private final String trpWeightText = "TrapWeight";

    public FeatureWeights(int cycle, float monsterWeight, float itemWeight, float equipWeight, float trapWeight)
    {
        this.cycles = cycle;
        this.monsterWeight = monsterWeight;
        this.itemWeight = itemWeight;
        this.equipWeight = equipWeight;
        this.trapWeight = trapWeight;
    }
    public FeatureWeights(String weightsText)
    {
        String[] weightLines = weightsText.split(System.getProperty("line.separator"));
        if(weightLines.length != 5)
        {
            String exceptionText ="Expected 5 lines of weights but got: "+weightLines.length;
            throw new IllegalArgumentException(exceptionText);
        }
        this.cycles = (int)retrieveValue(weightLines[0]);
        this.monsterWeight = retrieveValue(weightLines[1]);
        this.itemWeight = retrieveValue(weightLines[2]);
        this.equipWeight = retrieveValue(weightLines[3]);
        this.trapWeight = retrieveValue(weightLines[4]);

    }

    private float retrieveValue(String weightLine)
    {
        String toReturn = weightLine;
        toReturn = toReturn.substring(toReturn.indexOf(":")+1);
        toReturn = toReturn.substring(0,toReturn.indexOf(";"));

        return Float.parseFloat(toReturn);
    }
    public String toString()
    {
        String eol = System.getProperty("line.separator");
        String weightsText = "";
        weightsText += cycleText+":"+cycles+";"+eol;
        weightsText += monsterWeightText+":"+monsterWeight+";"+eol;
        weightsText += itemWeightText+":"+itemWeight+";"+eol;
        weightsText += equipWeightText+":"+ equipWeight +";"+eol;
        weightsText += trpWeightText+":"+ trapWeight +";"+eol;

        return weightsText;
    }
}

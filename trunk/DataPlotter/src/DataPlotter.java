
import com.badlogic.gdx.Gdx;
import org.math.plot.*;
import uni.aau.game.dda.FeatureWeights;
import uni.aau.game.dda.FitnessCalculator;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;


public class DataPlotter
{
    private static boolean showFeatures = false;
    public static void main(String[] args)
    {

        if(showFeatures)
        {
            showCurrentFeatures();
        }
        else
        {
            showPlaythroughStats();
        }
    }
    private static void showPlaythroughStats()
    {

        FileReader fileReader = null;
        double[] stats = null;
        final String file = "playStats-nolearning";
        final String path = System.getProperty("user.dir")+"/playthroughStats/"+file;
        final int max=600;
        try
        {
            fileReader = new FileReader(new File(path));

            LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
            while (lineNumberReader.readLine() != null){}
            stats = new double[max];//lineNumberReader.getLineNumber()];
            lineNumberReader.close();
            fileReader.close();
            fileReader = new FileReader(new File(path));
            BufferedReader br = new BufferedReader(fileReader);
            String line = null;
            int counter = 0;
            float total = 0;
            while ((line = br.readLine()) != null)
            {
                double value =Double.valueOf(line);
                stats[counter] =value;
                total+=stats[counter];
                counter++;

                if(counter==max)
                {
                    break;
                }
            }
            System.out.println("Average is: "+(total/((float)counter)));


        }
        catch(IOException i)
        {

        }finally
        {
            if(fileReader != null)
            {
                try
                {
                    fileReader.close();
                }
                catch(IOException e)
                {

                }
            }

        }
        if(stats == null)
        {
            return;
        }





        Plot2DPanel plot = new Plot2DPanel();
        Arrays.sort(stats);
        //plot.addHistogramPlot("Playthrough stats",stats,1,22,22);

        double[] depthReached = new double[22];
        for(int i = 0; i<max;i++)
        {
            depthReached[(int)stats[i]-1]++;
        }
        System.out.println("Games won: "+depthReached[21]);
        plot.addBarPlot("Depth reached",depthReached);

        plot.setAxisLabel(0, "Depth reached");
        plot.setAxisLabel(1,"Number of games");


        JFrame frame = new JFrame(file+"-steps:"+max);
        frame.setSize(800,500);
        frame.setContentPane(plot);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    private static void showCurrentFeatures()
    {
        FitnessCalculator.initialize();
        int lastCycle =  FitnessCalculator.getCurrentWeights().cycles;//350;
        FeatureWeights currentWeights = null;
        double[] monsterWeights = new double[lastCycle];
        double[] itemWeights = new double[lastCycle];
        double[] trapWeights = new double[lastCycle];
        double[] equipWeights = new double[lastCycle];

        monsterWeights[0] = 10;
        itemWeights[0] = 10;
        trapWeights[0] = 10;
        equipWeights[0] = 10;

        for(int curCycle = 1;curCycle<lastCycle;curCycle++)
        {
            currentWeights = FitnessCalculator.getFeatureWeights(curCycle);
            if(currentWeights == null)
            {
                Gdx.app.log("DataPlotter","Features are null? cycle:"+curCycle);
                break;
            }
            monsterWeights[curCycle] = currentWeights.monsterWeight;
            itemWeights[curCycle] = currentWeights.itemWeight;
            trapWeights[curCycle] = currentWeights.trapWeight;
            equipWeights[curCycle] = currentWeights.equipWeight;
        }
        Plot2DPanel plot = new Plot2DPanel();

        plot.addLinePlot("Monster",monsterWeights);
        plot.addLinePlot("Items",itemWeights);
        plot.addLinePlot("Traps",trapWeights);
        plot.addLinePlot("Equip",equipWeights);
        plot.setAxisLabel(0,"Steps");
        plot.setAxisLabel(1,"Weight value");
        plot.setFixedBounds(0,0,lastCycle+10);

        JFrame frame = new JFrame(FitnessCalculator.getWeightsPath());
        frame.setSize(800,500);
        frame.setContentPane(plot);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

}

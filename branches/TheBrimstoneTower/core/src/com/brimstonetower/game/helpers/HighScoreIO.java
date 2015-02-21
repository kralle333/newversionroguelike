package com.brimstonetower.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class HighScoreIO
{
    public static class DescriptionScoreMap
    {
        public int score;
        public String description;

        public DescriptionScoreMap(int score, String description)
        {
            this.score = score;
            this.description = description;
        }
    }

    private static DescriptionScoreMap[] _scores;

    private static final int _maxScores = 10;
    private static Preferences preferencesScores;
    private static boolean _isInitialized = false;

    public static void initialize()
    {
        if (!_isInitialized)
        {
            _scores = new DescriptionScoreMap[_maxScores];
            preferencesScores = Gdx.app.getPreferences("scores");
            for (int i = 0; i < _maxScores; i++)
            {
                int value = preferencesScores.getInteger("scoreValue" + i);
                String text = preferencesScores.getString("scoreText" + i);
                _scores[i] = new DescriptionScoreMap(value, text);
            }
            _isInitialized = true;
        }
    }
    public static void clearScores()
    {
        initialize();
        preferencesScores.clear();
        preferencesScores.flush();
        _isInitialized = false;
        initialize();
    }
    public static String getScoreText(int i)
    {
        if (i < 0 || i >= _maxScores)
        {
            return null;
        }

        return preferencesScores.getString("scoreText" + i);
    }

    public static int getScoreValue(int i)
    {
        if (i < 0 || i >= _maxScores)
        {
            return -1;
        }

        return preferencesScores.getInteger("scoreValue" + i);
    }

    public static void putScore(String killedBy, String nameOfPlayer, int depth, int score)
    {
        DescriptionScoreMap scoreToPut;
        if (depth == 22)
        {
            scoreToPut = new DescriptionScoreMap(score, nameOfPlayer + " escaped the dungeons");
        }
        else
        {
            if (killedBy == "poison")
            {
                scoreToPut = new DescriptionScoreMap(score, nameOfPlayer + ": killed by " + killedBy + " on depth " + depth);
            }
            else
            {
                scoreToPut = new DescriptionScoreMap(score, nameOfPlayer + ": killed by a " + killedBy + " on depth " + depth);
            }
        }

        for (int i = 0; i < _maxScores; i++)
        {
            if (scoreToPut.score > _scores[i].score)
            {
                DescriptionScoreMap temp = _scores[i];
                _scores[i] = scoreToPut;
                scoreToPut = temp;
            }
        }
        saveScores();
    }

    private static void saveScores()
    {
        for (int i = 0; i < _maxScores; i++)
        {
            preferencesScores.putInteger("scoreValue" + i, _scores[i].score);
            preferencesScores.putString("scoreText" + i, _scores[i].description);
        }
        preferencesScores.flush();
    }
}

package com.brimstonetower.game.helpers;


import com.brimstonetower.game.gamestateupdating.GameAction;

public class PlaythroughStatistic
{
    private float _playerDamage = 0;
    private int _hitsOnPlayer = 0;
    private float _monsterDamage = 0;
    private int _hitsOnMonsters = 0;
    private int _potionsUsed = 0;
    private int _scrollsUsed = 0;
    private int _depthReached = 0;
    private boolean _isRecording = false;


    public void startRecording()
    {
        _isRecording = true;
    }

    public void endRecording()
    {
        _isRecording = false;
    }

    public void reset()
    {
        _playerDamage = 0;
        _hitsOnPlayer = 0;
        _monsterDamage = 0;
        _hitsOnMonsters = 0;
        _potionsUsed = 0;
        _scrollsUsed = 0;
        _depthReached = 0;
    }


    public void retrieveData(GameAction action)
    {
        if (_isRecording)
        {

        }
    }

    public int calculateScore()
    {
        return (int) (_monsterDamage + _hitsOnMonsters + _potionsUsed + _scrollsUsed) * _depthReached;
    }

}

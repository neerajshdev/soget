package com.njsh.downloader;

public class SpeedMeter
{
    private int speedValue;
    private int steps;
    private long elapsedTime, sTime, cTime;

    public SpeedMeter()
    {
    }


    public void start()
    {
        speedValue = 0;
        elapsedTime = 0;
        sTime = System.currentTimeMillis();
    }

    // return a boolean value that tells the meter updated its value
    public boolean update(int deltaSteps)
    {
        boolean isValueUpdate = false;
        cTime = System.currentTimeMillis();
        elapsedTime += cTime - sTime;

        steps += deltaSteps;

        // on each 1 sec interval
        if (elapsedTime > 1000)
        {
            speedValue = steps;
            steps = 0;
            elapsedTime -= 1000;
            isValueUpdate = true;
        }

        sTime = cTime;
        return isValueUpdate;
    }


    public int getSpeedValue()
    {
        return speedValue;
    }
}

package zookeeperclient;

public class Schedule
{
    public Schedule(int initialDelayInSeconds,int periodInSeconds)
    {
        this.initialDelayInSeconds=initialDelayInSeconds;
        this.periodInSeconds=periodInSeconds;
    }
    int initialDelayInSeconds;
    int periodInSeconds;
}

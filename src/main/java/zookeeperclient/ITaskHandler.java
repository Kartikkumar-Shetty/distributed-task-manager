package zookeeperclient;


public interface ITaskHandler
{
    void LeaderCallback();
    void FollowerCallback();
}



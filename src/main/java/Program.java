import org.apache.zookeeper.KeeperException;
import zookeeperclient.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Program {
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            Thread t = new Thread(new startZookeeper());
            t.run();
            Thread.sleep(1000000000);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }

    }

}


class startZookeeper implements Runnable, ITaskHandler
{
    List<Scheduler> schedules;
    @Override
    public void run() {
        schedules = new ArrayList<>();
        schedules.add(new Scheduler("job1", new Schedule(2,2),new sch1()));
        schedules.add(new Scheduler("job2", new Schedule(3,3),new sch2()));
        System.out.println("Hello Zookeeper");
        try
        {
            new TaskManager("127.0.0.1:2181", 3000,  taskType.ROLLING, schedules).electLeader();
        } catch (KeeperException e) {
            System.out.println(e.toString());
        }

    }

    @Override
    public void LeaderCallback() {
        System.out.println("I am the leader handler");
    }

    @Override
    public void FollowerCallback() {
        System.out.println("I am the folower handler");
    }
}
class sch1 implements IJob
{
    @Override
    public Runnable execute() {
        return()-> System.out.println("This is a job1");
    }
}
class sch2 implements IJob
{
    @Override
    public Runnable execute() {
        return()->  System.out.println("This is a job2");
    }
}
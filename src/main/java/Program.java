import org.apache.zookeeper.KeeperException;
import zookeeperclient.ITaskHandler;
import zookeeperclient.TaskManager;

import java.io.IOException;

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
    @Override
    public void run() {
        System.out.println("Hello Zookeeper");
        try {
            new TaskManager("127.0.0.1:2181", 3000, this).electLeader();
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
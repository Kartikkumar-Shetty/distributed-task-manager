import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import zookeeperclient.TaskManager;
import zookeeperclient.ZookeeperClient;

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


class startZookeeper implements Runnable
{
    @Override
    public void run() {
        System.out.println("Hello Zookeeper");
        try {
            new TaskManager("127.0.0.1:2181", 3000).electLeader();
        } catch (KeeperException e) {
            System.out.println(e.toString());
        }

    }

}
import org.apache.zookeeper.CreateMode;
import zookeeperclient.TaskManager;
import zookeeperclient.ZookeeperClient;

import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello Zookeeper");
        new TaskManager("127.0.0.1:2181", 3000).ElectLeader();
        Thread.sleep(1000000000);
    }

}

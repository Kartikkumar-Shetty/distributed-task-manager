package zookeeperclient;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public interface IZookeeperClient {
    String create(String path, CreateMode mode) throws KeeperException, InterruptedException;
    List<String> getChildren(String path, boolean toWatch, Watcher watcher);
    Stat exists(String path, Watcher watcher);
    void disconnect();
}

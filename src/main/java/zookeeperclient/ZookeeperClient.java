package zookeeperclient;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

public class ZookeeperClient {
    ZooKeeper zk;
    public ZookeeperClient(String host, int timeout)
    {
        try
        {
            this.zk = new ZooKeeper(host,timeout, null, false);
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ex.toString());
        }
    }
    public String create(String path, CreateMode mode) throws KeeperException, InterruptedException
    {
        try
        {
            return this.zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        }
        catch (KeeperException kex)
        {
            System.out.println("Keeper Exception: " + kex.toString());
            return "";
        }
        catch (Exception e)
        {
            //throw e;
            System.out.println("Keeper Exception: " + e.toString());
        }
        return "";
    }
    public List<String> getChildren(String path) throws InterruptedException, KeeperException {
        List<String> children;
        try
        {
            children = this.zk.getChildren(path,false);
            return children;
        }
        catch (Exception e)
        {
            //throw e;
            System.out.println("Keeper Exception: " + e);
            return null;
        }

    }
}

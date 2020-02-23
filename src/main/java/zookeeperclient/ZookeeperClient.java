package zookeeperclient;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;


import java.util.List;

public class ZookeeperClient {
    ZooKeeper zk;
    private String path;

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
    public List<String> getChildren(String path,boolean toWatch, Watcher watcher) throws InterruptedException, KeeperException {
        List<String> children;
        try
        {
            if (!toWatch)
            {
                children = this.zk.getChildren(path, false);
            }
            else
            {
                children = this.zk.getChildren(path,watcher);
            }
            return children;
        }
        catch (Exception e)
        {
            //throw e;
            System.out.println("Keeper Exception: " + e);
            return null;
        }

    }
    public Stat exists(String path, Watcher watcher) throws InterruptedException, KeeperException {
        Stat exists;
        try
        {
           exists = this.zk.exists(path, watcher);
        } catch (InterruptedException e) {
            throw e;
        } catch (KeeperException e) {
            throw e;
        }
        return exists;
    }
    public void disconnect() throws InterruptedException {
        try {
            this.zk.close();
        } catch (InterruptedException e) {
            throw e;
        }
    }
    public void deleteNode(String path) throws KeeperException, InterruptedException {
        this.zk.delete(path, 0);
    }
}

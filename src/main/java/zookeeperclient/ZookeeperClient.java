package zookeeperclient;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;


import java.util.List;

public class ZookeeperClient implements IZookeeperClient {
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

    public String create(String path, CreateMode mode) throws KeeperException, InterruptedException {
        try
        {
            return this.zk.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        }
        catch (KeeperException kex)
        {
            throw kex;
        }
        catch (Exception e)
        {
            //throw e;
            throw e;
        }
    }

    public List<String> getChildren(String path,boolean toWatch, Watcher watcher) {
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

    public Stat exists(String path, Watcher watcher) {
        Stat exists;
        try
        {
           exists = this.zk.exists(path, watcher);
            return exists;
        } catch (InterruptedException | KeeperException e) {
            System.out.println(e);
        }
        return null;
    }

    public void disconnect(){
        try {
            this.zk.close();
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void deleteNode(String path) throws KeeperException, InterruptedException {
        this.zk.delete(path, 0);
    }
}

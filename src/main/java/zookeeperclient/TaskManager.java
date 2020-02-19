package zookeeperclient;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TaskManager implements Watcher {
    ZookeeperClient client;
    String nodeID;
    String nodeBasePath;
    String nodeRegPath;
    Boolean isLeader;
    public TaskManager(String host, int timeout){
        client = new ZookeeperClient(host, timeout);
        nodeBasePath = "/leaderelection";
        nodeRegPath = nodeBasePath + "/node";
    }

    public void electLeader() throws KeeperException
    {
        try
        {
            System.out.println("Trying to win Election");
            client.create(nodeBasePath, CreateMode.PERSISTENT);
            String node = client.create(nodeRegPath, CreateMode.EPHEMERAL_SEQUENTIAL);
            this.nodeID = getNode(node);
            this.runElection();
        }
        catch (Exception ex)
        {
            System.out.println("Exception: "+ ex);
        }
    }

    private void runElection() throws KeeperException, InterruptedException {
        List<String> children = client.getChildren(nodeBasePath);
        String smallestNode = this.getLowestNode(children);
        System.out.println("Smallest Node:" + smallestNode + ", me:"+ this.nodeID);

        if (smallestNode.equals(nodeID))
        {
            System.out.println("I am the leader");
            isLeader=true;
        }
        else
        {
            System.out.println("I am not the leader");
            isLeader=false;

        }
        this.setWatch(nodeBasePath + "/" + smallestNode);

    }
    public String getLowestNode(List<String> nodes)
    {
        System.out.println("Trying to Sort");
        nodes.sort(this::compareNodes);
        return nodes.get(0);
    }
    public int compareNodes(String node1, String node2)
    {
        int n1 = Integer.parseInt(node1.replace("node",""));
        int n2 = Integer.parseInt(node2.replace("node",""));
        return n1-n2;
    }
    private String getNode(String nodefullpath)
    {
        String[] nodeComponents = nodefullpath.split("/");
        return nodeComponents[nodeComponents.length-1];
    }

    private void setWatch(String path) throws KeeperException, InterruptedException {
        try
        {
            System.out.println("Setting Watch");
            System.out.println(path);
            Stat exists = this.client.exists(path, this);
            System.out.println("Watch Added, Node Info: " + exists.toString());
        }
        catch(KeeperException e)
        {
            System.out.println("Keeper Exception");
        }
        catch(Exception e)
        {
            throw e;
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("watch Called");
        try {
            this.runElection();
        }
        catch (KeeperException | InterruptedException e) {
            System.out.println(e.toString());
        }
    }
}

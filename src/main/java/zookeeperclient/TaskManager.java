package zookeeperclient;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TaskManager {
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

    public void ElectLeader()
    {
        try
        {
            System.out.println("Trying to win Election");
            client.create(nodeBasePath, CreateMode.PERSISTENT);
            String node = client.create(nodeRegPath, CreateMode.EPHEMERAL_SEQUENTIAL);
            this.nodeID = getNode(node);
            List<String> children = client.getChildren(nodeBasePath);
            System.out.println("Peers:" + children.get(0));
            String smallestNode = this.getLowestNode(children);
            System.out.println("Smallest Node:" + smallestNode + ", me:"+ this.nodeID + ", Compare:" + smallestNode.equals(nodeID));

            if (smallestNode.equals(nodeID))
            {
                System.out.println("I am the leader");
                isLeader=true;
            }
        }
        catch (Exception ex)
        {
            System.out.println("Exception: "+ ex);
        }
    }
    public String getLowestNode(List<String> nodes)
    {
        System.out.println("Trying to Sort");
        nodes.sort(this::compareNodes);
        System.out.println("Node now Sorted");
        System.out.println("Smallest Node" + nodes.get(0));
        return nodes.get(0);
    }
    public int compareNodes(String node1, String node2)
    {
        System.out.println("Comparing: node1" + node1 + ", node2" + node2);
        int n1 = Integer.parseInt(node1.replace("node",""));
        int n2 = Integer.parseInt(node2.replace("node",""));
        System.out.println("Comparing: node1" + a1 + ", node2" + b1);
        return n1-n2;
    }
    private String getNode(String fullnode)
    {
        String[] nodeComponents = fullnode.split("/");
        return nodeComponents[nodeComponents.length-1];
    }
}

package zookeeperclient;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskManager extends Thread implements Watcher, Runnable
{
    String host;
    int timeout;
    ITaskHandler handler;
    ZookeeperClient client;
    String nodeID;
    String nodeBasePath;
    String nodeRegPath;
    int myPosition;
    taskType taskType;
    List<String> children;
    List<Scheduler> schedules;
    ScheduledExecutorService schedulerService = Executors.newScheduledThreadPool(1);
    List<ScheduledFuture<?>> scheduledFutures;


    Thread t;
    public TaskManager(String host, int timeout, taskType taskType, List<Scheduler> schedules)
    {

        this.host=host;
        this.timeout = timeout;
        this.taskType= taskType;
        this.schedules= schedules;
        scheduledFutures = new ArrayList<ScheduledFuture<?>>();
        //connectToZookeeper(host, timeout);
        client = new ZookeeperClient(host, timeout);
        nodeBasePath = "/leaderelection";
        nodeRegPath = nodeBasePath + "/node";
    }

/*    private void connectToZookeeper(String host, int timeout) {
        client = new ZookeeperClient(host, timeout);
        nodeBasePath = "/leaderelection";
        nodeRegPath = nodeBasePath + "/node";
    }
*/

    public void electLeader() throws KeeperException, InterruptedException {
            System.out.println("Trying to win Election");
            setupNodes();
            this.runElection();
    }

    private void setupNodes() throws KeeperException, InterruptedException {
        System.out.println("creating nodes Persistent");
        client.create(nodeBasePath, CreateMode.PERSISTENT);
        System.out.println("creating nodes Ephemeral");
        String node = client.create(nodeRegPath, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("done");
        this.nodeID = getNode(node);
    }

    private List<String> getChildrenList(String path, boolean toWatch) throws KeeperException, InterruptedException {
        return this.client.getChildren(nodeBasePath,toWatch, this);
    }

    private void runElection() throws KeeperException, InterruptedException
    {
        stopAllJobs();
        this.children = this.getChildrenList(this.nodeBasePath,false);

        myPosition = this.getMyPosition(children, this.nodeID);
        System.out.println("Smallest Node:" + this.children.get(myPosition) + ", me:"+ this.nodeID + ", my position:" + myPosition);

        if (!isLeader()){
            this.setWatch(nodeBasePath + "/" + this.children.get(myPosition-1));
        }
        this.getChildrenList(nodeBasePath,true);
        t= new Thread(this);
        t.run();

    }

    protected boolean isLeader() {
        if (myPosition==0)
        {
            System.out.println("I am the leader");
            return true;
        }
        else
        {
            System.out.println("I am not the leader");
            return false;
        }
    }

    private void stopAllJobs()
    {
        try {

            if (t != null) {
                t.interrupt();
                for (ScheduledFuture<?> s : this.scheduledFutures) {
                    s.cancel(false);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Error in stopping jobs, Exception:" + e);
        }
    }

    private int getMyPosition(List<String> nodes, String nodeID)
    {
        System.out.println("Trying to Sort");
        nodes.sort(this::compareNodes);
        for (int i=0;i<nodes.size();i++)
        {
            if (nodes.get(i).equals(nodeID))
            {
                return i;
            }
        }
        return -1;
    }

    private int compareNodes(String node1, String node2)
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

    protected void setWatch(String path) {
        try
        {
            System.out.println("Setting Watch for Path: " + path);
            Stat exists = this.client.exists(path, this);
            System.out.println("Watch Added, Node Info: " + exists.toString());
        }
        catch(Exception e)
        {
            throw e;
        }
    }

    @Override
    public void process(WatchedEvent event)
    {
        try {
            processWatch(event);
        } catch (KeeperException e) {
            System.out.println("Exception:" + e);
        } catch (InterruptedException e) {
            System.out.println("Exception:" + e);
        }
    }

    protected void processWatch(WatchedEvent event) throws KeeperException, InterruptedException {
        System.out.println("State:" + event.getState());
        System.out.println("Type:" + event.getType());
        if (event.getType().toString() == "NodeChildrenChanged")
        {
            try
            {
                this.children = this.getChildrenList(nodeBasePath,true);
            }
            catch (KeeperException | InterruptedException e) {
                throw e;
            }
        }
        if (event.getType().toString()=="NodeDeleted")
        {
            try {
                this.runElection();
            }
            catch (KeeperException | InterruptedException e) {
                throw e;
            }
        }
    }

    @Override
    public void run()
    {
         for (int i = 0; i < this.schedules.size(); i++)
         {
             Scheduler s = schedules.get(i);
             ScheduledFuture<?> future = this.schedulerService.scheduleWithFixedDelay(this.scheduleRunner(s.jobName),
                     s.schedule.initialDelayInSeconds,
                     s.schedule.periodInSeconds,
                     TimeUnit.SECONDS);
             this.scheduledFutures.add(future);
            }
    }
    public Runnable scheduleRunner(String jobName)
    {
        //if(isLeader)
        //{
        return () -> {
            for (int i = 0; i < this.schedules.size(); i++) {
                //System.out.println("comparing schedule name: " + schedules.get(i).jobName + " and job name:" + jobName);
                if (schedules.get(i).jobName == jobName && this.canExecuteJob(i, this.myPosition)) {
                    //System.out.println("running job");
                    schedules.get(i).job.execute();
                    break;
                }
            }
            //System.out.println("job not found: " + jobName);
        };
        //}

    }
    protected boolean canExecuteJob(int jobPosition, int myPosition)
    {
        int nodecount = this.children.size();
        if (nodecount==1)
        {
            //System.out.println("Only one node");
            return true;
        }
        else
        {
            if (jobPosition<nodecount)
            {
                if (jobPosition == myPosition)
                {
                    //System.out.println("matches my position: " + jobPosition);
                    return true;
                }
            }
            else
            {
                //System.out.println("checking for mod for : " + jobPosition + ", mod " + nodecount  + ", value:" + (jobPosition%nodecount));
                if ((jobPosition%nodecount) == myPosition)
                {
                    //System.out.println("matches my mod position: " + myPosition);
                    return true;
                }
            }
        }
        return false;
    }
}
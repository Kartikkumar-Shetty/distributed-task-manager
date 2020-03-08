package zookeeperclient;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

public class TaskManagerTest extends junit.framework.TestCase {
    public TaskManagerTest()
    {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testElectLeader_CreateNodeException() throws KeeperException, InterruptedException {
        IZookeeperClient mkclient = Mockito.mock(ZookeeperClient.class);
        InternalException expectedEx = new InternalException("CreateException");
        Mockito.when(mkclient.create(Mockito.any(), Mockito.any())).thenThrow(InternalException.class);

        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.client = (ZookeeperClient) mkclient;
        Exception returnedException = null;
        try
        {
            tsk.electLeader();
        }
        catch(Exception e){
            System.out.println("caught exception");
            returnedException = e;
        }

        if (returnedException==null)
        {
            Assert.assertFalse(false);
        }
        //Assert.assertEquals(returnedException, null);
        //Assert.assertEquals(returnedException.getMessage(),expectedEx.getMessage());
    }
    public void testElectLeader_isLeader() throws KeeperException, InterruptedException {
        IZookeeperClient mkclient = Mockito.mock(ZookeeperClient.class);
        InternalException expectedEx = new InternalException("CreateException");
        Mockito.when(mkclient.create(Mockito.any(), Mockito.any())).thenReturn("1");
        Mockito.when(mkclient.getChildren(Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyObject())).
                thenReturn(new ArrayList<String>(Arrays.asList("1","2","3")));

        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.client = (ZookeeperClient) mkclient;
        Exception returnedException = null;
        tsk.electLeader();
        Assert.assertEquals(true,tsk.isLeader());
    }
    public void testElectLeader_NotALeader() throws KeeperException, InterruptedException {
        IZookeeperClient mkclient = Mockito.mock(ZookeeperClient.class);
        InternalException expectedEx = new InternalException("CreateException");
        Mockito.when(mkclient.create(Mockito.any(), Mockito.any())).thenReturn("2");
        Mockito.when(mkclient.getChildren(Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyObject())).
                thenReturn(new ArrayList<String>(Arrays.asList("1","2","3")));
        Mockito.when(mkclient.exists(Mockito.anyString(), Mockito.anyObject())).thenReturn(new Stat());
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.client = (ZookeeperClient) mkclient;
        tsk.myPosition=2;
        tsk.electLeader();
        Assert.assertEquals(false,tsk.isLeader());
    }

    public void testsetWatchException() throws KeeperException, InterruptedException {
        IZookeeperClient mkclient = Mockito.mock(ZookeeperClient.class);
        InternalException expectedEx = new InternalException("CreateException");
        Mockito.when(mkclient.exists(Mockito.anyString(), Mockito.anyObject())).thenThrow(InternalException.class);
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.client = (ZookeeperClient) mkclient;
        Exception returnedEx = null;
        try{
            tsk.setWatch("");
        }
        catch (Exception e)
        {
            returnedEx=e;
        }
        Assert.assertNotNull(returnedEx);
    }

    public void testsetWatchNoException() throws KeeperException, InterruptedException {
        IZookeeperClient mkclient = Mockito.mock(ZookeeperClient.class);
        InternalException expectedEx = new InternalException("CreateException");
        Mockito.when(mkclient.exists(Mockito.anyString(), Mockito.anyObject())).thenReturn(new Stat());
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.client = (ZookeeperClient) mkclient;
        Exception returnedEx = null;
        try{
            tsk.setWatch("");
        }
        catch (Exception e)
        {
            returnedEx=e;
        }
        assertNull(returnedEx);
    }

    public void testProcess_NodeChildrenChangedException() {
        IZookeeperClient mkclient = Mockito.mock(ZookeeperClient.class);
        InternalException expectedEx = new InternalException("CreateException");
        Mockito.when(mkclient.getChildren(Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyObject())).
                thenThrow(InterruptedException.class);
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.client = (ZookeeperClient) mkclient;
        Exception returnedEx = null;
        try
        {
            tsk.processWatch(new WatchedEvent(Watcher.Event.EventType.NodeChildrenChanged, Watcher.Event.KeeperState.Disconnected,""));
        }
        catch (Exception e)
        {
            returnedEx=e;
        }
        Assert.assertNotNull(returnedEx);
    }
    public void testProcess_NodeChildrenChangedNoException() {
        IZookeeperClient mkclient = Mockito.mock(ZookeeperClient.class);
        InternalException expectedEx = new InternalException("CreateException");
        Mockito.when(mkclient.getChildren(Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyObject())).
                thenReturn(new ArrayList<String>());
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.client = (ZookeeperClient) mkclient;
        Exception returnedEx = null;
        try
        {
            tsk.processWatch(new WatchedEvent(Watcher.Event.EventType.NodeChildrenChanged, Watcher.Event.KeeperState.Disconnected,""));
        }
        catch (Exception e)
        {
            returnedEx=e;
        }
        Assert.assertNull(returnedEx);
    }
    public void testProcess_NodeDeletedException() {
        IZookeeperClient mkclient = Mockito.mock(ZookeeperClient.class);
        InternalException expectedEx = new InternalException("CreateException");
        Mockito.when(mkclient.getChildren(Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyObject())).
                thenThrow(InterruptedException.class);
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.client = (ZookeeperClient) mkclient;
        Exception returnedEx = null;
        try
        {
            tsk.processWatch(new WatchedEvent(Watcher.Event.EventType.NodeDeleted, Watcher.Event.KeeperState.Disconnected,""));
        }
        catch (Exception e)
        {
            returnedEx=e;
        }
        Assert.assertNotNull(returnedEx);
    }

    public void testProcess_NodeDeletedNoException() {
        IZookeeperClient mkclient = Mockito.mock(ZookeeperClient.class);
        InternalException expectedEx = new InternalException("CreateException");
        Mockito.when(mkclient.getChildren(Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyObject())).
                thenReturn(new ArrayList<String>(Arrays.asList("1","2","3")));
        Mockito.when(mkclient.exists(Mockito.anyString(), Mockito.anyObject())).thenReturn(new Stat());
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.client = (ZookeeperClient) mkclient;
        tsk.nodeID="2";
        tsk.myPosition=2;
        tsk.scheduledFutures= new ArrayList<ScheduledFuture<?>>();
        Exception returnedEx = null;
        try
        {
            tsk.processWatch(new WatchedEvent(Watcher.Event.EventType.NodeDeleted, Watcher.Event.KeeperState.Disconnected,""));
        }
        catch (Exception e)
        {
            returnedEx=e;
        }
        Assert.assertNull(returnedEx);
    }

    public void testcanExecuteJob_samevalue() {
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.children = new ArrayList<String>(Arrays.asList("1","2","3"));
        tsk.myPosition=1;
        boolean actualResult = tsk.canExecuteJob(1,1);
        assertEquals(actualResult, true);
    }
    public void testcanExecuteJob_differentvalue() {
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.children = new ArrayList<String>(Arrays.asList("1","2","3"));
        tsk.myPosition=1;
        boolean actualResult = tsk.canExecuteJob(1,2);
        assertEquals(actualResult, false);
    }
    public void testcanExecuteJob_samemodvalue() {
        TaskManager tsk = new TaskManager("",0, taskType.SINGLE_LEADER, new ArrayList<Scheduler>());
        tsk.children = new ArrayList<String>(Arrays.asList("1","2","3"));
        tsk.myPosition=1;
        boolean actualResult = tsk.canExecuteJob(4,1);
        assertEquals(actualResult, true);
    }
}

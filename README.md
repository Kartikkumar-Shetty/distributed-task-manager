# distributed-task-manager
A java library to distribute tasks among the nodes in a clustered environment using zookeeper

If you want to schedule a tasks to run on multiple nodes parallely you have to make sure that every node runs just one 
of the tasks in the schedule, in case the number of tasks in the schedule increases you will have to distribute the tasks evenly
accross the nodes and re-distribute the tasks in case the number of nodes increases and decreases.
This library is a POC that uses zookeeper to achieve this, every node in the cluster reisters with zookeeper 
when they join the cluster, when a node joins or leaves the cluster all the nodes get a notification and 
they redistrubute the tasks accordingy.

package zookeeperclient;

public class Scheduler implements Runnable {
    public String jobName;
    public Schedule schedule;
    public IJob job;
    public Scheduler(String jobName, Schedule schedule, IJob job)
    {
        this.jobName = jobName;
        this.schedule = schedule;
        this.job = job;
    }

    @Override
    public void run() {
        this.job.execute();
    }
}

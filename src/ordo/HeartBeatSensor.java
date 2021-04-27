package ordo;

import config.Hosts;
import formats.Format;
import map.MapReduce;
import map.Mapper;

import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static config.Hosts.workersIP;

/**
 * Implement a heartbeat mechanism that check the availability of the workers, if a worker is down the sensor will
 * reboot them
 *
 * @author Hamza Mouddene
 * @version 1.0
 */
public class HeartBeatSensor {

    /**
     * Main method of this class
     * @param args command line
     */
    public static void main(String[] args) {
        int nbWorkers = Integer.parseInt(args[0]);
        Integer[] workersON = new Integer[nbWorkers];
        Mapper[] mappersON = new Mapper[nbWorkers];
        Format[] readersON = new Format[nbWorkers];
        Format[] writersON = new Format[nbWorkers];
        CallBack[] callBacksON = new CallBack[nbWorkers];
        State jobState = null;
        MapReduce mr = null;
        try {
            while (true) {
                Registry registry1, registry2;
                for (int i = 0; i < nbWorkers; i++) {
                    try {
                        registry1 = LocateRegistry.getRegistry(Hosts.workersIP[i], 8000 + i + 1);
                        Worker server = (Worker) registry1.lookup("//localhost:" + (8000 + i + 1) +
                                "/Worker" + (i + 1));

                        /** Check the state of workers */
                        if (server.beat().equals("up")) {
                            workersON[i] = 1;
                            System.out.println("Worker on port " + (8000 + i + 1) + " up.");
                        } else if (server.beat().equals("map")) {
                            workersON[i] = 2;
                            mappersON[i] = server.getMapper();
                            readersON[i] = server.getReader();
                            writersON[i] = server.getWriter();
                            callBacksON[i] = server.getCallBack();
                            System.out.println("Worker on port " + (8000 + i + 1) + " is mapping.");
                        } else if (server.beat().equals("done")) {
                            workersON[i] = 3;
                            System.out.println("Worker on port " + (8000 + i + 1) + " done.");
                        }
                    } catch (ConnectException exception) {
                        System.out.println("Worker on port " + (8000 + i + 1) + " down.");
                        System.out.println("Reboot worker on port " + (8000 + i + 1) + ".");
                        Runtime.getRuntime().exec("./relaunch_worker.sh");
                        System.out.println("Rebooting worker on port " + (8000 + i + 1) + " done with success.");
                        Thread.sleep(2000);
                        if (workersON[i] == 2) {
                            System.out.println("Restart map of worker running on port " + (8000 + i + 1) + ".");
                            registry1 = LocateRegistry.getRegistry(workersIP[i], 8000 + (i + 1));
                            Worker w = (Worker) registry1.lookup("//localhost:" + (8000 + i + 1) + "/Worker" + (i + 1));
                            w.runMap(mappersON[i], readersON[i], writersON[i], callBacksON[i]);
                        }
                    }
                    Thread.sleep(500);
                    try {
                        registry2 = LocateRegistry.getRegistry("behemot.enseeiht.fr", 9999);
                        JobInterface job = (JobInterface) registry2.lookup("//localhost:9999/Job");
                        /** Check the state of job */
                        if (job.beat() == State.UP) {
                            jobState = State.UP;
                            System.out.println("Job on port 9999 up.");
                        } else if (job.beat() == State.START_INITIALIZE) {
                            jobState = State.START_INITIALIZE;
                            mr = job.getMapReduce();
                            System.out.println("Job on port 9999 start initialize.");
                        } else if (job.beat() == State.END_INITIALIZE) {
                            jobState = State.END_INITIALIZE;
                            System.out.println("Job on port 9999 end initialize.");
                        } else if (job.beat() == State.START_MAP) {
                            jobState = State.START_MAP;
                            System.out.println("Job on port 9999 start map.");
                        } else if (job.beat() == State.END_MAP) {
                            jobState = State.END_MAP;
                            System.out.println("Job on port 9999 end map.");
                        } else if (job.beat() == State.START_REDUCE) {
                            jobState = State.START_REDUCE;
                            System.out.println("Job on port 9999 start reduce.");
                        } else if (job.beat() == State.END_REDUCE) {
                            jobState = State.END_REDUCE;
                            System.out.println("Job on port 9999 end reduce.");
                        }
                    } catch (ConnectException exception) {
                        System.out.println("Job on port 9999 down.");
                        System.out.println("Reboot job on port 9999.");
                        Runtime.getRuntime().exec("./relaunch_job.sh");
                        System.out.println("Rebooting worker on port 9999 done with success.");
                        Thread.sleep(2000);
                        System.out.println("test : " + (jobState != State.UP));
                        if (jobState != State.UP) {
                            System.out.println("1");
                            registry2 = LocateRegistry.getRegistry("behemot.enseeiht.fr", 9999);
                            System.out.println("2");
                            JobInterface job = (JobInterface) registry2.lookup("//localhost:9999/Job");
                            System.out.println("3");
                            job.relaunchJob(mr);
                            System.out.println("4");
                        }
                    }
                    Thread.sleep(500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

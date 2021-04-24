package ordo;

import config.Hosts;
import formats.Format;
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

        try {
            while (true) {
                Registry registry1, registry2;
                for (int i = 0; i < nbWorkers; i++) {
                    try {
                        registry1 = LocateRegistry.getRegistry(Hosts.workersIP[i], 8000 + i + 1);
                        Worker server = (Worker) registry1.lookup("//localhost:" + (8000 + i + 1) +
                                "/Worker" + (i + 1));
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
                        } else {
                            workersON[i] = 3;
                            System.out.println("Worker on port " + (8000 + i + 1) + " done.");
                        }
                    } catch (ConnectException exception) {
                        System.out.println("Worker on port " + (8000 + i + 1) + " down.");
                        System.out.println("Reboot worker on port " + (8000 + i + 1) + ".");
                        Runtime.getRuntime().exec("./relaunch.sh");
                        System.out.println("Rebooting worker on port " + (8000 + i + 1) + " done with success.");
                        Thread.sleep(2000);
                        if (workersON[i] == 2) {
                            System.out.println("Restart map of worker running on port " + (8000 + i + 1) + ".");
                            registry2 = LocateRegistry.getRegistry(workersIP[i], 8000 + (i + 1));
                            Worker w = (Worker) registry2.lookup("//localhost:" + (8000 + i + 1) + "/Worker" + (i + 1));
                            w.runMap(mappersON[i], readersON[i], writersON[i], callBacksON[i]);
                        }
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

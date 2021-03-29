package ordo;

import config.Hosts;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.ConnectException;

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
        try {
            while (true) {
                Registry registry;
                for (int i = 0; i <= Hosts.workersON.size(); i++) {
                    try {
                        registry = LocateRegistry.getRegistry(Hosts.workersON.get(i), 8000 + i + 1);
                        Worker server = (Worker) registry.lookup("//" + Hosts.workersON.get(i) + ":" + (8000 + i + 1) + "/Worker" + (i + 1));
                        if (server.beat().equals("up")) {
                            System.out.println("Worker on port " + (8000 + i + 1) + " up.");
                        }
                    } catch (ConnectException exception) {
                        System.out.println("Worker on port " + (8000 + i + 1) + " down.");
                        System.out.println("Reboot worker on port " + (8000 + i + 1) + ".");
                        new WorkerImpl("//" + Hosts.workersON.get(i) + ":" + (8000 + i + 1) + "/Worker" + (i + 1));
                    }
                    Thread.sleep(2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

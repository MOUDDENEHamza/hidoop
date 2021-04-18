package ordo;

import config.Hosts;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.ConnectException;
import java.util.ArrayList;

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
        ArrayList<String> workersON = new ArrayList<>();
        int nbWorkers = Integer.parseInt(args[0]);
        try {
            while (true) {
                Registry registry;
                for (int i = 0; i < nbWorkers; i++) {
                    try {
                        registry = LocateRegistry.getRegistry(Hosts.workersIP[i], 8000 + i + 1);
                        Worker server = (Worker) registry.lookup("//localhost:" + (8000 + i + 1) +
                                "/Worker" + (i + 1));
                        if (server.beat().equals("up")) {
                            workersON.add(Hosts.workersIP[i]);
                            System.out.println("Worker on port " + (8000 + i + 1) + " up.");
                        }
                    } catch (ConnectException exception) {
                        System.out.println("Worker on port " + (8000 + i + 1) + " down.");
                        System.out.println("Reboot worker on port " + (8000 + i + 1) + ".");
                        Runtime.getRuntime().exec("ssh hmoudden@" + Hosts.workersIP[i] + ".enseeiht.fr 'cd " +
                                "nosave/hadoop && java -cp src ordo.WorkerImpl 800$((\"$i\" + 1)) $((\"$i\" + 1))'");
                    }
                    Thread.sleep(2000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

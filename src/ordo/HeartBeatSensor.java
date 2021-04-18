package ordo;

import config.Hosts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static void execCmd(String cmd) throws IOException, InterruptedException {
        try {

            // -- Linux --

            // Run a shell command
            System.out.println("0");
            Process process = Runtime.getRuntime().exec("./relaunch.sh");
            System.out.println("1");
            // Run a shell script
            // Process process = Runtime.getRuntime().exec("path/to/hello.sh");

            /**StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }*/

            System.out.println("2");
            int exitVal = process.waitFor();
            System.out.println("3");
            if (exitVal == 0) {
                System.out.println("Success!");
                //System.out.println(output);
                System.exit(0);
            } else {
                //abnormal...
                System.out.println("Failed!");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

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
                        execCmd("./relaunch.sh " + Hosts.workersIP[i] + " " + i);
                        System.out.println("Rebooting worker on port " + (8000 + i + 1) + " done with success.");
                    }
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

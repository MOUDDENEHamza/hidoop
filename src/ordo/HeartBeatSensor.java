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
        ProcessBuilder processBuilder = new ProcessBuilder();

        // -- Linux --

        // Run a shell command
        //processBuilder.command("bash", "-c", "ssh hmoudden@succube.enseeiht.fr/147.127.133.3 'cd nosave/hadoop && java -cp src ordo.WorkerImpl 8001 1'");

        // Run a shell script
        processBuilder.command("./relaunch.sh");

        try {

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                System.out.println(output);
                System.exit(0);
            } else {
                System.out.println("Failed!" + exitVal);
                System.out.println(output);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

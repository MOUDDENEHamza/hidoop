package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

    public static void main(String[] args) throws IOException {
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

}

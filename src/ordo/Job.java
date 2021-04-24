package ordo;

import formats.*;
import map.MapReduce;
import hdfs.*;
import static config.Hosts.*;
import java.io.*;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Job class realizes the JobInterface
 *
 * @author Hamza Mouddene
 * @version 1.0
 */
public class Job extends UnicastRemoteObject implements JobInterface {

    /**
     * Attributes of Job class
     */
    private Format.Type inputFormat;             // The format of the file in input
    private String inputFileName;                // The name of HDFS file
    private final ArrayList<Worker> workers;     // List of workers

    /**
     * Constructor of Job class
     */
    public Job() throws RemoteException {
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(WorkerImpl.port);
            registry.rebind("//localhost:9999/Job", this);
            System.out.println("Registry existent");
        } catch (Exception e) {
            try {
                System.out.println("Registry nonexistent, create registry");
                registry = LocateRegistry.createRegistry(WorkerImpl.port);
                registry.rebind("//localhost:9999/Job", this);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        this.workers = new ArrayList<>();
    }

    /**
     * Get the input file format
     *
     * @return the input file format
     */
    public Format.Type getInputFormat() {
        return inputFormat;
    }

    @Override
    public void setInputFormat(Format.Type ft) {
        this.inputFormat = ft;
    }

    /**
     * Get the input file name
     *
     * @return the input file name
     */
    public String getInputFileName() {
        return this.inputFileName;
    }

    @Override
    public void setInputFileName(String FileName) {
        this.inputFileName = FileName;
    }

    @Override
    public void startJob(MapReduce mr) {
        try {
            Registry registry1, registry2;
            Request nameProviderRequest;
            int nbChunks;
            Format reader, writer;

            // Get number of chunks from name provider
            registry1 = LocateRegistry.getRegistry(nameProviderIP, NameProvider.NAME_PROVIDER_PORT);
            nameProviderRequest = (Request) registry1.lookup("//localhost:" + NameProvider.NAME_PROVIDER_PORT
                    + "/ClientRequest");
            ArrayList<Pair<Integer, Pair<String, ServerRecord>>> readRequest;
            readRequest = nameProviderRequest.askReading(this.getInputFileName());
            nbChunks = readRequest.size();

            // Reminder called when the execution of map is done
            CallBackImpl cb = new CallBackImpl(nbChunks);
            String[] fileNames = new String[nbChunks];

            // Map
            for (int i = 0; i < nbChunks; i++) {
                if (this.getInputFormat() == Format.Type.LINE) {
                    reader = new LineFormat("serverData/" + readRequest.get(i).getRight().getRight().getName() + "/"
                            + readRequest.get(i).getRight().getLeft());
                } else {
                    reader = new KVFormat("serverData/" + readRequest.get(i).getRight().getRight().getName() + "/"
                            + readRequest.get(i).getRight().getLeft());
                }
                writer = new KVFormat(inputFileName + "-chunk" + (i + 1));
                registry2 = LocateRegistry.getRegistry(workersIP[i], 8000 + (i + 1));
                workers.add((Worker) registry2.lookup("//localhost:" + (8000 + i + 1) + "/Worker" + (i + 1)));
                workers.get(i).runMap(mr, reader, writer, cb);
                fileNames[i] = inputFileName + "-chunk" + (i + 1);
            }

            cb.waitForFinished();

            MergeFiles mf = new MergeFiles(fileNames);
            mf.mergeFiles(this.getInputFileName() + "-allChunks");
            mf.deleteFiles();

            // Open reader and writer
            reader = new KVFormat(this.getInputFileName() + "-allChunks");
            writer = new KVFormat(this.getInputFileName() + "-res");
            reader.open(Format.OpenMode.R);
            writer.open(Format.OpenMode.W);

            // Reduce
            mr.reduce(reader, writer);

            // Close reader and writer
            reader.close();
            writer.close();

            // Delete allChunks file
            File allChunksFile = new File(this.getInputFileName() + "-allChunks");
            allChunksFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String beat() throws RemoteException {
        return "up";
    }

    /**
     * The main method of WorkerImpl class
     *
     * @param args contain the command line
     */
    public static void main(String[] args) throws IOException {
        System.out.println("***************************** Job *****************************\n");
        System.out.println("Address : " + InetAddress.getLocalHost() + " | Port : 9999");
        System.out.println("url : //localhost:9999/Job");

        // Turn on job RMI server
        new Job();
    }

}

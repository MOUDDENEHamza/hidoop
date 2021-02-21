package ordo;

import formats.*;
import map.MapReduce;
import hdfs.*;

import java.io.*;
import java.util.concurrent.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.*;
import java.util.*;

/**
 * Job class realizes the JobInterface
 *
 * @author Hamza Mouddene
 * @version 1.0
 */
public class Job implements JobInterface {

    /**
     * Attributs of Job class
     */
    private Format.Type inputFormat;             // The format of the file in input
    private String inputFileName;                // The name of HDFS file
    private ArrayList<Worker> workers;           // List of workers
    private String mode;
    String[] workersIp = {"147.127.133.2", "147.127.133.80", "147.127.133.163", "147.127.135.222"};

    /**
     * Constructor of Job class
     */
    public Job(String mode) {
        this.mode = mode;
        this.workers = new ArrayList<Worker>();
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
    public void startJob(MapReduce mr) throws IOException, ExecutionException, InterruptedException {
        try {
            Registry registry1, registry2;
            Request nameProviderRequest;
            int nbChunks;
            Format reader, writer;
            CallBack[] callBacks;

            // Get number of chunks from name provider
            if (this.mode.equals("local")) {
                registry1 = LocateRegistry.getRegistry(NameProvider.NAME_PROVIDER_PORT);
            } else {
                registry1 = LocateRegistry.getRegistry("147.127.135.160", NameProvider.NAME_PROVIDER_PORT);
            }
            nameProviderRequest = (Request) registry1.lookup("//localhost:" + NameProvider.NAME_PROVIDER_PORT
                    + "/ClientRequest");
            ArrayList<Pair<Integer, Pair<String, ServerRecord>>> readRequest = null;
            readRequest = nameProviderRequest.askReading(this.getInputFileName());
            nbChunks = readRequest.size();

            // Reminder called when the execution of map is done
            CallBack cb = new CallBack();
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
                if (this.mode.equals("local")) {
                    registry2 = LocateRegistry.getRegistry(8000 + (i + 1));
                } else {
                    registry2 = LocateRegistry.getRegistry(workersIp[i], 8000 + (i + 1));
                }
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

}

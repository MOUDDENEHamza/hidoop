package ordo;

import formats.Format;
import map.MapReduce;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

/**
 * Launch a parallel process
 *
 * @author Hamza Mouddene
 * @version 1.0
 */
public interface JobInterface extends Remote {

    /**
     * Set the input format
     *
     * @param ft define the format of the file in input
     */
    void setInputFormat(Format.Type ft) throws RemoteException;

    /**
     * Set the name of HDFS file containing data to process
     *
     * @param FileName is the name of HDFS file
     */
    void setInputFileName(String FileName) throws RemoteException;

    /**
     * Launch maps and workers on machines
     *
     * @param mr corresponding to MapReduce to run in parallel
     */
    void startJob(MapReduce mr) throws IOException, ExecutionException, InterruptedException, RemoteException;

    /**
     * Send a heartbeat to a remote machine
     *
     * @return the state of job
     * @throws RemoteException that may occur during the execution of a remote method call
     */
    String beat() throws RemoteException;

}
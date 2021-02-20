package hdfs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface for the RMI communication between a client and the Name Provider.
 */
public interface Request extends Remote {

    /**
     * Ask to the Name Provider to write a file to the HDFS filesystem. It only gives an indication where chunks should
     * be written, nothing is registered to the Name Provider so far (in the case the client don't write chunks to the
     * servers).
     * @param chunks represents the chunks metadatas of the file to write
     * @return The servers where each chunk may be written.
     * @throws RemoteException
     */
    ArrayList<ServerRecord> askWriting(ArrayList<ChunkMetadata> chunks) throws RemoteException;

    /**
     * Ask the Name Provider to read a file from the HDFS filesystem.
     * @param fileName Name of the file to read from the HDFS filesystem
     * @return An array list containing the part number of the chunk, its SHA256 hash and a server having the chunk
     * stored
     * @throws RemoteException
     */
    ArrayList<Pair<Integer, Pair<String, ServerRecord>>> askReading(String fileName) throws RemoteException;

    /**
     * Ask the Name Provider where each chunk is stored to delete it properly.
     * @param fileName the name in the HDFS filesystem of the file to delete
     * @return a hash map containing the hash of the chunk, and the servers where the chunk is being stored.
     * @throws RemoteException
     */
    HashMap<String, ArrayList<ServerRecord>> askDeleting(String fileName) throws RemoteException;

    /**
     * Get the list of the files in the HDFS filesystem.
     * @return a string containing informations about the chunks / files stored on the HDFS filesystem
     * @throws RemoteException
     */
    String askList() throws RemoteException;

    /**
     * returns the number of servers registered to the Name Provider
     * @return the number of currently available servers on HDFS.
     * @throws RemoteException
     */
    int getNumberOfServers() throws RemoteException;
}

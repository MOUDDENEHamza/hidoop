package hdfs;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the RMI communication between a server and the Name Provider
 */
public interface Register extends Remote {

    /**
     * Get an available port for a HDFS server to communicate with a client.
     * @return An available port to use for the client-server communication
     * @throws RemoteException
     */
    int getAvailablePort() throws RemoteException;

    /**
     * Register a server to the Name Provider
     * @param sr Server Record containing the metadatas of the server to register. @see {@link ServerRecord}
     * @return True if the registration succeed and false otherwise.
     * @throws RemoteException
     */
    boolean addServer(ServerRecord sr) throws RemoteException;

    /**
     * Signal to the Name Provider that a new chunk has been successfully added to a HDFS server
     * @param sr The Server Record of the HDFS server owning the chunk @see {@link ServerRecord}
     * @param cm The metadatas of the chunk from the HDFS server @see {@link ChunkMetadata}
     * @param hash The SHA256 hash of the chunk written on the HDFS server.
     * @throws RemoteException
     */
    void addChunk(ServerRecord sr, ChunkMetadata cm, String hash) throws RemoteException;

    /**
     * Signal that a chunk has been deleted from a HDFS server
     * @param fileName The path of the file in the HDFS server
     * @param server The ServerRecord of the HDFS server deleting the file @see {@link ServerRecord}
     * @throws RemoteException
     */
    void deleteChunk(String fileName, ServerRecord server) throws RemoteException;
}

package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Reminder called when the execution of map is done
 *
 * @author Hamza Mouddene
 * @version 1.0
 */
public interface CallBack extends Remote {

    /**
     * When the task is done, we increment the counter then if the counter is equal to the number of available fragments
     * it prevents that the process has finished
     *
     * @throws RemoteException that may occur during the execution of a remote method call
     */
    void runMapDone() throws RemoteException;

    /**
     * Wait all threads finishing map execution
     *
     * @throws RemoteException that may occur during the execution of a remote method call
     */
    void waitForFinished() throws RemoteException;
}
